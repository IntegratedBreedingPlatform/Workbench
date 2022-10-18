import { Component, OnInit } from '@angular/core';
import { NameTypeDetails } from '../../shared/germplasm/model/name-type.model';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { NameTypeService } from '../../shared/name-type/service/name-type.service';
import { SORT_PREDICATE_NONE } from '../../germplasm-manager/germplasm-search-resolve-paging-params';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { AlertService } from '../../shared/alert/alert.service';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { FilterType } from '../../shared/column-filter/column-filter.component';
import { CropSettingsContext } from '../crop-Settings.context';

@Component({
    selector: 'jhi-name-type',
    templateUrl: './name-types-pane.component.html'
})
export class NameTypesPaneComponent implements OnInit {

    itemsPerPage: any = 20;

    ColumnLabels = ColumnLabels;

    nameTypes: NameTypeDetails[];
    page: any;
    predicate: any;
    totalItems: number;
    private previousPage: any;
    reverse: boolean;
    isLoading: boolean;
    eventSubscriber: Subscription;

    nameTypeMetadataFilterRequest: {};

    filters = {
        code: {
            key: 'code',
            type: FilterType.TEXT,
            value: ''
        },
        name: {
            key: 'name',
            type: FilterType.TEXT,
            value: ''
        },
        description: {
            key: 'description',
            type: FilterType.TEXT,
            value: ''
        },
        date: {
            key: 'date',
            type: FilterType.DATE,
            from: null,
            to: null
        },
    }

    constructor(public translateService: TranslateService,
                private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private nameTypeService: NameTypeService,
                private alertService: AlertService,
                private router: Router,
                private modalService: NgbModal,
                private cropSettingsContext: CropSettingsContext,
                private eventManager: JhiEventManager,

    ) {

        this.page = 1;
        this.totalItems = 0;
        this.predicate = ColumnLabels.CODE;
        this.reverse = true;
    }

    ngOnInit(): void {
        this.loadAll();
        this.registerNameTypeChanged();
    }

    loadAll() {
        this.nameTypeService.searchNameTypes({
                code: this.filters.code.value,
                name: this.filters.name.value,
                description: this.filters.description.value,
                nameTypeDateFrom: this.filters.date.from ? `${this.filters.date.from.year}-${this.filters.date.from.month}-${this.filters.date.from.day}` : '',
                nameTypeDateTo: this.filters.date.to ? `${this.filters.date.to.year}-${this.filters.date.to.month}-${this.filters.date.to.day}` : ''
            }, this.addSortParam({
                page: this.page - 1,
                size: this.itemsPerPage
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<NameTypeDetails[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.nameTypes = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    addSortParam(params) {
        const sort = this.predicate && this.predicate !== SORT_PREDICATE_NONE ? {
            sort: [this.getSort()]
        } : {};
        return Object.assign(params, sort);
    }

    getSort() {
        if (this.predicate === SORT_PREDICATE_NONE) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.sort();
        }
    }

    sort() {
        this.router.navigate(['./'], {
            queryParams:
                {
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
                },
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge'
        });
        this.loadAll();
    }

    editNameType(nameType: any) {
        this.cropSettingsContext.nameTypeDetails = nameType;
        this.router.navigate(['/', { outlets: { popup: 'name-type-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    deleteNameType(nameType: any) {
        this.nameTypeService.getMetadata(nameType.id).toPromise().then((metadata) => {
            let deleteNameTypeFormStudyPromise = Promise.resolve({});
            let deleteNameTypeFormGermplasmListPromise = Promise.resolve({});

            if (metadata.germplasm > 0) {
                this.alertService.error('crop-settings-manager.name-type.validation.delete.error.used.in.germplasm');

            } else {
                const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
                const messages = [];

                if (metadata.studies > 0) {
                    messages.push(this.translateService.instant('crop-settings-manager.name-type.modal.delete.summary.study'));
                }
                if (metadata.germplasmList > 0) {
                    messages.push(this.translateService.instant('crop-settings-manager.name-type.modal.delete.summary.germplasm.list'));
                }

                confirmModalRef.componentInstance.title = this.translateService.instant('crop-settings-manager.confirmation.title');
                confirmModalRef.componentInstance.message = this.translateService.instant('crop-settings-manager.name-type.modal.delete.warning', {
                    param1: nameType.name, param2: messages.map((item) => '<li>' + item + '</li>').join('')
                });

                confirmModalRef.result.then(() => {
                    if (metadata.studies > 0) {
                        deleteNameTypeFormStudyPromise = this.nameTypeService.deleteNameTypeFromStudy(nameType.id).toPromise();

                    }
                    if (metadata.germplasmList > 0) {
                        deleteNameTypeFormGermplasmListPromise = this.nameTypeService.deleteNameTypeFromGermplasmList(nameType.id).toPromise();

                    }
                    Promise.all([
                        deleteNameTypeFormStudyPromise,
                        deleteNameTypeFormGermplasmListPromise
                    ]).then(() => {
                            this.nameTypeService.deleteNameType(nameType.id).toPromise().then((result) => {
                                this.alertService.success('crop-settings-manager.name-type.modal.delete.success');
                                this.loadNameType();
                            }).catch((response) => {
                                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                            });
                            confirmModalRef.dismiss();
                        },
                        (error) => {
                            confirmModalRef.dismiss();
                            this.onError(error);
                        });
                });
            }
        }).catch((error) => {
            this.onError(error);
        });

    }

    async loadNameType() {
        this.loadAll();
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll();
    }

    registerNameTypeChanged() {
        this.eventSubscriber = this.eventManager.subscribe('nameTypeViewChanged', (event) => {
            this.loadAll();
        });
    }

    applyFilters() {
        this.resetTable();
    }

    resetFilters() {
        this.loadAll();
    }

}

export enum ColumnLabels {
    'CODE' = 'fcode',
    'NAME' = 'fname',
    'DESCRIPTION' = 'fdesc',
    'DATE' = 'fdate'
}
