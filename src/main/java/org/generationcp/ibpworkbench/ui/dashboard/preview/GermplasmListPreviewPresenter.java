
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/19/13 Time: 7:20 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreviewPresenter implements InitializingBean {

	public static final String FOLDER = "FOLDER";
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreviewPresenter.class);
	private static final int BATCH_SIZE = 500;
	public static final int MAX_LIST_FOLDER_NAME_LENGTH = 50;
	private GermplasmListPreview view;
	private Project project;

	@Autowired
	private WorkbenchDataManager manager;
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private SessionData sessionData;

	public GermplasmListPreviewPresenter(final GermplasmListPreview view, final Project project) {
		this.view = view;

		this.project = project;
	}

	public GermplasmListPreviewPresenter() {
	}

	public void generateInitialTreeNode() {
		List<GermplasmList> germplasmListParent;

		try {
			germplasmListParent =
					this.germplasmListManager.getAllTopLevelListsBatched(this.project.getUniqueID(),
							GermplasmListPreviewPresenter.BATCH_SIZE);
		} catch (final MiddlewareQueryException e) {
			GermplasmListPreviewPresenter.LOG.error(e.getLocalizedMessage(), e);
			germplasmListParent = new ArrayList<GermplasmList>();
		}

		this.view.generateTree(germplasmListParent);

	}

	public void addGermplasmListNode(final int parentGermplasmListId, final Object itemId) {
		List<GermplasmList> germplasmListChildren;

		try {
			germplasmListChildren =
					this.germplasmListManager.getGermplasmListByParentFolderIdBatched(parentGermplasmListId, this.project.getUniqueID(),
							GermplasmListPreviewPresenter.BATCH_SIZE);
		} catch (final MiddlewareQueryException e) {
			GermplasmListPreviewPresenter.LOG.error(e.getLocalizedMessage(), e);
			germplasmListChildren = new ArrayList<GermplasmList>();
		}

		this.view.addGermplasmListNode(parentGermplasmListId, germplasmListChildren, itemId);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// does nothing
	}

	public boolean isFolder(final Integer id) {
		try {
			return this.germplasmListManager.getGermplasmListById(id).isFolder();
		} catch (final MiddlewareQueryException e) {
			GermplasmListPreviewPresenter.LOG.error(e.getLocalizedMessage(), e);
			return false;
		} catch (final NullPointerException e) {
			GermplasmListPreviewPresenter.LOG.error(e.getLocalizedMessage(), e);
			return false;
		}
	}

	public boolean hasChildren(final Integer id) throws MiddlewareQueryException {
		return !this.germplasmListManager.getGermplasmListByParentFolderId(id, this.project.getUniqueID(), 0, Integer.MAX_VALUE).isEmpty();
	}

	public GermplasmList getGermplasmListParent(final Integer id) throws GermplasmListPreviewException {
		try {
			return this.germplasmListManager.getGermplasmListById(id).getParent();

		} catch (final MiddlewareQueryException e) {
			throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.DATABASE_ERROR), e);
		} catch (final NullPointerException e) {
			throw new GermplasmListPreviewException(GermplasmListPreviewException.NO_PARENT, e);
		}

	}

	public boolean renameGermplasmListFolder(final String newName, final Integer id) throws GermplasmListPreviewException {
		try {

			this.validateGermplasmListFolderName(newName);

			final GermplasmList gpList = this.germplasmListManager.getGermplasmListById(id);

			if (!gpList.isFolder()) {
				throw new GermplasmListPreviewException(GermplasmListPreviewException.NOT_FOLDER);
			}

			gpList.setName(newName);

			this.germplasmListManager.updateGermplasmList(gpList);

			return true;
		} catch (final MiddlewareQueryException e) {
			GermplasmListPreviewPresenter.LOG.error(e.getLocalizedMessage(), e);
			throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE));
		}
	}

	/**
	 * @param folderName of the germplasm
	 * @param id of the parent
	 * @return ID of the newly added germplasmList, null if not successful
	 */
	public Integer addGermplasmListFolder(final String folderName, final Integer id) throws GermplasmListPreviewException {
		GermplasmList gpList, newList;
		try {

			this.validateGermplasmListFolderName(folderName);

			final Integer userId =
					this.manager.getLocalIbdbUserId(this.sessionData.getUserData().getUserid(), this.sessionData.getSelectedProject()
							.getProjectId());
			final long currentDate = DateUtil.getCurrentDateAsLongValue();
			if (id != null) {
				gpList = this.germplasmListManager.getGermplasmListById(id);

				if (null != gpList && !gpList.isFolder()) {
					newList =
							new GermplasmList(null, folderName, currentDate, GermplasmListPreviewPresenter.FOLDER, userId, folderName,
									gpList.getParent(), 0);
				} else {
					newList =
							new GermplasmList(null, folderName, currentDate, GermplasmListPreviewPresenter.FOLDER, userId, folderName,
									gpList, 0);
				}
			} else {
				newList =
						new GermplasmList(null, folderName, currentDate, GermplasmListPreviewPresenter.FOLDER, userId, folderName, null, 0);
			}

			newList.setDescription(folderName);
			newList.setProgramUUID(this.project.getUniqueID());
			return this.germplasmListManager.addGermplasmList(newList);

		} catch (final MiddlewareQueryException e) {
			throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
		}
	}

	private void checkIfUnique(final String folderName, final String programUUID) throws GermplasmListPreviewException {
		final List<GermplasmList> duplicate = this.germplasmListManager.getGermplasmListByName(folderName, programUUID, 0, 1, null);
		if (duplicate != null && !duplicate.isEmpty()) {
			throw new GermplasmListPreviewException(GermplasmListPreviewException.NAME_NOT_UNIQUE);
		}
	}

	protected void validateGermplasmListFolderName(final String germplasmListFolderName) throws GermplasmListPreviewException {

		if (germplasmListFolderName == null || germplasmListFolderName.trim().isEmpty()) {
			throw new GermplasmListPreviewException(GermplasmListPreviewException.BLANK_NAME);
		} else if (germplasmListFolderName.equals(this.view.getListLabel())) {
			throw new GermplasmListPreviewException(GermplasmListPreviewException.INVALID_NAME);
		} else if (germplasmListFolderName.trim().length() > GermplasmListPreviewPresenter.MAX_LIST_FOLDER_NAME_LENGTH) {
			throw new GermplasmListPreviewException(GermplasmListPreviewException.LONG_NAME);
		}

		this.checkIfUnique(germplasmListFolderName, this.project.getUniqueID());

	}

	public GermplasmList validateForDeleteGermplasmList(final Integer id) throws GermplasmListPreviewException {
		GermplasmList gpList;

		try {
			if (id == null) {
				throw new GermplasmListPreviewException(GermplasmListPreviewException.NO_SELECTION);
			}

			gpList = this.germplasmListManager.getGermplasmListById(id);

			if (gpList == null) {
				throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE));
			}

			final Integer cropUserId =
					this.manager.getCurrentIbdbUserId(this.sessionData.getSelectedProject().getProjectId(), this.sessionData.getUserData()
							.getUserid());

			if (null == cropUserId) {
				throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE));
			}

			if (!cropUserId.equals(gpList.getUserId())) {
				throw new GermplasmListPreviewException(GermplasmListPreviewException.NOT_USER);
			}

			if (this.hasChildren(gpList.getId())) {
				throw new GermplasmListPreviewException(GermplasmListPreviewException.HAS_CHILDREN);
			}

		} catch (final MiddlewareQueryException e) {
			throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
		}

		return gpList;
	}

	/**
	 * Assumes that the deletion has been validated
	 * 
	 * @param gpList list to be deleted
	 * @return deleted integer id
	 * @throws Error
	 */
	public Integer deleteGermplasmListFolder(final GermplasmList gpList) throws GermplasmListPreviewException {
		try {
			return this.germplasmListManager.deleteGermplasmList(gpList);
		} catch (final MiddlewareQueryException e) {
			GermplasmListPreviewPresenter.LOG.error(e.getLocalizedMessage(), e);
			throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE));
		}
	}

	public Integer dropGermplasmListToParent(final Integer id, final Integer parentId) throws GermplasmListPreviewException {
		try {
			final GermplasmList gpList = this.germplasmListManager.getGermplasmListById(id);

			if (parentId != null) {
				final GermplasmList parent = this.germplasmListManager.getGermplasmListById(parentId);
				gpList.setParent(parent);
			} else {
				gpList.setParent(null);
			}

			return this.germplasmListManager.updateGermplasmList(gpList);

		} catch (final MiddlewareQueryException e) {
			throw new GermplasmListPreviewException(this.messageSource.getMessage(Message.ERROR_DATABASE), e);
		}
	}

	public void updateProjectLastOpenedDate() {
		try {
			// set the last opened project in the session
			final IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

			final ProjectUserInfoDAO projectUserInfoDao = this.manager.getProjectUserInfoDao();
			final ProjectUserInfo projectUserInfo =
					projectUserInfoDao.getByProjectIdAndUserId(this.project.getProjectId().intValue(), app.getSessionData().getUserData()
							.getUserid());
			if (projectUserInfo != null) {
				projectUserInfo.setLastOpenDate(new Date());
				this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
			}

			this.project.setLastOpenDate(new Date());
			this.manager.mergeProject(this.project);

			app.getSessionData().setLastOpenedProject(this.project);

		} catch (final MiddlewareQueryException e) {
			GermplasmListPreviewPresenter.LOG.error(e.toString(), e);
		}
	}

	public void setView(final GermplasmListPreview view) {
		this.view = view;
	}

	public GermplasmListPreview getView() {
		return this.view;
	}
}
