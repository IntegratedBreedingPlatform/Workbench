import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';


@Component({
    selector: 'jhi-germplasm-tab',
    templateUrl: './germplasm-tab.component.html',
    styles: []
})
export class GermplasmTabComponent implements OnInit {

    constructor(private paramContext: ParamContext) {
        this.paramContext.readParams();
    }

    ngOnInit() {
    }

}