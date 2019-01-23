import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../app.constants';
import { PrintLabelsContext } from './print-labels.context';
import { HttpClient } from '@angular/common/http';

declare const cropName: string;

@Injectable()
export class PrintLabelsService {

    private baseUrl = SERVER_API_URL;

    constructor(
        private http: HttpClient,
        private context: PrintLabelsContext
    ) {
    }

    getLabelsNeededSummary() {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${cropName}/labelPrinting/${printingLabelType}/labels/summary`;
        return this.http.post(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId
        });

    }
}
