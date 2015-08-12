
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.UnpermittedDeletionException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.service.api.FieldbookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/19/13 Time: 7:21 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class NurseryListPreviewPresenter implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreviewPresenter.class);
	public static final int MAX_STUDY_FOLDER_NAME_LENGTH = 255;

	private NurseryListPreview view;
	private Project project;

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SessionData sessionData;
	
	@Autowired
	private FieldbookService fieldbookService;

	@Resource
	private MessageSource messages;

	public static final String STUDY_DELETE_NOT_PERMITTED = "study.delete.not.permitted";

	public NurseryListPreviewPresenter(NurseryListPreview view, Project project) {
		this.view = view;
		this.project = project;

	}

	public NurseryListPreviewPresenter() {

	}

	public void generateInitialTreeNodes() {

		List<FolderReference> root;
		try {
			root = this.studyDataManager.getRootFolders(this.project.getUniqueID());
			this.view.generateTopListOfTree(root);
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// do nothing
	}

	public boolean isFolder(Integer value) {
		try {
			boolean isStudy = this.studyDataManager.isStudy(value);
			NurseryListPreviewPresenter.LOG.info("isFolder = " + !isStudy);
			return !isStudy;
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
		}

		return false;
	}

	public void renameNurseryListFolder(String newFolderName, Integer folderId) throws NurseryListPreviewException {
		try {

			this.validateStudyFolderName(newFolderName);

			this.studyDataManager.renameSubFolder(newFolderName, folderId, this.project.getUniqueID());
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
		}
	}

	public void deleteNurseryListFolder(Integer id) throws MiddlewareQueryException, NurseryListPreviewException {
		Integer cropUserId =
					this.manager.getCurrentIbdbUserId(this.sessionData.getSelectedProject().getProjectId(),
						this.sessionData.getUserData().getUserid());
		try {
			fieldbookService.deleteStudy(id, cropUserId);
		} catch (UnpermittedDeletionException e) {
			Integer studyUserId = fieldbookService.getStudy(id).getUser();
			throw new NurseryListPreviewException(this.messages.getMessage(NurseryListPreviewPresenter.STUDY_DELETE_NOT_PERMITTED,
					new String[] {fieldbookService.getOwnerListName(studyUserId)},
					"You are not able to delete this nursery or trial as you are not the owner. The owner is {0}." ,
					LocaleContextHolder.getLocale()));
		}
	}

	public Object getStudyNodeParent(Integer newItem) {
		try {
			return this.studyDataManager.getParentFolder(newItem);
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public boolean moveNurseryListFolder(Integer sourceId, Integer targetId, boolean isAStudy) throws NurseryListPreviewException {
		try {
			return this.studyDataManager.moveDmsProject(sourceId, targetId, isAStudy);
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
			throw new NurseryListPreviewException(e.getMessage());
		}
	}

	public Integer addNurseryListFolder(String name, Integer id) throws NurseryListPreviewException {
		try {

			this.validateStudyFolderName(name);

			Integer parentFolderId = id;
			if (!this.isFolder(id)) {
				// get parent
				DmsProject dmsProject = this.studyDataManager.getParentFolder(id);
				if (dmsProject == null) {
					throw new NurseryListPreviewException(NurseryListPreviewException.NO_PARENT);
				}
				parentFolderId = dmsProject.getProjectId();
			}
			return this.studyDataManager.addSubFolder(parentFolderId, name, name, this.project.getUniqueID());
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
			throw new NurseryListPreviewException(e.getMessage());
		}
	}

	protected void validateStudyFolderName(String name) throws NurseryListPreviewException {
		if (name == null || "".equals(name.trim())) {
			throw new NurseryListPreviewException(NurseryListPreviewException.BLANK_NAME);
		}
		if (name.equals(NurseryListPreview.NURSERIES_AND_TRIALS)) {
			throw new NurseryListPreviewException(NurseryListPreviewException.INVALID_NAME);
		}

		if (name.length() > NurseryListPreviewPresenter.MAX_STUDY_FOLDER_NAME_LENGTH) {
			throw new NurseryListPreviewException(NurseryListPreviewException.TOO_LONG);
		}
	}

	public Integer validateForDeleteNurseryList(Integer id) throws NurseryListPreviewException {
		NurseryListPreviewPresenter.LOG.info("id = " + id);
		if (id == null) {
			throw new NurseryListPreviewException(NurseryListPreviewException.NO_SELECTION);
		}
		DmsProject dmsProject;

		try {
			dmsProject = this.studyDataManager.getProject(id);

		} catch (MiddlewareQueryException e) {
			throw new NurseryListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
		}

		if (dmsProject == null) {
			throw new NurseryListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE));
		}

		if (this.hasChildren(id)) {
			throw new NurseryListPreviewException(NurseryListPreviewException.HAS_CHILDREN);
		}

		return id;
	}

	private boolean hasChildren(Integer id) throws NurseryListPreviewException {
		List<Reference> studyChildren;

		try {
			studyChildren = this.studyDataManager.getChildrenOfFolder(id, this.project.getUniqueID());
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(e.getMessage(), e);
			throw new NurseryListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
		}
		if (studyChildren != null && !studyChildren.isEmpty()) {
			NurseryListPreviewPresenter.LOG.info("hasChildren = true");
			return true;
		}
		NurseryListPreviewPresenter.LOG.info("hasChildren = false");
		return false;

	}

	public void addChildrenNode(int parentId) {
		List<Reference> studyChildren;

		try {
			studyChildren = this.studyDataManager.getChildrenOfFolder(parentId, this.project.getUniqueID());
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
			studyChildren = new ArrayList<Reference>();
		}

		this.view.addChildrenNode(parentId, studyChildren);

	}

	public void updateProjectLastOpenedDate() {
		try {

			// set the last opened project in the session
			IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

			ProjectUserInfoDAO projectUserInfoDao = this.manager.getProjectUserInfoDao();
			ProjectUserInfo projectUserInfo =
					projectUserInfoDao.getByProjectIdAndUserId(this.project.getProjectId().intValue(), app.getSessionData().getUserData()
							.getUserid());
			if (projectUserInfo != null) {
				projectUserInfo.setLastOpenDate(new Date());
				this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
			}

			this.project.setLastOpenDate(new Date());
			this.manager.mergeProject(this.project);

			app.getSessionData().setLastOpenedProject(this.project);

		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
		}
	}

	public StudyType getStudyType(int studyId) {
		try {
			Study study = this.studyDataManager.getStudy(studyId);
			if (study != null && study.getType() != null) {
				return StudyType.getStudyType(study.getType());
			}
			return null;
		} catch (MiddlewareQueryException e) {
			NurseryListPreviewPresenter.LOG.error(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
			return null;
		}
	}

	public void setView(NurseryListPreview view) {
		this.view = view;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void processToolbarButtons(Object treeItem) {
		// to be overridden
	}
}
