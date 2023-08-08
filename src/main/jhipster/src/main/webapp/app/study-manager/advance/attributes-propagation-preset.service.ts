import {Injectable} from "@angular/core";
import {SERVER_API_URL} from "../../app.constants";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ParamContext} from "../../shared/service/param.context";
import {AttributesPropagationPresetModel} from "./attributes-propagation-preset.model";

@Injectable()
export class AttributesPropagationPresetService {

    private baseUrl = SERVER_API_URL;

    constructor(
        private http: HttpClient,
        private paramContext: ParamContext
    ){
    }

    getAllAttributesPropagationPresets(): Observable<AttributesPropagationPresetModel[]> {
        const options: HttpParams = new HttpParams()
            .append('toolId', '23')
            .append('toolSection', 'ATTRIBUTES_PROPAGATION_PRESET');

        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets`;
        return this.http.get<AttributesPropagationPresetModel[]>(this.baseUrl + resourceUrl, {
            params: options,
        });
    }

    addPreset(preset: AttributesPropagationPresetModel): Observable<AttributesPropagationPresetModel> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets`;
        return this.http.put<AttributesPropagationPresetModel>(this.baseUrl + resourceUrl, preset);
    }

    updatePreset(preset: AttributesPropagationPresetModel): Observable<void> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets/${preset.id}`;
        return this.http.put<void>(this.baseUrl + resourceUrl, preset);
    }

    deletePreset(presetId: number): Observable<AttributesPropagationPresetModel> {
        const options: HttpParams = new HttpParams()
            .append('presetId', presetId.toString());
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/presets/${presetId}`;
        return this.http.delete<AttributesPropagationPresetModel>(this.baseUrl + resourceUrl);
    }

}