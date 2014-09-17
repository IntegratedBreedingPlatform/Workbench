package org.generationcp.ibpworkbench.ui.programmethods;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class EditBreedingMethodsWindow extends BaseSubWindow {

    private static final long serialVersionUID = 3983198771242295731L;


    private BreedingMethodForm breedingMethodForm;

    private Button cancelButton;

    private Button editBreedingMethodButton;

    private Component buttonArea;

    private VerticalLayout layout;

    private ProgramMethodsPresenter presenter;

    protected MethodView modelBean;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { "methodName", "methodDescription", "methodType", "methodCode" };

    public EditBreedingMethodsWindow(ProgramMethodsPresenter presenter,MethodView methodView) {
        this.presenter = presenter;

        this.modelBean = methodView;

        assemble();
    }

    protected void initializeComponents() {
        //newBreedingMethodTitle = new Label("Add Breeding Method");
        //newBreedingMethodTitle.setStyleName("gcp-content-title");

        //layout.addComponent(newBreedingMethodTitle);

        breedingMethodForm = new BreedingMethodForm(
        		presenter.getMethodClasses(),modelBean);

        cancelButton = new Button("Cancel");
        editBreedingMethodButton = new Button("Save");
        editBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();
    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        this.setModal(true);
        this.setWidth("700px");
        this.setResizable(false);
        this.center();
        this.setCaption("Edit Breeding Method");


        this.addStyleName(Reindeer.WINDOW_LIGHT);

        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("450px");

        final Panel p = new Panel();
        p.setStyleName("form-panel");
        p.setSizeFull();

        final VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
        vl.addComponent(breedingMethodForm);
        vl.setExpandRatio(breedingMethodForm,1.0F);

        p.addComponent(vl);
        layout.addComponent(p);
        layout.addComponent(buttonArea);

        layout.setExpandRatio(p,1.0F);
        layout.setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        layout.setSpacing(true);
        layout.setMargin(true);

        setContent(layout);
    }

    protected void initializeActions() {

        editBreedingMethodButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    breedingMethodForm.commit();
                } catch (Validator.EmptyValueException e) {
                    MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
                    return;
                } catch (Validator.InvalidValueException e) {
                    MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
                    return;
                }

                sessionData.getUniqueBreedingMethods().remove(EditBreedingMethodsWindow.this.modelBean);
                sessionData.getProjectBreedingMethodData().remove(EditBreedingMethodsWindow.this.modelBean.getMid());

                MethodView bean = ((BeanItem<MethodView>) breedingMethodForm.getItemDataSource()).getBean();
                if (StringUtils.isEmpty(bean.getMtype())) {
                    MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), "Please select a Generation Advancement Type");
                    return;
                }

                MethodView result = presenter.editBreedingMethod(bean);

                MessageNotifier.showMessage(clickEvent.getComponent().getWindow().getParent().getWindow(),messageSource.getMessage(Message.SUCCESS),result.getMname() + " breeding method is updated.");

                EditBreedingMethodsWindow.this.getParent().removeWindow(EditBreedingMethodsWindow.this);
            }
        });

        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                clickEvent.getComponent().getWindow().getParent().removeWindow(clickEvent.getComponent().getWindow());

            }
        });
    }


    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        editBreedingMethodButton = new Button("Save");
        editBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(editBreedingMethodButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    public void refreshVisibleItems(){
        breedingMethodForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
    }
}

