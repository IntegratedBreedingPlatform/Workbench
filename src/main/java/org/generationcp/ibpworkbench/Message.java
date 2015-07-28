
package org.generationcp.ibpworkbench;

public enum Message {
	ACCOUNT, ACTIONS, ACTIVITY_NEXT, ACTIVITY_RECENT, BREEDING_MANAGER_LIST_MANAGER, BREEDING_MANAGER_BROWSE_FOR_GERMPLASMS_AND_LISTS, CONTACT_CREATE, BREEDING_VIEW_DATASET_SELECT, DASHBOARD, DATASETS, ERROR, ERROR_UPLOAD, ERROR_INVALID_FILE, ERROR_DATABASE, ERROR_IN_GETTING_TOP_LEVEL_STUDIES, ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID, ERROR_LOGIN_INVALID, TOOL_VERSIONS, PARAMETER, TOOL_TYPE, HELP, HOME, EMAIL, LOGIN, PASSWORD, LOGIN_TITLE, PROJECT_CREATE, PROJECT_DELETE, PROJECT_CREATE_TITLE, PROJECT_DASHBOARD_TITLE, PROJECT_TITLE, PROJECT_TABLE_CAPTION, PREVIEW_TAB_CAPTION, PROGRAMS_LABEL, PREVIEW_LABEL, ADD_A_PROGRAM, PROJECT, ACTION, DATE, DATE_DUE, OWNER, NAME, STATUS, STUDY_NAME, RECENT, SIGNOUT, USER_GUIDE, USER_GUIDE_1, USERNAME, WORKBENCH_TITLE, WORKFLOW_PREVIEW_TITLE

	// Workbench Dashboard Window
	, TOOL_NAME, TOOL_USERS, VERSION

	// Workbench Dashboard
	, ACTIVITIES, PROJECT_DETAIL, ROLES, ROLE_TABLE_TITLE, PROJECT_TABLE_TOOLTIP, PROGRAM_TABLE_TOOLTIP, ROLE_TABLE_TOOLTIP, START_DATE, MY_STUDIES, SHARED_STUDIES, NURSERIES, NURSERIES_AND_TRIALS, TRIALS

	// General
	, SAVE, CANCEL, OK, YES, NO, DONE

	// Forgot Password
	, FORGOT_PASSWORD, SECURITY_QUESTION, SECURITY_ANSWER, ERROR_USERNAME_INVALID, ENTER_USERNAME_LABEL, ERROR_IN_USERNAME_RETRIEVAL, ERROR_IN_QUESTION_RETRIEVAL, ERROR_NO_SECURITY_QUESTIONS, YOUR_PASSWORD, ERROR_IN_PASSWORD_RETRIEVAL, ERROR_SECURITY_ANSWER_INVALID, BTN_RETRIEVE

	// Register User Account
	, REGISTER_USER_ACCOUNT, REGISTER_USER_ACCOUNT_FORM, REGISTER_SUCCESS, REGISTER_SUCCESS_DESCRIPTION, USER_ACC_POS_TITLE, USER_ACC_NAME, USER_ACC_FNAME, USER_ACC_MIDNAME, USER_ACC_LNAME, USER_ACC_EMAIL, USER_ACC_PASSWORD, USER_ACC_PASSWORD_CONFIRM, USER_ACC_USERNAME, USER_ACC_ROLE

	// Error Notification
	, UPLOAD_ERROR, UPLOAD_ERROR_DESC, LAUNCH_BREEDING_VIEW, LAUNCH_TOOL_ERROR, LAUNCH_TOOL_ERROR_DESC, INVALID_TOOL_ERROR_DESC, LOGIN_ERROR, LOGIN_DB_ERROR_DESC, DATABASE_ERROR, SAVE_PROJECT_ERROR_DESC, SAVE_USER_ACCOUNT_ERROR_DESC, ADD_CROP_TYPE_ERROR_DESC, FILE_NOT_FOUND_ERROR, FILE_NOT_FOUND_ERROR_DESC, FILE_ERROR, FILE_CANNOT_PROCESS_DESC, FILE_CANNOT_OPEN_DESC, PARSE_ERROR, WORKFLOW_DATE_PARSE_ERROR_DESC, CONFIG_ERROR, CONTACT_ADMIN_ERROR_DESC, CONTACT_DEV_ERROR_DESC, INVALID_URI_ERROR, INVALID_URI_ERROR_DESC, INVALID_URI_SYNTAX_ERROR_DESC, INVALID_URL_PARAM_ERROR, INVALID_URL_PARAM_ERROR_DESC

