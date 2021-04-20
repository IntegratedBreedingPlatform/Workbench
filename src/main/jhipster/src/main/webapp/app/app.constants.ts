// These constants are injected via webpack environment variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application

export const VERSION = process.env.VERSION;
export const DEBUG_INFO_ENABLED: boolean = !!process.env.DEBUG_INFO_ENABLED;
export const SERVER_API_URL = process.env.SERVER_API_URL;
export const BUILD_TIMESTAMP = process.env.BUILD_TIMESTAMP;
export const GERMPLASM_BROWSER_DEFAULT_URL = '/ibpworkbench/maingpsb/germplasm-';
export const GERMPLASM_LIST_MANAGER_URL = '/ibpworkbench/bm/list-manager';
export const BREEDING_METHODS_BROWSER_DEFAULT_URL = '/ibpworkbench/content/ProgramMethods';
export const GERMPLASM_DETAILS_URL = '/ibpworkbench/main/app/#/germplasm-details/';
export const PEDIGREE_DETAILS_URL = '/ibpworkbench/maingpsb/pedigree-details/';
export const INVENTORY_DETAILS_URL = '/ibpworkbench/controller/jhipster#/inventory-details/';
export const GRAPHICAL_QUERIES_URL = '/ibpworkbench/controller/graphical-queries/';
export const STUDY_URL = '/Fieldbook/TrialManager/openTrial/';
export const MAX_PAGE_SIZE = process.env.MAX_PAGE_SIZE;
export const USER_PROGRAM_INFO = '/ibpworkbench/controller/userProgramController/userProgramInfo';
export const INSTITUTE_LOGO_PATH = process.env.INSTITUTE_LOGO_PATH;
export const GERMPLASM_LABEL_PRINTING_TYPE = 'Germplasm';

// HELP
export const HELP_BASE_URL = '/ibpworkbench/controller/help/getUrl/';
export const HELP_MANAGE_SAMPLES = 'MANAGE_SAMPLES';
export const HELP_MANAGE_STUDIES_CREATE_PLANTING_LABELS = 'MANAGE_STUDIES_CREATE_PLANTING_LABELS';
export const HELP_MANAGE_GERMPLASM = 'MANAGE_GERMPLASM';
export const HELP_NAVIGATION_BAR_ABOUT_BMS = 'NAVIGATION_BAR_ABOUT_BMS';
export const HELP_NAVIGATION_ASK_FOR_SUPPORT = 'NAVIGATION_BAR_ASK_FOR_SUPPORT';
export const HELP_DASHBOARD = 'DASHBOARD';
