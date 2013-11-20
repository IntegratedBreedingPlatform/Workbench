package org.generationcp.ibpworkbench.util.tomcat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.ibpworkbench.util.tomcat.WebAppStatus.State;

public class WebAppStatusInfo {
    
    private Map<String, WebAppStatus> webAppStatus = new LinkedHashMap<String, WebAppStatus>();
    
    public void addStatus(String contextPath, WebAppStatus status) {
        webAppStatus.put(contextPath, status);
    }
    
    public boolean isDeployed(String contextPath) {
        WebAppStatus status = webAppStatus.get(contextPath);
        return status != null;
    }
    
    public boolean isRunning(String contextPath) {
        WebAppStatus status = webAppStatus.get(contextPath);
        return status == null ? false : status.getState() == State.RUNNING;
    }
    
    public List<WebAppStatus> asList() {
        return new ArrayList<WebAppStatus>(webAppStatus.values());
    }
}
