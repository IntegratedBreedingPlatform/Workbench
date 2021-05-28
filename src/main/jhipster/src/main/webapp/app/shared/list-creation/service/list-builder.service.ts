export abstract class ListBuilderService {

    abstract openSaveModal(param: any): Promise<any>;

    abstract getIdColumnName(): string;
}
