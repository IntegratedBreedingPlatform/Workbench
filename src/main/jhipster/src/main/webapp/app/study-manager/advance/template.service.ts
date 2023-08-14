import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ParamContext } from '../../shared/service/param.context';
import { TemplateModel } from './template.model';

@Injectable()
export class TemplateService {

    private baseUrl = SERVER_API_URL;

    constructor(
        private http: HttpClient,
        private paramContext: ParamContext
    ) {
    }

    getAllTemplates(): Observable<TemplateModel[]> {
        const options: HttpParams = new HttpParams()
            .append('templateType', 'DESCRIPTORS');

        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/templates`;
        return this.http.get<TemplateModel[]>(this.baseUrl + resourceUrl, {
            params: options,
        });
    }

    addTemplate(model: TemplateModel): Observable<TemplateModel> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/templates`;
        return this.http.put<TemplateModel>(this.baseUrl + resourceUrl, model);
    }

    updateTemplate(model: TemplateModel): Observable<void> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/templates/${model.templateId}`;
        return this.http.put<void>(this.baseUrl + resourceUrl, model);
    }

    deleteTemplate(templateId: number): Observable<TemplateModel> {
        const resourceUrl = `crops/${this.paramContext.cropName}/programs/${this.paramContext.programUUID}/templates/${templateId}`;
        return this.http.delete<TemplateModel>(this.baseUrl + resourceUrl);
    }

}
