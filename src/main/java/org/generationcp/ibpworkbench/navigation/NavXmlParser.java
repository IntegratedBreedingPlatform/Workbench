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
    private final String xPathPattern = "[/\\w]+[\\?[\\w]+\\=[\\w]+[\\&[\\w]+\\=[\\w]+]*]*";
    
    /**
     * Instantiates a new nav xml parser.
     *
     * @param uriFragment the uri fragment
     */
    public NavXmlParser(String uriFragment) {
        this();
        setUriFragment(uriFragment);
    }
    
    
    
    /**
     * Instantiates a new nav xml parser.
     * 
     * TODO: error handling
     */
    public NavXmlParser() {
        xPath = XPathFactory.newInstance().newXPath();
        try {
            file = new File(getClass().getResource("nav.xml").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            reader = new FileReader(file);
            source = new InputSource(reader);
            root = (Node) xPath.evaluate("/root", source, XPathConstants.NODE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }  
    }
    
    /**
     * Gets the details of an entry in the xml document using the 
     * current URI fragment setUriFragment().
     *
     * @return the xpath details
     */
    public Map<String, String> getXpathDetails() {
        
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
            //TODO: tell dev to fix the xml entry 
            e.printStackTrace();
        } 
        
        return xPathDetails;
    }
    

    /**
     * Validate uri fragment.
     *
     * @return true, if successful
     */
    public boolean validateUriFragment() {
        if(validateUriFragmentSyntax(uriFragment)) {
            String status = "";
            try {
                status = xPath.evaluate(xPathUriFragment, root);
            } catch (XPathExpressionException e) {
                //TODO: tell dev to fix the xml entry 
                e.printStackTrace();
            }
            return status.length() > 0;
        } else {
            //TODO: add on screen error message?
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
