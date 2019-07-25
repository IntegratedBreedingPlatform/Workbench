var mockData = [
    {
        id: '2',
        name: 'CROP_MANAGEMENT',
        description: 'Crop management',
        selectable: false,
        children: [
            {
                id: '4',
                name: 'MANAGE_PROGRAM',
                description: 'Manage program',
                selectable: false,
                children: [
                    {
                        id: '5',
                        name: 'MANAGE_PROGRAM_SETTINGS',
                        description: 'Manage program settings',
                        selectable: true,
                    }
                ]
            }
        ],
    },
    {
        id: '6',
        name: 'BREEDING_ACTIVITIES',
        description: 'Breeding activities',
        selectable: false,
        children: [
            {
                id: '8',
                name: 'MANAGE_GERMPLASM',
                description: 'Manage Germplasm',
                selectable: true,
                children: [
                    {
                        id: '9',
                        name: 'DELETE_GERMPLASM',
                        description: 'Delete Germplasm',
                        selectable: true,
                    },
                ]
            },
            {
                id: '7',
                name: 'MANAGE_STUDIES',
                description: 'Manage Studies',
                selectable: true,
            },
        ],
    },
];
export default mockData;