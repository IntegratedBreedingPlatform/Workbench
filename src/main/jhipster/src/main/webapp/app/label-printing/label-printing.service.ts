import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../app.constants';
import { LabelPrintingContext } from './label-printing.context';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LabelType, OriginResourceMetadata, PresetSetting } from './label-printing.model';

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

    getOriginResourceMetadada(): Observable<OriginResourceMetadata> {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${cropName}/labelPrinting/${printingLabelType}/metadata`;
        return this.http.post<OriginResourceMetadata>(this.baseUrl + resourceUrl, {
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

    getAllPresets(): Observable<PresetSetting[]> {
        const programId = this.context.programId;
        const options: HttpParams = new HttpParams()
            .append('programUUID', programId)
            .append('toolId', '23')
            .append('toolSection', 'DATASET_LABEL_PRINTING_PRESET');

        const resourceUrl = `crops/${cropName}/presets`;
        return this.http.get<PresetSetting[]>(this.baseUrl + resourceUrl, {
            params: options,
        });
    }

    addPreset(preset: PresetSetting): Observable<PresetSetting> {
        const resourceUrl = `crops/${cropName}/presets`;
        return this.http.put<PresetSetting>(this.baseUrl + resourceUrl,  preset );
    }

    deletePreset(presetId: number): Observable<PresetSetting> {
        const options: HttpParams = new HttpParams()
            .append('presetId', presetId.toString());
        const resourceUrl = `crops/${cropName}/presets/${presetId}`;
        return this.http.delete<PresetSetting>(this.baseUrl + resourceUrl);
    }
}
