import { Component, OnInit } from '@angular/core';
import { ProgramService } from '../../shared/program/service/program.service';
import { Program } from '../../shared/program/model/program';
import { finalize, map } from 'rxjs/operators';
import { Principal } from '../../shared';
import { INSTITUTE_LOGO_PATH } from '../../app.constants';
import { HelpService } from '../../shared/service/help.service';
import { JhiLanguageService } from 'ng-jhipster';
import { CropService } from '../../shared/crop/service/crop.service';
import { Subject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramContext } from './program.context';
import { NavbarMessageEvent } from '../../shared/model/navbar-message.event';
import { SEARCH_GERMPLASM_LISTS_PERMISSION } from '../../shared/auth/permissions';
import { Select2OptionData } from 'ng-select2';
import { ProgramUsageService } from '../../shared/service/program-usage.service';
import { ParamContext } from '../../shared/service/param.context';

@Component({
    selector: 'jhi-program',
    templateUrl: './program.component.html',
    styleUrls: [
        './program.scss'
    ]
})
export class ProgramComponent implements OnInit {

    MANAGE_STUDIES_VIEW_PERMISSIONS = ['ADMIN', 'STUDIES', 'MANAGE_STUDIES', 'VIEW_STUDIES'];
    SEARCH_GERMPLASM_LISTS_PERMISSION = SEARCH_GERMPLASM_LISTS_PERMISSION;
    PERMISSIONS = [
        ...this.MANAGE_STUDIES_VIEW_PERMISSIONS,
        ...SEARCH_GERMPLASM_LISTS_PERMISSION
    ];

    instituteLogoPath = '/ibpworkbench/controller/' + INSTITUTE_LOGO_PATH;

    user?: any;

    crops: string[];
    cropName: string;
    cropChanged = new Subject<string>();

    programsById: { [key: string]: Program } = {};
    programModel: string;
    programChanged = new Subject<string>();
    programDropdownOptions: any;

    itemCount: any;
    pageSize = 20;

    isLoading = false;
    helpLink: string;

    initialData: Select2OptionData[];

    constructor(
        private programService: ProgramService,
        private cropService: CropService,
        private principal: Principal,
        private helpService: HelpService,
        private languageService: JhiLanguageService,
        private router: Router,
        private route: ActivatedRoute,
        private paramContext: ParamContext,
        public context: ProgramContext,
        public programUsageService: ProgramUsageService,
    ) {
    }

    async ngOnInit() {
        /*
         * specially needed for history.back that comes with programUUID params and no crop,
         * thus causing an error in identity()
         */
        this.paramContext.clear();
        const identity = await this.principal.identity();
        this.user = identity;

        this.crops = await this.cropService.getCrops().toPromise();

        this.programUsageService.getLastestSelectedProgram(this.user.id).toPromise().then((response) => {
            const userProgramSelected = response.body;
            if (userProgramSelected) {
                this.getPrograms(1, undefined).subscribe((res) => {
                    if (this.programsById[userProgramSelected.uniqueID]) {
                        this.programModel = userProgramSelected.uniqueID;
                        this.context.program = userProgramSelected;
                        this.initialData = [{ id: userProgramSelected.uniqueID, text: userProgramSelected.name }]
                    }
                }, (err) => {
                });
            }
        });

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
                    params.data.page = params.data.page || 1;
                    this.getPrograms(params.data.page, params.data.term).subscribe((res) => success(res.body), failure);
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

    private getPrograms(page: number, query = '') {
        this.isLoading = true;
        return this.programService.getPrograms(this.cropName, query, {
            page: page - 1,
            size: this.pageSize
        }).pipe(map((res) => {
            this.itemCount = res.headers.get('X-Total-Count');
            Object.assign(this.programsById, res.body.reduce((prev, curr) => {
                prev[curr.uniqueID] = curr;
                return prev;
            }, {}));
            return res;
        })).pipe(
            finalize(() => this.isLoading = false)
        );
    }

    onOpenProgram() {
        const message: NavbarMessageEvent = { programSelected: this.context.program };
        window.parent.postMessage(message, '*');
    }

    async displayProgramInfo() {
        const program = this.context.program;
        // force authority retrieval for specific program
        await this.principal.identity(true, program.crop, program.uniqueID)
        if (this.principal.hasAnyAuthorityDirect(this.MANAGE_STUDIES_VIEW_PERMISSIONS)) {
            this.router.navigate(['my-studies'], {
                relativeTo: this.route,
                queryParams: {
                    programUUID: this.programModel
                }
            })
        } else if (this.principal.hasAnyAuthorityDirect(SEARCH_GERMPLASM_LISTS_PERMISSION)) {
            this.router.navigate(['my-lists'], {
                relativeTo: this.route,
                queryParams: {
                    programUUID: this.programModel
                }
            })
        }
    }

    onCropChange() {
        // workaround to trigger select2 ajax reload
        if (this.cropName) {
            this.programModel = null;
        }
    }

    onProgramChange(programSelected: any): void {
        this.context.program = this.programsById[programSelected];
        this.displayProgramInfo();
    }
}
