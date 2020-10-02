import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ViewEncapsulation } from '@angular/core';


@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-germplasm-tab',
    templateUrl: './germplasm-tab.component.html',
    // Use Bootstrap 4 css file for this component to match with ng-bootstrap widgets.
    // Updating the bootstrap version globally will break other modules (sample manager, lot creation, etc)
    styleUrls: ['../../../../../node_modules/bootstrap/dist/css/bootstrap.css']
})
export class GermplasmTabComponent implements OnInit {

    constructor(private paramContext: ParamContext) {
        this.paramContext.readParams();
    }

    ngOnInit() {
    }

}