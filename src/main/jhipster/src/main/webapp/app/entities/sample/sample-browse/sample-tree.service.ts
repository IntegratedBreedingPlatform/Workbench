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
    private initTreeUrl = `${this.url}/loadInitTreeTable`;

    constructor(private http: HttpClient) {
    }

    getInitTree(req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        return this.http.get<TreeNode[]>(this.initTreeUrl, {params: options, observe: 'response'})
            .map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res));
    }

    expand(parentKey: string, req?: any): Observable<HttpResponse<TreeNode[]>> {
        const options = createRequestOption(req);
        return this.http.get<TreeNode[]>(this.getUrl(parentKey), {params: options, observe: 'response'})
            .map((res: HttpResponse<TreeNode[]>) => this.convertArrayResponse(res, parentKey));
    }

    private getUrl(parentKey: string) {
        return `${this.url}/expandTreeTable/${parentKey}/${this.isFolderOnly}`;
    }

    private convertArrayResponse(res: HttpResponse<TreeNode[]>, parentKey?: string): HttpResponse<TreeNode[]> {
        const jsonResponse: TreeNode[] = res.body;
        const body: TreeNode[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i], parentKey));
        }
        return res.clone({body});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: TreeNode = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertItemFromServer(treeNode: TreeNode, parentKey?: string): TreeNode {
        const copy: TreeNode = Object.assign({}, treeNode);
        copy.parentId = parentKey;
        return copy;
    }
}
