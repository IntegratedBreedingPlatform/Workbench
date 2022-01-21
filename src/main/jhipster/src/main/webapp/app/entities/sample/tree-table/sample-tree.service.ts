import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TreeNode } from './tree-node.model';
import { map } from 'rxjs/operators';
import { TreeService } from '../../../shared/tree/tree.service';
import { ParamContext } from '../../../shared/service/param.context';
import 'rxjs-compat/add/observable/empty';

@Injectable()
export class SampleTreeService implements TreeService {

    private resourceUrl;
    private crop;
    private programUUID;

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    private setCropAndProgram(crop: string, programUUID: string) {
        this.crop = crop;
        this.programUUID = programUUID;
        this.resourceUrl = `/bmsapi/crops/${crop}/programs/${programUUID}/sample-list-folders/`;
    }

    move(source: string, target: string): Observable<any> {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);

        // FIXME IBP-5413
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

    expand(parentKey: string, req?: any): Observable<TreeNode[]> {
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
            params
        }).pipe(map((res: TreeNode[]) => {
            return res.map((treeNode) =>  {
                const copy: TreeNode = Object.assign({}, treeNode);
                copy.parentId = parentKey;
                copy.id = treeNode.key;
                copy.name = treeNode.title;
                return copy;
            });
        }));
    }

    init() {
        return this.expand('');
    }

    persist(folders: string[]) {
        // TODO
        return Observable.empty();
    }
}
