package org.generationcp.ibworkbench.restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.database.CropDatabaseGenerator;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.ResourceFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import com.vaadin.ui.Window;

import org.junit.Assert;

@Ignore
public class RestoreUtilTest {

	protected static final Logger LOG = LoggerFactory.getLogger(RestoreUtilTest.class);

	private HibernateUtil hibernateUtil;
	private WorkbenchDataManagerImpl workbenchDataManager;

	private MySQLUtil mySqlUtil;

	private SimpleResourceBundleMessageSource messageSource;

	private static final String cropType = CropType.CropEnum.MAIZE.toString();

	private static final String filename = "Maize_Tutorial_002-20140806.sql";
	private static final String prefixDirectory = "/updatedIbdbScripts";

	private static final String DEFAULT_IBDB_GIT_URL = "https://github.com/digitalabs/IBDBScripts";

	private File scriptsDir;
	private File tempInstallationDir;
	private File tempDir;
	private String gitUrl;

	@After
	public void tearDown() throws IOException {
		if (this.tempInstallationDir != null && this.tempInstallationDir.isDirectory()) {
			FileUtils.deleteDirectory(this.tempInstallationDir);
		}
		if (this.tempDir != null && this.tempDir.isDirectory()) {
			FileUtils.deleteDirectory(this.tempDir);
		}
	}

