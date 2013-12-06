package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Configurable
public class NurseryListPreviewPresenter implements InitializingBean {
    
    private final NurseryListPreview view;
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreviewPresenter.class);

    private Project project;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private ManagerFactory managerFactory;


    public NurseryListPreviewPresenter(NurseryListPreview view, Project project) {
        this.view = view;
        this.project = project;
        
        if(this.project != null){
            if (view.getManagerFactoryProvider() != null){
                setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
            }
         }

    }

    public void generateInitialTreeNodes(){
    	
    	List<FolderReference> centralRootFolders = new ArrayList<FolderReference>();
    	List<FolderReference> localRootFolders = new ArrayList<FolderReference>();
    	try {
    		centralRootFolders = this.getManagerFactory().getStudyDataManager().getRootFolders(Database.CENTRAL);
    		localRootFolders = this.getManagerFactory().getStudyDataManager().getRootFolders(Database.LOCAL);
    		
    		view.generateTopListOfTree(centralRootFolders,localRootFolders);
    	} catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
    	}
    }
    
    
//    public List<TreeNode> generateTreeNodes(){
//    	
//        List<StudyNode> studyNodes = new ArrayList<StudyNode>();
//
//        try {
//            studyNodes = this.getManagerFactory().getStudyDataManager().getAllNurseryAndTrialStudyNodes();
//        } catch (MiddlewareQueryException e) {
//            LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
//            studyNodes = new ArrayList<StudyNode>();
//        }
//
//        List<TreeNode> localTreeNode = createTreeNodesByInstance(studyNodes, Database.LOCAL);
//        List<TreeNode> centralTreeNode = createTreeNodesByInstance(studyNodes, Database.CENTRAL);
//        
//        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
//        treeNodes.add(new TreeNode(TreeNode.getNextId(), messageSource.getMessage(Message.MY_STUDIES), 
//                                localTreeNode, localTreeNode == null || localTreeNode.size() == 0 ? true : false));
//        treeNodes.add(new TreeNode(TreeNode.getNextId(), messageSource.getMessage(Message.SHARED_STUDIES), 
//                                centralTreeNode, centralTreeNode == null || centralTreeNode.size() == 0 ? true : false));
//
//        view.generateTree(treeNodes);
//        
//        return treeNodes;
//
//        
//    }


