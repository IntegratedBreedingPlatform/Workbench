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
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * <b>Description</b>: Handles the parsing of nav.xml and the validation of the URI fragment.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
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
    
    /** The node used to store the contents of nav.xml
     * Used to avoid reparsing the xml file.*/
    private Node root;
    
    /** The raw uri fragment. */
    private String uriFragment;
    
    /** The URI fragment with no parameters. 
     * Used in querying the xml document. */
    private String xPathUriFragment;
    
    /** The pattern used to validate incoming URI fragments. 
     * e.g. "/home/openProject?projectId=3&projectName=demo" */
    private final static String xPathPattern = "[/\\w]+[\\?[\\w]+\\=[\\w]+[\\&[\\w]+\\=[\\w]+]*]*";
    
    /**
     * Instantiates a new nav xml parser.
     *
     * @param uriFragment the uri fragment
     */
    public NavXmlParser(String uriFragment) throws Exception {
        this();
        setUriFragment(uriFragment);
    }
    
    
    
    /**
     * Instantiates a new nav xml parser.
     * 
     */
    public NavXmlParser() throws InternationalizableException {
        xPath = XPathFactory.newInstance().newXPath();
        try {
            file = new File(getClass().getResource("nav.xml").toURI());
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException", e);
            throwConfigError(e);
        }
        try {
            reader = new FileReader(file);
            source = new InputSource(reader);
            root = (Node) xPath.evaluate("/root", source, XPathConstants.NODE);
        } catch (FileNotFoundException e) {
            LOG.error("FileNotFoundException", e);
            throwConfigError(e);
        } catch (XPathExpressionException e) {
            LOG.error("XPathExpressionException", e);
            throwConfigError(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                LOG.error("IOException", e );
                throwConfigError(e);
            }
        }  
    }
    
    private void throwConfigError(Exception e) throws InternationalizableException {
        throw new InternationalizableException(e, 
                Message.CONFIG_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
    }
    
    /**
     * Gets the details of an entry in the xml document using the 
     * current URI fragment setUriFragment().
     *
     * @return the xpath details
     */
    public Map<String, String> getXpathDetails() throws InternationalizableException {
        
        String level;
        String className;
        String label;
        Map<String, String> xPathDetails  = new HashMap<String, String>();
        
        try {
            level = xPath.evaluate(xPathUriFragment + "/@level", root);
            className = xPath.evaluate(xPathUriFragment + "/@class", root);
            label = xPath.evaluate(xPathUriFragment + "/@label", root);

            xPathDetails.put("level", level);
            xPathDetails.put("className", className);
            xPathDetails.put("label", label);
        } catch (XPathExpressionException e) {
            LOG.error("XPathExpressionException: Please fix the XML entry", e);
            throwConfigError(e);
        } 
        
        return xPathDetails;
    }
    

    /**
     * Validate uri fragment.
     *
     * @return true, if successful
     */
    public boolean validateUriFragment() throws InternationalizableException {
        if(validateUriFragmentSyntax(uriFragment)) {
            String status = "";
            try {
                status = xPath.evaluate(xPathUriFragment, root);
            } catch (XPathExpressionException e) {
                LOG.error("XPathExpressionException: Please fix the XML entry", e);
                throw new InternationalizableException(e, 
                        Message.INVALID_URI_ERROR, Message.INVALID_URI_ERROR_DESC);
            }
            return status.length() > 0;
        } else {
            return false;
        }
    }
    
    /**
     * Validate uri fragment syntax.
     *
     * @param uriFragment the uri fragment
     * @return true, if successful
     */
    private boolean validateUriFragmentSyntax(String uriFragment) {
        return uriFragment.matches(xPathPattern);
    }
    
    /**
     * Removes the parameters in the URI fragment.
     *
     * @param uriFragment the uri fragment
     * @return the string
     */
    private String cleanUriFragment(String uriFragment) {
        return "/root" + uriFragment.split("\\?")[0];
    }

    /**
     * Sets the uri fragment.
     *
     * @param uriFragment the new uri fragment
     */
    public void setUriFragment(String uriFragment) {
        this.uriFragment = uriFragment;
        setXPathUriFragment(cleanUriFragment(uriFragment));
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
     * Sets the x path uri fragment.
     *
     * @param xPathUriFragment the new x path uri fragment
     */
    private void setXPathUriFragment(String xPathUriFragment) {
        this.xPathUriFragment = xPathUriFragment;
    }

}
