export abstract class TreeService {
    abstract expand(id: any): any;
    abstract create(folderName: string, parentId: string, isParentCropList: boolean): any;
    abstract rename(newFolderName: string, folderId: string): any;
    abstract delete(folderId: string): any;
    abstract move(source: string, target: string, isParentCropList: boolean): any;
    abstract init(): any;
    abstract persist(cropFolders: string[], programFolders: string[]): any;
}
