import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../../model/role.model';
import { UserService } from '../../services/user.service';
import { RoleService } from '../../services/role.service';
import { CropService } from '../../services/crop.service';
import { TranslateService } from '@ngx-translate/core';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../shared/alert/alert.service';
import { Pageable } from '../../../shared/model/pageable';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Location } from '../../../shared/location/model/location';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import {JhiEventManager, JhiLanguageService} from 'ng-jhipster';
import { Subscription } from 'rxjs';
import { RoleFilter } from '../../model/role-filter.model';
import { Crop } from '../../../shared/model/crop.model';
import { User } from '../../../shared/user/model/user.model';
import { UserRole } from '../../../shared/user/model/user-role.model';
import { SiteAdminContext } from '../../site-admin-context';
import { FilterType, ColumnFilterComponent } from '../../../shared/column-filter/column-filter.component';
import { UserSearchRequest } from '../../model/UserSearchRequest';
import { Select2OptionData } from 'ng-select2';

@Component({
    selector: 'jhi-users-pane',
    templateUrl: 'users-pane.component.html',
    styleUrls: ['users-pane.component.css']
})

export class UsersPaneComponent implements OnInit {

    itemsPerPage: any = 20;
    page: number;
    predicate: any;
    previousPage: number;
    reverse: boolean;
    totalItems: number;
    users: User[];

    ColumnLabels = ColumnLabels;

    eventSubscriber: Subscription;

    userSearchRequest: UserSearchRequest;

    user: User;

    originalUser: User;

    public roles: Role[];

    crops: Promise<Crop[]>;

    private isLoading: boolean;

    private userFilters: any;

    constructor(private userService: UserService,
                private roleService: RoleService,
                private cropService: CropService,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                public translateService: TranslateService,
                private jhiLanguageService: JhiLanguageService,
                private modalService: NgbModal,
                private alertService: AlertService,
                private eventManager: JhiEventManager,
                private context: SiteAdminContext
    ) {
        this.page = 1;
        this.totalItems = 0;
        this.predicate = ['person.lastName'];
        this.reverse = false;
        this.userSearchRequest = new UserSearchRequest();

        if (!this.filters) {
            this.filters = this.getInitialFilters();
            this.request.status = 0;
            ColumnFilterComponent.reloadFilters(this.filters, this.request);
        }
    }

    get filters() {
        return this.userFilters;
    }

    set filters(filters) {
        this.userFilters = filters;
    }

