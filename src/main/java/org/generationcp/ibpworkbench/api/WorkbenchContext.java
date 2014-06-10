package org.generationcp.ibpworkbench.api;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.generationcp.ibpworkbench.SessionData;

/**
 * This is a singleton bean held in Workbench application context to track all the session scoped data
 * {@link SessionData} for all logged-in users.
 * 
 * @author Naymesh Mistry
 * 
 */
public class WorkbenchContext {

	private final Map<Integer, SessionData> currentSessions = new ConcurrentHashMap<Integer, SessionData>();

	public void add(Integer userId, SessionData sessionData) {
		if (userId != null && sessionData != null) {
			currentSessions.put(userId, sessionData);
		}
	}

	public void remove(Integer userId) {
		if (userId != null) {
			currentSessions.remove(userId);
		}
	}

	public Map<Integer, SessionData> getCurrentSessions() {
		return Collections.unmodifiableMap(this.currentSessions);
	}
}
