import { ListMetadataComponent } from '../shared/list-creation/list-metadata.component';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ListService } from '../shared/list-creation/service/list.service';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';

@Component({
    selector: 'jhi-germplasm-list-metadata',
    templateUrl: '../shared/list-creation/list-metadata.component.html',
    providers: [
        { provide: ListService, useClass: GermplasmListService },
    ],
})
export class GermplasmListMetadataComponent extends ListMetadataComponent {

}