	// Tray Notification
	, UPLOAD_SUCCESS, UPLOAD_SUCCESS_DESC

	, LOC_NAME, LOC_ABBR, LOC_TYPE, LOC_COUNTRY, LOC_GEOGRAPHICAL_DETAILS, BREED_METH_NAME, BREED_METH_DESC, BREED_METH_GRP, BREED_METH_CODE, BREED_METH_TYPE, BREED_METH_CLASS

	// Tool configuration update
	, UPDATING, UPDATING_TOOLS_CONFIGURATION

	// Create new project
	, BASIC_DETAILS_LABEL, BREEDING_WORKFLOWS_LABEL, PROJECT_MEMBERS_LABEL, BREEDING_METHODS_LABEL, LOCATIONS_LABEL

	// MARS Workflow Diagram
	, PROJECT_PLANNING, POPULATION_DEVELOPMENT, FIELD_TRIAL_MANAGEMENT, GENOTYPING, PHENOTYPIC_ANALYSIS, QTL_ANALYSIS, QTL_SELECTION, RECOMBINATION_CYCLE, FINAL_BREEDING_DECISION, BROWSE_GERMPLASM_INFORMATION, BROWSE_STUDIES_AND_DATASETS, BROWSE_GERMPLAM_LISTS, BROWSE_GENOTYPING_DATA, BREEDING_MANAGER, FIELDBOOK, MANAGE_GENOTYPING_DATA, BREEDING_VIEW, BREEDING_VIEW_QTL, BREEDING_VIEW_SINGLE_SITE_ANALYSIS, OPTIMAS, CLICK_TO_LAUNCH_GERMPLASM_BROWSER, CLICK_TO_LAUNCH_GENOTYPING_DATA, CLICK_TO_LAUNCH_STUDY_BROWSER, CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER, CLICK_TO_LAUNCH_BREEDING_MANAGER, CLICK_TO_LAUNCH_FIELDBOOK, CLICK_TO_LAUNCH_GDMS, CLICK_TO_LAUNCH_BREEDING_VIEW, CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS, CLICK_TO_LAUNCH_OPTIMAS, CLICK_TO_LAUNCH_BREEDING_MANAGER_LIST_MANAGER, CLICK_TO_BROWSE_FOR_GERMPLASMS_AND_LISTS, LIST_MANAGER, CLICK_TO_LAUNCH_LIST_MANAGER, MAKE_CROSSES, CLICK_TO_LAUNCH_CROSSING_MANAGER, CLICK_TO_LAUNCH_CROSS_STUDY_BROWSER

	// MABC Workflow
	, MANAGE_ONTOLOGIES, CLICK_TO_LAUNCH_ONTOLOGY_BROWSER, MANAGE_TRIALS, BROWSE_GERMPLASM_AND_LISTS, CLICK_TO_LAUNCH_GERMPLASM_AND_LIST_BROWSER, META_ANALYSIS

	// Breeding View Window
	, STUDY_EFFECT, DATASET_OF_TEXT, NAME_HEADER, DESCRIPTION_HEADER, PROPERTY_HEADER, SCALE_HEADER, METHOD_HEADER, DATATYPE_HEADER, ERROR_IN_GETTING_DATASET_FACTOR, ERROR_IN_GETTING_DATASET_VARIATE, ERROR_PLEASE_CONTACT_ADMINISTRATOR

	// Breeding View Input
	, ENVIRONMENT, DESIGN_TYPE, RUN_BREEDING_VIEW, BACK, NEXT, BV_VERSION, BV_PROJECT_TYPE, BV_ANALYSIS_NAME, BV_SITE_ENVIRONMENT, BV_SPECIFY_ENV_FACTOR, BV_SELECT_ENV_FOR_ANALYSIS, BV_SPECIFY_NAME_FOR_ANALYSIS_ENV, BV_DESIGN, BV_SPECIFY_REPLICATES, BV_SPECIFY_BLOCKS, BV_SPECIFY_ROW_FACTOR, BV_SPECIFY_COLUMN_FACTOR, BV_GENOTYPES, BV_TITLE, BV_DATASET_NAME, BV_DATASOURCE_NAME, BV_DATA_SELECTED_FOR_ANALYSIS_HEADER, BV_ANALYSIS_NAME_HEADER, BV_CHOOSE_ENVIRONMENT_HEADER, BV_CHOOSE_ENVIRONMENT_DESCRIPTION, BV_CHOOSE_ENVIRONMENT_FOR_ANALYSIS_DESC, BV_SPECIFY_DESIGN_DETAILS_HEADER, BV_SPECIFY_GENOTYPES_HEADER, BV_STUDY_TREE_TITLE, BV_STUDY_TREE_DESCRIPTION, BV_DATASET_DETAIL_TITLE, BV_DATASET_DETAIL_DESCRIPTION

