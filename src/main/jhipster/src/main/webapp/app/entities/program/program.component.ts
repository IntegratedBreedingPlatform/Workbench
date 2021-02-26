import { Component, OnInit } from '@angular/core';
import { ProgramService } from '../../shared/program/service/program.service';
import { Program } from '../../shared/program/model/program';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { Principal } from '../../shared';
import { HELP_DASHBOARD } from '../../app.constants';
import { HelpService } from '../../shared/service/help.service';

@Component({
    selector: 'jhi-program',
    templateUrl: './program.component.html',
    styleUrls: [
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
    helpLink: string;

    constructor(
        private programService: ProgramService,
        private principal: Principal,
        private helpService: HelpService
    ) {
    }

    async ngOnInit() {
        // We get user last opened project ff programUUID is not present in the local storage
        if (!localStorage['programUUID']) {
            const identity = await this.principal.identity();
            this.user = identity;
        }

        this.loadPage();

        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_DASHBOARD).toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {});
        }
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
