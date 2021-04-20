import { Component, OnInit } from '@angular/core';
import { ProgramService } from '../../shared/program/service/program.service';
import { Program } from '../../shared/program/model/program';
import { HttpResponse } from '@angular/common/http';
import { finalize, map } from 'rxjs/operators';
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

    programsById: {[key: string]: Program};
    // bound to dropdown
    programModel: string;
    programChanged = new Subject<string>();
    // after debounce time
    programSelected: string;
    programDropdownOptions: any;

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
            .switchMap(() => {
                this.page = 1;
                this.loadPrograms(this.page);
                return of('');
            }).subscribe();

        this.programChanged
            .debounceTime(500)
            .switchMap(() => {
                this.programSelected = this.programModel;
                this.displayProgramInfo();
                return of('');
            }).subscribe();

        /*
        if (!this.helpLink || !this.helpLink.length) {
            this.helpService.getHelpLink(HELP_DASHBOARD).toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {});
        }
         */

        this.programDropdownOptions = {
            ajax: {
                delay: 500,
                transport: function(params, success, failure) {
                    this.loadPrograms(params.data.page, params.data.term).subscribe((res) => success(res.body), failure);
                }.bind(this),
                processResults: function(programs: Program[], params) {
                    params.page = params.page || 1;
                    return {
                        results: programs.map((program) => {
                            return {
                                id: program.uniqueID,
                                text: program.name
                            };
                        }),
                        pagination: {
                            more: (params.page * this.pageSize) < this.itemCount
                        }
                    }
                }.bind(this)
            }
        };
    }

    private loadPrograms(page: number, query = '') {
        this.isLoading = true;
        const programsObservable = this.programService.getPrograms(this.cropName, query, {
            page,
            size: this.pageSize
        }).pipe(map((res) => {
            this.itemCount = res.headers.get('X-Total-Count');
            return res;
        })).pipe(
            finalize(() => this.isLoading = false)
        );
        programsObservable.subscribe((resp: HttpResponse<Program[]>) => {
            this.programsById = resp.body.reduce((prev, curr) => {
                prev[curr.uniqueID] = curr;
                return prev;
            }, {});
        });
        return programsObservable;
    }

    onOpenProgram() {
        window.parent.postMessage({ programSelected: this.programsById[this.programSelected] }, '*');
    }

    displayProgramInfo(): any {
        this.router.navigate(['my-studies'], {
            relativeTo: this.route,
            queryParams: {
                cropName: this.cropName,
                programUUID: this.programSelected
            }
        })
    }

    /*
     * FIXME
     *  - not possible because of dropdown pagination?
     *  - preselected crop -> easier -> no pagination
     */
    isPreSelected(program: Program) {
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
