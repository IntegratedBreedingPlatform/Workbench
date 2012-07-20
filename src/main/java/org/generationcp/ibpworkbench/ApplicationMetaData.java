package org.generationcp.ibpworkbench;

import java.io.Serializable;

import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.middleware.pojos.User;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * 
 * Holds data for one user session. Prepared for multi-session scenarios.
 * 
 * @author Jeffrey Morales
 */
public class ApplicationMetaData implements TransactionListener, Serializable {

	private static final long serialVersionUID = -4306116753397203283L;

	private User userData;
    
    private SpringContextApplication app;

    private static ThreadLocal<ApplicationMetaData> instance =
        new ThreadLocal<ApplicationMetaData>();
    
    public ApplicationMetaData(SpringContextApplication app) {
    	
        this.app = app;

        instance.set(this);
    }

    @Override
    public void transactionStart(Application application,
                                 Object transactionData) {

        if (this.app == application)
            instance.set(this);
    }

    @Override
    public void transactionEnd(Application application,
                               Object transactionData) {

        if (this.app == application)
            instance.set(null);
    }

    public static User getUserData() {
        return instance.get().userData;
    }

    public static void setUserData(User userData) {
        instance.get().userData = userData;
    }
}