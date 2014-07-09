package org.generationcp.ibpworkbench.ui.dashboard.preview;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Tree;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
* Created with IntelliJ IDEA.
* User: cyrus
* Date: 12/26/13
* Time: 10:34 PM
* To change this template use File | Settings | File Templates.
*/
@Configurable
class NurseryTreeDropHandler implements DropHandler {
    private final Tree tree;
    private final NurseryListPreviewPresenter presenter;

    public NurseryTreeDropHandler(Tree tree, NurseryListPreviewPresenter presenter) {
        this.tree = tree;
        this.presenter = presenter;
    }

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Override
    public void drop(DragAndDropEvent dropEvent) {
        // Called whenever a drop occurs on the component

        // Make sure the drag source is the same tree
        Transferable t = dropEvent.getTransferable();

        // see the comment in getAcceptCriterion()
        if (t.getSourceComponent() != tree
                || !(t instanceof DataBoundTransferable)) {
            return;
        }

        Tree.TreeTargetDetails dropData = ((Tree.TreeTargetDetails) dropEvent
                .getTargetDetails());

        Object sourceItemId = ((DataBoundTransferable) t).getItemId();
        // FIXME: Why "over", should be "targetItemId" or just
        // "getItemId"
        Object targetItemId = dropData.getItemIdOver();

        // Location describes on which part of the node the drop took
        // place
        VerticalDropLocation location = dropData.getDropLocation();

        moveNode(sourceItemId, targetItemId, location);

    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }

    /**
     * Move a node within a tree onto, above or below another node depending
     * on the drop location.
     *
     * @param sourceItemId id of the item to move
     * @param targetItemId id of the item onto which the source node should be moved
     * @param location     VerticalDropLocation indicating where the source node was
     *                     dropped relative to the target node
     */
    private void moveNode(Object sourceItemId, Object targetItemId,
                          VerticalDropLocation location) {
    	
    	if(location != VerticalDropLocation.MIDDLE || sourceItemId.equals(targetItemId)){
        	return;
        }
    	
    	HierarchicalContainer container = (HierarchicalContainer) tree
                .getContainerDataSource();
        
        if(sourceItemId.equals(NurseryListPreview.SHARED_STUDIES) || sourceItemId.equals(NurseryListPreview.MY_STUDIES)){
    		MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(),messageSource.getMessage(Message.INVALID_OPERATION),messageSource.getMessage(Message.UNABLE_TO_MOVE_ROOT_FOLDERS));
            return;
    	}
    	
    	if ((targetItemId instanceof String && ((String) targetItemId).equals(NurseryListPreview.SHARED_STUDIES)) || (targetItemId instanceof Integer && ((Integer) targetItemId) > 0)) {
            MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(),messageSource.getMessage(Message.INVALID_OPERATION),messageSource.getMessage(Message.INVALID_CANNOT_MOVE_ITEM,tree.getItemCaption(sourceItemId),messageSource.getMessage(Message.SHARED_STUDIES)));
            return;
        }

        if (container.hasChildren(sourceItemId)) {
            MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), messageSource.getMessage(Message.INVALID_OPERATION),messageSource.getMessage(Message.INVALID_CANNOT_MOVE_ITEM_WITH_CHILD,tree.getItemCaption(sourceItemId)));
            return;
        }
        
        if (targetItemId instanceof Integer && !presenter.isFolder((Integer)targetItemId)) {
        	DmsProject parentFolder = (DmsProject)presenter.getStudyNodeParent((Integer) targetItemId);
        	if(parentFolder != null){
        		if(((Integer) targetItemId).intValue() < 0 && parentFolder.getProjectId().equals(
        				NurseryListPreview.ROOT_FOLDER)){
        			targetItemId = NurseryListPreview.MY_STUDIES;
        		} else {
        			targetItemId = parentFolder.getProjectId();
        		}
        	} else {
        		targetItemId = NurseryListPreview.MY_STUDIES;
        	}
        	
        	
        }

        boolean success = true;
        try {
            int actualTargetId = 0;
            // switch to using the root folder id if target is the root of the local folder
            if (targetItemId instanceof String && ((String) targetItemId).equals(NurseryListPreview.MY_STUDIES)) {
                actualTargetId = NurseryListPreview.ROOT_FOLDER;
            } else {
                actualTargetId = (Integer)targetItemId;
            }

            Integer source = (Integer)sourceItemId;

            success = presenter.moveNurseryListFolder(source, actualTargetId, !presenter.isFolder(source));
        } catch (Error error) {
            MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(),messageSource.getMessage(Message.ERROR), error.getMessage());
            success = false;
        }

        // only perform UI change if backend modification was successful
        if (success) {

        	container.setParent(sourceItemId, targetItemId);
            container.moveAfterSibling(sourceItemId, null);
            
        }
    }

}
