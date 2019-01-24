import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../app.constants';
import { PrintLabelsContext } from './print-labels.context';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

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

    getOriginResourceMetadada(): Observable<Map<string, string>> {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${cropName}/labelPrinting/${printingLabelType}/metadata`;
        return this.http.post<Map<string, string>>(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId
        });
    }
}
