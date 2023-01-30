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
import { UserSearchRequest } from '../../../shared/user/model/user-search-request.model';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';
import { RoleFilter } from '../../models/role-filter.model';
import { Crop } from '../../../shared/model/crop.model';
import { User } from '../../../shared/user/model/user.model';
import { UserRole } from '../../../shared/user/model/user-role.model';
import { SiteAdminContext } from '../../site-admin-context';

@Component({
    selector: 'jhi-users-pane',
    templateUrl: 'users-pane.component.html',
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
    showConfirmStatusDialog = false;
    showErrorNotification = false;
    confirmStatusTitle = 'Confirm';
    confirmMessage = 'Please confirm that you would like to deactivate/activate this user account.';
    user: User;
    originalUser: User;

    public roles: Role[];
    public userSelected: User;
    public crops: Crop[] = [];
    private message: string;

    // TODO upgrade angular, use ngIf-else and async pipe
    private isLoading: boolean;

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
    }

    editUser(user: User) {
        this.context.user = Object.assign({}, user);
        this.context.user.crops = user.crops.map((x) => Object.assign({}, x));
        this.context.user.userRoles = user.userRoles.map((x) => Object.assign({}, x));
        this.router.navigate(['/', { outlets: { popup: 'user-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    addUser() {
        this.context.user = new User(null, '', '', '', [], [], '', true, false);
        this.router.navigate(['/', { outlets: { popup: 'user-edit-dialog' }, }], { queryParamsHandling: 'merge' });
    }

    ngOnInit() {
        // get all users
        this.loadAll(this.request);
        const roleFilter = new RoleFilter();
        // get all roles
        this.roleService
            .searchRoles(roleFilter, null)
            .subscribe(
                (roles) => this.roles = roles.body,
                (error) => {
                    // XXX
                    // handleReAuthentication is called on
                    // userService error
                });
        this.cropService
            .getAll()
            .subscribe((crops) => {
                this.crops = crops;
                this.cropService.crops = this.crops;
            });

        this.userService.onUserAdded.subscribe((user) => {
                this.message = `${user.username} user was successfully saved!`;
                this.loadAll(this.request);
                // this.sortAfterAddOrEdit();
            }
        );

        this.userService.onUserUpdated.subscribe((user) => {
                this.message = `${user.username} user was successfully updated!`;
                this.loadAll(this.request);
                // this.sortAfterAddOrEdit();
            }
        );
    }

    private loadAll(request: UserSearchRequest) {
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

    changedActiveStatus() {
        const status = this.userSelected.active;
        this.userSelected.active = !status;
        this.userService
            .update(this.userSelected)
            .subscribe(
                (resp) => {
                    this.userSelected = null;
                },
                (error) => {
                    this.errorServiceMessage = error.json().errors[0].message;
                    this.showErrorNotification = true;
                    this.userSelected.active = status;
                    this.userSelected = null;
                    this.alertService.error('error.custom', { param: this.errorServiceMessage });
                });

        this.showConfirmStatusDialog = false;

    }

    showUserStatusConfirmPopUp(e: any) {
        this.userSelected = e;
        this.showConfirmStatusDialog = true;
        this.confirmMessage = 'Please confirm that you would like to ';

        if (e.active === true) {
            this.confirmMessage = this.confirmMessage + 'deactivate this user account.';
        } else {
            this.confirmMessage = this.confirmMessage + 'activate this user account.';
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Confirmation';
        confirmModalRef.componentInstance.message = this.confirmMessage;
        confirmModalRef.result.then(() => {
            this.changedActiveStatus();
        }, () => confirmModalRef.dismiss());
    }

    closeUserStatusConfirmPopUp() {
        this.showConfirmStatusDialog = false;
    }

    getCropsTitleFormat(crops) {
        return crops.map((crop) => crop.cropName).splice(1).join(' and ');
    }

    getRoleNamesTitleFormat(roles: string[]) {
        return roles.splice(1).join(' and ');
    }

    hasSuperAdminRole(userRoles) {
        return userRoles.some((userRole) => userRole.role.name === 'SuperAdmin');
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
        this.eventSubscriber = this.eventManager.subscribe('userViewChanged', (event) => {
            this.loadAll(this.request);
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
    'USER_NAME' = 'username',
    'FIRST_NAME' = 'person.firstName',
    'LAST_NAME' = 'person.lastName',
    'EMAIL' = 'person.email',
    'STATUS' = 'status',
}
