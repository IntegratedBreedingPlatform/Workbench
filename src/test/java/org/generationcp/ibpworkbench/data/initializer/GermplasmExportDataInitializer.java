package org.generationcp.ibpworkbench.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.pojo.CustomReportType;

public class GermplasmExportDataInitializer {
    public static final String TEST_REPORT_CODE = "MFbShipList";
    public static final String TEST_REPORT_NAME = "Template, Maize";

    public static List<CustomReportType> createCustomReportTypeList() {
        CustomReportType reportType = new CustomReportType(TEST_REPORT_CODE, TEST_REPORT_NAME);
        List<CustomReportType> customReportTypes = new ArrayList<>();
        customReportTypes.add(reportType);

        return customReportTypes;
    }
}
