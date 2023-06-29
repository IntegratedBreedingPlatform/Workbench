import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Role } from '../../model/role.model';
import { RoleService } from '../../services/role.service';
import { RoleFilter } from '../../model/role-filter.model';
import { Pageable } from '../../../shared/model/pageable';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { Location } from '../../../shared/location/model/location';
import { TranslateService } from '@ngx-translate/core';
import { JhiLanguageService } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../shared/alert/alert.service';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';
import { SiteAdminContext } from '../../site-admin-context';

@Component({
    selector: 'jhi-roles-pane',
    templateUrl: 'roles-pane.component.html',
    styleUrls: ['roles-pane.component.css']
})

export class RolesPaneComponent implements OnInit {

    itemsPerPage: any = 20;
    page: number;
    message: string;
    predicate: any;
    previousPage: number;
    reverse: boolean;
    totalItems: number;

    roles: Role[];

    ColumnLabels = ColumnLabels;

    private isLoading: boolean;

    roleSearchRequest: RoleFilter;

    eventSubscriber: Subscription;

    constructor(private roleService: RoleService,
                private router: Router,
                public translateService: TranslateService,
                private jhiLanguageService: JhiLanguageService,
                private modalService: NgbModal,
                private alertService: AlertService,
                private eventManager: JhiEventManager,
                private context: SiteAdminContext) {

        this.page = 1;
        this.totalItems = 0;
        this.predicate = ['name'];
        this.reverse = false;
        this.roleSearchRequest = new RoleFilter();
    }

    ngOnInit() {
        this.loadAll(this.request);
        this.registerRoleChanged();
    }

    private loadAll(request: RoleFilter) {
        this.isLoading = true;
        this.roleService.searchRoles(new RoleFilter(),
            <Pageable>({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<Role[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    getPermissionsTitleFormat(permissions) {
        return permissions.map((permission) => permission.description).splice(1).join(' and ');
    }

    isSuperAdminRole(role: Role) {
        return role.name === 'SuperAdmin';
    }

    editRole(role: Role) {
        this.context.role = Object.assign({}, role);
        this.context.role.permissions = role.permissions.map((x) => Object.assign({}, x));
        this.router.navigate(['/', { outlets: { popup: 'role-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    addRole() {
        this.context.role = new Role();
        this.router.navigate(['/', { outlets: { popup: 'role-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    private onSuccess(data: any[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.roles = data;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.loadAll(this.request);
        }
    }

    private getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    get request() {
        return this.roleSearchRequest;
    }

    set request(request: RoleFilter) {
        this.roleSearchRequest = request;
    }

    resetTable() {
        this.page = 1;
        this.previousPage = 1;
        this.loadAll(this.request);
    }

    sort() {
        this.page = 1;
        this.loadAll(this.request);
    }

    trackId(index: number, item: Location) {
        return item.id;
    }

    registerRoleChanged() {
        this.eventSubscriber = this.eventManager.subscribe('onRoleViewChanged', (event) => {
            this.loadAll(this.request);
        });
    }
}
    export enum ColumnLabels {
    'ROLE_NAME' = 'name',
    'ROLE_DESCRIPTION' = 'description',
    'ROLE_TYPE' = 'roleType.id',
    'STATUS' = 'active',

}
