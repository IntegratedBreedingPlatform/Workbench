import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../app.constants';
import { LabelPrintingContext } from './label-printing.context';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LabelType } from './label-printing.model';

declare const cropName: string;

@Injectable()
export class LabelPrintingService {

    private baseUrl = SERVER_API_URL;

    constructor(
        private http: HttpClient,
        private context: LabelPrintingContext
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

    getAvailableLabelFields(): Observable<LabelType[]> {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${cropName}/labelPrinting/${printingLabelType}/labelTypes`;
        return this.http.post<LabelType[]>(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId
        });
    }

    download(fileExtension: any, labelsGeneratorInput: any) {

        labelsGeneratorInput.datasetId = this.context.datasetId;
        labelsGeneratorInput.studyId = this.context.studyId;

        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${cropName}/labelPrinting/${printingLabelType}/labels/${fileExtension}`;
        return this.http.post(`${this.baseUrl + resourceUrl}`, labelsGeneratorInput,
            {
                responseType: 'blob',
                observe: 'response'
            }
        );
    }

}
