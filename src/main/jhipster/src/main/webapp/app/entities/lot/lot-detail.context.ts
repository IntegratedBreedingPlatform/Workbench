import { Injectable } from '@angular/core';
import { Lot } from '../../shared/inventory/model/lot.model';

@Injectable()
export class LotDetailContext {
    lotId: number;
    isModal: boolean;

    notifyLotDetailChanges() {
        if (window.parent) {
            // Notify the parent window that lot attribute has changed
            window.parent.postMessage('lot-details-changed', '*');
        }
    }
}
