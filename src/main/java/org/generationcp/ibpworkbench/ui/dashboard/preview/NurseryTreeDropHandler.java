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
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;

/**
* Created with IntelliJ IDEA.
* User: cyrus
* Date: 12/26/13
* Time: 10:34 PM
* To change this template use File | Settings | File Templates.
*/
class NurseryTreeDropHandler implements DropHandler {
    private final Tree tree;
    private final NurseryListPreviewPresenter presenter;

    public NurseryTreeDropHandler(Tree tree, NurseryListPreviewPresenter presenter) {
        this.tree = tree;
        this.presenter = presenter;
    }


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
        HierarchicalContainer container = (HierarchicalContainer) tree
                .getContainerDataSource();

        if ((targetItemId instanceof String && ((String) targetItemId).equals(NurseryListPreview.SHARED_STUDIES)) || (targetItemId instanceof Integer && ((Integer) targetItemId) > 0)) {
            MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), "Error occurred", "Cannot move folder to Public Studies");
            return;
        }

        if (container.hasChildren(sourceItemId)) {
            MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), "Error occurred", "Cannot move folder with child elements");
            return;
        }

        // Sorting goes as
        // - If dropped ON a node, we append it as a child
        // - If dropped on the TOP part of a node, we move/add it before
        // the node
        // - If dropped on the BOTTOM part of a node, we move/add it
        // after the node

        boolean success = true;
        try {
            int actualTargetId = 0;
            // switch to using the root folder id if target is the root of the local folder
            if (targetItemId instanceof String && ((String) targetItemId).equals(NurseryListPreview.MY_STUDIES)) {
                actualTargetId = NurseryListPreview.ROOT_FOLDER;
            } else {
                actualTargetId = (Integer)targetItemId;
            }

            success = presenter.moveNurseryListFolder((Integer) sourceItemId, actualTargetId);
        } catch (Error error) {
            MessageNotifier.showError(IBPWorkbenchApplication.get().getMainWindow(), error.getMessage(), "");
            success = false;
        }

        // only perform UI change if backend modification was successful
        if (success) {

            if (location == VerticalDropLocation.MIDDLE) {
                if (container.setParent(sourceItemId, targetItemId)
                        && container.hasChildren(targetItemId)) {
                    // move first in the container
                    container.moveAfterSibling(sourceItemId, null);
                }
            } else if (location == VerticalDropLocation.TOP) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    // reorder only the two items, moving source above target
                    container.moveAfterSibling(sourceItemId, targetItemId);
                    container.moveAfterSibling(targetItemId, sourceItemId);
                }
            } else if (location == VerticalDropLocation.BOTTOM) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    container.moveAfterSibling(sourceItemId, targetItemId);
                }
            }
        }
    }

}
