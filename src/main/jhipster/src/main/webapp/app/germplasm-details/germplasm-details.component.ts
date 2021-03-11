import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { PopupService } from '../shared/modal/popup.service';
import { ActivatedRoute } from '@angular/router';
import { GermplasmDetailsContext } from './germplasm-details.context';

@Component({
    selector: 'jhi-germplasm-details',
    templateUrl: './germplasm-details.component.html'
})
export class GermplasmDetailsComponent implements OnInit {

    constructor() {
    }

    ngOnInit(): void {
    }

}
