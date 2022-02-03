export abstract class TreeService {
    abstract expand(id: any): any;
    abstract create(folderName: string, parentId: string): any;
    abstract rename(newFolderName: string, folderId: string): any;
    abstract delete(folderId: string): any;
    abstract move(source: string, target: string): any;
    abstract init(): any;
    abstract persist(folders: string[]): any;
}