//    private List<TreeNode> createTreeNodesByInstance(List<StudyNode> studyNodes, Database instance){
//        List<TreeNode> returnNodes = new ArrayList<TreeNode>();
//        
//        List<StudyNode> instanceNodes = new ArrayList<StudyNode>();
//        // Get all study nodes from the given instance
//        for (StudyNode studyNode : studyNodes){
//            if (instance == Database.CENTRAL && studyNode.getId() >= 0){
//                instanceNodes.add(studyNode);
//            } else if (instance == Database.LOCAL && studyNode.getId() < 0){
//                instanceNodes.add(studyNode);
//            } 
//        }
//        
//        //Build year tree nodes for the instance
//        Map<String, List<TreeNode>> yearTreeNodes = new HashMap<String, List<TreeNode>>();
//
//        if (instanceNodes.size() > 0){
//            String currentYear = instanceNodes.get(0).getStartYear();
//            List<StudyNode> currentYearStudyNodes = new ArrayList<StudyNode>();
//            for (StudyNode studyNode : instanceNodes){
//                if (studyNode.getStartYear().equals(currentYear)){
//                    currentYearStudyNodes.add(studyNode);
//                } else {
//                    // Create Tree Node for the year
//                    List<TreeNode> yearTreeNode = createTreeNodesByYear(currentYearStudyNodes, currentYear);
//                    yearTreeNodes.put(currentYear, yearTreeNode);
//                    
//                    // Change the year
//                    currentYear = studyNode.getStartYear();
//                    currentYearStudyNodes = new ArrayList<StudyNode>();
//                    currentYearStudyNodes.add(studyNode);
//                }
//                
//            }
//    
//            // Create Tree Node for the last year
//            List<TreeNode> yearTreeNode = createTreeNodesByYear(currentYearStudyNodes, currentYear);
//            yearTreeNodes.put(currentYear, yearTreeNode);
//        }
//        
//        if (yearTreeNodes.size() > 0){
//            List<String> years = new ArrayList<String>(yearTreeNodes.keySet());
//            Collections.sort(years, new Comparator<String>(){
//                @Override
//                public int compare(String arg0, String arg1) {
//                    return arg1.compareTo(arg0);
//                }});
//            
//            for (String year : years){
//                List<TreeNode> currentYearTreeNodes = yearTreeNodes.get(year);
//                returnNodes.add(new TreeNode(TreeNode.getNextId(), year, currentYearTreeNodes, currentYearTreeNodes == null || currentYearTreeNodes.size() == 0 ? true : false));
//            }
//            
//        }
//
//
//        return returnNodes;        
//    }
    

    
//    private List<TreeNode> createTreeNodesByYear(List<StudyNode> studyNodes, String year){
//        List<TreeNode> returnNodes = new ArrayList<TreeNode>();
//        
//        List<StudyNode> yearNodes = new ArrayList<StudyNode>();
//        // Get all study nodes with the given year
//        for (StudyNode studyNode : studyNodes){
//            if (studyNode.getStartYear().equals(year)){
//                yearNodes.add(studyNode);
//                
//            }
//        }
//        
//        // Build season tree nodes of the given year
//        List<TreeNode> drySeasonNodes = createTreeNodesBySeason(yearNodes, Season.DRY);
//        List<TreeNode> wetSeasonNodes = createTreeNodesBySeason(yearNodes, Season.WET);
//        List<TreeNode> generalSeasonNodes = createTreeNodesBySeason(yearNodes, Season.GENERAL);
//        
//        returnNodes.add(new TreeNode(TreeNode.getNextId(), Season.DRY.getLabel(),  drySeasonNodes, drySeasonNodes == null || drySeasonNodes.size() == 0 ? true : false));
//        returnNodes.add(new TreeNode(TreeNode.getNextId(),  Season.WET.getLabel(),  wetSeasonNodes, wetSeasonNodes == null || wetSeasonNodes.size() == 0 ? true : false));
//        returnNodes.add(new TreeNode(TreeNode.getNextId(),  Season.GENERAL.getLabel(),  generalSeasonNodes, generalSeasonNodes == null || generalSeasonNodes.size() == 0 ? true : false));
//        
//        return returnNodes;        
//    }
    
    
//    private List<TreeNode> createTreeNodesBySeason(List<StudyNode> studyNodes, Season season){
//        List<TreeNode> returnNodes = new ArrayList<TreeNode>();
//        
//        List<StudyNode> seasonNodes = new ArrayList<StudyNode>();
//        // Get all study nodes with the given season
//        for (StudyNode studyNode : studyNodes){
//            if (studyNode.getSeason() == season){
//                seasonNodes.add(studyNode);
//            }
//        }
//        
//        // Build study type tree nodes of the given season
//        List<TreeNode> nurseryNodes = createTreeNodesByStudyType(seasonNodes, StudyType.N);
//        List<TreeNode> trialNodes = createTreeNodesByStudyType(seasonNodes, StudyType.T);
//        
//        returnNodes.add(new TreeNode(TreeNode.getNextId(), messageSource.getMessage(Message.NURSERIES),  nurseryNodes, false));
//        returnNodes.add(new TreeNode(TreeNode.getNextId(), messageSource.getMessage(Message.TRIALS),  trialNodes, false));
//        
//        return returnNodes;        
//    }
//    
//    private List<TreeNode> createTreeNodesByStudyType(List<StudyNode> studyNodes, StudyType studyType){
//        List<TreeNode> returnNodes = new ArrayList<TreeNode>();
//        for (StudyNode studyNode : studyNodes){
//            if (studyNode.getStudyType() == studyType){
//                returnNodes.add(new TreeNode(TreeNode.getNextId(), studyNode.getName(),  new ArrayList<TreeNode>(), true));
//
//            }
//        }
//        return returnNodes;        
//    }
    
    
    @Override
    public void afterPropertiesSet() throws Exception {
    }


    
    public ManagerFactory getManagerFactory() {
        
        return managerFactory;
    }



    
    public void setManagerFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    public boolean isFolder(Integer value) {
        try {
        	boolean isStudy = this.getManagerFactory().getStudyDataManager().isStudy(value);
        	return !isStudy;
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
        
        return false;
    }

    public void renameNurseryListFolder(String s, Integer value) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void deleteNurseryListFolder(StudyNode studyNode) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public Object getStudyNodeParent(Integer newItem) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public Integer addNurseryListFolder(String s, Integer value) {
        
    	return null;
    }

    public StudyNode validateForDeleteNurseryList(Integer value) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
    
    public void addChildrenNode(int parentId) throws InternationalizableException{
    	List<Reference> studyChildren = new ArrayList<Reference>();

        try {
            studyChildren = this.getManagerFactory().getStudyDataManager().getChildrenOfFolder(new Integer(parentId));
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            studyChildren = new ArrayList<Reference>();
        }
        
        view.addChildrenNode(parentId, studyChildren);

    }
}
