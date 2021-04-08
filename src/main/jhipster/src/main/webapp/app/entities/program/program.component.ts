import { Component, OnInit } from '@angular/core';
import { ProgramService } from '../../shared/program/service/program.service';
import { Program } from '../../shared/program/model/program';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { Principal } from '../../shared';
import { INSTITUTE_LOGO_PATH } from '../../app.constants';
import { HelpService } from '../../shared/service/help.service';
import { JhiLanguageService } from 'ng-jhipster';
import { CropService } from '../../shared/crop/service/crop.service';
import { of, Subject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'jhi-program',
    templateUrl: './program.component.html',
    styleUrls: [
        './program.scss'
    ]
})
export class ProgramComponent implements OnInit {

    instituteLogoPath = '/ibpworkbench/controller/' + INSTITUTE_LOGO_PATH;

    user?: any;

    crops: string[];
    cropName: string;
    cropChanged = new Subject<string>();

    programs: Program[];
    // bound to dropdown
    program: Program;
    programChanged = new Subject<string>();
    // after debounce time
    programSelected: Program;

    itemCount: any;
    pageSize = 20;
    page = 1;

    isLoading = false;
    helpLink: string;

    constructor(
        private programService: ProgramService,
        private cropService: CropService,
        private principal: Principal,
        private helpService: HelpService,
        private languageService: JhiLanguageService,
        private router: Router,
        private route: ActivatedRoute
    ) {
    }

    async ngOnInit() {
        // We get user last opened project ff programUUID is not present in the local storage
        if (!localStorage['programUUID']) {
            const identity = await this.principal.identity();
            this.user = identity;
        }

        this.cropService.getCrops().subscribe((crops) => this.crops = crops);
        this.cropChanged
            .debounceTime(500)
            .switchMap(() =>  {
                this.isLoading = true;
                return this.programService.getPrograms(this.cropName, {
                    page: this.page - 1,
                    size: this.pageSize
                }).pipe(
                    finalize(() => this.isLoading = false)
                )
            })
            .subscribe((resp: HttpResponse<Program[]>) => {
                this.programs = resp.body;
            });

        this.programChanged
            .debounceTime(500)
            .switchMap(() => {
                this.programSelected = this.program;
                this.router.navigate(['my-studies'], {
                    relativeTo: this.route,
                    queryParams: {
                        programUUID: this.program.uniqueID
                    }
                })
                return of('');
            }).subscribe();

        /*
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_DASHBOARD).toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {});
        }
         */
    }

    onProgramSelect() {
        window.parent.postMessage({ programSelected: this.program }, '*');
    }

    isSelected(program: Program) {
        return program.uniqueID ===
            (localStorage['programUUID'] ? localStorage['programUUID'] : this.user.selectedProgramUUID);
    }

    onCropChange() {
        this.cropChanged.next();
    }

    onProgramChange() {
        this.programChanged.next();
    }

}
