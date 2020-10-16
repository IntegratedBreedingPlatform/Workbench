import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { ActivatedRoute } from '@angular/router';
import { isNumeric } from '../util/is-numeric';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-column-filter',
    templateUrl: './column-filter.component.html',
    styleUrls: ['./column-filter.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class ColumnFilterComponent implements OnInit, OnDestroy {

    public isCollapsed = false;

    @Input() resultSearch: any;
    @Input() request: any;

    private _filters;

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

    filtersAdded = [];

    FILTER_TYPES = FilterType;

    selectedFilter: any = '';
    cropName: string;

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

    static transformTextWithMatchOptionsFilter(filter, request) {
        request[filter.key] = {
            type: filter.matchType,
            value: filter.value
        }
    }

    static transformPedigreeOptionsFilter(filter, request) {
        request[filter.key] = {
            type: filter.pedigreeType,
            generationLevel: filter.value
        }
    }

    static transformAttributesFilter(filter, request) {
        const attributes: any = {}
        for (const attribute of filter.attributes) {
            attributes[attribute.code] = attribute.value;
        }
        request[filter.key] = attributes;
    }

    static resetAttributesFilter(filter, request) {
        request[filter.key] = undefined;

        // Remove all attributes column
        for (const attribute of filter.attributes) {
            request.addedColumnsPropertyIds.pop(attribute.code);
        }

        filter.attributes = [];
    }

    static resetDateFilter(filter, request, fromProperty, toProperty) {
        request[fromProperty] = undefined;
        request[toProperty] = undefined;
        filter.from = undefined;
        filter.to = undefined;
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
                    return Promise.resolve(filter.attributes.map((attribute) => `${attribute.code} : ${attribute.value}`)
                        .join(', '));
                }
                return Promise.resolve();
            case FilterType.TEXT:
            case FilterType.LIST:
            case FilterType.BOOLEAN:
            case FilterType.MODAL:
                return Promise.resolve(filter.value);
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

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
    }

    constructor(private jhiAlertService: JhiAlertService,
                private activatedRoute: ActivatedRoute,
                private modal: NgbModal,
                private eventManager: JhiEventManager) {

        this.activatedRoute.queryParams.subscribe((params) => {
            this.cropName = params.cropName;
        });
    }

    AddFilter() {
        this._filters.forEach((filter) => {
            if (filter.key === this.selectedFilter) {
                filter.added = true;
                this.filtersAdded.push(filter);
                this.selectedFilter = '';
                ColumnFilterComponent.updateBadgeLabel(filter);
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
        this.resultSearch.searchResultDbId = '';
        this.apply(filter);
    }

    updateTextFilter(filter: any, key: string) {
        if (filter.transform) {
            filter.transform(this.request);
        } else {
            this.request[key] = filter.value;
        }
        this.resultSearch.searchResultDbId = '';
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
            this.resultSearch.searchResultDbId = '';
            this.apply(filter);
        });
    }

    updateFilter(filter) {
        if (filter.transform) {
            filter.transform(this.request);
        }
        this.resultSearch.searchResultDbId = '';
        this.apply(filter);
    }

    reset(filter) {
        this._reset(filter);
        this.resultSearch.searchResultDbId = '';
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
        filter.added = false;
        this.filtersAdded.splice(this.filtersAdded.indexOf(filter), 1);
        this.reset(filter);
    }

    clearAll() {
        for (let i = this.filtersAdded.length - 1; i >= 0; i--) {
            const filter = this.filtersAdded[i];
            if (!filter.default) {
                filter.added = false;
                this.filtersAdded.splice(i, 1);
                this._reset(filter);
            }
        }
        this.resultSearch.searchResultDbId = '';
        this.transition();
    }

    resetAll($event) {
        $event.preventDefault();
        for (let i = this.filtersAdded.length - 1; i >= 0; i--) {
            const filter = this.filtersAdded[i];
            this._reset(filter);
            ColumnFilterComponent.updateBadgeLabel(filter);
        }
        this.resultSearch.searchResultDbId = '';
        this.transition();
    }

    hasClearableFilters() {
        return this.filtersAdded.some((filter) => !filter.default);
    }

    openModal(filter) {
        filter.open(this.modal, this.request).then(() => {
            this.resultSearch.searchResultDbId = '';
            this.apply(filter);
        });
    }

    apply(filter) {
        ColumnFilterComponent.updateBadgeLabel(filter);
        this.transition();
    }

    transition() {
        this.eventManager.broadcast({ name: 'columnFiltersChanged', content: '' });
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }

    updateRadioFilter(filter: any, key: string) {
        this.request[key] = filter.value;
        this.resultSearch.searchResultDbId = '';
        this.apply(filter);
    }

    addAttributesColumn(attribute) {
        if (!this.request.addedColumnsPropertyIds.some((e) => e === attribute.code)) {
            this.request.addedColumnsPropertyIds.push(attribute.code);
        }
    }

    removeAttributesColumn(attribute) {
        this.request.addedColumnsPropertyIds = this.request.addedColumnsPropertyIds.filter((e) => e !== attribute.code);
        this.eventManager.broadcast({ name: 'clearSort', content: '' });
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
    ATTRIBUTES
}
