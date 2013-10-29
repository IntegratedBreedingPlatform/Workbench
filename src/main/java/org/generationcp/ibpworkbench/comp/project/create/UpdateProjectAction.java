package org.generationcp.ibpworkbench.comp.project.create;

import com.vaadin.ui.Button;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateProjectAction implements Button.ClickListener  {

    private CreateProjectPanel projectPanel;

    public UpdateProjectAction(CreateProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
    }




    @Override
    public void buttonClick(Button.ClickEvent event) {

        if (projectPanel.validate()) {

        }


    }


}
