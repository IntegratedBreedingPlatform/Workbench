import { Component, OnInit } from '@angular/core';
import { GermplasmListImportComponent, HEADERS } from './germplasm-list-import.component';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmListImportContext } from './germplasm-list-import.context';
import { finalize } from 'rxjs/internal/operators/finalize';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { toUpper } from '../../shared/util/to-upper';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmManagerContext } from '../../germplasm-manager/germplasm-manager.context';
import { Router } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';
import { GermplasmListImportMultiMatchesComponent } from './germplasm-list-import-multi-matches.component';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { GermplasmListImportManualMatchesComponent } from './germplasm-list-import-manual-matches.component';
import { GermplasmListCreationComponent } from '../../shared/list-creation/germplasm-list-creation.component';
import { GermplasmListEntry } from '../../shared/list-creation/model/germplasm-list';
import { ListModel } from '../../shared/list-builder/model/list.model';
import { GermplasmListVariableMatchesComponent } from './germplasm-list-variable-matches.component';
import { exportDataJsonToExcel } from '../../shared/util/file-utils';
import { ColumnFilterComponent, FilterType } from '../../shared/column-filter/column-filter.component';
import { Select2OptionData } from 'ng-select2';
import { NameTypeService } from '../../shared/name-type/service/name-type.service';
import { NameTypeDetails } from '../../shared/germplasm/model/name-type.model';
import { ColumnFilterTransitionEventModel } from '../../shared/column-filter/column-filter-transition-event.model';
import { Subscription } from 'rxjs';
import { GermplasmMatchRequest } from '../../entities/germplasm/germplasm-match-request.model';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';

@Component({
    selector: 'jhi-germplasm-list-import-review',
    templateUrl: './germplasm-list-import-review.component.html'
})
export class GermplasmListImportReviewComponent implements OnInit {

    static readonly COLUMN_FILTER_EVENT_NAME = 'matchesFiltersChanged';

    COLUMN_FILTER_EVENT_NAME = GermplasmListImportReviewComponent.COLUMN_FILTER_EVENT_NAME;

    eventSubscriber: Subscription;

    HEADERS = HEADERS;
    page = 0;
    pageSize = 10;

    isLoading: boolean;
    isSaving: boolean;

    skipMultipleMatches: boolean;
    skipDataWithoutMatches: boolean;

    // show matches html section
    showMatchesOption = SHOW_MATCHES_OPTIONS.ALL;
    SHOW_MATCHES_OPTIONS = SHOW_MATCHES_OPTIONS;

    // rows from file/data that matches
    dataSingleMatches: any[] = [];
    dataMultipleMatches: any[] = [];

    // rows from file/data that doesn't match
    dataWithOutMatches: any[] = [];

    // table displayed in html
    rows: any[] = [];

    // matches from db
    matches: GermplasmDto[];
    matchesByGUID: { [key: string]: GermplasmDto; } = {};
    matchesByGid: { [key: string]: GermplasmDto; } = {};
    matchesByName: { [key: string]: GermplasmDto[]; } = {};

    selectManualMatchesResult: any = {};
    selectMultipleMatchesResult: any = {};
    variableMatchesResult: any = {};

    germplasmFilters: any;
    request: GermplasmMatchRequest = new GermplasmMatchRequest();

    constructor(
        private translateService: TranslateService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private paramContext: ParamContext,
        private popupService: PopupService,
        private alertService: AlertService,
        private germplasmService: GermplasmService,
        private nameTypeService: NameTypeService,
        private breedingMethodService: BreedingMethodService,
        private germplasmManagerContext: GermplasmManagerContext,
        private router: Router,
        private eventManager: JhiEventManager,
        private context: GermplasmListImportContext
    ) {
    }

    ngOnInit(): void {
        this.isLoading = true;
        this.filters = this.getInitialFilters();
        this.registerFiltersChanged();
        this.processContextInputs();
        ColumnFilterComponent.reloadFilters(this.filters, this.request);
        this.loadGermplasmMatchesTable();

    }

    private processContextInputs() {
        this.context.newVariables.forEach((variable) => {
            if (variable.alias) {
                this.variableMatchesResult[toUpper(variable.alias)] = variable.id;
            }
            this.variableMatchesResult[toUpper(variable.name)] = variable.id;

        });
        this.request.gids = [];
        this.request.germplasmUUIDs = [];
        this.request.names = [];
        this.context.data.forEach((row) => {
            if (row[HEADERS['GUID']]) {
                this.request.germplasmUUIDs.push(row[HEADERS['GUID']]);
            }
            if (row[HEADERS['GID']]) {
                this.request.gids.push(row[HEADERS['GID']]);
            }

            if (row[HEADERS['DESIGNATION']]) {
                this.request.names.push(row[HEADERS['DESIGNATION']]);

            }
        });
    }

