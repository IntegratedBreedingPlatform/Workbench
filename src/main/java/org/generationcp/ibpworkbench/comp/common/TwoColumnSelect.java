package org.generationcp.ibpworkbench.comp.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.datatype.DatatypeAttribute;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * A two-column select component replacement {@link TwinColSelect} provided by
 * Vaadin.
 * 
 * @author Glenn Marintes
 */
public class TwoColumnSelect extends HorizontalLayout {
	private static final Logger LOG = LoggerFactory.getLogger(TwoColumnSelect.class);
    private static final long serialVersionUID = 1L;
    
    private Table leftSelect;
    private Table rightSelect;
    private Button btnRight;
    private Button btnLeft;
    
    public TwoColumnSelect() {
        assemble();
    }
    
    public TwoColumnSelect(String caption) {
        setCaption(caption);
        assemble();
    }
    
    /**
     * Get the Left Table component. 
     * 
     * @return
     */
    public Table getLeftSelect() {
        return leftSelect;
    }
    
    /**
     * Get the Right Table component.
     * 
     * @return
     */
    public Table getRightSelect() {
        return rightSelect;
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    protected void initializeComponents() {
        leftSelect = new Table();
        leftSelect.setImmediate(true);
        leftSelect.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        rightSelect = new Table();
        rightSelect.setImmediate(true);
        rightSelect.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        
        leftSelect.addStyleName("no-stripes");
        rightSelect.addStyleName("no-stripes");
        
        btnRight = new Button(">>");
        btnLeft = new Button("<<");
    }
    
    protected void initializeLayout() {
        setMargin(false);
        setSpacing(true);
        
        addComponent(leftSelect);
        setExpandRatio(leftSelect, 1.0f);
        
        Component buttonArea = createButtonArea();
        addComponent(buttonArea);
        setExpandRatio(leftSelect, 1.0f);
        
        addComponent(rightSelect);
        setExpandRatio(rightSelect, 1.0f);
        
        // set the sizes
        
        buttonArea.setWidth("40px");
        buttonArea.setHeight("100%");
        
        //leftSelect.setSizeFull();
        leftSelect.setWidth("100%");
        leftSelect.setHeight("150px");
        //rightSelect.setSizeFull();
        rightSelect.setWidth("100%");
        rightSelect.setHeight("150px");
    }
    
    protected Component createButtonArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(false);
        
        Label dummyLabel = new Label(" ");
        dummyLabel.setWidth("100%");
        dummyLabel.setHeight("18px");
        layout.addComponent(dummyLabel);
        
        layout.addComponent(btnRight);
        layout.addComponent(btnLeft);
        
        btnRight.setWidth("100%");
        btnLeft.setWidth("100%");
        
        return layout;
    }
    
    protected void initializeActions() {
        btnLeft.addListener(new ItemMoveClickListener(rightSelect, leftSelect));
        btnRight.addListener(new ItemMoveClickListener(leftSelect, rightSelect));
        
        addListener(new ItemMoveClickListener(rightSelect, leftSelect));
        addListener(new ItemMoveClickListener(leftSelect, rightSelect));
    }
    
    public void removeAllItems() {
        leftSelect.removeAllItems();
        rightSelect.removeAllItems();
    }

    public void setContainerDataSource(Container container, Container container2, String fieldToShowForCaption) {
    	leftSelect.setSelectable(true);
    	leftSelect.setContainerDataSource(container);
    	rightSelect.setSelectable(true);
    	rightSelect.setContainerDataSource(container2);
    	
    	leftSelect.setVisibleColumns(new Object[] {fieldToShowForCaption});
    	rightSelect.setVisibleColumns(new Object[] {fieldToShowForCaption});
    }

    public void select(Object itemId) {
        leftSelect.removeItem(itemId);
        rightSelect.addItem(itemId);
        rightSelect.select(itemId);
    }

    public void setValue(Object itemId) {
        if (rightSelect.containsId(itemId)) {
            rightSelect.setValue(itemId);
        }
    }

    public void setItemCaption(Object itemId, String caption) {
        leftSelect.setItemCaption(itemId, caption);
        rightSelect.setItemCaption(itemId, caption);
    }
    
    public void setFieldForCaption(String columnName){
    		leftSelect.setVisibleColumns(new Object[] {columnName});
    }

    public Object getValue() {
        // select all items in the right pane
        rightSelect.setValue(rightSelect.getItemIds());
        
        return rightSelect.getValue();
    }

    public void setLeftColumnCaption(String string) {
        leftSelect.setCaption(string);
    }

    public void setRightColumnCaption(String string) {
        rightSelect.setCaption(string);
    }

    public void setMultiSelect(boolean multiSelect) {
        leftSelect.setMultiSelect(multiSelect);
        rightSelect.setMultiSelect(multiSelect);
    }

    public void setNullSelectionAllowed(boolean nullSelectionAllowed) {
        leftSelect.setNullSelectionAllowed(nullSelectionAllowed);
        rightSelect.setNullSelectionAllowed(nullSelectionAllowed);
    }

    public void addItem(Object itemId) {
        leftSelect.addItem(itemId);
    }
    
    /**
     * A ClickListener implementation that moves selected items between two
     * {@link ListSelect} components.
     */
    private class ItemMoveClickListener implements ClickListener, LayoutClickListener {
        private static final long serialVersionUID = 1L;
        
        private Table sourceSelect;
        private Table destSelect;
        
        public ItemMoveClickListener(Table sourceSelect, Table destSelect) {
            this.sourceSelect = sourceSelect;
            this.destSelect = destSelect;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void buttonClick(ClickEvent event) {
            moveSelectedItems();
        }
        
        @Override
        public void layoutClick(LayoutClickEvent event) {
            if (event.getClickedComponent() == sourceSelect && event.isDoubleClick()) {
                moveSelectedItems();
            }
        }
        
        protected void moveSelectedItems() {
            Container container = sourceSelect.getContainerDataSource();
            
            Indexed indexedContaner = null;
            if (Indexed.class.isInstance(container)) {
                indexedContaner = (Indexed) container;
            }
            
            // get the selected values in Left Select
            Set<Object> selectedItemIds = (Set<Object>) sourceSelect.getValue();
            if (selectedItemIds.size() == 0) {
                return;
            }
            
            int firstSelectedItemIndex = -1;
            for (Object itemId : selectedItemIds) {
                // get the index of this item
                if (indexedContaner != null) {
                    int index = indexedContaner.indexOfId(itemId);
                    if (firstSelectedItemIndex == -1 || index < firstSelectedItemIndex) {
                        firstSelectedItemIndex = index;
                    }
                }
                
                // remove item from the Left Select
                sourceSelect.removeItem(itemId);
                
                // move the item to the Right Select
                // if the Right Select does not contain the item yet
                if (!destSelect.containsId(itemId)) {
                    destSelect.addItem(itemId);
                }
            }
            
            // select the values we have just added to the Right Select
            destSelect.setValue(selectedItemIds);
            
            // move Left Select selection to the item after/before
            // the first selected item in the Left Select
            int selectItemIndex = -1;
            if (firstSelectedItemIndex < container.getItemIds().size()) {
                selectItemIndex = firstSelectedItemIndex;
            }
            else if (firstSelectedItemIndex >= 0) {
                selectItemIndex = container.getItemIds().size() - 1;
            }

            if (selectItemIndex >= 0) {   	
                Object itemId = indexedContaner.getIdByIndex(selectItemIndex);
                sourceSelect.select(itemId);
            }
            
        }
    }
}
