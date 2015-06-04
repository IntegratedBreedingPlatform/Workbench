
package org.generationcp.ibpworkbench.security;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Created by cyrus on 4/6/15.
 */
public class ForgotPasswordEmailService {

	private static final Logger LOG = LoggerFactory.getLogger(ForgotPasswordEmailService.class);

	public static final String BMS_LOGO_LOC = "/WEB-INF/static/images/logo.png";

	@Resource
	private WorkbenchUserService workbenchUserService;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private ServletContext servletContext;

	@Resource
	private JavaMailSender mailSender;

	@Resource
	private TemplateEngine templateEngine;

	@Resource
	private MessageSource messageSource;

	@Value("${mail.server.sender.email}")
	private String senderEmail;

	@Value("${reset.expiry.hours}")
	private Integer noOfHoursBeforeExpire;

	public void doRequestPasswordReset(User user) throws MessagingException {

		UserInfo userInfo = null;
		try {
			userInfo = this.workbenchDataManager.getUserInfoByUsername(user.getName());

			String generatedURL = this.generateResetPasswordUrl(userInfo);
			this.sendForgotPasswordRequest(user.getPerson().getDisplayName(), user.getPerson().getEmail(), generatedURL);

		} catch (MiddlewareQueryException e) {
			ForgotPasswordEmailService.LOG.error(e.getMessage(), e);
		}
	}

	public String generateResetPasswordUrl(UserInfo userInfo) throws MiddlewareQueryException {
		// generate a strong a unique randomized string
		final String token = UUID.randomUUID().toString();

		final String url = WorkbenchAppPathResolver.getFullWebAddress("ibpworkbench/controller/auth/reset/" + token);

		// update workbench user_info table
		userInfo.setResetToken(token);

		userInfo.setResetExpiryDate(this.getTokenExpiryDate());

		this.workbenchDataManager.updateUserInfo(userInfo);

		return url;
	}

	protected Date getTokenExpiryDate() {
		Calendar cal = Calendar.getInstance(Locale.getDefault(Locale.Category.DISPLAY));
		cal.add(Calendar.HOUR, this.noOfHoursBeforeExpire);

		return cal.getTime();
	}

	/**
	 * Pre-req: a validated user email account + username
	 */
	public void sendForgotPasswordRequest(final String recipientName, final String recipientEmail, final String forgotPasswordUrl)
			throws MessagingException {

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		// true = multipart
		final MimeMessageHelper message = this.getMimeMessageHelper(mimeMessage);

		try {
			// prepare the evaluation context
			Context ctx = new Context(LocaleContextHolder.getLocale());
			ctx.setVariable("recipientName", recipientName);
			ctx.setVariable("forgotPasswordUrl", forgotPasswordUrl);
			ctx.setVariable("bmsLogo", ForgotPasswordEmailService.BMS_LOGO_LOC);
			ctx.setVariable("expireHrs", this.noOfHoursBeforeExpire);

			message.setSubject(this.messageSource.getMessage("forgot.mail.subject", new String[] {}, "", LocaleContextHolder.getLocale()));
			message.setFrom(this.senderEmail);
			message.setTo(recipientEmail);

			final String htmlContent = this.processTemplate(ctx);
			// true = isHtml
			message.setText(htmlContent, true);

			// add BMS logo
			message.addInline(ForgotPasswordEmailService.BMS_LOGO_LOC, this.retrieveLogoImage(), "image/png");

			ForgotPasswordEmailService.LOG.info("Sent password reset to {} with URL token {}", recipientEmail, forgotPasswordUrl);
		} catch (IOException e) {
			ForgotPasswordEmailService.LOG.error(e.getMessage(), e);
		} finally {
			// send the message
			this.mailSender.send(mimeMessage);
		}
	}

	protected ByteArrayResource retrieveLogoImage() throws IOException {
		return new ByteArrayResource(IOUtils.toByteArray(this.servletContext.getResourceAsStream(ForgotPasswordEmailService.BMS_LOGO_LOC)));
	}

	protected String processTemplate(Context ctx) {
		return this.templateEngine.process("forgot-password-email", ctx);
	}

	protected MimeMessageHelper getMimeMessageHelper(MimeMessage mimeMessage) throws MessagingException {
		return new MimeMessageHelper(mimeMessage, true, "UTF-8");
	}

	public User validateResetToken(String token) throws InvalidResetTokenException {
		UserInfo userInfo = null;
		try {
			userInfo = this.workbenchDataManager.getUserInfoByResetToken(token);

			if (!this.isResetTokenValid(userInfo)) {
				throw new InvalidResetTokenException("Token is not valid");
			}

			return this.workbenchUserService.getUserByUserid(userInfo.getUserId());

		} catch (MiddlewareQueryException e) {
			throw new InvalidResetTokenException(e.getMessage(), e);
		}
	}

	protected boolean isResetTokenValid(UserInfo userInfo) {
		return null != userInfo && this.getTokenExpiryDate().after(userInfo.getResetExpiryDate());
	}

	public void deleteToken(UserAccountModel user) throws MiddlewareQueryException {
		UserInfo userInfo = this.workbenchDataManager.getUserInfoByUsername(user.getUsername());
		userInfo.setResetToken(null);
		userInfo.setResetExpiryDate(null);

		this.workbenchDataManager.updateUserInfo(userInfo);

	}
}