    private loadGermplasmMatchesTable() {
        this.isLoading = true;
        this.germplasmService.getGermplasmMatches(this.request)
            .pipe(finalize(() => this.isLoading = false))
            .subscribe((matches) => {
                this.matches = matches;
                this.matchesByGUID = {};
                this.matchesByGid = {};
                this.matchesByName = {};

                this.matches.forEach((match) => {
                    if (match.germplasmUUID) {
                        this.matchesByGUID[toUpper(match.germplasmUUID)] = match;
                    }
                    if (match.gid) {
                        this.matchesByGid[match.gid] = match;
                    }
                    if (match.names) {
                        match.names.forEach((name) => {
                            if (!this.matchesByName[toUpper(name.name)]) {
                                this.matchesByName[toUpper(name.name)] = [];
                            }
                            this.matchesByName[toUpper(name.name)].push(match);
                        });
                    }

                });

                this.dataSingleMatches = [];
                this.dataMultipleMatches = [];
                this.dataWithOutMatches = [];
                this.context.data.forEach((row, index) => {
                    const guidMatch = this.matchesByGUID[toUpper(row[HEADERS.GUID])];
                    const gidMatch = this.matchesByGid[row[HEADERS.GID]];
                    const nameMatches = this.matchesByName[toUpper(row[HEADERS.DESIGNATION])];
                    row[HEADERS.ROW_NUMBER] = ++index;
                    row[HEADERS.GID_MATCHES] = [];
                    if (guidMatch) {
                        this.dataSingleMatches.push(row);
                        row[HEADERS.GID_MATCHES].push(guidMatch);
                    } else if (gidMatch) {
                        this.dataSingleMatches.push(row);
                        row[HEADERS.GID_MATCHES].push(gidMatch);
                    } else if (nameMatches) {
                        if (nameMatches.length > 1) {
                            this.dataMultipleMatches.push(row);
                            row[HEADERS.GID_MATCHES] = nameMatches;

                        } else {
                            this.dataSingleMatches.push(row);
                            row[HEADERS.GID_MATCHES] = nameMatches;
                        }
                    } else {
                        this.dataWithOutMatches.push(row);
                    }
                });
                this.rows = [...this.dataSingleMatches, ...this.dataMultipleMatches, ...this.dataWithOutMatches];
            });
    }

    get filters() {
        return this.germplasmFilters;
    }

    set filters(filters) {
        this.germplasmFilters = filters;
    }

    private registerFiltersChanged() {
        this.eventSubscriber = this.eventManager.subscribe(GermplasmListImportReviewComponent.COLUMN_FILTER_EVENT_NAME, (event: ColumnFilterTransitionEventModel) => {
            this.loadGermplasmMatchesTable();
        });
    }

