import { FileDownloadHelper } from '../../../../../../main/webapp/app/entities/sample/file-download.helper';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

describe('File Download Helper Test', () => {

    let fileDownloadHelper: FileDownloadHelper;

    beforeEach(() => {
        fileDownloadHelper = new FileDownloadHelper();
    });

    describe('FileDownloadHelper', () => {

        it('Should extract the filename from HttpResponse header.', () => {

            const testFileName = 'testFileName.csv';
            const headers = new HttpHeaders().append('content-disposition', `attachment; filename=${testFileName};`);
            const httpResponse = new HttpResponse<Blob>(
                {
                    headers
                }
            );

            const result = fileDownloadHelper.getFileNameFromResponseContentDisposition(httpResponse);
            expect(result).toEqual(testFileName);

        });

        it('Should save the blob in the browser', () => {

            const blob: Blob = new Blob();
            const fileName = 'fileName.csv';

            const link = document.createElement('a');
            spyOn(link, 'click');

            document.createElement = jasmine.createSpy('a').and.returnValue(link);

            fileDownloadHelper.save(blob, fileName);

            expect(link.click).toHaveBeenCalled();
            expect(link.download).toEqual(fileName);

        });
    });

});
