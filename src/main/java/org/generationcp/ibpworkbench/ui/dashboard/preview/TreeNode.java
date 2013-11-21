/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

public class TreeNode{

    private long id;

    private String name;

    private List<TreeNode> treeNodeList;

    private boolean isLeaf;

    public TreeNode(long id, String name, List<TreeNode> treeNodes, boolean isLeaf) {
        this.id = id;
        this.name = name;
        this.treeNodeList = treeNodes;
        this.isLeaf = isLeaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeNode> getTreeNodeList() {
        return treeNodeList;
    }

    public void setTreeNodeList(List<TreeNode> treeNodeList) {
        this.treeNodeList = treeNodeList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TreeNode [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", treeNodeList=");
        builder.append(treeNodeList);
        builder.append(", isLeaf=");
        builder.append(isLeaf);
        builder.append("]");
        return builder.toString();
    }

    
    
}