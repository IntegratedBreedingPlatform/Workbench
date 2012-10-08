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

package org.generationcp.middleware.manager.test;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGermplasmDataManagerImpl{

    private static ManagerFactory factory;
    private static GermplasmDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmDataManager();
    }

    @Test
    public void testGetAllLocations() throws Exception {
        long start = System.currentTimeMillis();
        List<Location> locationList = manager.getAllLocations(5, 10);
        Assert.assertTrue(locationList != null);

        System.out.println("testGetAllLocations(5,10) RESULTS: ");
        for (Location l : locationList) {
            System.out.println("  " + l);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountAllLocations() throws Exception {
        long start = System.currentTimeMillis();
        long count = manager.countAllLocations();
        System.out.println("testCountAllLocations(): " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetLocationByName() throws Exception {
        String name = "AFGHANISTAN";
        long start = System.currentTimeMillis();
        List<Location> locationList = manager.getLocationByName(name, 0, 5, Operation.EQUAL);
        Assert.assertTrue(locationList != null);
        System.out.println("testGetLocationByName(" + name + ") RESULTS: ");
        for (Location l : locationList) {
            System.out.println("  " + l);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountLocationByName() throws Exception {
        String name = "AFGHANISTAN";
        long start = System.currentTimeMillis();
        long count = manager.countLocationByName(name, Operation.EQUAL);
        System.out.println("testCountLocationByName(" + name + "): " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByName() throws Exception {
        String name = "IR 10";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null, null,
                Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByName(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByName() throws Exception {
        String name = "IR 10";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null, null, Database.CENTRAL);
        System.out.println("testCountGermplasmByName(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null,
                Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null, Database.CENTRAL);
        System.out.println("testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                new Integer(1), null, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatus(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameWithStatus() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, new Integer(1), null,
                Database.CENTRAL);
        System.out.println("testCountGermplasmByNameWithStatus(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
                new Integer(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatusAndType(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByNameWithStatusAndType() throws Exception {
        String name = "IR 64";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, new Integer(1),
                GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        System.out.println("testCountGermplasmByNameWithStatusAndType(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatusUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, new Integer(
                1), null, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatusUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByNameWithStatusAndTypeUsingLike() throws Exception {
        String name = "IR%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, new Integer(
                1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByNameWithStatusAndTypeUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);

        System.out.println("testGetGermplasmByLocationNameUsingEqual(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByLocationNameUsingEqual() throws Exception {
        String name = "Philippines";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByLocationName(name, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testCountGermplasmByLocationNameUsingEqual(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByLocationName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByLocationNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByLocationNameUsingLike() throws Exception {
        String name = "International%";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByLocationName(name, Operation.LIKE, Database.CENTRAL);
        System.out.println("testCountGermplasmByLocationNameUsingLike(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByMethodNameUsingEqual(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByMethodNameUsingEqual() throws Exception {
        String name = "SINGLE CROSS";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByMethodName(name, Operation.EQUAL, Database.CENTRAL);
        System.out.println("testCountGermplasmByMethodNameUsingEqual(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testGetGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        long start = System.currentTimeMillis();
        List<Germplasm> germplasmList = manager.getGermplasmByMethodName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
        Assert.assertTrue(germplasmList != null);

        System.out.println("testGetGermplasmByMethodNameUsingLike(" + name + ") RESULTS: ");
        for (Germplasm g : germplasmList) {
            System.out.println("  " + g);
        }
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByMethodNameUsingLike() throws Exception {
        String name = "%CROSS%";
        long start = System.currentTimeMillis();
        long count = manager.countGermplasmByMethodName(name, Operation.LIKE, Database.CENTRAL);
        System.out.println("testCountGermplasmByMethodNameUsingLike(" + name + ") RESULTS: " + count);
        long end = System.currentTimeMillis();
    }

    @Test
    public void testGetGermplasmByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmByGID(gid);
        System.out.println("testGetGermplasmByGID(" + gid + "): " + germplasm);
    }

    @Test
    public void testGetGermplasmWithPrefName() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Germplasm germplasm = manager.getGermplasmWithPrefName(gid);

        System.out.println("testGetGermplasmWithPrefName(" + gid + ") RESULTS: " + germplasm);
        if (germplasm != null) {
            System.out.println("  preferredName = " + germplasm.getPreferredName());
        }
    }

    @Test
    public void testGetGermplasmWithPrefAbbrev() throws Exception {
        Integer gid = Integer.valueOf(151);
        Germplasm germplasm = manager.getGermplasmWithPrefAbbrev(gid);

        System.out.println("testGetGermplasmWithPrefAbbrev(" + gid + ") RESULTS: " + germplasm);
        System.out.println("  preferredName = " + germplasm.getPreferredName());
        System.out.println("  preferredAbbreviation = " + germplasm.getPreferredAbbreviation());
    }

    @Test
    public void testGetGermplasmNameByID() throws Exception {
        Integer gid = Integer.valueOf(42268);
        Name name = manager.getGermplasmNameByID(gid);
        System.out.println("testGetGermplasmNameByID(" + gid + ") RESULTS: " + name);
    }

    @Test
    public void testGetNamesByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Name> names = manager.getNamesByGID(gid, null, null);
        System.out.println("testGetNamesByGID(" + gid + ") RESULTS: " + names);
    }

    @Test
    public void testGetPreferredNameByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        System.out.println("testGetPreferredNameByGID(" + gid + ") RESULTS: " + manager.getPreferredNameByGID(gid));
    }

    @Test
    public void testGetPreferredAbbrevByGID() throws Exception {
        Integer gid = Integer.valueOf(1);
        System.out.println("testGetPreferredAbbrevByGID(" + gid + ") RESULTS: " + manager.getPreferredAbbrevByGID(gid));
    }

    @Test
    public void testGetNameByGIDAndNval() throws Exception {
        Integer gid = Integer.valueOf(1);
        String nVal = "GCP-TEST";
        System.out.println("testGetNameByGIDAndNval(" + gid + ", " + nVal + ") RESULTS: " + manager.getNameByGIDAndNval(gid, nVal));
    }

    @Test
    public void testGetNamesByGIDWithStatus() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(1);
        GermplasmNameType type = null;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        System.out.println("testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status + ", type=" + type + ") RESULTS: " + names);
    }

    @Test
    public void testGetNamesByGIDWithStatusAndType() throws Exception {
        Integer gid = Integer.valueOf(50533);
        Integer status = Integer.valueOf(8);
        GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
        List<Name> names = manager.getNamesByGID(gid, status, type);
        System.out.println("testGetNamesByGIDWithStatusAndType(gid=" + gid + ", status" + status + ", type=" + type + ") RESULTS: " + names);
    }

    @Test
    public void testGetAttributesByGID() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Attribute> attributes = manager.getAttributesByGID(gid);
        System.out.println("testGetAttributesByGID(" + gid + ") RESULTS: " + attributes);
    }

    @Test
    public void testAddMethod() throws MiddlewareQueryException {
        Method method = new Method();
        method.setMid(-1);
        method.setMname("yesno");
        method.setGeneq(0);
        method.setLmid(2);
        method.setMattr(0);
        method.setMcode("UGM");
        method.setMdate(19980610);
        method.setMdesc("yay");
        method.setMfprg(0);
        method.setMgrp("S");
        method.setMprgn(0);
        method.setReference(0);
        method.setUser(0);

        method.setMtype("GEN");

        // add the method
        manager.addMethod(method);

        method = manager.getMethodByID(-1);
        System.out.println("testAddMethod(" + method + ") RESULTS: " + method);

        // delete the method
        manager.deleteMethod(method);
    }

    @Test
    public void testAddMethods() throws MiddlewareQueryException {
        List<Method> methods = new ArrayList<Method>();
        methods.add(new Method(-1, "GEN", "S", "UGM", "yesno", "description 1", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610)));
        methods.add(new Method(-2, "GEN", "S", "UGM", "yesno", "description 2", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610)));
        methods.add(new Method(-3, "GEN", "S", "UGM", "yesno", "description 3", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610)));

        // add the methods
        int methodsAdded = manager.addMethod(methods);

        System.out.println("testAddMethods() Methods added: " + methodsAdded);

        for (int i = 1; i <= methodsAdded; i++) {
            Method method = manager.getMethodByID(-i);
            System.out.println("  " + method);
            // delete the method
            manager.deleteMethod(method);
        }

    }

    @Test
    public void testAddLocation() throws MiddlewareQueryException {
        Location location = new Location();
        location.setLocid(-1);
        location.setCntryid(1);
        location.setLabbr("");
        location.setLname("TEST-LOCATION-1");
        location.setLrplce(1);
        location.setLtype(1);
        location.setNllp(1);
        location.setSnl1id(1);
        location.setSnl2id(1);
        location.setSnl3id(1);

        // add the location
        manager.addLocation(location);
        System.out.println("testAddLocation(" + location + ") RESULTS: \n  " + manager.getLocationByName("TEST-LOCATION-1", 0, 5, Operation.EQUAL));

        // cleanup
        manager.deleteLocation(manager.getLocationByName("TEST-LOCATION-1", 0, 5, Operation.EQUAL).get(0));
    }

    @Test
    public void testAddLocations() throws MiddlewareQueryException {

        List<Location> locations = new ArrayList<Location>();

        Location location1 = new Location();
        location1.setLocid(-2);
        location1.setCntryid(1);
        location1.setLabbr("");
        location1.setLname("TEST-LOCATION-2");
        location1.setLrplce(1);
        location1.setLtype(1);
        location1.setNllp(1);
        location1.setSnl1id(1);
        location1.setSnl2id(1);
        location1.setSnl3id(1);

        Location location2 = new Location();
        location2.setLocid(-3);
        location2.setCntryid(1);
        location2.setLabbr("");
        location2.setLname("TEST-LOCATION-3");
        location2.setLrplce(1);
        location2.setLtype(1);
        location2.setNllp(1);
        location2.setSnl1id(1);
        location2.setSnl2id(1);
        location2.setSnl3id(1);

        locations.add(location1);
        locations.add(location2);

        // add the location
        int locationsAdded = manager.addLocation(locations);

        System.out.println("testAddLocations() Locations added: " + locationsAdded);
        System.out.println("  " + manager.getLocationByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL));
        System.out.println("  " + manager.getLocationByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL));

        // cleanup
        manager.deleteLocation(manager.getLocationByName("TEST-LOCATION-2", 0, 5, Operation.EQUAL).get(0));
        manager.deleteLocation(manager.getLocationByName("TEST-LOCATION-3", 0, 5, Operation.EQUAL).get(0));
    }

    @Test
    public void testGetGidAndNidByGermplasmNames() throws Exception {
        List<String> germplasmNames = new ArrayList<String>();
        germplasmNames.add("UCR2010001");
        germplasmNames.add("UCR2010002");
        germplasmNames.add("UCR2010003");

        List<GidNidElement> results = manager.getGidAndNidByGermplasmNames(germplasmNames);
        System.out.println("testGetGidAndNidByGermplasmNames(" + germplasmNames + ") RESULTS: " + results);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
