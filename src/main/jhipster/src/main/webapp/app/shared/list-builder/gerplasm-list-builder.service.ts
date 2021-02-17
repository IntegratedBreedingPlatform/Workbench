import { ListBuilderService } from './list-builder.service';
import { GermplasmListCreationComponent } from '../../germplasm-manager/germplasm-list/germplasm-list-creation.component';
import { Component, Injectable } from '@angular/core';
import { GermplasmListEntry } from '../model/germplasm-list';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ColumnLabels } from '../../germplasm-manager/germplasm-search.component';

@Injectable()
export class GerplasmListBuilderService implements ListBuilderService {

    constructor(private modalService: NgbModal) {
    }

    async save(param: any): Promise<any> {
        const germplasmListCreationModalRef = this.modalService.open(GermplasmListCreationComponent as Component,
            { size: 'lg', backdrop: 'static' });
        germplasmListCreationModalRef.componentInstance.entries = param.map((row, i) => {
            return <GermplasmListEntry>({
                gid: row[ColumnLabels.GID],
                entryNo: i + 1
            });
        });
        await germplasmListCreationModalRef.result;
    }

}
