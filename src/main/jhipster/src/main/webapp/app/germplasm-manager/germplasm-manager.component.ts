import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ViewEncapsulation } from '@angular/core';

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-germplasm-manager',
    templateUrl: './germplasm-manager.component.html',
    // Use Bootstrap 4 css file for this component to match with ng-bootstrap widgets.
    // Updating the bootstrap version globally will break other modules (sample manager, lot creation, etc)
    // TODO migrate IBP-4093
    styleUrls: ['../../content/css/global-bs4.scss']
})
export class GermplasmManagerComponent implements OnInit {

    constructor(private paramContext: ParamContext) {
        this.paramContext.readParams();
    }

    ngOnInit() {
    }

}
