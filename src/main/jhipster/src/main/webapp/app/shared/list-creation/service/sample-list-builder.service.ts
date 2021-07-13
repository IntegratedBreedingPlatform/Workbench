import { Component, Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ListBuilderService } from './list-builder.service';
import { SampleListEntry } from '../../model/sample-list';
import { SampleListCreationComponent } from '../sample-list-creation.component';
import { ParamContext } from '../../service/param.context';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class SampleListBuilderService implements ListBuilderService {
    constructor(private modalService: NgbModal,
                private context: ParamContext,
                private http: HttpClient) {
    }

    openSaveModal(param: any): Promise<any> {
        const modalRef = this.modalService.open(SampleListCreationComponent as Component,
            { size: 'lg', backdrop: 'static' });
        modalRef.componentInstance.entries = param.map((row, i) => {
            const entry = new SampleListEntry();
            entry.sampleId = row['SAMPLE_ID'];
            // TODO IBP-4375 list_data / set new entry_no
            entry.sampleNumber = i + 1;
            return entry;
        });
        return modalRef.result;
    }

    getIdColumnName(): string {
        return 'SAMPLE_ID';
    }

}
