import { TreeService } from '../tree.service';
import { HttpClient } from '@angular/common/http';
import { TreeNode } from '../index';
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';

@Injectable()
export class StudyTreeService extends TreeService {

    constructor(private http: HttpClient,
                private paramContext: ParamContext) {
        super();
    }

    expand(parentKey: any): any {
        const url = `/bmsapi/crops/${this.paramContext.cropName}/studies/tree`;
        const params = {};
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

    create(folderName: string, parentId: string, isParentCropList: boolean) {
        // TODO
    }

    rename(newFolderName: string, folderId: string) {
        // TODO
    }

    delete(folderId: string) {
        // TODO
    }

    move(source: string, target: string, isParentCropList: boolean) {
        // TODO
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
