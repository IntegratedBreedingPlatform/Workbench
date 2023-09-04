export const MANAGE_INVENTORY_PERMISSIONS = [
    'ADMIN',
    'CROP_MANAGEMENT',
    'MANAGE_INVENTORY',
];
export const MANAGE_LOT_PERMISSIONS = [
    ...MANAGE_INVENTORY_PERMISSIONS,
    'MANAGE_LOTS',
];

export const MI_MANAGE_FILES_PERMISSION = [
    ...MANAGE_LOT_PERMISSIONS,
    'UPDATE_LOTS',
    'MI_MANAGE_FILES'
];

export const LOT_LABEL_PRINTING_PERMISSIONS = [
    ...MANAGE_LOT_PERMISSIONS, 'LOT_LABEL_PRINTING'
];

export const MANAGE_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM'
];

export const SEARCH_GERMPLASM_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'SEARCH_GERMPLASM'
];

export const IMPORT_GERMPLASM_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'IMPORT_GERMPLASM'
];

export const IMPORT_GERMPLASM_UPDATES_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'IMPORT_GERMPLASM_UPDATES'
];

export const ADD_GERMPLASM_ENTRIES_TO_LIST_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'MG_ADD_ENTRIES_TO_LIST'
];

export const DELETE_GERMPLASM_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'DELETE_GERMPLASM'
];

export const MERGE_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'MERGE_GERMPLASM'
];

export const CODE_GERMPLASM_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'CODE_GERMPLASM'
];

export const GERMPLASM_LABEL_PRINTING_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'GERMPLASM_LABEL_PRINTING'
];

export const SITE_ADMIN_PERMISSIONS = [
    'ADMIN',
    'ADMINISTRATION',
    'SITE_ADMIN'
];

export const ADD_PROGRAM_PERMISSION = [
    'ADMIN',
    'CROP_MANAGEMENT',
    'ADD_PROGRAM',
    'MANAGE_PROGRAMS'
];

export const MANAGE_STUDIES_PERMISSIONS = [
    'ADMIN',
    'STUDIES',
    'MANAGE_STUDIES'
];

export const MS_MANAGE_FILES_OBSERVATIONS_PERMISSION = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_OBSERVATIONS',
    'MS_MANAGE_FILES_OBSERVATIONS'
];

export const MANAGE_FILES_ENVIRONMENT_PERMISSION = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_ENVIRONMENT',
    'MS_MANAGE_FILES_ENVIRONMENT'
];

export const EDIT_GERMPLASM_PERMISSION = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'EDIT_GERMPLASM'
];

export const MG_MANAGE_FILES_PERMISSION = [
    ...EDIT_GERMPLASM_PERMISSION,
    'MG_MANAGE_FILES'
];

export const GROUP_GERMPLASM_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'GROUP_GERMPLASM'
];

export const UNGROUP_GERMPLASM_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'UNGROUP_GERMPLASM'
];

export const GERMPLASM_AUDIT_PERMISSION = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'VIEW_GERMPLASM_CHANGE_HISTORY'
]

export const CREATE_INVENTORY_LOT_PERMISSIONS = [
    ...MANAGE_GERMPLASM_PERMISSIONS,
    'MG_MANAGE_INVENTORY',
    'MG_CREATE_LOTS'
];

export const MANAGE_CROP_SETTINGS_PERMISSIONS = [
    'ADMIN',
    'CROP_MANAGEMENT',
    'MANAGE_CROP_SETTINGS',
];

export const MANAGE_GERMPLASM_LIST_PERMISSION = [
    'ADMIN',
    'LISTS',
    'MANAGE_GERMPLASM_LISTS'
];

export const SEARCH_GERMPLASM_LISTS_PERMISSION = [
    ...MANAGE_GERMPLASM_LIST_PERMISSION, 'SEARCH_GERMPLASM_LISTS'
];

export const GERMPLASM_LIST_LABEL_PRINTING_PERMISSIONS = [
    ...MANAGE_GERMPLASM_LIST_PERMISSION,
    'GERMPLASM_LIST_LABEL_PRINTING'
];

export const STUDY_ENTRIES_LABEL_PRINTING_PERMISSIONS = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_STUDY_ACTIONS',
    'MS_MANAGE_OBSERVATION_UNITS',
    'MS_EXPORT_STUDY_ENTRIES'
];

export const OBSERVATION_DATASET_LABEL_PRINTING_PERMISSIONS = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_STUDY_ACTIONS',
    'MS_DESIGN_AND_PLANNING_OPTIONS',
    'MS_CREATE_PLANTING_LABELS'
];
/**
 * To simplify, we don't specify here which specific granular permission
 * needs access to the germplasm selector (e.g ADD_GERMPLASM_LIST_ENTRIES),
 * we just add the "View" type permission that gives access to the module.
 */
export const GERMPLASM_SELECTOR_PERMISSIONS = [
    ...SEARCH_GERMPLASM_PERMISSIONS,
    ...SEARCH_GERMPLASM_LISTS_PERMISSION,
    ...MANAGE_STUDIES_PERMISSIONS,
    'QUERIES',
    'GRAPHICAL_QUERIES',
];

