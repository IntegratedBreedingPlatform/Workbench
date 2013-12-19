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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

/**
 * <b>Description</b>: Utility class for URI-related functions.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 21, 2012.
 */
public final class UriUtils {

    private UriUtils() {}
    
    /**
     * Gets the uri parameters. Similar to {@link ServletRequest}.getParameterMap().
     *
     * @param uriFragment the uri fragment
     * @return the uri parameters
     */
    public static Map<String, List<String>> getUriParameters(String uriFragment) {
        Map<String, List<String>> parameterMap = new HashMap<String, List<String>>();

        try {
            String parameters = uriFragment.split("\\?")[1];

            String[] wholeParamPair = parameters.split("\\&");
            String[] paramPair = null;
            List<String> tempList;

            for(String currentWholeParamPair : wholeParamPair) {
                paramPair = currentWholeParamPair.split("=");
                if(!parameterMap.isEmpty()) {
                    tempList = parameterMap.get(paramPair[0]);
                    if(tempList == null) {
                        tempList = new ArrayList<String>();
                        tempList.add(paramPair[1]);
                        parameterMap.put(paramPair[0], tempList);
                    } else {
                        tempList.add(paramPair[1]);
                    }
                } else {
                    tempList = new ArrayList<String>();
                    tempList.add(paramPair[1]);
                    parameterMap.put(paramPair[0], tempList);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing
        }

        return parameterMap;

    }
}