import { Component, Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ListBuilderService } from './list-builder.service';
import { GermplasmListCreationComponent } from '../germplasm-list-creation.component';
import { ParamContext } from '../../service/param.context';
import { GermplasmListEntry } from '../../model/germplasm-list';
import { HttpClient } from '@angular/common/http';
import { ColumnLabels } from '../../../germplasm-manager/germplasm-search.component';

@Injectable()
export class GermplasmListBuilderService implements ListBuilderService {

    constructor(private modalService: NgbModal,
                private context: ParamContext,
                private http: HttpClient) {
    }

    openSaveModal(param: any): Promise<any> {
        const modalRef = this.modalService.open(GermplasmListCreationComponent as Component, {
                size: 'lg', backdrop: 'static' });
        modalRef.componentInstance.entries = param.map((row, i) => {
            return <GermplasmListEntry>({
                gid: row[ColumnLabels.GID],
                entryNo: i + 1
            });

        });
        return modalRef.result;
    }

    getIdColumnName(): string {
        return ColumnLabels.GID;
    }

}
