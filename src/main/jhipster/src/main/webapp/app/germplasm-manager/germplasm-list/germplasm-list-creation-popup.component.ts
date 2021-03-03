import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmListCreationComponent } from '../../shared/list-creation/germplasm-list-creation.component';

@Component({
    selector: 'jhi-germplasm-list-creation-popup',
    template: '',
})
export class GermplasmListCreationPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmListCreationComponent as Component);
    }

}