    editUser(user: User) {
        this.context.user = Object.assign({}, user);
        this.context.user.crops = user.crops.map((x) => Object.assign({}, x));
        if (!user.userRoles) {
            this.context.user.userRoles = [];
        } else {
            this.context.user.userRoles = user.userRoles.map((x) => Object.assign({}, x));
        }

        this.router.navigate(['/', { outlets: { popup: 'user-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    addUser() {
        this.context.user = new User(null, '', '', '', [], [], '', true, false);
        this.router.navigate(['/', { outlets: { popup: 'user-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    ngOnInit() {
        // get all users
        this.loadAll(this.request);
        this.crops = this.cropService.getAll().toPromise();
        this.registerUserChanged();
    }

    loadAll(request: UserSearchRequest) {
        this.isLoading = true;
        this.userService.searchUsers(
            this.request,
            <Pageable>({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.getSort()
            })
        ).pipe(finalize(() => {
            this.isLoading = false;
        })).subscribe(
            (res: HttpResponse<User[]>) => this.onSuccess(res.body, res.headers),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    changedActiveStatus(user) {
        const userSelected = JSON.parse(JSON.stringify(user));
        userSelected.active = !userSelected.active;
        this.userService
            .update(userSelected)
            .subscribe(
                (resp) => {
                    this.loadAll(this.request);

                },
                (res: HttpErrorResponse) =>
                    this.onError(res)
            );
    }

    showUserStatusConfirmPopUp(userSelected: User) {
        let confirmMessage;
        if (userSelected.active) {
            confirmMessage = this.translateService.instant('site-admin.user.deactive.message');
        } else {
            confirmMessage = this.translateService.instant('site-admin.user.active.message');
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = this.translateService.instant('site-admin.user.confirm.message.title');
        confirmModalRef.componentInstance.message = confirmMessage;
        confirmModalRef.result.then(() => {
            this.changedActiveStatus(userSelected);
        }, () => confirmModalRef.dismiss());
    }

    getCropsTitleFormat(crops) {
        return crops.map((crop) => crop.cropName).splice(1).join(' and ');
    }

    getRoleNamesTitleFormat(userRoles: UserRole[]) {
        return userRoles.map((userRole) => userRole.role.name).splice(1).join(' and ');
    }

    hasSuperAdminRole(userRoles) {
        return userRoles && userRoles.some((userRole) => userRole.role.name === 'SuperAdmin');
    }

    private onSuccess(data: any[], headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.users = data;
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
        return this.userSearchRequest;
    }

    set request(request: UserSearchRequest) {
        this.userSearchRequest = request;
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

    registerUserChanged() {
        this.eventSubscriber = this.eventManager.subscribe('onUserViewChanged', (event) => {
            this.loadAll(this.request);
        });

        this.eventSubscriber = this.eventManager.subscribe('columnFiltersChanged', (event) => {
            if (event && event.content && event.content.filterBy) {
                this.filterBy(event.content.filterBy);
            }
            this.resetTable();
        });
    }

    resetFilters() {
        this.userSearchRequest = new UserSearchRequest();
        this.loadAll(this.request);
    }

    private filterBy(filterBy: { [p: string]: any }) {
        if (!filterBy) {
            return;
        }
        const entries = Object.entries(filterBy);
        if (entries.length === 0) {
            return;
        }
        this.resetFilters();
    }

    private getInitialFilters() {
        return [
            {
                key: 'status', name: 'Status', default: true, defaultValue: 0, type: FilterType.RADIOBUTTON,
                options: Promise.resolve([{
                    id: 0, name: 'Active'
                }, {
                    id: 1, name: 'Inactive'
                }, {
                    id: undefined, name: 'All'
                }])
            },
            { key: 'userName', name: 'User Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'firstName', name: 'First Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'lastName', name: 'Last Name', placeholder: 'Contains Text', type: FilterType.TEXT },
            { key: 'email', name: 'Email', placeholder: 'Contains Text', type: FilterType.TEXT },
            {
                key: 'roleIds', name: 'Role', type: FilterType.DROPDOWN, values: this.getRoleOptions(), multipleSelect: false,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            },
            {
                key: 'crops', name: 'Crop', type: FilterType.DROPDOWN, values: this.getCropOptions(), multipleSelect: false,
                transform(req) {
                    ColumnFilterComponent.transformDropdownFilter(this, req);
                },
                reset(req) {
                    ColumnFilterComponent.resetDropdownFilter(this, req);
                },
            }]
    }

    private getRoleOptions(): Promise<Select2OptionData[]> {
        return this.roleService.getRoles().toPromise().then((roles: Role[]) => {
            return roles.map((role: Role) => {
                return {
                    id: role.id.toString(),
                    text: role.name
                }
            });
        });
    }

    private getCropOptions(): Promise<Select2OptionData[]> {
        return this.cropService.getAll().toPromise().then((cropsArray: Crop[]) => {
            return cropsArray.map((crop: Crop) => {
                return {
                    id: crop.cropName,
                    text: crop.cropName
                }
            });
        });
    }

}

// TODO upgrade angular, use ngIf-as
@Pipe({ name: 'dedupRoleNames' })
export class DedupRoleNamesPipe implements PipeTransform {
    transform(userRoles: UserRole[]): string[] {
        if (!userRoles || !userRoles.length) {
            return null;
        }
        return userRoles.map((userRole) => userRole.role.name)
            .filter((item, pos, self) => {
                // remove dups
                return self.indexOf(item) === pos;
            });
    }
}

export enum ColumnLabels {
    'USER_NAME' = 'name',
    'FIRST_NAME' = 'person.firstName',
    'LAST_NAME' = 'person.lastName',
    'EMAIL' = 'person.email',
    'STATUS' = 'status',
}
