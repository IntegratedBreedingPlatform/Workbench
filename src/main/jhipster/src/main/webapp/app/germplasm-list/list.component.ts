import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { GermplasmList } from './germplasm-list.model';

@Component({
    selector: 'jhi-list',
    templateUrl: './list.component.html'
})
export class ListComponent implements OnInit {

    @Input()
    germplasmList: GermplasmList;

    constructor() {

    }

    ngOnInit(): void {
    }

}