	// Project Location
	, FILTER, LOCATION_COUNTRY_FILTER, LOCATION_TYPE_FILTER, LOCATION_AVAILABLE_LOCATIONS, LOCATION_SELECTED_LOCATIONS, LOCATION_ADD_NEW, LOCATION_SUCCESSFULLY_CONFIGURED, SAVE_CHANGES, SUCCESS, FAIL, ADD, EDIT, TITLE, SELECT, METHODS_ADD_NEW, METHODS_SUCCESSFULLY_CONFIGURED, TOTAL_ENTRIES, SELECTED, ALL_LOCATIONS, AVAILABLE_METHODS

	// Backup + Restore IBDB
	, BACKUP_IBDB_WINDOW_CAPTION, BACKUP_IBDB_WINDOW_DESC, BACKUP_IBDB_SELECT_PROJECT_CAPTION, BACKUP_IBDB_COMPLETE, BACKUP_IBDB_CANNOT_PERFORM_OPERATION, RESTORE_IBDB_WINDOW_CAPTION, RESTORE, RESTORE_IBDB_SELECT_PROJECT_CAPTION, RESTORE_IBDB_TABLE_SELECT_CAPTION, RESTORE_IBDB_COMPLETE, RESTORE_IBDB_CONFIRM, UPLOAD_IBDB_CAPTION

	// User tools and form
	, USER_TOOLS_WINDOW_CAPTION, USER_TOOLS_FORM_CAPTION, USER_TOOLS_LISTSELECT_CAPTION, USER_TOOLS_ADDED, USER_TOOLS_REMINDER_TEXT, FORM_VALIDATION_FAIL, FORM_VALIDATION_FIELDS_INVALID, USER_TOOLS_EDIT_EXISTS_ERROR, USER_TOOLS_ADD_EXISTS_ERROR, USER_TOOLS_UPDATED, FORM_VALIDATION_USER_TOOLS_NAME_REQUIRED, FORM_VALIDATION_USER_TOOLS_TITLE_REQUIRED, FORM_VALIDATION_USER_TOOLS_TYPE_REQUIRED, FORM_VALIDATION_ALPHANUM_ONLY, FORM_VALIDATION_INVALID_URL, FORM_VALIDATION_USER_TOOLS_PATH_REQUIRED, TOOL_PATH

	// Administration / Configuration Workflow
	, ADMINISTRATION_TITLE, MEMBERS_LINK, BACKUP_IBDB_LINK, RESTORE_IBDB_LINK, PROJECT_METHODS_LINK, PROJECT_LOCATIONS_LINK, SOFTWARE_LICENSE_AGREEMENT, SOFTWARE_LICENSE_AGREEMENT_DETAILS

	, RESTORE_IBDB_LINK_DESC, BACKUP_IBDB_LINK_DESC

	// Project Planning Workflow links
	, BREEDING_PLANNER_LINK, BREEDING_PLANNER_MARS, PEDIGREE_ANALYSIS_LINK, GERMPLASM_BROWSER_LINK, GERMPLASM_LIST_BROWSER_LINK, STUDY_BROWSER_LINK, CROP_FINDER_LINK

	// Breeding Management
	, BREEDING_MANAGEMENT_TITLE, IMPORT_PEDIGREES_LINK, EDIT_PEDIGREES_LINK, LIST_MANAGER_LINK, CROSS_MANAGER_LINK, NURSERY_MANAGER_LINK

	// Analysis Pipeline
	, ANALYSIS_PIPELINE_TITLE, SINGLE_SITE_ANALYSIS_LOCAL_LINK, SINGLE_SITE_ANALYSIS_CENTRAL_LINK, MULTI_SITE_ANALYSIS_LINK

