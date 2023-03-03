import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { Select2OptionData } from 'ng-select2';
import { MatchType } from '../column-filter/column-filter-text-with-match-options-component';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';
import { BreedingMethodSearchRequest } from '../breeding-method/model/breeding-method-search-request.model';
import { HttpResponse } from '@angular/common/http';
import { BreedingMethod } from '../breeding-method/model/breeding-method';
import { BreedingMethodService } from '../breeding-method/service/breeding-method.service';
import { BreedingMethodClassMethodEnum } from '../breeding-method/model/breeding-method-class.enum';
import { BreedingMethodTypeEnum } from '../breeding-method/model/breeding-method-type.model';

@Component({
    selector: 'jhi-breeding-methods-select',
    templateUrl: './breeding-methods-select.component.html'
})
export class BreedingMethodsSelectComponent implements OnInit {

    static readonly BREEDING_METHODS_PAGE_SIZE = 300;

    static readonly DERIVATIVE_METHOD_TYPES: BreedingMethodTypeEnum[] = [BreedingMethodTypeEnum.DERIVATIVE, BreedingMethodTypeEnum.MAINTENANCE];
    static readonly GENERATIVE_METHOD_TYPES: BreedingMethodTypeEnum[] = [BreedingMethodTypeEnum.GENERATIVE];

    @Input() value: string;
    @Output() valueChange = new EventEmitter<string>();
    @Input() breedingMethodFilterTypeValue = BreedingMethodFilterTypeEnum.DERIVATIVE_AND_MAINTENANCE_ONLY;
    @Output() breedingMethodFilterTypeValueChange = new EventEmitter<BreedingMethodFilterTypeEnum>();
    @Input() useFavoriteBreedingMethods = false;
    @Input() disabled = false;
    @Input() showFavoritesCheckbox = true;
    @Input() disableFavoritesCheckbox = false;
    @Input() showMethodTypeFilterSection = true;
    @Input() showGenerativeMethodTypesOption = false;
    @Input() showDerivativeMethodTypesOption = true;
    @Input() showAllMethodTypesOption = true;
    @Input() disableMethodTypeFilterSection = false;
    @Input() helpLink: string;
    @Input() nonBulkingOnly = false;
    @Input() selectWidth = '100%';

    @Output() onMethodChanged = new EventEmitter<BreedingMethod>();

    BreedingMethodFilterTypeEnum = BreedingMethodFilterTypeEnum;

    eventSubscriber: Subscription;

    selectedBreedingMethodId: string;
    selectedBreedingMethodFilterTypeValue: BreedingMethodFilterTypeEnum;
    selectedUseFavoriteBreedingMethods;
    breedingMethods: BreedingMethod[] = [];
    breedingMethodOptions: any;
    breedingMethdosFilteredItemsCount;

    initialData: Select2OptionData[];

    constructor(private breedingMethodService: BreedingMethodService,
                private eventManager: JhiEventManager) {
    }

    ngOnInit(): void {
        this.selectedUseFavoriteBreedingMethods = this.useFavoriteBreedingMethods;
        this.selectedBreedingMethodFilterTypeValue = this.breedingMethodFilterTypeValue;
        // The breeding methods are retrieved only when the dropdown is opened, so we have to manually set the initial selected item on first load.
        // Get the breeding method and add it to the initial data.
        this.selectedBreedingMethodId = this.value;
        if (this.value) {
            this.breedingMethodService.queryBreedingMethod(Number(this.value)).toPromise().then((breedingMethod) => {
                this.initialData = [{ id: String(breedingMethod.mid), text: breedingMethod.code + ' - ' + breedingMethod.name }];
                if (breedingMethod.type === BreedingMethodTypeEnum.GENERATIVE.toString()) {
                    // If the initial breeding method type is generative, select the Generative filter option by default
                    this.selectedBreedingMethodFilterTypeValue = BreedingMethodFilterTypeEnum.GENERATIVE_ONLY;
                } else {
                    // If the initial breeding method type is derivative or maintenance, select the Derivative filter option by default
                    this.selectedBreedingMethodFilterTypeValue = BreedingMethodFilterTypeEnum.DERIVATIVE_AND_MAINTENANCE_ONLY;
                }
                this.breedingMethods = this.breedingMethods.concat(breedingMethod);
            });
        }

        this.instantiateBreedingMethodsOptions();
    }

    instantiateBreedingMethodsOptions(): void {
        this.breedingMethodOptions = {
            ajax: {
                delay: 500,
                transport: function (params, success, failure) {
                    params.data.page = params.data.page || 1;

                    if (params.data.page === 1) {
                        this.breedingMethods = [];
                    }

                    const breedingMethodSearchRequest: BreedingMethodSearchRequest = new BreedingMethodSearchRequest();
                    breedingMethodSearchRequest.nameFilter = {
                        type: MatchType.STARTSWITH,
                        value: params.data.term
                    };

                    if (this.selectedBreedingMethodFilterTypeValue === BreedingMethodFilterTypeEnum.DERIVATIVE_AND_MAINTENANCE_ONLY) {
                        breedingMethodSearchRequest.methodTypes = BreedingMethodsSelectComponent.DERIVATIVE_METHOD_TYPES;
                    } else if (this.selectedBreedingMethodFilterTypeValue === BreedingMethodFilterTypeEnum.GENERATIVE_ONLY) {
                        breedingMethodSearchRequest.methodTypes = BreedingMethodsSelectComponent.GENERATIVE_METHOD_TYPES;
                    }

                    const pagination = {
                        page: (params.data.page - 1),
                        size: BreedingMethodsSelectComponent.BREEDING_METHODS_PAGE_SIZE
                    };

                    if (this.nonBulkingOnly) {
                        breedingMethodSearchRequest.methodClassIds = [BreedingMethodClassMethodEnum.NON_BULKING_BREEDING_METHOD_CLASS];
                    }

                    this.breedingMethodService.searchBreedingMethods(
                        breedingMethodSearchRequest,
                        this.selectedUseFavoriteBreedingMethods,
                        pagination
                    ).subscribe((res: HttpResponse<BreedingMethod[]>) => {
                        this.breedingMethodsFilteredItemsCount = res.headers.get('X-Total-Count');
                        success(res.body);
                    }, failure);
                }.bind(this),
                processResults: function (methods, params) {
                    params.page = params.page || 1;

                    this.breedingMethods = this.breedingMethods.concat(...methods);

                    return {
                        results: methods.map((method: BreedingMethod) => {
                            return {
                                id: String(method.mid),
                                text: method.code + ' - ' + method.name
                            };
                        }),
                        pagination: {
                            more: (params.page * BreedingMethodsSelectComponent.BREEDING_METHODS_PAGE_SIZE) < this.breedingMethodsFilteredItemsCount
                        }
                    };
                }.bind(this)
            }
        };
    }

    onValueChanged($event): void {
        const selectedBreedingMethod = this.breedingMethods.find((method: BreedingMethod) => String(method.mid) === this.selectedBreedingMethodId);
        if (selectedBreedingMethod) {
            this.valueChange.emit(this.selectedBreedingMethodId);
            this.onMethodChanged.emit(selectedBreedingMethod);
        }
    }

    onBreedingMethodFilterTypeValueChanged(): void {
        this.selectedBreedingMethodId = null;
        this.breedingMethods = [];
        this.breedingMethodFilterTypeValueChange.emit(this.selectedBreedingMethodFilterTypeValue);
    }
}

export enum BreedingMethodFilterTypeEnum {
    DERIVATIVE_AND_MAINTENANCE_ONLY = 1,
    ALL_METHODS = 2,
    GENERATIVE_ONLY = 3
}
