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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

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
@Configurable
public class BreadCrumb extends HorizontalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(BreadCrumb.class);
    
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
        super();
        setLevel(level);
        setUriFragment(uriFragment);
        setLabel(label);
        setClassName(className);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
    
    public void throwContactAdminError(Exception e) throws Exception {
        throw new InternationalizableException(e, Message.CONFIG_ERROR,
                Message.CONTACT_ADMIN_ERROR_DESC);
    }

    /**
     * Initializes the listener.
     * 
     * TODO: Put error handling
     * @throws Exception 
     */
    private void initListener() throws Exception {
        try {
            setListener((ActionListener) Class.forName(getClassName()).getConstructor().newInstance());
            // TODO: clean me up before you go go
        } catch (IllegalArgumentException e) {
            LOG.error("IllegalArgumentException", e);
            throwContactAdminError(e);
        } catch (SecurityException e) {
            LOG.error("SecurityException", e);
            throwContactAdminError(e);
        } catch (InstantiationException e) {
            LOG.error("InstantiationException", e);
            throwContactAdminError(e);
        } catch (IllegalAccessException e) {
            LOG.error("IllegalAccessException", e);
            throwContactAdminError(e);
        } catch (InvocationTargetException e) {
            LOG.error("InvocationTargetException", e);
            throwContactAdminError(e);
        } catch (NoSuchMethodException e) {
            LOG.error("NoSuchMethodException", e);
            throwContactAdminError(e);
        } catch (ClassNotFoundException e) {
            LOG.error("ClassNotFoundException", e);
            throwContactAdminError(e);
        }
    }

    /**
     * Initializes the breadcrumb components.
     * @throws Exception 
     */
    private void init() throws Exception {
        button = new Button(getLabel());
        button.setStyleName(BaseTheme.BUTTON_LINK);

        initListener();

        Method m = null;
        try {
            m = getListener().getClass().getMethod("doAction", Event.class);
        } catch (SecurityException e) {
            LOG.error("SecurityException", e);
            throwContactAdminError(e);
        } catch (NoSuchMethodException e) {
            LOG.error("NoSuchMethodException", e);
            throwContactAdminError(e);
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
