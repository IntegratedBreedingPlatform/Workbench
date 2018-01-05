
package org.generationcp.ibpworkbench.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

import com.vaadin.terminal.gwt.server.WebBrowser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.common.WebClientInfo;
import org.generationcp.ibpworkbench.model.AskSupportFormModel;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
	private WorkbenchDataManager workbenchDataManager;

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

	public void doRequestPasswordReset(User user) throws MessagingException {

		UserInfo userInfo = null;
		try {
			userInfo = this.workbenchDataManager.getUserInfoByUsername(user.getName());

			String generatedURL = this.generateResetPasswordUrl(userInfo);
			this.sendForgotPasswordRequest(user.getPerson().getDisplayName(), user.getPerson().getEmail(), generatedURL);

		} catch (MiddlewareQueryException e) {
			WorkbenchEmailSenderService.LOG.error(e.getMessage(), e);
		}
	}

	public String generateResetPasswordUrl(UserInfo userInfo) {
		// generate a strong a unique randomized string
		final String token = UUID.randomUUID().toString();
		final String url = WorkbenchAppPathResolver.getFullWebAddress(servletContext.getContextPath() + RESET_LINK + token);

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


	public void sendFeedback(final AskSupportFormModel askSupportForm) throws MessagingException {
		Project lastOpenedProject = contextUtil.getProjectInContext();
		List<ProjectActivity> projectActivitiesByProjectId = workbenchDataManager.getProjectActivitiesByProjectId(lastOpenedProject.getProjectId(), 0, 1);

		User currentUser = contextUtil.getCurrentWorkbenchUser();
		// bms user account used
		String accountFullName = currentUser.getPerson().getDisplayName();
		String accountUsername = currentUser.getName();
		String accountEmail = currentUser.getPerson().getEmail();

		WebBrowser webBrowser = webClientInfo.getWebBrowser();
		String browser = webBrowser.getBrowserApplication();
		String screenResolution = String.format("%s width x %s height", webBrowser.getScreenWidth(), webBrowser.getScreenHeight());

		String lastOpenedProgram = !Objects.equals(lastOpenedProject,null) ? lastOpenedProject.getProjectName(): "N/A";
		String lastOpenedCrop =  !Objects.equals(lastOpenedProject,null) ? lastOpenedProject.getCropType().getCropName() : "N/A";
		String lastOpenedModule = (!projectActivitiesByProjectId.isEmpty()) ? projectActivitiesByProjectId.get(0).getName() : "N/A";

		// prepare message
		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		// true = multipart
		final MimeMessageHelper message = this.getMimeMessageHelper(mimeMessage);

		Context ctx = new Context(LocaleContextHolder.getLocale());
		ctx.setVariable("fullName",askSupportForm.getName());
		ctx.setVariable("email",askSupportForm.getEmail());
		ctx.setVariable("summary",askSupportForm.getSummary());
		ctx.setVariable("requestCategory",AskSupportFormModel.CATEGORIES[Integer.valueOf(askSupportForm.getRequestCategory())]);
		ctx.setVariable("description",askSupportForm.getDescription());
		ctx.setVariable("accountFullName",accountFullName);
		ctx.setVariable("accountUsername",accountUsername);
		ctx.setVariable("accountEmail",accountEmail);
		ctx.setVariable("browserInfo",browser);
		ctx.setVariable("bmsVersion",this.workbenchProperties.getProperty("workbench.version", "4.0"));
		ctx.setVariable("screenResolution",screenResolution);
		ctx.setVariable("lastOpenedProgram",lastOpenedProgram);
		ctx.setVariable("lastOpenedCrop",lastOpenedCrop);
		ctx.setVariable("lastOpenedModule",lastOpenedModule);

		message.setSubject(askSupportForm.getSummary());
		message.setFrom(askSupportForm.getEmail());
		message.setTo(this.senderEmail);

		final String htmlContent = this.processTemplate(ctx,"ask-support-email");
		message.setText(htmlContent,true);

		String attachName = askSupportForm.getFile().getOriginalFilename();

		if (StringUtils.isNotEmpty(attachName)) {
			message.addAttachment(attachName, new InputStreamSource() {
				@Override
				public InputStream getInputStream() throws IOException {
					return askSupportForm.getFile().getInputStream();
				}
			});
		}

		WorkbenchEmailSenderService.LOG.info("Sent feedback mail from {}", askSupportForm.getEmail());

		this.mailSender.send(mimeMessage);
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
			ctx.setVariable("bmsLogo", WorkbenchEmailSenderService.BMS_LOGO_LOC);
			ctx.setVariable("expireHrs", this.noOfHoursBeforeExpire);

			message.setSubject(this.messageSource.getMessage("forgot.mail.subject", new String[] {}, "", LocaleContextHolder.getLocale()));
			message.setFrom(this.senderEmail);
			message.setTo(recipientEmail);

			final String htmlContent = this.processTemplate(ctx,"forgot-password-email");
			// true = isHtml
			message.setText(htmlContent, true);

			// add BMS logo
			message.addInline(WorkbenchEmailSenderService.BMS_LOGO_LOC, this.retrieveLogoImage(), "image/png");

			WorkbenchEmailSenderService.LOG.info("Sent password reset to {} with URL token {}", recipientEmail, forgotPasswordUrl);

			// send the message
			this.mailSender.send(mimeMessage);

		} catch (IOException e) {
			WorkbenchEmailSenderService.LOG.error(e.getMessage(), e);
		}
	}

	protected ByteArrayResource retrieveLogoImage() throws IOException {
		return new ByteArrayResource(IOUtils.toByteArray(this.servletContext.getResourceAsStream(WorkbenchEmailSenderService.BMS_LOGO_LOC)));
	}

	protected String processTemplate(Context ctx,String template) {
		return this.templateEngine.process(template, ctx);
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

	public void deleteToken(UserAccountModel user) {
		UserInfo userInfo = this.workbenchDataManager.getUserInfoByUsername(user.getUsername());
		userInfo.setResetToken(null);
		userInfo.setResetExpiryDate(null);

		this.workbenchDataManager.updateUserInfo(userInfo);

	}
}
