package org.generationcp.ibpworkbench;

public enum Message {
     account
    ,actions
    ,activity_next
    ,activity_recent
    ,contact_create
    ,dashboard
    ,datasets
    ,error_login_invalid
    ,help
    ,home
    ,email
    ,login
    ,password
    ,login_title
    ,project_create
    ,project_dashboard_title
    ,project_title
    ,project_table_caption
    ,project
    ,action
    ,date
    ,date_due
    ,owner
    ,status
    ,recent
    ,signout
    ,user_guide
    ,user_guide_1
    ,USERNAME
    ,workbench_title,
    
    //General
    SAVE,
    CANCEL,
    
    //Register User Account
    REGISTER_USER_ACCOUNT,
    REGISTER_USER_ACCOUNT_FORM,
    USER_ACC_POS_TITLE,
    USER_ACC_FNAME,
    USER_ACC_MIDNAME,
    USER_ACC_LNAME,
    USER_ACC_EMAIL,
    USER_ACC_USERNAME,
    USER_ACC_PASSWORD,
    USER_ACC_PASSWORD_CONFIRM,
    
    //Error Notification
    UPLOAD_ERROR,
    UPLOAD_ERROR_DESC,
    LAUNCH_TOOL_ERROR,
    LAUNCH_TOOL_ERROR_DESC,
    INVALID_TOOL_ERROR_DESC,
    LOGIN_ERROR,
    LOGIN_DB_ERROR_DESC,
    DATABASE_ERROR,
    SAVE_PROJECT_ERROR_DESC,
    SAVE_USER_ACCOUT_ERROR_DESC,
    ADD_CROP_TYPE_ERROR_DESC,
    FILE_NOT_FOUND_ERROR,
    FILE_NOT_FOUND_ERROR_DESC,
    FILE_ERROR,
    FILE_CANNOT_PROCESS_DESC,
    FILE_CANNOT_OPEN_DESC,
    PARSE_ERROR,
    WORKFLOW_DATE_PARSE_ERROR_DESC,
    CONFIG_ERROR,
    CONTACT_ADMIN_ERROR_DESC,
    INVALID_URI_ERROR,
    INVALID_URI_ERROR_DESC,
    INVALID_URI_SYNTAX_ERROR_DESC,
    INVALID_URL_PARAM_ERROR,
    INVALID_URL_PARAM_ERROR_DESC,
    
    //Tray Notification
    UPLOAD_SUCCESS,
    UPLOAD_SUCCESS_DESC,
    
    LOC_NAME,
    LOC_ABBR
    
    // Tool configuration update
    ,UPDATING
    ,UPDATING_TOOLS_CONFIGURATION
}
