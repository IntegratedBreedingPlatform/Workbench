import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ActivatedRoute } from '@angular/router';
import { GermplasmDetailsContext } from './germplasm-details.context';

@Component({
    selector: 'jhi-germplasm-details',
    templateUrl: './germplasm-details.component.html'
})
export class GermplasmDetailsComponent implements OnInit {

    constructor(private paramContext: ParamContext, private germplasmDetailsContext: GermplasmDetailsContext,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        const gid = this.route.snapshot.paramMap.get('gid');
        this.germplasmDetailsContext.gid = Number(gid);
        this.paramContext.readParams();
    }

}
