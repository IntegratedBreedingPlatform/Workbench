package org.generationcp.ibpworkbench.util;

import javax.servlet.http.Cookie;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.actions.LoginAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieUtils {
	public static final class LoginCookieProperties {
		public static final String REMEMBER_OPT = "ibpworkbench_isremember";
		public static final String USERNAME = "ibpworkbench_username";
		public static final String PASSWORD = "ibpworkbench_password";
		
	}
	
    private static final Logger LOG = LoggerFactory.getLogger(CookieUtils.class);
	
    public static void setupCookies(Cookie... cookies) {
    	for(Cookie cookie : cookies) {
    		LOG.debug("Setting up cookie [" + cookie.getName() + "] = [" + cookie.getValue() + "]" );
        	
    		cookie.setMaxAge(3600 * 1000 * 24 * 365 * 10);
        	cookie.setPath(IBPWorkbenchApplication.get().getURL().getPath());
        	
        	IBPWorkbenchApplication.getResponse().addCookie(cookie);
    	}
    }
    
    public static void removeCookies(String... properties) {
    	Cookie[] cookies = IBPWorkbenchApplication.getRequest().getCookies();
    	for (Cookie cookie : cookies) {
    		
    		for (String property : properties) {
    			if (property.equals(cookie.getName())) {
    				LOG.debug("Removing cookie [" + cookie.getName() + "] = [" + cookie.getValue() + "]" );
    	        	
    				cookie.setMaxAge(0);
    	    		IBPWorkbenchApplication.getResponse().addCookie(cookie);    				
    			}
    		}
    	}
    }
    
    public static String getCookieValue(String property) {
    	try {
    		Cookie[] cookies = IBPWorkbenchApplication.getRequest().getCookies();
        	for (Cookie cookie : cookies) {
        		if (cookie.getName().equals(property)) {
        			return cookie.getValue();
        		}
        	}	
    	} catch (Exception e) {
    		// do nothing;
    	}
		return "";
    }
}