	// Data Management
	, DATA_MANAGEMENT_TITLE, GDMS_LINK

	// Decision Support
	, DECISION_SUPPORT_TITLE, TABLE_VIEWER_LINK, MBDT_LINK, CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL, CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL, CLICK_TO_LAUNCH_BREEDING_VIEW_MULTI_SITE_ANALYSIS, CLICK_TO_LAUNCH_MBDT, CLICK_TO_LAUNCH_NURSERY_MANAGER, PROJECT_METHODS_DESC, PROJECT_LOCATIONS_DESC, MEMBERS_LINK_DESC, GENOTYPIC_DATA_BROWSER_LINK, GENOTYPIC_DATA_BROWSER_DESC, STATISTICAL_ANALYSIS, BREEDING_DECISION, MANAGE_NURSERIES, NURSERY_TEMPLATE, HEAD_TO_HEAD_LAUNCH, CLICK_TO_LAUNCH_HEAD_TO_HEAD, CLICK_TO_LAUNCH_NURSERY_TEMPLATE, SINGLE_SITE_ANALYSIS_LINK, LOGIN_SUBTITLE, FIELDBOOK_CREATE, BREEDING_PLANNER_MABC, BREEDING_PLANNER_MAS, MBDT_MABC, OPTIMAS_MARS, MULTI_SITE_ANALYSIS_MANAGER, CLICK_TO_LAUNCH_USER_TOOLS, REMEMBER_ME, DELETE_PROJECT_LINK, MAIN_HEAD_TO_HEAD_LAUNCH, CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD, QUERY_FOR_ADAPTED_GERMPLASM, CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM, GXE_SELECT_DATA_FOR_ANALYSIS_HEADER, GXE_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION, GXE_ENVIRONMENT_FACTOR_HEADER, GXE_ENVIRONMENT_FACTOR_DESCRIPTION, GXE_ENVIRONMENT_GROUPS_HEADER, GXE_ENVIRONMENT_GROUPS_DESCRIPTION, GXE_REVIEW_SELECTED_DATASET, GXE_FACTOR_TABLE_HEADER, GXE_FACTOR_TABLE_DESCRIPTION, GXE_TRAIT_TABLE_HEADER, GXE_TRAIT_TABLE_DESCRIPTION, GXE_ENVIRONMENT_GROUPS_SPECIFY, GXE_SELECTED_INFO, GXE_SELECTED_ENVIRONMENT_FACTOR, GXE_SELECTED_ENVIRONMENT_GROUP_FACTOR, GXE_ADJUSTED_MEANS_HEADER, GXE_ADJUSTED_MEANS_DESCRIPTION, GXE_SELECT_TRAITS_FOR_ANALYSIS, META_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION, META_BUILD_NEW_ANALYSIS_HEADER, META_REVIEW_ENVIRONMENTS, META_SELECTED_ENVIRONMENTS, META_BUILD_NEW_ANALYSIS_DESCRIPTION, META_SELECT_DATASETS_FOR_ANALYSIS, META_SELECT_DATASETS_FOR_ANALYSIS_DESCRIPTION, META_SELECT_ENV_VAR_FOR_ANALYSIS, META_SELECT_ENV_VAR_FOR_ANALYSIS_DESC, META_SELECT_VARIATES, META_SELECT_FACTORS_FOR_ANALYSIS, META_SELECT_FACTORS_FOR_ANALYSIS_DESC, META_SELECT_FACTORS, EXPORT_DATA, SSA_NON_NUMERIC_CATEGORICAL_VAR_ERROR, SSA_NUMERIC_CATEGORICAL_VAR_WARNING

