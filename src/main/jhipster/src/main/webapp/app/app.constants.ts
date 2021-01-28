// These constants are injected via webpack environment variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application

export const VERSION = process.env.VERSION;
export const DEBUG_INFO_ENABLED: boolean = !!process.env.DEBUG_INFO_ENABLED;
export const SERVER_API_URL = process.env.SERVER_API_URL;
export const BUILD_TIMESTAMP = process.env.BUILD_TIMESTAMP;
export const GERMPLASM_BROWSER_DEFAULT_URL = '/ibpworkbench/maingpsb/germplasm-';

// HELP
export const HELP_BASE_URL = '/ibpworkbench/controller/help/getUrl/';
export const HELP_MANAGE_SAMPLES = 'MANAGE_SAMPLES';
export const HELP_MANAGE_STUDIES_CREATE_PLANTING_LABELS = 'MANAGE_STUDIES_CREATE_PLANTING_LABELS';
export const HELP_MANAGE_GERMPLASM = 'MANAGE_GERMPLASM';
export const HELP_ABOUT_BMS = 'ABOUT_BMS';
