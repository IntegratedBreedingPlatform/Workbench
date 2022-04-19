// These constants are injected via webpack environment variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application

export const VERSION = process.env.VERSION;
export const DEBUG_INFO_ENABLED: boolean = !!process.env.DEBUG_INFO_ENABLED;
export const SERVER_API_URL = process.env.SERVER_API_URL;
export const BUILD_TIMESTAMP = process.env.BUILD_TIMESTAMP;
export const GERMPLASM_BROWSER_DEFAULT_URL = '/ibpworkbench/maingpsb/germplasm-';
export const GERMPLASM_LIST_MANAGER_URL = '/ibpworkbench/controller/jhipster#germplasm-list';
export const BREEDING_METHODS_BROWSER_DEFAULT_URL = '/ibpworkbench/content/ProgramMethods';
export const GERMPLASM_DETAILS_URL = '/ibpworkbench/main/app/#/germplasm-details/';
export const GERMPLASM_SEARCH_SELECTOR = '/ibpworkbench/controller/jhipster#/germplasm-selector/';
export const INVENTORY_DETAILS_URL = '/ibpworkbench/controller/jhipster#/inventory-details/';
export const GRAPHICAL_QUERIES_URL = '/ibpworkbench/controller/graphical-queries/';
export const STUDY_URL = '/Fieldbook/TrialManager/openTrial/';
export const MAX_PAGE_SIZE = process.env.MAX_PAGE_SIZE;
export const USER_PROGRAM_INFO = '/ibpworkbench/controller/userProgramController/context/program';
export const INSTITUTE_LOGO_PATH = process.env.INSTITUTE_LOGO_PATH;
export const GERMPLASM_LABEL_PRINTING_TYPE = 'Germplasm';
export const GERMPLASM_LIST_LABEL_PRINTING_TYPE = 'Germplasm List';
export const FILE_MANAGER_URL = '/ibpworkbench/controller/jhipster#/file-manager/';
export const FILE_UPLOAD_SUPPORTED_TYPES = process.env.FILE_UPLOAD_SUPPORTED_TYPES;
export const TINY_BLANK_IMAGE = 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==';
export const EMPTY_PAGE_URL = 'about:blank';
export const FEEDBACK_ENABLED = process.env.FEEDBACK_ENABLED;
export const FEEDBACK_SURVEY_ID = process.env.FEEDBACK_SURVEY_ID;

// HELP
export const HELP_BASE_URL = '/ibpworkbench/controller/help/getUrl/';
export const HELP_MANAGE_SAMPLES = 'MANAGE_SAMPLES';
export const HELP_MANAGE_GERMPLASM = 'MANAGE_GERMPLASM';
export const HELP_MANAGE_GERMPLASM_IMPORT = 'MANAGE_GERMPLASM_IMPORT';
export const HELP_MANAGE_GERMPLASM_IMPORT_TEMPLATE = 'MANAGE_GERMPLASM_IMPORT_TEMPLATE';
export const HELP_MANAGE_GERMPLASM_IMPORT_UPDATES = 'MANAGE_GERMPLASM_IMPORT_UPDATES';
export const HELP_MANAGE_GERMPLASM_IMPORT_UPDATES_TEMPLATE = 'MANAGE_GERMPLASM_IMPORT_UPDATES_TEMPLATE';
export const HELP_NAVIGATION_BAR_ABOUT_BMS = 'NAVIGATION_BAR_ABOUT_BMS';
export const HELP_NAVIGATION_ASK_FOR_SUPPORT = 'NAVIGATION_BAR_ASK_FOR_SUPPORT';
export const HELP_DASHBOARD = 'DASHBOARD';
export const HELP_GERMPLASM_LIST = 'GERMPLASM_LIST';
export const HELP_GERMPLASM_LIST_IMPORT = 'GERMPLASM_LIST_IMPORT';
export const HELP_GERMPLASM_LIST_IMPORT_UPDATE = 'GERMPLASM_LIST_IMPORT_UPDATE';
export const HELP_LABEL_PRINTING_GERMPLASM_MANAGER = 'LABEL_PRINTING_GERMPLASM_MANAGER';
export const HELP_LABEL_PRINTING_GERMPLASM_LIST_MANAGER = 'LABEL_PRINTING_GERMPLASM_LIST_MANAGER';
export const HELP_LABEL_PRINTING_STUDY_MANAGER = 'LABEL_PRINTING_STUDY_MANAGER';
export const HELP_LABEL_PRINTING_INVENTORY_MANAGER = 'LABEL_PRINTING_INVENTORY_MANAGER';
export const HELP_GRAPHICAL_QUERIES = 'GRAPHICAL_QUERIES';
export const HELP_MANAGE_PROGRAM_SETTINGS = 'MANAGE_PROGRAM_SETTINGS';
export const HELP_NAME_RULES_FOR_NEW_GERMPLASM = 'NAME_RULES_FOR_NEW_GERMPLASM';
