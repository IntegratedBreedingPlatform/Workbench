import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { TreeNode } from './tree-node.model';
import { createRequestOption } from '../../../shared';

export type EntityResponseType = HttpResponse<TreeNode>;

@Injectable()
export class SampleTreeService {

    private isFolderOnly = 0;
    private url = '/Fieldbook/SampleListTreeManager';
    private bmsapiUrl;
    private initTreeUrl = `${this.url}/loadInitTree/${this.isFolderOnly}`;
    private programUID: string;

    constructor(private http: HttpClient) {
    }

    setCrop(crop: string) {
        // url: '/bmsapi/sampleLists/' + cropName + '/sampleListFolder?folderName=' + folderName + '&parentId=' + parentFolderId + '&programUUID=' + currentProgramId,
        this.bmsapiUrl = `/bmsapi/sampleLists/${crop}/sampleListFolder`;
    }

    setProgram(programUID: string) {
        this.programUID = programUID;
    }

    getInitTree(req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        return this.http.get<TreeNode[]>(this.initTreeUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res));
    }

    // TODO
    move(source: string, target: string, req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        const isCropList = target === 'CROPLISTS';
        const url = `${this.bmsapiUrl}/${source}/move?newParentId=${target}&isCropList=${isCropList}&&programUUID=${this.programUID}`;
        return this.http.get<TreeNode[]>(url, { params: options, observe: 'response' })
            .map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res));
    }

    expand(parentKey: string, req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        return this.http.get<TreeNode[]>(this.getUrl(parentKey), { params: options, observe: 'response' })
            .map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res, parentKey));
    }

    private getUrl(parentKey: string) {
        return `${this.url}/expandTree/${parentKey}/${this.isFolderOnly}`;
    }

    private convertArrayResponse(res: HttpResponse<TreeNode[]>, parentKey?: string): HttpResponse<TreeNode[]> {
        const jsonResponse: TreeNode[] = res.body;
        const body: TreeNode[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i], parentKey));
        }
        return res.clone({ body });
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: TreeNode = this.convertItemFromServer(res.body);
        return res.clone({ body });
    }

    private convertItemFromServer(treeNode: TreeNode, parentKey?: string): TreeNode {
        const copy: TreeNode = Object.assign({}, treeNode);
        copy.parentId = parentKey;
        copy.id = treeNode.key;
        copy.name = treeNode.title;
        return copy;
    }
}
