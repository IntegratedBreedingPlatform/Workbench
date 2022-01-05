export const MANAGE_INVENTORY_PERMISSIONS = [
    'ADMIN',
    'CROP_MANAGEMENT',
    'MANAGE_INVENTORY',
];
export const MANAGE_LOT_PERMISSIONS = [
    ...MANAGE_INVENTORY_PERMISSIONS,
    'MANAGE_LOTS',
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

/**
 * {@see org.generationcp.middleware.pojos.workbench.PermissionsEnum.HAS_MANAGE_STUDIES_VIEW}
 */
export const MANAGE_STUDIES_VIEW_PERMISSIONS = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_MANAGE_OBSERVATION_UNITS',
    'MS_WITHDRAW_INVENTORY',
    'MS_CREATE_PENDING_WITHDRAWALS',
    'MS_CREATE_CONFIRMED_WITHDRAWALS',
    'MS_MANAGE_FILES',
    'MS_CREATE_LOTS'
];

export const MS_MANAGE_FILES_PERMISSION = [
    ...MANAGE_STUDIES_PERMISSIONS,
    'MS_MANAGE_OBSERVATION_UNITS',
    'MS_MANAGE_FILES'
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
