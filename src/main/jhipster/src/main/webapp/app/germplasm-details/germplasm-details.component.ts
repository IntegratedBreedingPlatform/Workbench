import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../shared/service/param.context';
import { ActivatedRoute } from '@angular/router';
import { GermplasmDetailsContext } from './germplasm-details.context';
import { DomSanitizer } from '@angular/platform-browser';
import { GermplasmDetailsUrlService } from '../shared/germplasm/service/germplasm-details.url.service';
import { FileService } from '../shared/file/service/file.service';
import { VIEW_PEDIGREE_INFORMATION_PERMISSION } from '../shared/auth/permissions';

@Component({
    selector: 'jhi-germplasm-details',
    templateUrl: './germplasm-details.component.html'
})
export class GermplasmDetailsComponent implements OnInit {

    safeUrl: any;
    isFileStorageConfigured: boolean;
    VIEW_PEDIGREE_INFORMATION_PERMISSION = VIEW_PEDIGREE_INFORMATION_PERMISSION;

    constructor(private paramContext: ParamContext, public germplasmDetailsContext: GermplasmDetailsContext,
                private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private fileService: FileService) {
    }

    ngOnInit(): void {
        const gid = this.route.snapshot.paramMap.get('gid');
        this.germplasmDetailsContext.gid = Number(gid);
        this.paramContext.readParams();

        // Link to open Germplasm Details page to a new tab.
        this.safeUrl = this.germplasmDetailsUrlService.getUrl(this.germplasmDetailsContext.gid);

        // Only show 'Open to a new tab' button if the page is shown inside a modal window.
        this.germplasmDetailsContext.isModal = this.route.snapshot.queryParamMap.has('modal');

        this.fileService.isFileStorageConfigured().then((isFileStorageConfigured) => this.isFileStorageConfigured = isFileStorageConfigured);
    }

}