	@Before
	public void setUp() throws Exception {
		final String propertyFile = "workbench.properties";
		final String testPropertyFile = "test.properties";
		final String prefix = "workbench";

		final DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters(propertyFile, prefix);
		this.hibernateUtil =
				new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), workbenchDb.getUsername(),
						workbenchDb.getPassword());
		final HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(this.hibernateUtil.getSessionFactory());
		this.workbenchDataManager = Mockito.spy(new WorkbenchDataManagerImpl(sessionProvider));

		this.mySqlUtil = new MySQLUtil();
		this.mySqlUtil.setMysqlDriver(workbenchDb.getDriverName());
		this.mySqlUtil.setUsername(workbenchDb.getUsername());
		this.mySqlUtil.setPassword(workbenchDb.getPassword());
		this.mySqlUtil.setMysqlHost(workbenchDb.getHost());
		this.mySqlUtil.setMysqlPort(Integer.valueOf(workbenchDb.getPort()));

		final InputStream in = new FileInputStream(new File(ResourceFinder.locateFile(testPropertyFile).toURI()));
		final Properties prop = new Properties();
		prop.load(in);

		final String mysqlDumpPath = prop.getProperty("test.mysqlDumpPath", null);
		final String mysqlPath = prop.getProperty("test.mysqlExePath", null);

		this.mySqlUtil.setMysqlDumpPath(mysqlDumpPath);
		this.mySqlUtil.setMysqlPath(mysqlPath);

		this.messageSource = Mockito.mock(SimpleResourceBundleMessageSource.class);

		this.scriptsDir = new File(RestoreUtilTest.prefixDirectory + "/database/local");
		this.scriptsDir.mkdir();

		final String ibdbScriptsGitUrl = prop.getProperty("test.ibdb.scripts.git.url", null);
		if (ibdbScriptsGitUrl == null) {
			// we use the default url
			this.gitUrl = RestoreUtilTest.DEFAULT_IBDB_GIT_URL;
		} else {
			this.gitUrl = ibdbScriptsGitUrl;
		}

		this.gitUrl += "/trunk/local";
		this.tempInstallationDir = new File(RestoreUtilTest.prefixDirectory);
		this.tempDir = new File("temp");
	}

	private String getTestProjectName() {
		return "Test-" + UUID.randomUUID().toString();
	}

	private void doIbdbScriptsCheckout() throws SVNException {
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		try {
			final SvnCheckout checkout = svnOperationFactory.createCheckout();
			checkout.setSingleTarget(SvnTarget.fromFile(this.scriptsDir));
			final SVNURL url = SVNURL.parseURIEncoded(this.gitUrl);
			checkout.setSource(SvnTarget.fromURL(url));
			checkout.run();
		} catch (final SVNException e) {
			throw e;
		} finally {
			svnOperationFactory.dispose();
		}

	}

	private String copyRestoreFile() {
		File newRestoreFile = null;
		try {
			final File restoreFile = new File(ResourceFinder.locateFile(RestoreUtilTest.filename).toURI());
			// copy to the checkout directory
			newRestoreFile = new File(this.tempInstallationDir.getAbsolutePath(), RestoreUtilTest.filename);
			FileUtils.copyFile(restoreFile, newRestoreFile);
		} catch (final FileNotFoundException e) {
			RestoreUtilTest.LOG.error(e.getMessage(), e);
		} catch (final URISyntaxException e) {
			RestoreUtilTest.LOG.error(e.getMessage(), e);
		} catch (final IOException e) {
			RestoreUtilTest.LOG.error(e.getMessage(), e);
		}
		return newRestoreFile != null ? newRestoreFile.getAbsolutePath() : "";
	}

	private boolean hasInternetConnection() {
		final Socket sock = new Socket();
		final InetSocketAddress addr = new InetSocketAddress("www.google.com", 80);
		try {
			sock.connect(addr, 3000);
			return true;
		} catch (final IOException e) {
			return false;
		} finally {
			try {
				sock.close();
			} catch (final IOException e) {
			}
		}
	}

	@Test
	public void testRestoreIfUserHasConfirmed() throws URISyntaxException {
		if (this.hasInternetConnection()) {

			if (this.mySqlUtil.getMysqlDumpPath() == null || "".equalsIgnoreCase(this.mySqlUtil.getMysqlDumpPath())) {
				Assert.fail("MySQL executables is not setup properly to run the test");
			}
			try {
				this.doIbdbScriptsCheckout();
				final Project newProject = new Project();
				newProject.setCropType(new CropType(RestoreUtilTest.cropType));
				newProject.setLastOpenDate(new Date());
				newProject.setStartDate(new Date());
				newProject.setProjectName(this.getTestProjectName());
				newProject.setUserId(1);

				final Project project = this.workbenchDataManager.addProject(newProject);
				this.workbenchDataManager.saveOrUpdateProject(project);

				final CropDatabaseGenerator generator = new CropDatabaseGenerator(project.getCropType());
				generator.generateDatabase();

				final String fullFilePath = this.copyRestoreFile();

				final ConfirmDialog confirmDialog = new CustomConfirmDialog(true);

				final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(project, new Window());
				final ContextUtil contextUtil = Mockito.mock(ContextUtil.class);
				Mockito.when(contextUtil.getProjectInContext()).thenReturn(project);
				Mockito.when(contextUtil.getCurrentWorkbenchUser()).thenReturn(new WorkbenchUser(1));
				restoreAction.setContextUtil(contextUtil);
				restoreAction.setMysqlUtil(this.mySqlUtil);
				restoreAction.setWorkbenchDataManager(this.workbenchDataManager);
				restoreAction.setMessageSource(this.messageSource);
				restoreAction.setRestoreFile(new File(fullFilePath));
				restoreAction.onClose(confirmDialog);

				this.workbenchDataManager.deleteProject(project);

				Assert.assertFalse("There is a restore process error", restoreAction.isHasRestoreError());

			} catch (final SVNException e) {
				Assert.fail("Error during checkout of ibdbscripts => " + e.getMessage());
			} catch (final Exception e) {
				Assert.fail(
						"Error happened during the restore process, please check the sql scripts: More error details => " + e.getMessage());
			}
		} else {
			Assert.fail("Server does not have internet connection");
		}
	}

	@Test
	public void testRestoreIfUserDidNotConfirmed() throws URISyntaxException {
		if (this.hasInternetConnection()) {
			if (this.mySqlUtil.getMysqlDumpPath() == null) {
				Assert.fail("MySQL executables is not setup properly to run the test");
			}
			try {
				this.doIbdbScriptsCheckout();
				final Project newProject = new Project();
				newProject.setCropType(new CropType(RestoreUtilTest.cropType));
				newProject.setLastOpenDate(new Date());
				newProject.setStartDate(new Date());
				newProject.setProjectName(this.getTestProjectName());
				newProject.setUserId(1);

				final Project project = this.workbenchDataManager.addProject(newProject);
				this.workbenchDataManager.saveOrUpdateProject(project);

				final CropDatabaseGenerator generator;
				generator = new CropDatabaseGenerator(project.getCropType());
				generator.generateDatabase();

				final ConfirmDialog confirmDialog = new CustomConfirmDialog(false);

				final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(project, new Window());
				restoreAction.onClose(confirmDialog);

				this.workbenchDataManager.deleteProject(project);

				Assert.assertTrue("Should return false since user did not confirm the restore process", restoreAction.isHasRestoreError());

			} catch (final SVNException e) {
				Assert.fail("Error during checkout of ibdbscripts => " + e.getMessage());
			} catch (final Exception e) {
				Assert.fail(
						"Error happened during the restore process, please check the sql scripts: More error details => " + e.getMessage());
			}
		} else {
			Assert.fail("Server does not have internet connection");
		}
	}

	// we will use this class to simulate that the user has confirmed in the dialog
	private class CustomConfirmDialog extends ConfirmDialog {

		private static final long serialVersionUID = 1L;

		CustomConfirmDialog(final boolean isConfirmed) {
			super();
			this.setConfirmed(isConfirmed);
		}
	}

}
