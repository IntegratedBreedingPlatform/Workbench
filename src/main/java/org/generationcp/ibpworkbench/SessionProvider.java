package org.generationcp.ibpworkbench;

/**
 * Created by cyrus on 1/28/14.
 */
public class SessionProvider implements IWorkbenchSession {

    private SessionData sessionData;

    public SessionProvider() {
    }

    public SessionProvider(SessionData sessionData) {
        this.setSessionData(sessionData);
    }

    @Override
    public SessionData getSessionData() {
        return sessionData;
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;

    }
}
