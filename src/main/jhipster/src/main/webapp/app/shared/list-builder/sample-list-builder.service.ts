import { ListBuilderService } from './list-builder.service';
import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Injectable()
export class SampleListBuilderService implements ListBuilderService {

    constructor(private modalService: NgbModal) {
    }

    async save(param: any): Promise<any> {
        // TODO
    }

}
