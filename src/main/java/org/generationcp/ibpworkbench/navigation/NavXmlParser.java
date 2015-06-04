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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * <b>Description</b>: Handles the parsing of nav.xml and the validation of the URI fragment.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jun 19, 2012.
 */
public class NavXmlParser {

	private static final Logger LOG = LoggerFactory.getLogger(NavXmlParser.class);

	/** The x path. */
	private final XPath xPath;

	/** The file. */
	private File file;

	/** The reader. */
	private FileReader reader;

	/** The source. */
	private InputSource source;

	/**
	 * The node used to store the contents of nav.xml Used to avoid reparsing the xml file.
	 */
	private Node root;

	/** The raw uri fragment. */
	private String uriFragment;

	/**
	 * The URI fragment with no parameters. Used in querying the xml document.
	 */
	private String xPathUriFragment;

	/**
	 * The pattern used to validate incoming URI fragments. e.g. "/home/openProject?projectId=3&projectName=demo"
	 */
	private final static String xPathPattern = "[/\\w]+[\\?[\\w]+\\=[\\w]+[\\&[\\w]+\\=[\\w]+]*]*";

	/**
	 * Instantiates a new nav xml parser.
	 *
	 * @param uriFragment the uri fragment
	 */
	public NavXmlParser(String uriFragment) throws Exception {
		this();
		this.setUriFragment(uriFragment);
	}

	/**
	 * Instantiates a new nav xml parser.
	 * 
	 */
	public NavXmlParser() throws InternationalizableException {
		this.xPath = XPathFactory.newInstance().newXPath();
		try {
			this.file = new File(this.getClass().getResource("nav.xml").toURI());
		} catch (URISyntaxException e) {
			NavXmlParser.LOG.error("URISyntaxException", e);
			this.throwConfigError(e);
		}
		try {
			this.reader = new FileReader(this.file);
			this.source = new InputSource(this.reader);
			this.root = (Node) this.xPath.evaluate("/viewList", this.source, XPathConstants.NODE);
		} catch (FileNotFoundException e) {
			NavXmlParser.LOG.error("FileNotFoundException", e);
			this.throwConfigError(e);
		} catch (XPathExpressionException e) {
			NavXmlParser.LOG.error("XPathExpressionException", e);
			this.throwConfigError(e);
		} finally {
			try {
				this.reader.close();
			} catch (IOException e) {
				NavXmlParser.LOG.error("IOException", e);
				this.throwConfigError(e);
			}
		}
	}

	private void throwConfigError(Exception e) throws InternationalizableException {
		throw new InternationalizableException(e, Message.CONFIG_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
	}

	/**
	 * Gets the details of an entry in the xml document using the current URI fragment setUriFragment().
	 *
	 * @return the xpath details
	 */
	public Map<String, String> getXpathDetails() throws XPathExpressionException, NullPointerException {
		Map<String, String> xPathDetails = new HashMap<String, String>();
		Node view;
		try {
			view = (Node) this.xPath.evaluate(this.xPathUriFragment, this.root, XPathConstants.NODE);

			NamedNodeMap map = view.getAttributes();
			xPathDetails.put("level", map.getNamedItem("level").getNodeValue());
			xPathDetails.put("className", map.getNamedItem("class").getNodeValue());
			xPathDetails.put("label", map.getNamedItem("label").getNodeValue());
		} catch (XPathExpressionException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		}
		return xPathDetails;
	}

	/**
	 * Validate uri fragment syntax.
	 *
	 * @param uriFragment the uri fragment
	 * @return true, if successful
	 */
	public boolean validateUriFragmentSyntax(String uriFragment) {
		return uriFragment.matches(NavXmlParser.xPathPattern);
	}

	/**
	 * Sets the uri fragment.
	 *
	 * @param uriFragment the new uri fragment
	 */
	public void setUriFragment(String uriFragment) {
		this.uriFragment = uriFragment;
		this.setXPathUriFragment(uriFragment);
	}

	/**
	 * Gets the uri fragment.
	 *
	 * @return the uri fragment
	 */
	public String getUriFragment() {
		return this.uriFragment;
	}

	/**
	 * Sets the x path uri fragment.
	 *
	 * @param xPathUriFragment the new x path uri fragment
	 */
	private void setXPathUriFragment(String xPathUriFragment) {
		this.xPathUriFragment = "/viewList/view[@path='" + xPathUriFragment.split("\\?")[0] + "']";
	}

}
