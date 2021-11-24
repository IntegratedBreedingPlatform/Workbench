import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmListCloneComponent } from '../../shared/list-creation/germplasm-list-clone.component';

@Component({
    selector: 'jhi-germplasm-list-creation-popup',
    template: '',
})
export class GermplasmListClonePopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmListCloneComponent as Component);
    }

}
