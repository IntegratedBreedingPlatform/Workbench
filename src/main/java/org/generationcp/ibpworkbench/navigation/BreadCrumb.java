/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.navigation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.generationcp.ibpworkbench.actions.ActionListener;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

/**
 * <b>Description</b>: This class represents a view/state of the application. It
 * stores the URI fragment, the label/title of the view and the class name of
 * the Listener to generate the view.
 * 
 * Temporarily set to be a Horizontal Layout until UI for the workbench is
 * finalized.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: May 25, 2012
 */
public class BreadCrumb extends HorizontalLayout{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8213285942426539122L;

    /** The level. */
    private int level;
    
    /** The uri fragment. */
    private String uriFragment;
    
    /** The label. */
    private String label;
    
    /** The class name. */
    private String className;

    /** The button. */
    private Button button;
    
    /** The v label. */
    private Label vLabel;

    /** The listener. */
    private ActionListener listener;

    /**
     * Instantiates a new bread crumb.
     *
     * @param level the level
     * @param label the label
     * @param uriFragment the uri fragment
     * @param className the class name
     */
    public BreadCrumb(int level, String label, String uriFragment, String className) {
        setLevel(level);
        setUriFragment(uriFragment);
        setLabel(label);
        setClassName(className);

        init();
    }

    /**
     * Initializes the listener.
     * 
     * TODO: Put error handling
     */
    private void initListener() {
        try {
            setListener((ActionListener) Class.forName(getClassName()).getConstructor().newInstance());
            // TODO: clean me up before you go go
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the breadcrumb components.
     */
    private void init() {
        button = new Button(getLabel());
        button.setStyleName(BaseTheme.BUTTON_LINK);

        initListener();

        Method m = null;
        try {
            m = getListener().getClass().getMethod("doAction", Event.class);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        button.addListener(ClickEvent.class, getListener(), m);

        if (level > 0) {
            vLabel = new Label(">");
            addComponent(vLabel);
        }

        addComponent(button);
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the uri fragment.
     *
     * @return the uri fragment
     */
    public String getUriFragment() {
        return uriFragment;
    }

    /**
     * Sets the uri fragment.
     *
     * @param uriFragment the new uri fragment
     */
    public void setUriFragment(String uriFragment) {
        this.uriFragment = uriFragment;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets the class name.
     *
     * @param className the new class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the listener.
     *
     * @param listener the new listener
     */
    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    /**
     * Gets the listener.
     *
     * @return the listener
     */
    public ActionListener getListener() {
        return listener;
    }

}
