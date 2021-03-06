/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.navigation;

import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.HorizontalLayout;

/**
 * <b>Description</b>: This class represents the navigation trail for the application. It stores application views in a hierarchical form.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: May 25, 2012.
 */
public class CrumbTrail extends HorizontalLayout {

	private static final Logger LOG = LoggerFactory.getLogger(CrumbTrail.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8565016223061317505L;

	/** The crumb trail. */
	private final LinkedList<BreadCrumb> crumbTrail;

	/**
	 * The limit of how many breadcrumbs will be visible/displayed. Not yet implemented.
	 */
	private int limit;

	/**
	 * Flag variable to determine if a view is accessed using a button/link within the application
	 */
	private boolean linkAccessed;

	/** The nav props. */
	private final ResourceBundle navProps;

	/** The nav xml parser. */
	private final NavXmlParser navXmlParser;

	/**
	 * Instantiates a new crumb trail.
	 * 
	 * @throws Exception
	 */
	public CrumbTrail() throws Exception {
		super();
		this.crumbTrail = new LinkedList<BreadCrumb>();
		this.navProps = ResourceBundle.getBundle("org.generationcp.ibpworkbench.navigation.Navigation");
		this.linkAccessed = false;

		this.limit = Integer.parseInt(this.navProps.getString("navLimit"));

		this.navXmlParser = new NavXmlParser();

		this.addBreadCrumb("/Home", "Home", -1, 0, "org.generationcp.ibpworkbench.actions.HomeAction");
		this.setMargin(new MarginInfo(false, false, false, true));

	}

	/**
	 * Updates the crumbtrail.
	 *
	 * @param viewId the view id
	 * @param labelToAppend the label to append
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public void updateCrumbTrail(String viewId, String labelToAppend) throws InternationalizableException, NumberFormatException, Exception {

		int currentLevel = this.crumbTrail.isEmpty() ? -1 : this.crumbTrail.peekLast().getLevel();
		this.navXmlParser.setUriFragment(viewId);

		try {
			Map<String, String> map = this.navXmlParser.getXpathDetails();

			String breadCrumbLabel = map.get("label");
			if (!StringUtils.isEmpty(labelToAppend)) {
				breadCrumbLabel += labelToAppend;
			}
			this.addBreadCrumb(viewId, breadCrumbLabel, currentLevel, Integer.parseInt(map.get("level")), map.get("className"));
		} catch (XPathExpressionException e) {
			// we will just log the exception for debug purposes
			// we do not need to handle this exception
			CrumbTrail.LOG.error("XPathExpressionException: Please check the XPathExpression/viewId used.", e);
		} catch (NullPointerException e) {
			// we will just log the exception for debug purposes
			// we do not need to handle this exception
			CrumbTrail.LOG.error(viewId + " cannot be found in nav.xml.");
		}
	}

	/**
	 * Adds the bread crumb to the linked list of bread crumbs. Removes other bread crumbs if the existing bread crumbs are not part of a
	 * hierarchical view.
	 *
	 * @param viewId the view id
	 * @param caption the caption
	 * @param currentLevel the current level
	 * @param level the level
	 * @param className the class name
	 */
	private void addBreadCrumb(String viewId, String caption, int currentLevel, int level, String className) throws Exception {

		BreadCrumb lastBreadCrumb;
		int currLevel = currentLevel;

		while (currLevel >= level) {
			lastBreadCrumb = this.crumbTrail.removeLast();
			this.removeComponent(lastBreadCrumb);
			currLevel--;
		}

		BreadCrumb newBreadCrumb = new BreadCrumb(level, caption, viewId, className);
		newBreadCrumb.setDebugId("newBreadCrumb");

		if (this.getLastBreadCrumb() != null) {
			this.getLastBreadCrumb().removeStyleName("gcp-crumb-active");
		}

		newBreadCrumb.setSpacing(true);
		newBreadCrumb.setStyleName("gcp-crumb-active");
		this.crumbTrail.add(newBreadCrumb);
		this.addComponent(newBreadCrumb);
	}

	/**
	 * Sets the limit.
	 *
	 * @param limit the new limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Gets the limit.
	 *
	 * @return the limit
	 */
	public int getLimit() {
		return this.limit;
	}

	/**
	 * Sets the link accessed.
	 *
	 * @param linkAccessed the new link accessed
	 */
	public void setLinkAccessed(boolean linkAccessed) {
		this.linkAccessed = linkAccessed;
	}

	/**
	 * Checks if is link accessed.
	 *
	 * @return true, if is link accessed
	 */
	public boolean isLinkAccessed() {
		return this.linkAccessed;
	}

	/**
	 * Gets the last bread crumb uri.
	 *
	 * @return the last bread crumb uri
	 */
	public String getLastBreadCrumbUri() {
		if (!this.crumbTrail.isEmpty()) {
			return this.crumbTrail.peekLast().getUriFragment();
		}
		return null;
	}

	/**
	 * Gets the last bread crumb.
	 *
	 * @return the last bread crumb
	 */
	public BreadCrumb getLastBreadCrumb() {
		if (!this.crumbTrail.isEmpty()) {
			return this.crumbTrail.peekLast();
		}

		return null;
	}

	/**
	 * Gets the crumb trail.
	 *
	 * @return the crumb trail
	 */
	public LinkedList<BreadCrumb> getCrumbTrailList() {
		return this.crumbTrail;
	}

}
