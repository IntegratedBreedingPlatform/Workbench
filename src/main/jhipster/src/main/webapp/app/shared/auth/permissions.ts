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

export const SEARCH_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'SEARCH_GERMPLASM'
];

export const IMPORT_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'IMPORT_GERMPLASM'
]

export const IMPORT_GERMPLASM_UPDATES_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'IMPORT_GERMPLASM_UPDATES'
]
