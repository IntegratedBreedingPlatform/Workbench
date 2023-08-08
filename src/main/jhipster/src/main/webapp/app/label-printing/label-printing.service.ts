import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../app.constants';
import { LabelPrintingContext } from './label-printing.context';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LabelType, OriginResourceMetadata, LabelPrintingPresetSetting, Sortable } from './label-printing.model';
import { ParamContext } from '../shared/service/param.context';

@Injectable()
export class LabelPrintingService {

    private baseUrl = SERVER_API_URL;

    constructor(
        private http: HttpClient,
        private context: LabelPrintingContext,
        private paramContext: ParamContext
    ) {
    }

    getLabelsNeededSummary() {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/labelPrinting/${printingLabelType}/labels/summary`;
        return this.http.post(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId,
            searchRequestId: this.context.searchRequestId,
            listId: this.context.listId
        });

    }

    getOriginResourceMetadada(): Observable<OriginResourceMetadata> {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/labelPrinting/${printingLabelType}/metadata`;
        return this.http.post<OriginResourceMetadata>(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId,
            searchRequestId: this.context.searchRequestId,
            listId: this.context.listId
        });
    }

    getAvailableLabelFields(): Observable<LabelType[]> {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/labelPrinting/${printingLabelType}/labelTypes`;
        return this.http.post<LabelType[]>(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId,
            searchRequestId: this.context.searchRequestId,
            listId: this.context.listId
        });
    }

    getSortableFields(): Observable<Sortable[]> {
        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/labelPrinting/${printingLabelType}/sortable-fields`;
        return this.http.get<Sortable[]>(this.baseUrl + resourceUrl);
    }

    download(fileExtension: any, labelsGeneratorInput: any) {

        labelsGeneratorInput.datasetId = this.context.datasetId;
        labelsGeneratorInput.studyId = this.context.studyId;
        labelsGeneratorInput.searchRequestId = this.context.searchRequestId;
        labelsGeneratorInput.listId = this.context.listId;

        const printingLabelType = this.context.printingLabelType;
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/labelPrinting/${printingLabelType}/labels/${fileExtension}`;
        return this.http.post(`${this.baseUrl + resourceUrl}`, labelsGeneratorInput,
            {
                responseType: 'blob',
                observe: 'response'
            }
        );
    }

    getAllPresets(toolSection): Observable<LabelPrintingPresetSetting[]> {
        const options: HttpParams = new HttpParams()
            .append('toolId', '23')
            .append('toolSection', toolSection);

        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets`;
        return this.http.get<LabelPrintingPresetSetting[]>(this.baseUrl + resourceUrl, {
            params: options,
        });
    }

    addPreset(preset: LabelPrintingPresetSetting): Observable<LabelPrintingPresetSetting> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets`;
        return this.http.put<LabelPrintingPresetSetting>(this.baseUrl + resourceUrl, preset);
    }

    updatePreset(preset: LabelPrintingPresetSetting): Observable<void> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets/${preset.id}`;
        return this.http.put<void>(this.baseUrl + resourceUrl, preset);
    }

    deletePreset(presetId: number): Observable<LabelPrintingPresetSetting> {
        const options: HttpParams = new HttpParams()
            .append('presetId', presetId.toString());
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets/${presetId}`;
        return this.http.delete<LabelPrintingPresetSetting>(this.baseUrl + resourceUrl);
    }

    getDefaultSettings() {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/labelPrinting/${this.context.printingLabelType}/default-settings`;
        return this.http.post<LabelPrintingPresetSetting>(this.baseUrl + resourceUrl, {
            datasetId: this.context.datasetId,
            studyId: this.context.studyId,
            searchRequestId: this.context.searchRequestId,
            listId: this.context.listId
        });
    }
}
