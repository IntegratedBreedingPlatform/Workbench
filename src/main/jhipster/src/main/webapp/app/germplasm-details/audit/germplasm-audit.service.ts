import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { createRequestOption } from '../../shared';
import { GermplasmNameAudit } from './names/germplasm-name-audit.model';
import { GermplasmAttributeAudit } from './attributes/germplasm-attribute-audit.model';
import { GermplasmBasicDetailsAudit } from './basic-details/germplasm-basic-details-audit.model';
import { GermplasmReferenceAudit } from './basic-details/germplasm-reference-audit.model';
import { GermplasmProgenitorDetailsAudit } from './progenitors/germplasm-progenitor-details-audit.model';
import { GermplasmOtherProgenitorAudit } from './progenitors/germplasm-other-progenitors-audit.model';

@Injectable()
export class GermplasmAuditService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getNameChanges(gid: number, nameId: number, request: any): Observable<HttpResponse<GermplasmNameAudit[]>> {
        const params = createRequestOption(request);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/names/${nameId}/changes?programUUID=${this.context.programUUID}`;
        return this.http.get<GermplasmNameAudit[]>(url,
            { params, observe: 'response' });
    }

    getAttributeChanges(gid: number, attributeId: number, request: any): Observable<HttpResponse<GermplasmAttributeAudit[]>> {
        const params = createRequestOption(request);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attributes/${attributeId}/changes?programUUID=${this.context.programUUID}`;
        return this.http.get<GermplasmAttributeAudit[]>(url,
            { params, observe: 'response' });
    }

    getBasicDetailsChanges(gid: number, request: any): Observable<HttpResponse<GermplasmBasicDetailsAudit[]>> {
        const params = createRequestOption(request);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/basic-details/changes?programUUID=${this.context.programUUID}`;
        return this.http.get<GermplasmBasicDetailsAudit[]>(url,
            { params, observe: 'response' });
    }

    getReferenceChanges(gid: number, request: any): Observable<HttpResponse<GermplasmReferenceAudit[]>> {
        const params = createRequestOption(request);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/references/changes?programUUID=${this.context.programUUID}`;
        return this.http.get<GermplasmReferenceAudit[]>(url,
            { params, observe: 'response' });
    }

    getProgenitorDetailsChanges(gid: number, request: any): Observable<HttpResponse<GermplasmProgenitorDetailsAudit[]>> {
        const params = createRequestOption(request);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/progenitor-details/changes?programUUID=${this.context.programUUID}`;
        return this.http.get<GermplasmProgenitorDetailsAudit[]>(url,
            { params, observe: 'response' });
    }

    getOtherProgenitorsChanges(gid: number, request: any): Observable<HttpResponse<GermplasmOtherProgenitorAudit[]>> {
        const params = createRequestOption(request);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/other-progenitors/changes?programUUID=${this.context.programUUID}`;
        return this.http.get<GermplasmOtherProgenitorAudit[]>(url,
            { params, observe: 'response' });
    }

}
