import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { TreeNode } from './tree-node.model';
import { map } from 'rxjs/operators';
import { ParamContext } from '../../../shared/service/param.context';

export type EntityResponseType = HttpResponse<TreeNode>;

@Injectable()
export class SampleTreeService {

    private resourceUrl;
    private crop;
    private programUUID;

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    setCropAndProgram(crop: string, programUUID: string) {
        this.crop = crop;
        this.programUUID = programUUID;
        this.resourceUrl = `/bmsapi/crops/${crop}/programs/${programUUID}/sample-list-folders/`;
    }

    move(source: string, target: string): Observable<any> {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);

        const isCropList = target === 'CROPLISTS';
        const sourceId = source === 'LISTS' || source === 'CROPLISTS' ? 0 : source;
        const targetId = target === 'LISTS' || target === 'CROPLISTS' ? 0 : target;

        const url = `${this.resourceUrl}/${sourceId}/move?newParentId=${targetId}&isCropList=${isCropList}`;
        return this.http.put<any>(url, { observe: 'response' });
    }

    delete(folderId: string): Observable<HttpResponse<any>> {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);

        const url = `${this.resourceUrl}/${folderId}`;
        return this.http.delete<TreeNode[]>(url, { observe: 'response' });
    }

    create(folderName: string, parentId: string) {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);

        const id = parentId === 'LISTS' || parentId === 'CROPLISTS' ? 0 : parentId;
        const url = `${this.resourceUrl}?folderName=${folderName}&parentId=${id}`;
        return this.http.post<any>(url, { observe: 'response' });
    }

    rename(newFolderName: string, folderId: string) {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);

        const url = `${this.resourceUrl}/${folderId}?newFolderName=${newFolderName}`;
        return this.http.put<TreeNode[]>(url, { observe: 'response' });
    }

    expand(parentKey: string, req?: any): Observable<HttpResponse<TreeNode[]>> {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);

        const url = `/bmsapi/crops/${this.crop}/sample-lists/tree`;
        const params = {
            onlyFolders: '0'
        };
        if (parentKey) {
            params['parentFolderId'] = parentKey;
        }
        if (this.programUUID) {
            params['programUUID'] = this.programUUID;
        }
        return this.http.get<TreeNode[]>(url, {
            params,
            observe: 'response'
        }).pipe(map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res, parentKey)));

    }

    private convertArrayResponse(res: HttpResponse<TreeNode[]>, parentKey?: string): HttpResponse<TreeNode[]> {
        const jsonResponse: TreeNode[] = res.body;
        const body: TreeNode[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i], parentKey));
        }
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
