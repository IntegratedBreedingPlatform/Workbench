import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TransactionService } from '../../shared/inventory/service/transaction.service';
import { ParamContext } from '../../shared/service/param.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../shared/alert/alert.service';
import { LocationService } from '../../shared/location/service/location.service';
import { BreedingMethodSearchRequest } from '../../shared/breeding-method/model/breeding-method-search-request.model';
import { MatchType } from '../../shared/column-filter/column-filter-text-with-match-options-component';
import { HttpResponse } from '@angular/common/http';
import { BreedingMethod } from '../../shared/breeding-method/model/breeding-method';
import { HelpService } from '../../shared/service/help.service';
import { BreedingMethodTypeEnum } from '../../shared/breeding-method/model/breeding-method-type.model';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { DatasetService } from '../../shared/dataset/service/dataset.service';

@Component({
    selector: 'jhi-advance-study',
    templateUrl: './advance-study.component.html'
})
export class AdvanceStudyComponent implements OnInit {

    static readonly LOCATIONS_PAGE_SIZE = 300;
    static readonly BREEDING_METHODS_PAGE_SIZE = 300;

    breedingMethodOptions: any;
    breedingMethodSelected: string;
    useFavoriteBreedingMethods = true;
    breedingMethodCheck: boolean;
    linesCheck = true;
    bulksCheck = true;
    breedingMethodType: any;
    isLoading: boolean;
    isSuccess: boolean;
    helpLink: string;
    methodTypes: any[];
    studyId: any;
    trialDatasetId: any;
    selectedInstances: any[];
    trialInstances: any[] = [];
    replicationNumber: number;
    replicationsOptions: any[] = [];
    checkallReplcations = true;

    constructor(private route: ActivatedRoute,
                private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private transactionService: TransactionService,
                private eventManager: JhiEventManager,
                private paramContext: ParamContext,
                private activeModal: NgbActiveModal,
                private alertService: AlertService,
                private locationService: LocationService,
                private router: Router,
                private breedingMethodService: BreedingMethodService,
                private helpService: HelpService,
                private datasetService: DatasetService
    ) {
        this.paramContext.readParams();
    }

    ngOnInit(): void {
        this.isLoading = false;
        this.isSuccess = false;
        this.breedingMethodCheck = true;
        this.breedingMethodType = '2';
        this.methodTypes = [BreedingMethodTypeEnum.DERIVATIVE, BreedingMethodTypeEnum.MAINTENANCE];

        this.studyId = Number(this.route.snapshot.queryParamMap.get('studyId'));
        this.trialDatasetId = Number(this.route.snapshot.queryParamMap.get('trialDatasetId'));
        this.selectedInstances = this.route.snapshot.queryParamMap.get('trialInstances').split(',');
        this.replicationNumber =  Number(this.route.snapshot.queryParamMap.get('noOfReplications'));

        this.initializeReplicationOptions(this.replicationNumber);

        this.datasetService.getDataset(this.studyId, this.trialDatasetId).toPromise().then((response) => {
            response.body.instances.forEach((instance) => {
                if (this.selectedInstances.includes(instance.instanceNumber.toString())) {
                    this.trialInstances.push({
                        instanceNumber: instance.instanceNumber,
                        locAbbr: instance.locationName + ' - (' + instance.locationAbbreviation + ')',
                        abbrCode: instance.customLocationAbbreviation
                    });
                }
            });
        });
        this.loadBreedingMethods();
        // Get helplink url
        if (!this.helpLink || !this.helpLink.length) {

            this.helpService.getHelpLink('MANAGE_CROP_BREEDING_METHODS').toPromise().then((response) => {
                this.helpLink = response.body;
            }).catch((error) => {
            });
        }
    }

    initializeReplicationOptions(replicationNumber: number) {
        if (replicationNumber !== 0) {
            for (let rep = 1; rep <= replicationNumber; rep++) {
                this.replicationsOptions.push({repIndex: rep, selected: true});
            }
        }
    }

    checkUncheckAll() {
        this.replicationsOptions.forEach((replication) => replication.selected = !this.checkallReplcations);
    }

    togglecheck(repCheck) {
        this.checkallReplcations = !repCheck;
    }

    loadBreedingMethods() {
        this.breedingMethodOptions = {
            ajax: {
                delay: 500,
                transport: function(params, success, failure) {
                    params.data.page = params.data.page || 1;

                    const breedingMethodSearchRequest: BreedingMethodSearchRequest = new BreedingMethodSearchRequest();
                    breedingMethodSearchRequest.nameFilter = {
                        type: MatchType.STARTSWITH,
                        value: params.data.term
                    };

                    breedingMethodSearchRequest.methodTypes = this.methodTypes;
                    const pagination = {
                        page: (params.data.page - 1),
                        size: AdvanceStudyComponent.BREEDING_METHODS_PAGE_SIZE
                    };

                    this.breedingMethodService.searchBreedingMethods(
                        breedingMethodSearchRequest,
                        this.useFavoriteBreedingMethods,
                        pagination
                    ).subscribe((res: HttpResponse<BreedingMethod[]>) => {
                        this.breedingMethodsFilteredItemsCount = res.headers.get('X-Total-Count');
                        success(res.body);
                    }, failure);
                }.bind(this),
                processResults: function(methods, params) {
                    params.page = params.page || 1;

                    return {
                        results: methods.map((method: BreedingMethod) => {
                            return {
                                id: method.code,
                                text: method.code + ' - ' + method.name
                            };
                        }),
                        pagination: {
                            more: (params.page * AdvanceStudyComponent.BREEDING_METHODS_PAGE_SIZE) < this.breedingMethodsFilteredItemsCount
                        }
                    };
                }.bind(this)
            }
        };
    }

    save(): void {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-creation-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });

    }

    fetch(): void {
        if (this.breedingMethodType === '1') {
            this.methodTypes = [];

        } else if (this.breedingMethodType === '2') {
            this.methodTypes = [BreedingMethodTypeEnum.DERIVATIVE, BreedingMethodTypeEnum.MAINTENANCE];
        }
        this.loadBreedingMethods();

    }

    dismiss() {
        // Handle closing of modal when this page is loaded outside of Angular.
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'cancel', 'value': '' }, '*');
        }
    }
}