    dismiss() {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component, { backdrop: 'static' });
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import.cancel.confirm');
        confirmModalRef.result.then(() => this.modal.dismiss());
    }

    back() {
        this.modal.close();
        const variables = [...this.context.newVariables, ...this.context.unknownVariableNames]

        if (variables && variables.length) {
            const modalRef = this.modalService.open(GermplasmListVariableMatchesComponent as Component, { size: 'lg', backdrop: 'static' });
            modalRef.result.then((variableMatchesResult) => {
                if (variableMatchesResult) {
                    this.modalService.open(GermplasmListImportReviewComponent as Component, { size: 'lg', backdrop: 'static' });
                } else {
                    this.modalService.open(GermplasmListImportComponent as Component, { size: 'lg', backdrop: 'static' });
                }
            });
        } else {
            this.modalService.open(GermplasmListImportComponent as Component, { size: 'lg', backdrop: 'static' });
        }
    }

    keys(obj) {
        return Object.keys(obj);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    toUpper(param) {
        return toUpper(param);
    }

    onShowMatchesOptionChange() {
        switch (this.showMatchesOption) {
            case SHOW_MATCHES_OPTIONS.ALL:
                this.rows = [...this.dataSingleMatches, ...this.dataMultipleMatches, ...this.dataWithOutMatches];
                break;
            case SHOW_MATCHES_OPTIONS.WITH_A_SINGLE_MATCH:
                this.rows = this.dataSingleMatches;
                break;
            case SHOW_MATCHES_OPTIONS.WITH_MULTIPLE_MATCHES:
                this.rows = this.dataMultipleMatches;
                break;
            case SHOW_MATCHES_OPTIONS.WITHOUT_MATCHES:
                this.rows = this.dataWithOutMatches;
                break;
        }
    }

    async save() {
        this.selectManualMatchesResult = {};
        this.selectMultipleMatchesResult = {};

        try {

            let doContinue = await this.processMultiMatches();
            if (!doContinue) {
                return;
            }

            doContinue = await this.processManualMatches();
            if (!doContinue) {
                return;
            }

            doContinue = await this.showSummaryConfirmation();
            if (!doContinue) {
                return;
            }

            // Proceed with save
            const newGermplasmList = [];
            let index = 0;
            this.context.data.forEach((row) => {
                const germplasmList = {};
                const singleMatch = this.dataSingleMatches.find((matchEntry) =>
                    Boolean(matchEntry[HEADERS.ROW_NUMBER] === row[HEADERS.ROW_NUMBER])
                );

                Object.keys(this.variableMatchesResult).forEach((variableName) => {
                    germplasmList[variableName] = row[variableName];
                });

                // Single Match
                if (singleMatch) {
                    germplasmList[HEADERS.ROW_NUMBER] = ++index;
                    germplasmList[HEADERS.GID] = singleMatch[HEADERS.GID_MATCHES][0].gid;
                    newGermplasmList.push(germplasmList);
                // Manual Match
                } else if (Object.keys(this.selectManualMatchesResult).length > 0 && this.selectManualMatchesResult[row[HEADERS.ROW_NUMBER]]) {
                    if (this.selectManualMatchesResult[row[HEADERS.ROW_NUMBER]]) {
                        germplasmList[HEADERS.ROW_NUMBER] = ++index;
                        germplasmList[HEADERS.GID] = this.selectManualMatchesResult[row[HEADERS.ROW_NUMBER]];
                        newGermplasmList.push(germplasmList);
                    }
                // Multi Match
                } else if (Object.keys(this.selectMultipleMatchesResult).length > 0 && this.selectMultipleMatchesResult[row[HEADERS.ROW_NUMBER]]) {
                    germplasmList[HEADERS.ROW_NUMBER] = ++index;
                    germplasmList[HEADERS.GID] = this.selectMultipleMatchesResult[row[HEADERS.ROW_NUMBER]];
                    newGermplasmList.push(germplasmList);
                }
            });

            this.modal.close();
            const model = new ListModel();
            const germplasmListCreationModalRef = this.modalService.open(GermplasmListCreationComponent as Component, { size: 'lg', backdrop: 'static' });
            germplasmListCreationModalRef.componentInstance.entries = newGermplasmList.map((row) => {
                const entry = new GermplasmListEntry();
                entry.gid = row[HEADERS.GID];
                entry.entryNo = Number(row[HEADERS.ROW_NUMBER]);
                entry.data = Object.keys(this.variableMatchesResult).reduce((map, variableName) => {
                    if (row[variableName]) {
                        map[this.variableMatchesResult[variableName]] = { value: row[variableName] };
                    }
                    return map;
                }, {});
                return entry
            });

            germplasmListCreationModalRef.componentInstance.model = model;
            germplasmListCreationModalRef.result.then(() => {
                    this.eventManager.broadcast({ name: 'listNameFilter', content: model.listName });
            });

        } catch (error) {
            this.onError(error);
        }
        this.isSaving = false;
    }

    private async showSummaryConfirmation() {
        const countMultiMatches = this.context.data.filter((row) => this.selectMultipleMatchesResult[row[HEADERS.ROW_NUMBER]]).length,
            countManualMatches = this.context.data.filter((row) => this.selectManualMatchesResult[row[HEADERS.ROW_NUMBER]]).length,
            countSingleMatches = this.dataSingleMatches.length,
            countOmittedMatches = this.context.data.length - countMultiMatches - countManualMatches - countSingleMatches,
            messages = [];

        if (countSingleMatches) {
            messages.push(this.translateService.instant('germplasm-list.import.review.summary.single.matches', { param: countSingleMatches }));
        }

        if (countMultiMatches) {
            messages.push(this.translateService.instant('germplasm-list.import.review.summary.multiples.matches', { param: countMultiMatches }));
        }
        if (countManualMatches) {
            messages.push(this.translateService.instant('germplasm-list.import.review.summary.manual.matches', { param: countManualMatches }));
        }
        if (countOmittedMatches) {
            messages.push(this.translateService.instant('germplasm-list.import.review.summary.omitted.matches', { param: countOmittedMatches }));
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component,
            { windowClass: 'modal-medium', backdrop: 'static' });
        confirmModalRef.componentInstance.title = this.translateService.instant('germplasm-list.import.review.summary.title');
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-list.import.review.summary.message', {
            param: messages.map((item) => '<li>' + item + '</li>').join('')
        });
        try {
            await confirmModalRef.result;
        } catch (rejected) {
            return false;
        }
        return true;

    }

    private async processMultiMatches(): Promise<boolean> {
        this.selectMultipleMatchesResult = {};
        if (Object.keys(this.dataMultipleMatches).length > 0 && !this.skipMultipleMatches) {
            const selectMatchesModalRef = this.modalService.open(GermplasmListImportMultiMatchesComponent as Component,
                { size: 'lg', backdrop: 'static' });
            selectMatchesModalRef.componentInstance.unassignedMatches = this.dataMultipleMatches;
            selectMatchesModalRef.componentInstance.selectMatchesResult = this.selectMultipleMatchesResult;

            try {
                await selectMatchesModalRef.result;
            } catch (rejected) {
                return false;
            }
        }
        return true;
    }

    private async processManualMatches(): Promise<boolean> {
        this.selectManualMatchesResult = {};
        if (Object.keys(this.dataWithOutMatches).length > 0 && !this.skipDataWithoutMatches) {
            const selectManualMatchesModalRef = this.modalService.open(GermplasmListImportManualMatchesComponent as Component,
                { windowClass: 'modal-extra-large', backdrop: 'static' });
            selectManualMatchesModalRef.componentInstance.rows = this.dataWithOutMatches.map((row) => Object.assign({}, row));
            selectManualMatchesModalRef.componentInstance.selectMatchesResult = this.selectManualMatchesResult;

            try {
                await selectManualMatchesModalRef.result;
            } catch (rejected) {
                return false;
            }
        }
        return true;
    }

    private onError(response: HttpErrorResponse | any) {
        if (!response.error) {
            this.alertService.error('error.general.client');
            console.error(response);
            return;
        }
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    exportTableToExcel($event) {
        $event.preventDefault();
        const dataTable = [];
        this.rows.forEach((row) => {
            const data = {};
            data[HEADERS.GID] = row[HEADERS.GID];
            data[HEADERS.GUID] = row[HEADERS.GUID];
            data[HEADERS.DESIGNATION] = row[HEADERS.DESIGNATION];
            this.context.newVariables.forEach((variable) => {
                if (row[toUpper(variable.alias)]) {
                    data[variable.alias] = row[toUpper(variable.alias)]
                } else {
                    data[variable.name] = row[toUpper(variable.name)]
                }
            });
            data[HEADERS.GID_MATCHES] = row['GID MATCHES'].map((germplasm) => germplasm.gid).join(',');
            dataTable.push(data);
        });

        exportDataJsonToExcel('reviewImportGermplasmList.xlsx', 'Observations', dataTable);
    }

    private getInitialFilters() {
        return [
            {
                key: 'nameTypes', name: 'Name Type', type: FilterType.DROPDOWN, values: this.getNameTypeOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            {
                key: 'methods', name: 'Breeding Method', type: FilterType.DROPDOWN, values: this.getBreedingMethodsOptions(), multipleSelect: true,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            {
                key: 'locationName', name: 'Location Name', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            },
            {
                key: 'locationAbbreviation', name: 'Location Abbreviation', placeholder: 'Search Text', type: FilterType.TEXT_WITH_MATCH_OPTIONS,
                matchType: MatchType.STARTSWITH
            }
        ];
    }

    private getNameTypeOptions(): Promise<Select2OptionData[]> {
        return this.nameTypeService.searchNameTypes({}, {
            page: 0,
            size: 100
        }).toPromise().then((res: HttpResponse<NameTypeDetails[]>) => {
            return res.body.map((type: NameTypeDetails) => {
                return { id: type.code,
                    text: type.name + ' (' + type.code + ')'
                }
            });
        });
    }

    private getBreedingMethodsOptions(): Promise<Select2OptionData[]> {
        return this.breedingMethodService.searchBreedingMethods({}, false, {
            page: 0,
            size: 300
        }).toPromise().then((res: HttpResponse<BreedingMethod[]>) => {
            return res.body.map((method: BreedingMethod) => {
                return { id: method.code,
                    text: method.name + ' (' + method.code + ')'
                }
            });
        });
    }
}

enum SHOW_MATCHES_OPTIONS {
    ALL = 'ALL',
    WITH_A_SINGLE_MATCH = 'WITH_A_SINGLE_MATCH',
    WITH_MULTIPLE_MATCHES = 'WITH_MULTIPLE_MATCHES',
    WITHOUT_MATCHES = 'WITHOUT_MATCHES'
}
