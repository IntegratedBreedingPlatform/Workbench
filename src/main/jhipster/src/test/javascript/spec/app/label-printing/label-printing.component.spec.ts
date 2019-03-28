import { AllLabelsPipe } from '../../../../../main/webapp/app/label-printing/label-printing.component';

describe('Label Printing', () => {

    describe('AllLabelsPipe', () => {
        const allLabelsPipe = new AllLabelsPipe();

        it('should reduce all label objects in a single array:', () => {

            const allLabels = [
                {
                    fields: [
                        {id: 1, name: 'label1'},
                        {id: 2, name: 'label2'}
                    ]
                },
                {
                    fields: [
                        {id: 3, name: 'label3'}
                    ]
                }
            ];
            expect(allLabelsPipe.transform(allLabels)).toEqual([
                { id: 1, name: 'label1' },
                { id: 2, name: 'label2' },
                { id: 3, name: 'label3' }
            ]);
        });

    });
});