	// Study Browser
	, STUDY_DETAILS_TEXT, CANCEL_LABEL, CLICK_DATASET_TO_VIEW_TEXT, CONFIRM_DIALOG_MESSAGE_OPEN_TABLE_VIEWER, DATASET_TEXT, DATASETS_TEXT, EMPTY_STRING, END_DATE_LABEL, ERROR_IN_GETTING_REPRESENTATION_BY_STUDY_ID, ERROR_IN_GETTING_STUDY_DETAIL_BY_ID, ERROR_IN_GETTING_STUDY_FACTOR, ERROR_IN_GETTING_STUDY_VARIATE, ERROR_IN_GETTING_VARIABLES_OF_DATASET, EXPORT_TO_EXCEL, FACTORS_TEXT, NAME_LABEL, NO_DATASETS_RETRIEVED_LABEL, OBJECTIVE_LABEL, OPEN_TABLE_VIEWER_LABEL, START_DATE_LABEL, TABLE_VIEWER_CAPTION, TABLE_VIEWER_OK_LABEL, TITLE_LABEL, TYPE_LABEL, VALUE_HEADER, VARIATES_TEXT, ERROR_INVALID_FORMAT, ERROR_IN_NUMBER_FORMAT, RESET, TITLE_SSA, TITLE_GXE, TITLE_METAANALYSIS

	// ADDITIONAL FOR PROGRAM SUMMARY
	, PI_NAME_LABEL, SITE_NAME_LABEL, STUDY_TYPE_LABEL

	// PROGRAM SUMMARIES
	, PROGRAM_SUMMARY_ACTIVITIES, PROGRAM_SUMMARY_TRIALS, PROGRAM_SUMMARY_NURSERY, INVALID_INPUT, INVALID_ITEM_NAME, INVALID_OPERATION, INVALID_NO_SELECTION, INVALID_ITEM_IS_FOLDER, INVALID_ITEM_NO_RENAME_SELECT, INVALID_CANNOT_RENAME_EMPTY_STRING, INVALID_CANNOT_RENAME_ITEM, LISTS, LAUNCH, OPEN_IN_LIST_MANAGER, RENAME_ITEM, ADD_FOLDER, RENAME_LIST_FOLDER, ITEM_NAME, FOLDER_NAME, INVALID_CANNOT_DELETE_ITEM, DELETE_LIST_FOLDER, DELETE_LIST_FOLDER_CONFIRM, INVALID_CANNOT_MOVE_ITEM, INVALID_CANNOT_MOVE_ITEM_WITH_CHILD, INVALID_CANNOT_MOVE_ITEM_TO_NURSERY_TRIAL, INVALID_NO_PROGRAM_SELECTED, MOVE_PUBLIC_LISTS_NOT_ALLOWED, MOVE_PUBLIC_STUDIES_NOT_ALLOWED, UNABLE_TO_MOVE_ROOT_FOLDERS, DELETE_PROGRAM_CONFIRM, OPEN_IN_STUDY_BROWSER, DELETE_ITEM, DELETE_ITEM_CONFIRM, LAUNCHED_APP, PROGRAM_SUMMARY_ALL, GXE_GENOTYPES_FACTOR_DESCRIPTION, FAVORITE_PROGRAM_METHODS, SIGNIN, LOGGED_IN, FAVORITE_PROGRAM_LOCATIONS

	// ADDITIONAL FOR CHANGES IN ADD LOCATION
	, LOC_PROVINCE, LOC_LATITUDE, LOC_LONGITUDE, LOC_ALTITUDE

	// ADDITIONAL FOR CHANGES IN CREATE/UPDATE PROGRAM
	, BASIC_DETAILS_CROP, BASIC_DETAILS_PROGRAM_NAME, BASIC_DETAILS_PROGRAM_STARTDATE, BASIC_DETAILS_OTHER_CROP_NAME

	, MINIMUM_CROP_VERSION_WARNING, NOT_AVAILABLE, WARNING

	, PLEASE_CHOOSE

	// FOR SSA
	, DOWNLOAD_INPUT_FILES

	// FOR BV OUTPUT FILE UPLOAD WINDOW
	, CLEAR, BROWSE, BV_UPLOAD_HEADER, BV_UPLOAD_DESCRIPTION, BV_UPLOAD_ZIP, BV_SELECT_STUDY_FOR_UPLOAD, BV_SELECT_STUDY_FOR_UPLOAD_DESCRIPTION, BV_UPLOAD_ERROR_HEADER, BV_UPLOAD_ERROR_INVALID_FORMAT, BV_UPLOAD_SUCCESSFUL_HEADER, BV_UPLOAD_SUCCESSFUL_DESCRIPTION, BV_UPLOAD_ERROR_CANNOT_UPLOAD_MEANS, BV_UPLOAD_ERROR_CANNOT_UPLOAD_SUMMARY, BV_UPLOAD_ERROR_CANNOT_UPLOAD_OUTLIER
}