export const STUDIES_EDITION_PERMISSIONS = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_MANAGE_OBSERVATION_UNITS',
    'MS_WITHDRAW_INVENTORY',
    'MS_CREATE_PENDING_WITHDRAWALS',
    'MS_CREATE_CONFIRMED_WITHDRAWALS',
    'MS_CANCEL_PENDING_TRANSACTIONS',
    'MS_MANAGE_FILES_OBSERVATIONS',
    'MS_CREATE_LOTS',
    'CREATE_STUDIES',
    'CLOSE_STUDY',
    'DELETE_STUDY',
    'LOCK_STUDY',
    'MS_GERMPLASM_AND_CHECKS',
    'MS_VIEW_GERMPLASM_AND_CHECKS',
    'MS_ADD_ENTRY_DETAILS_VARIABLES',
    'MS_MODIFY_ENTRY_DETAILS_VALUES',
    'MS_MODIFY_COLUMNS',
    'MS_REPLACE_GERMPLASM',
    'MS_ADD_NEW_ENTRIES',
    'MS_IMPORT_ENTRY_DETAILS',
    'MS_TREATMENT_FACTORS',
    'MS_VIEW_TREATMENT_FACTORS',
    'MS_ADD_TREATMENT_FACTORS_VARIABLES',
    'MS_EXPERIMENTAL_DESIGN',
    'MS_VIEW_EXPERIMENTAL_DESIGN',
    'MS_GENERATE_EXPERIMENTAL_DESIGN',
    'MS_DELETE_EXPERIMENTAL_DESIGN',
    'MS_ENVIRONMENT',
    'MS_VIEW_ENVIRONMENT',
    'MS_ADD_ENVIRONMENTAL_CONDITIONS_VARIABLES',
    'MS_ADD_ENVIRONMENT_DETAILS_VARIABLES',
    'MS_MODIFY_ENVIRONMENT_VALUES',
    'MS_MODIFY_NUMBER_OF_ENVIRONMENTS',
    'MS_MANAGE_FILES_ENVIRONMENT',
    'MS_STUDY_SETTINGS',
    'MS_VIEW_STUDY_SETTINGS',
    'MS_ADD_STUDY_SETTINGS_VARIABLES',
    'MS_CROSSES_AND_SELECTIONS',
    'MS_VIEW_CROSSES_AND_SELECTIONS',
    'MS_INVENTORY',
    'MS_VIEW_INVENTORY',
    'MS_SAMPLE_LISTS',
    'MS_VIEW_SAMPLE_LISTS',
    'MS_EXPORT_SAMPLE_LIST',
    'MS_DELETE_SAMPLES',
    'MS_SSA_RESULTS',
    'MS_VIEW_SSA_RESULTS',
    'MS_SSA_SUMMARY_STATISTICS',
    'MS_SSA_MEANS_BLUE',
    'MS_OBSERVATIONS',
    'MS_VIEW_OBSERVATIONS',
    'MS_ADD_OBSERVATION_TRAIT_VARIABLES',
    'MS_ADD_OBSERVATION_SELECTION_VARIABLES',
    'MS_MANAGE_PENDING_OBSERVATIONS',
    'MS_MANAGE_CONFIRMED_OBSERVATIONS',
    'MS_ACCEPT_PENDING_OBSERVATION',
    'MS_STUDY_ACTIONS',
    'MS_CREATE_GENOTYPING_SAMPLES',
    'MS_EXECUTE_CALCULATED_VARIABLES',
    'MS_DESIGN_AND_PLANNING_OPTIONS',
    'MS_EXPORT_DESIGN_TEMPLATE',
    'MS_CREATE_PLANTING_LABELS',
    'MS_ADVANCES',
    'MS_ADVANCE_STUDY',
    'MS_ADVANCE_STUDY_FOR_PLANTS',
    'MS_ANALYZE_WITH_STA_BRAPP',
    'MS_ANALYZE_WITH_DECISION_SUPPORT',
    'MS_CREATE_SUB_OBSERVATION_UNITS',
    'MS_CHANGE_PLOT_ENTRY',
    'MS_EXPORT_STUDY_BOOK',
    'MS_EXPORT_STUDY_ENTRIES',
    'MS_CROSSING_OPTIONS',
    'MS_EXPORT_CROSSING_TEMPLATE',
    'MS_IMPORT_CROSSES',
    'MS_DESIGN_NEW_CROSSES',
    'MS_FIELD_MAP_OPTIONS',
    'MS_MAKE_FIELD_MAP',
    'MS_VIEW_FIELD_MAP',
    'MS_DELETE_FIELD_MAP',
    'MS_CREATE_GEOREFERENCE',
    'MS_EDIT_GEOREFERENCE'
];

export const VIEW_GERMPLASM_DETAILS_PERMISSION = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_VIEW_GERMPLASM_DETAILS'
];

export const VIEW_PEDIGREE_INFORMATION_PERMISSION = [
    'VIEW_PEDIGREE_INFORMATION'
];
