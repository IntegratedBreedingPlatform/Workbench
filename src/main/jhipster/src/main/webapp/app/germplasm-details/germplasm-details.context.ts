import { Injectable } from '@angular/core';
import { GermplasmDto } from '../shared/germplasm/model/germplasm.model';

@Injectable()
export class GermplasmDetailsContext {
    gid: number;
    germplasm: GermplasmDto;
    isModal: boolean;

    notifyGermplasmDetailChanges() {
        if (window.parent) {
            // Notify the parent window that germplasm attribute has changed
            window.parent.postMessage('germplasm-details-changed', '*');
        }
    }
}
