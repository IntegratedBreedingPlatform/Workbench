import { Component, Input, OnDestroy, OnInit, QueryList, ViewChildren, ViewEncapsulation } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { isNumeric } from '../util/is-numeric';
import { NgbModal, NgbPopover } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../alert/alert.service';
import { Select2OptionData } from 'ng-select2/lib/ng-select2.interface';
import { ColumnFilterTransitionEventModel } from './column-filter-transition-event.model';

@Component({
    selector: 'jhi-column-filter',
    templateUrl: './column-filter.component.html',
    styleUrls: ['./column-filter.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ColumnFilterComponent implements OnInit, OnDestroy {

    static readonly DEFAULT_SECTION_LABEL = 'Filter table';
    static readonly DEFAULT_FILTER_DROPDOWN_LABEL = 'Search by';

    @ViewChildren('popoverButton')
    private popoverButtons: QueryList<NgbPopover>;

    @Input() isCollapsed = false;
    @Input() sectionLabel = ColumnFilterComponent.DEFAULT_SECTION_LABEL;
    @Input() filterDropdownLabel = ColumnFilterComponent.DEFAULT_FILTER_DROPDOWN_LABEL;
    @Input() resultSearch: any;
    @Input() request: any;
    @Input() eventName = 'columnFiltersChanged';

    private _filters;

    filtersAdded = [];

    FILTER_TYPES = FilterType;

    selectedFilter: any = '';
    cropName: string;

    addedNameTypes = [];
    addedAttributes = [];

    static transformMinMaxFilter(filter, request, minProperty, maxProperty) {
        request[minProperty] = filter.min;
        request[maxProperty] = filter.max;
    }

    static resetMinMaxFilter(filter, request, minProperty, maxProperty) {
        request[minProperty] = undefined;
        request[maxProperty] = undefined;
        filter.min = undefined;
        filter.max = undefined;
    }

    static transformDateFilter(filter, request, fromProperty, toProperty) {
        if (filter.from) {
            request[fromProperty] = `${filter.from.year}-${filter.from.month}-${filter.from.day}`;
        }
        if (filter.to) {
            request[toProperty] = `${filter.to.year}-${filter.to.month}-${filter.to.day}`;
        }
    }

    static transformNumberRangeFilter(filter, request, fromProperty, toProperty) {
            request[fromProperty] = filter.from;
            request[toProperty] = filter.to;
    }

    static transformPedigreeOptionsFilter(filter, request) {
        request[filter.key] = {
            type: filter.pedigreeType,
            generationLevel: filter.value
        };
    }

    static transformAttributesFilter(filter, request) {
        const attributes: any = {};
        for (const attribute of filter.attributes) {
            const attributeName = attribute.alias ? attribute.alias : attribute.name;
            attributes[attributeName] = attribute.value;
        }
        request[filter.key] = attributes;
    }

    static resetAttributesFilter(filter, request) {
        request[filter.key] = undefined;

        // Remove all attributes column
        for (const attribute of filter.attributes) {
            const attributeName = attribute.alias ? attribute.alias : attribute.name;
            const attributeIndex = request.addedColumnsPropertyIds.indexOf(attributeName);

            if (attributeIndex  > -1) {
                request.addedColumnsPropertyIds.splice(attributeIndex, 1);
            }
        }

        filter.attributes = [];
    }

    static transformNameTypesFilter(filter, request) {
        const nameTypes: any = {};
        for (const nameType of filter.nameTypes) {
            nameTypes[nameType.code] = nameType.value;
        }
        request[filter.key] = nameTypes;
    }

    static resetNameTypesFilter(filter, request) {
        request[filter.key] = undefined;

        // Remove all name types column
        for (const nameType of filter.nameTypes) {
            const nameTypeIndex = request.addedColumnsPropertyIds.indexOf(nameType.name);

            if (nameTypeIndex  > -1) {
                request.addedColumnsPropertyIds.splice(nameTypeIndex, 1);
            }
        }

        filter.nameTypes = [];
    }

    static resetRangeFilter(filter, request, fromProperty, toProperty) {
        request[fromProperty] = undefined;
        request[toProperty] = undefined;
        filter.from = undefined;
        filter.to = undefined;
    }

    static transformDropdownFilter(filter, request) {
        request[filter.key] = filter.selectedValues.map((option: Select2OptionData) => option.id);
    }

    static resetDropdownFilter(filter, request) {
        request[filter.key] = undefined;
        filter.selectedValues = [];
    }

    static transformVariablesFilter(filter, request) {
        const variables: any = {};
        for (const variable of filter.variables) {
            variables[variable.id] = variable.value;
        }
        request[filter.key] = variables;
    }

    static resetVariablesFilter(filter, request) {
        request[filter.key] = undefined;
        filter.variables = [];
    }

    static updateBadgeLabel(filter) {
        return ColumnFilterComponent.getBadgeLabelByType(filter).then((label) => {
            if (label) {
                filter.label = filter.name + ' :: ' + label;
            } else {
                filter.label = filter.name + ' :: All';
            }
        });
    }

    private static getBadgeLabelByType(filter: any) {
        switch (filter.type) {
            case FilterType.CHECKLIST:
                return filter.options.then((options) => {
                    return options.filter((option) => option.checked).map((option) => option.name)
                        .join(', ');
                });

            case FilterType.RADIOBUTTON:
                return filter.options.then((options) => {
                    for (const option of options) {
                        if (option.id === filter.value) {
                            return option.name;
                        }
                    }
                });

            case FilterType.DATE:
                const from = (filter.from && `${filter.from.year}-${filter.from.month}-${filter.from.day}`) || '';
                const to = (filter.to && `${filter.to.year}-${filter.to.month}-${filter.to.day}`) || '';
                return Promise.resolve(from || to ? (from + ' - ' + to) : '');

            case FilterType.NUMBER:
                if (!isNumeric(filter.min) && !isNumeric(filter.max)) {
                    return Promise.resolve();
                }
                return Promise.resolve((isNumeric(filter.min) ? filter.min : '')
                    + ' - '
                    + (isNumeric(filter.max) ? filter.max : ''));

            case FilterType.NUMBER_RANGE:
                if (!isNumeric(filter.from) && !isNumeric(filter.to)) {
                    return Promise.resolve();
                }
                return Promise.resolve((isNumeric(filter.from) ? filter.from : '')
                    + ' - '
                    + (isNumeric(filter.to) ? filter.to : ''));

            case FilterType.TEXT_WITH_MATCH_OPTIONS:
                if (filter.matchType && filter.value) {
                    return Promise.resolve(`${filter.matchType} : ${filter.value}`);
                }
                return Promise.resolve();

            case FilterType.PEDIGREE_OPTIONS:
                if (filter.value) {
                    return Promise.resolve(`${filter.pedigreeType} (Level ${filter.value})`);
                }
                return Promise.resolve();

            case FilterType.ATTRIBUTES:
                if (filter.attributes && filter.attributes.length) {
                    return Promise.resolve(filter.attributes.map((attribute) => `${attribute.alias ? attribute.alias : attribute.name} : ${attribute.value}`)
                        .join(', '));
                }
                return Promise.resolve();

            case FilterType.NAME_TYPES:
                if (filter.nameTypes && filter.nameTypes.length) {
                    return Promise.resolve(filter.nameTypes.map((nameType) => `${nameType.name} : ${nameType.value}`)
                        .join(', '));
                }
                return Promise.resolve();

            case FilterType.TEXT:
            case FilterType.LIST:
            case FilterType.BOOLEAN:
            case FilterType.MODAL:
                return Promise.resolve(filter.value);

            case FilterType.DROPDOWN:
                if (filter.selectedValues && filter.selectedValues.length) {
                    return Promise.resolve(filter.selectedValues.map((option: Select2OptionData) => `${option.text}`)
                        .join(', '));
                }
                return Promise.resolve();

            case FilterType.VARIABLES:
                if (filter.variables && filter.variables.length) {
                    return Promise.resolve(filter.variables.map((variable) => `${variable.alias ? variable.alias : variable.name} : ${variable.value}`)
                        .join(', '));
                }
                return Promise.resolve();

            default:
                return Promise.resolve();
        }
    }

    /**
     * Reload filters from request based on the filter type
     * @param filters the filters to reload
     * @param request the request object to reload from
     */
    public static reloadFilters(filters: any[], request: any) {
        if (!filters || !filters.length || !request) {
            return Promise.resolve();
        }
        const promises = filters.map((filter) => {
            switch (filter.type) {
                case FilterType.CHECKLIST:
                    const list = request[filter.key];
                    if (!list || !list.length) {
                        return Promise.resolve();
                    }
                    return filter.options.then((options) => {
                        options.forEach((option) => {
                            if (list.indexOf(option.id) !== -1) {
                                option.checked = true;
                            }
                        });
                    });
                case FilterType.RADIOBUTTON:
                    filter.value = request[filter.key];
                    return Promise.resolve();
                case FilterType.DATE:
                case FilterType.NUMBER:
                    filter.reload(request);
                    return Promise.resolve();
                case FilterType.TEXT:
                case FilterType.LIST:
                case FilterType.BOOLEAN:
                case FilterType.TEXT_WITH_MATCH_OPTIONS:
                case FilterType.PEDIGREE_OPTIONS:
                case FilterType.ATTRIBUTES:
                case FilterType.MODAL:
                    filter.value = request[filter.key];
                    return Promise.resolve();
                default:
                    return Promise.resolve();
            }
        });
        return Promise.all(promises).then(() => {
            return Promise.all(filters.map((filter) => {
                return ColumnFilterComponent.updateBadgeLabel(filter);
            }));
        });
    }

    @Input()
    set filters(value) {
        this._filters = value;

        this.filtersAdded = [];
        this._filters.filter((filter) => filter.default || filter.added).forEach((filter) => {
            this.filtersAdded.push(filter);
            ColumnFilterComponent.updateBadgeLabel(filter);
        });
    }

    get filters() {
        return this._filters;
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
    }

    constructor(private alertService: AlertService,
                private activatedRoute: ActivatedRoute,
                private modal: NgbModal,
                private eventManager: JhiEventManager) {

        this.activatedRoute.queryParams.subscribe((params) => {
            this.cropName = params.cropName;
        });
    }

    AddFilter() {
        this._filters.forEach((_filter) => {
            if ((_filter.key === this.selectedFilter && !this.filtersAdded.some((filter) => filter.key === this.selectedFilter))) {
                _filter.added = true;
                this.filtersAdded.push(_filter);
                this.selectedFilter = '';
                ColumnFilterComponent.updateBadgeLabel(_filter);
                return;
            }
        });
    }

    hasFiltersAvailable() {
        for (const filter of this._filters) {
            if (!filter.added && !filter.default) {
                return true;
            }
        }
        return false;
    }

    updateListFilter(filter) {
        this.request[filter.key] = filter.value.split(',');
        this.apply(filter)
    }

    updateTextFilter(filter: any, key: string) {
        if (filter.transform) {
            filter.transform(this.request);
        } else {
            this.request[key] = filter.value;
        }
        this.apply(filter);
    }

    updateTextWithMatchFilter(filter: any) {
        this.request[filter.key] = {
            type: filter.matchType,
            value: filter.value
        };
        this.apply(filter);
    }

    updateBooleanFilter(filter: any, key: string) {
        this.request[key] = filter.value;
        this.apply(filter);
    }

    updateCheckListFilter(filter, checkProperty: string) {
        filter.options.then((options) => {
            this.request[checkProperty] = options.filter((option) => {
                return option.checked;
            }).map((option) => {
                return option.id;
            });
            this.apply(filter);
        });
    }

    updateFilter(filter) {
        if (filter.transform) {
            filter.transform(this.request);
        }
        this.apply(filter);
    }

    reset(filter) {
        this._reset(filter);
        this.apply(filter);
    }

    private _reset(filter) {
        filter.value = filter.defaultValue;
        this.request[filter.key] = filter.defaultValue;

        if (filter.options) {
            filter.options.then((options) => {
                options.forEach((option) => {
                    option.checked = false;
                });
            });
        }

        if (filter.reset) {
            filter.reset(this.request);
        }

    }

    clear(filter) {
        if (filter.type === this.FILTER_TYPES.NAME_TYPES) {
            this.removeAllNameTypesColumn();
        } else if (filter.type === this.FILTER_TYPES.ATTRIBUTES) {
            this.removeAllAttributesColumn();
        }
        filter.added = false;
        this.filtersAdded.splice(this.filtersAdded.indexOf(filter), 1);
        this.reset(filter);
    }

    clearFilters() {
        for (let i = this.filtersAdded.length - 1; i >= 0; i--) {
            const filter = this.filtersAdded[i];
            if (!filter.default) {
                filter.added = false;
                this.filtersAdded.splice(i, 1);
                this._reset(filter);
            }
        }
    }

    clearAll() {
        this.clearFilters();
        this.removeAllAttributesColumn();
        this.removeAllNameTypesColumn();
        if (this.resultSearch) {
            this.resultSearch.searchResultDbId = '';
        }
        this.transition();
    }

    resetAll($event) {
        $event.preventDefault();
        for (let i = this.filtersAdded.length - 1; i >= 0; i--) {
            const filter = this.filtersAdded[i];
            this._reset(filter);
            ColumnFilterComponent.updateBadgeLabel(filter);
        }
        if (this.resultSearch) {
            this.resultSearch.searchResultDbId = '';
        }
        this.transition();
    }

    hasClearableFilters() {
        return this.filtersAdded.some((filter) => !filter.default);
    }

    openModal(filter) {
        filter.open(this.modal, this.request).then(() => {
            this.apply(filter);
        });
    }

    apply(filter) {
        if (this.resultSearch) {
            this.resultSearch.searchResultDbId = '';
        }
        ColumnFilterComponent.updateBadgeLabel(filter);
        this.popoverButtons.forEach((button) => button.close());
        this.transition();
    }

    transition() {
        this.eventManager.broadcast(new ColumnFilterTransitionEventModel(this.eventName, '', this.filtersAdded));
    }

    private onError(error) {
        this.alertService.error(error.message);
    }

    updateRadioFilter(filter: any, key: string) {
        if (filter.transform) {
            filter.transform(this.request);
        } else {
            this.request[key] = filter.value;
        }
        this.apply(filter);
    }

    addAttributesColumn(attribute) {
        const attributeName = attribute.alias ? attribute.alias : attribute.name;
        if (!this.request.addedColumnsPropertyIds.some((e) => e === attributeName)) {
            this.request.addedColumnsPropertyIds.push(attributeName);
            this.addedAttributes.push(attribute);
        }
    }

    removeAttributesColumn(attribute) {
        const attributeName = attribute.alias ? attribute.alias : attribute.name;
        this.request.addedColumnsPropertyIds = this.request.addedColumnsPropertyIds.filter((e) => e !== attributeName);
        this.addedAttributes = this.addedAttributes.filter((a) => a.name !== attributeName);
    }

    removeAllAttributesColumn() {
        const tempAddedAttributes = this.addedAttributes;
        tempAddedAttributes.forEach((attr) => {
           this.removeAttributesColumn(attr);
        });
    }

    addNameTypeColumn(nameType) {
        if (!this.request.addedColumnsPropertyIds.some((e) => e === nameType.name)) {
            this.request.addedColumnsPropertyIds.push(nameType.name);
            this.addedNameTypes.push(nameType);
        }
    }

    removeNameTypeColumn(nameType) {
        this.request.addedColumnsPropertyIds = this.request.addedColumnsPropertyIds.filter((e) => e !== nameType.name);
        this.addedNameTypes = this.addedNameTypes.filter((a) => a.name !== nameType.name);
    }

    removeAllNameTypesColumn() {
        const tempAddedNameTypes = this.addedNameTypes;
        tempAddedNameTypes.forEach((nameType) => {
           this.removeNameTypeColumn(nameType);
        });
    }

}

export enum FilterType {
    TEXT,
    LIST,
    DATE,
    NUMBER,
    RADIOBUTTON,
    CHECKLIST,
    MODAL,
    BOOLEAN,
    TEXT_WITH_MATCH_OPTIONS,
    PEDIGREE_OPTIONS,
    ATTRIBUTES,
    NUMBER_RANGE,
    NAME_TYPES,
    DROPDOWN,
    VARIABLES
}
