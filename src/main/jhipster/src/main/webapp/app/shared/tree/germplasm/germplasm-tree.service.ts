import { TreeService } from '../tree.service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { TreeNode } from '../index';
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';

@Injectable()
export class GermplasmTreeService extends TreeService {

    private readonly resourceUrl: string;

    constructor(private http: HttpClient,
                private paramContext: ParamContext) {
        super();
        this.resourceUrl = `/bmsapi/crops/${this.paramContext.cropName}`;
    }

    expand(parentKey: any): any {
        const url = `${this.resourceUrl}/germplasm-lists/tree`;
        const params = {
            onlyFolders: '0'
        };
        if (parentKey) {
            params['parentFolderId'] = parentKey;
        }
        if (this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        return this.http.get<TreeNode[]>(url, {
            params,
            observe: 'response'
        }).pipe(map((res: any) => res.body.map((item) => this.toTreeNode(item, parentKey))));
    }

    init(): any {
        const url = `${this.resourceUrl}/germplasm-lists/tree-state`;
        const params = {};
        if (this.paramContext.loggedInUserId) {
            params['userId'] = this.paramContext.loggedInUserId;
        }
        if (this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        return this.http.get<TreeNode[]>(url, {
            params,
            observe: 'response'
        }).pipe(map((res: any) => res.body.map((item) => this.toTreeNode(item, null))));
    }

    move(source: string, target: string, isParentCropList: boolean): Observable<HttpResponse<number>> {
        const url = `${this.resourceUrl}/germplasm-list-folders/${source}/move`;
        const params = {
            newParentId: target
        };
        /*
         * TODO Review backend. program should be sent always to resolve permissions
         *  but doing so while moving to folders inside crop section throws an error.
         *  May/may not relates to IBP-5285
         */
        if (!isParentCropList && this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        return this.http.put<HttpResponse<number>>(url, { observe: 'response' }, {params});
    }

    delete(folderId: string): Observable<HttpResponse<void>> {
        const url = `${this.resourceUrl}/germplasm-list-folders/${folderId}`;
        const params = {};
        if (this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        return this.http.delete<void>(url, { observe: 'response', params });
    }

    create(folderName: string, parentId: string, isParentCropList: boolean): Observable<HttpResponse<number>> {
        const url = `${this.resourceUrl}/germplasm-list-folders`;
        const params = {
            folderName,
            parentId
        };
        if (!isParentCropList && this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        return this.http.post<HttpResponse<number>>(url, { observe: 'response' }, {params});
    }

    rename(newFolderName: string, folderId: string): Observable<HttpResponse<number>> {
        const url = `${this.resourceUrl}/germplasm-list-folders/${folderId}`;
        const params = {
            newFolderName
        };
        if (this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        return this.http.put<HttpResponse<number>>(url, { observe: 'response' }, {params});
    }

    persist(foldersParam: string[]): any {
        const url = `${this.resourceUrl}/germplasm-lists/tree-state`;
        const params = {};
        if (this.paramContext.programUUID) {
            params['programUUID'] = this.paramContext.programUUID;
        }
        const body = {
            userId: this.paramContext.loggedInUserId,
            folders: foldersParam
        };
        return this.http.post(url, body, {params});
    }

    private toTreeNode(item: any, parentKey: any): TreeNode {
        const treeNode = <TreeNode>({
            name: item.title,
            key: item.key,
            parentId: parentKey,
            owner: item.owner,
            ownerId: item.ownerId,
            description: item.description,
            type: item.type,
            noOfEntries: item.noOfEntries,
            numOfChildren: item.numOfChildren,
            isFolder: item.isFolder,
            isLocked: item.isLocked,
            children: []
        });
        if (item.children) {
            item.children.forEach((node) => {
               treeNode.children.push(this.toTreeNode(node, treeNode.key));
            });
        }
        return treeNode;
    }
}
