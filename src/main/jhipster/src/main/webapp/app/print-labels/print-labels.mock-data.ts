export const printMockData = {
    labelTypes: [{
        title: 'Study Details',
        key: 'Study Details',
        fields: [{
            id: 'NREP',
            name: 'NREP'
        }, {
            id: 'LOCATION_NAME',
            name: 'LOCATION_NAME'
        }, {
            id: 'NPSEL',
            name: 'NPSEL'
        }, {
            id: 'Scale',
            name: 'Scale'
        }]
    }, {
        title: 'Dataset Details',
        key: 'Dataset Details',
        fields: [{
            id: 'NREP',
            name: 'NREP'
        }, {
            id: 'PLANT_NO',
            name: 'PLANT_NO'
        }, {
            id: 'NPSEL',
            name: 'NPSEL'
        }, {
            id: 'Scale',
            name: 'Scale'
        }]
    }],
    summaryData: {
        'headers': [
            'Environment',
            '# of sub-obs units',
            'Labels Needed'
        ],
        'values': [
            {
                'Environment': '1',
                '# of sub-obs units': '20',
                'Labels Needed': '20'
            },
            {
                'Environment': '2',
                '# of sub-obs units': '20',
                'Labels Needed': '20'
            }
        ],
        'totalNumberOfLabelsNeeded': 40
    }
};
