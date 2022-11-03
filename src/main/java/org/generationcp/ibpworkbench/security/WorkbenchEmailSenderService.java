package org.generationcp.ibpworkbench.security;

import org.apache.commons.io.IOUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.common.WebClientInfo;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
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

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by cyrus on 4/6/15.
 */
public class WorkbenchEmailSenderService {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchEmailSenderService.class);

	public static final String BMS_LOGO_LOC = "/WEB-INF/static/images/logo.png";
	public static final String RESET_LINK = "/controller/auth/reset/";

	@Resource
	private WorkbenchUserService workbenchUserService;

	@Resource
	private UserService userService;

	@Resource
	private Properties workbenchProperties;

	@Resource
	private ServletContext servletContext;

	@Resource
	private JavaMailSender mailSender;

	@Resource
	private TemplateEngine templateEngine;

	@Resource
	private MessageSource messageSource;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private WebClientInfo webClientInfo;

	@Value("${mail.server.sender.email}")
	private String senderEmail;

	@Value("${reset.expiry.hours}")
	private Integer noOfHoursBeforeExpire;

	public void doSendOneTimePasswordRequest(final WorkbenchUser user, final Integer otpCode, final boolean isNewDevice,
		final String deviceDetails, final String location)
		throws MessagingException {

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		// true = multipart
		final MimeMessageHelper message = this.getMimeMessageHelper(mimeMessage);

		try {

			final String recipientName = user.getPerson().getDisplayName();
			final String recipientEmail = user.getPerson().getEmail();

			// prepare the evaluation context
			final Context ctx = new Context(LocaleContextHolder.getLocale());
			ctx.setVariable("recipientName", recipientName);
			ctx.setVariable("otpCode", otpCode);
			ctx.setVariable("bmsLogo", WorkbenchEmailSenderService.BMS_LOGO_LOC);
			ctx.setVariable("deviceDetails", deviceDetails);
			ctx.setVariable("location", location);

			message.setSubject(
				this.messageSource.getMessage("one.time.password.mail.subject", new String[] {}, "", LocaleContextHolder.getLocale()));
			message.setFrom(this.senderEmail);
			message.setTo(recipientEmail);

			final String htmlContent =
				this.processTemplate(ctx, isNewDevice ? "one-time-password-new-device-email" : "one-time-password-email");
			// true = isHtml
			message.setText(htmlContent, true);

			// add BMS logo
			message.addInline(WorkbenchEmailSenderService.BMS_LOGO_LOC, this.retrieveLogoImage(), "image/png");

			// Send the message
			this.mailSender.send(mimeMessage);

		} catch (final IOException e) {
			WorkbenchEmailSenderService.LOG.error(e.getMessage(), e);
		}
	}

	public void doRequestPasswordReset(final WorkbenchUser user) throws MessagingException {

		UserInfo userInfo = null;
		try {
			userInfo = this.userService.getUserInfoByUsername(user.getName());

			final String generatedURL = this.generateResetPasswordUrl(userInfo);
			this.sendForgotPasswordRequest(user.getPerson().getDisplayName(), user.getPerson().getEmail(), generatedURL);

		} catch (final MiddlewareQueryException e) {
			WorkbenchEmailSenderService.LOG.error(e.getMessage(), e);
		}
	}

	public String generateResetPasswordUrl(final UserInfo userInfo) {
		// generate a strong a unique randomized string
		final String token = UUID.randomUUID().toString();
		final String url = WorkbenchAppPathResolver.getFullWebAddress(servletContext.getContextPath() + RESET_LINK + token);

		// update workbench user_info table
		userInfo.setResetToken(token);

		userInfo.setResetExpiryDate(this.getTokenExpiryDate());

		this.userService.updateUserInfo(userInfo);

		return url;
	}

	protected Date getTokenExpiryDate() {
		final Calendar cal = Calendar.getInstance(Locale.getDefault(Locale.Category.DISPLAY));
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
			final Context ctx = new Context(LocaleContextHolder.getLocale());
			ctx.setVariable("recipientName", recipientName);
			ctx.setVariable("forgotPasswordUrl", forgotPasswordUrl);
			ctx.setVariable("bmsLogo", WorkbenchEmailSenderService.BMS_LOGO_LOC);
			ctx.setVariable("expireHrs", this.noOfHoursBeforeExpire);

			message.setSubject(this.messageSource.getMessage("forgot.mail.subject", new String[] {}, "", LocaleContextHolder.getLocale()));
			message.setFrom(this.senderEmail);
			message.setTo(recipientEmail);

			final String htmlContent = this.processTemplate(ctx, "forgot-password-email");
			// true = isHtml
			message.setText(htmlContent, true);

			// add BMS logo
			message.addInline(WorkbenchEmailSenderService.BMS_LOGO_LOC, this.retrieveLogoImage(), "image/png");

			WorkbenchEmailSenderService.LOG.info("Sent password reset to {} with URL token {}", recipientEmail, forgotPasswordUrl);

			// send the message
			this.mailSender.send(mimeMessage);

		} catch (final IOException e) {
			WorkbenchEmailSenderService.LOG.error(e.getMessage(), e);
		}
	}

	protected ByteArrayResource retrieveLogoImage() throws IOException {
		return new ByteArrayResource(
			IOUtils.toByteArray(this.servletContext.getResourceAsStream(WorkbenchEmailSenderService.BMS_LOGO_LOC)));
	}

	protected String processTemplate(final Context ctx, final String template) {
		return this.templateEngine.process(template, ctx);
	}

	protected MimeMessageHelper getMimeMessageHelper(final MimeMessage mimeMessage) throws MessagingException {
		return new MimeMessageHelper(mimeMessage, true, "UTF-8");
	}

	public WorkbenchUser validateResetToken(final String token) throws InvalidResetTokenException {
		UserInfo userInfo = null;
		try {
			userInfo = this.userService.getUserInfoByResetToken(token);

			if (!this.isResetTokenValid(userInfo)) {
				throw new InvalidResetTokenException("Token is not valid");
			}

			return this.workbenchUserService.getUserByUserid(userInfo.getUserId());

		} catch (final MiddlewareQueryException e) {
			throw new InvalidResetTokenException(e.getMessage(), e);
		}
	}

	protected boolean isResetTokenValid(final UserInfo userInfo) {
		return null != userInfo && this.getTokenExpiryDate().after(userInfo.getResetExpiryDate());
	}

	public void deleteToken(final UserAccountModel user) {
		final UserInfo userInfo = this.userService.getUserInfoByUsername(user.getUsername());
		userInfo.setResetToken(null);
		userInfo.setResetExpiryDate(null);

		this.userService.updateUserInfo(userInfo);

	}
}
