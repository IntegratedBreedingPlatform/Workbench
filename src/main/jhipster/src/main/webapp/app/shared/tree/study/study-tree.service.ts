import { TreeService } from '../tree.service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { TreeNode } from '../index';
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class StudyTreeService extends TreeService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
        super();
    }

    expand(parentKey: any): any {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/studies/tree`;
        const params = {};
        if (parentKey) {
            params['parentFolderId'] = parentKey;
        }
        if (this.context.programUUID) {
            params['programUUID'] = this.context.programUUID;
        }
        return this.http.get<TreeNode[]>(url, {
            params,
            observe: 'response'
        }).pipe(map((res: any) => res.body.map((item) => this.toTreeNode(item, parentKey))));
    }

    create(folderName: string, parentId: string): Observable<HttpResponse<number>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/study-folders`;
        const params = {
            folderName,
            parentId
        }
        return this.http.post<HttpResponse<number>>(url, { observe: 'response' }, {params});
    }

    rename(newFolderName: string, folderId: string): Observable<HttpResponse<number>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/study-folders/${folderId}`;
        const params = {
            newFolderName
        };
        return this.http.put<HttpResponse<number>>(url, { observe: 'response' }, {params});
    }

    delete(folderId: string): Observable<HttpResponse<void>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/study-folders/${folderId}`;
        const params = {};
        return this.http.delete<void>(url, { observe: 'response', params });
    }

    move(source: string, target: string): Observable<TreeNode> {
        const url =  SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/study-folders/${source}/move`;
        const params = {
            newParentId: target
        };

        return this.http.put<HttpResponse<TreeNode>>(url, { observe: 'response' }, {params})
            .pipe(map((res: HttpResponse<TreeNode>) => this.toTreeNode(res, target)));
    }

    init() {
        return this.expand('');
    }

    persist(folders: string[]) {
        // TODO
        return Observable.empty();
    }

    private toTreeNode(item: any, parentKey: any): TreeNode {
        return <TreeNode>({
            name: item.title,
            key: item.key,
            parentId: parentKey,
            owner: item.owner,
            description: item.description,
            type: item.type,
            noOfEntries: item.noOfEntries,
            numOfChildren: item.numOfChildren,
            isFolder: item.isFolder,
        });
    }
}
