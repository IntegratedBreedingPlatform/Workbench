import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ProgramService } from '../../shared/program/service/program.service';
import { Program } from '../../shared/program/model/program';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { Principal } from '../../shared';

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-program',
    templateUrl: './program.component.html',
    // TODO migrate IBP-4093
    styleUrls: [
        '../../../content/css/global-bs4.scss',
        './program.scss'
    ]
})
export class ProgramComponent implements OnInit {

    user?: any;

    programs: Program[];
    itemCount: any;
    pageSize = 20;
    page = 1;
    isLoading = false;

    constructor(
        private programService: ProgramService,
        private principal: Principal
    ) {
    }

    async ngOnInit() {
        // We get user last opened project ff programUUID is not present in the local storage
        if (!localStorage['programUUID']) {
            const identity = await this.principal.identity();
            this.user = identity;
        }

        this.loadPage();
    }

    onProgramSelect(program: Program) {
        window.parent.postMessage({ programSelected: program }, '*');
    }

    isSelected(program: Program) {
        return program.programUUID ===
            (localStorage['programUUID'] ? localStorage['programUUID'] : this.user.selectedProgramUUID);
    }

    loadPage() {
        this.isLoading = true;
        this.programService.getPrograms({
            page: this.page - 1,
            size: this.pageSize
        }).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((resp: HttpResponse<Program[]>) => {
            this.programs = resp.body;
            this.itemCount = resp.headers.get('X-Total-Count');
        });
    }

}
