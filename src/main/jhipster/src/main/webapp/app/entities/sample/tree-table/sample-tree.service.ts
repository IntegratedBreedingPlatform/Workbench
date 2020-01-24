import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { TreeNode } from './tree-node.model';
import { createRequestOption } from '../../../shared';
import { map } from 'rxjs/operators';

export type EntityResponseType = HttpResponse<TreeNode>;

@Injectable()
export class SampleTreeService {

    private isFolderOnly = 0;
    private fieldbookUrl = '/Fieldbook/SampleListTreeManager';
    private resourceUrl;
    private programUID: string;

    constructor(private http: HttpClient) {
    }

    setCrop(crop: string) {
        this.resourceUrl = `/bmsapi/sampleLists/${crop}/sampleListFolder`;
    }

    setProgram(programUID: string) {
        this.programUID = programUID;
    }

    getInitTree(req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        const url = `${this.fieldbookUrl}/loadInitTree/${this.isFolderOnly}`;
        return this.http.get<TreeNode[]>(url, { params: options, observe: 'response' })
            .pipe(map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res)));
    }

    move(source: string, target: string): Observable<any> {
        const isCropList = target === 'CROPLISTS';
        const sourceId = source === 'LISTS' || source === 'CROPLISTS' ? 0 : source;
        const targetId = target === 'LISTS' || target === 'CROPLISTS' ? 0 : target;
        const url = `${this.resourceUrl}/${sourceId}/move?newParentId=${targetId}&isCropList=${isCropList}&programUUID=${this.programUID}`;
        return this.http.put<any>(url, { observe: 'response' });
    }

    delete(folderId: string): Observable<HttpResponse<any>> {
        const url = `${this.resourceUrl}/${folderId}`;
        return this.http.delete<TreeNode[]>(url, { observe: 'response' });
    }

    create(folderName: string, parentId: string) {
        const id = parentId === 'LISTS' || parentId === 'CROPLISTS' ? 0 : parentId;
        const url = `${this.resourceUrl}?folderName=${folderName}&parentId=${id}&&programUUID=${this.programUID}`;
        return this.http.post<any>(url, { observe: 'response' });
    }

    rename(newFolderName: string, folderId: string) {
        const url = `${this.resourceUrl}/${folderId}/?newFolderName=${newFolderName}`;
        return this.http.put<TreeNode[]>(url, { observe: 'response' });
    }

    expand(parentKey: string, req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        return this.http.get<TreeNode[]>(this.getUrl(parentKey), { params: options, observe: 'response' })
            .pipe(map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res, parentKey)));
    }

    private getUrl(parentKey: string) {
        return `${this.fieldbookUrl}/expandTree/${parentKey}/${this.isFolderOnly}`;
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
