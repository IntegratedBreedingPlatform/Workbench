import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import 'rxjs/add/operator/toPromise';
import { Role } from '../../models/role.model';
import { UserService } from '../../services/user.service';
import { RoleService } from '../../services/role.service';
import { CropService } from '../../services/crop.service';
import { TranslateService } from '@ngx-translate/core';
import { JhiLanguageService } from 'ng-jhipster/src/language';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../shared/alert/alert.service';
import { Pageable } from '../../../shared/model/pageable';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Location } from '../../../shared/location/model/location';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';
import { RoleFilter } from '../../models/role-filter.model';
import { Crop } from '../../../shared/model/crop.model';
import { User } from '../../../shared/user/model/user.model';
import { UserRole } from '../../../shared/user/model/user-role.model';
import { SiteAdminContext } from '../../site-admin-context';
import { FilterType, ColumnFilterComponent } from '../../../shared/column-filter/column-filter.component';
import { UserSearchRequest } from '../../models/UserSearchRequest';
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

    errorServiceMessage = '';
    isEditing = false;
    dialogTitle: string;
    confirmStatusTitle = 'Confirm';
    confirmMessage = 'Please confirm that you would like to deactivate/activate this user account.';
    user: User;
    originalUser: User;

    public roles: Role[];
    public crops: Crop[] = [];
    private message: string;

    // TODO upgrade angular, use ngIf-else and async pipe
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
        /* const roleFilter = new RoleFilter();
         // get all roles
         this.roleService
             .searchRoles(roleFilter, null)
             .subscribe(
                 (roles) => this.roles = roles.body,
                 (error) => {
                     // XXX
                     // handleReAuthentication is called on
                     // userService error
                 });*/

        // TODO change it to obtain the crop every time when it is need it.
        this.cropService
            .getAll()
            .subscribe((crops) => {
                this.crops = crops;
                this.cropService.crops = this.crops;
            });
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

    // TODO
    // - Move to interceptor
    // - see /ibpworkbench/src/main/web/src/apps/ontology/app-services/bmsAuth.js
    handleReAuthentication() {
        alert('Site Admin needs to authenticate you again. Redirecting to login page.');
        window.top.location.href = '/ibpworkbench/logout';
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
        this.confirmMessage = 'Please confirm that you would like to ';

        if (userSelected.active === true) {
            this.confirmMessage = this.confirmMessage + 'deactivate this user account.';
        } else {
            this.confirmMessage = this.confirmMessage + 'activate this user account.';
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Confirmation';
        confirmModalRef.componentInstance.message = this.confirmMessage;
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
            }, {
                key: 'roleId', name: 'Role', type: FilterType.DROPDOWN, values: this.getRoleOptions(), multipleSelect: false,
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
