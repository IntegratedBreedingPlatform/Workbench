import { Component, OnInit } from '@angular/core';
import { NgDataGridModel } from './../shared/components/datagrid/ng-datagrid.model';
import { User } from './../shared/models/user.model';
import { Role } from './../shared/models/role.model';
import './../shared/utils/array.extensions';
import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { UserComparator } from './user-comparator.component';
import { Crop } from '../shared/models/crop.model';
import { CropService } from '../shared/services/crop.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'users-datagrid',
    templateUrl: 'users-datagrid.component.html',
    styleUrls: [
        './users-datagrid.component.css'
    ],
    moduleId: module.id
})

export class UsersDatagrid implements OnInit {

    errorServiceMessage: string = '';
    isEditing = false;
    dialogTitle: string;
    showConfirmStatusDialog = false;
    showErrorNotification = false;
    confirmStatusTitle: string = 'Confirm';
    table: NgDataGridModel<User>;
    confirmMessage: string = 'Please confirm that you would like to deactivate/activate this user account.';
    user: User;
    originalUser: User;

    public roles: Role[];
    public userSelected: User;
    public crops: Crop[] = [];

    constructor(private userService: UserService,
                private roleService: RoleService,
                private cropService: CropService, private router: Router, private activatedRoute: ActivatedRoute) {
        // TODO migrate to angular datatables
        this.table = new NgDataGridModel<User>([], 25, new UserComparator(), <User>{ status: 'true' });
        this.initUser();
    }

    showNewUserForm() {
        this.userService.user = new User('0', '', '', '', [], [], '', 'true');;
        this.router.navigate(['user-card', { isEditing: false }], { relativeTo: this.activatedRoute });

    }

    showEditUserForm(user: User) {
        this.originalUser = user;
        this.userService.user = new User(user.id, user.firstName, user.lastName,
            user.username, user.crops, user.userRoles.map((x) => Object.assign({}, x)), user.email, user.status);
        this.router.navigate(['user-card', { isEditing: true }], { relativeTo: this.activatedRoute });

    }

    initUser() {
        this.user = new User('0', '', '', '', [], [], '', 'true');
    }

    ngOnInit() {
        if (this.table.sortBy == undefined) {
            this.table.sortBy = 'lastName';
        }
        //get all users
        this.loadTheUsersDataGridTable();

        // get all roles
        this.roleService
            .getFilteredRoles(null)
            .subscribe(
                roles => this.roles = roles,
                error => {
                    // XXX
                    // handleReAuthentication is called on
                    // userService error
                });
        this.cropService
            .getAll()
            .subscribe(crops => {
                this.crops = crops;
                this.cropService.crops = this.crops;
            });

        this.userService.onUserAdded.subscribe(() => {
            this.loadTheUsersDataGridTable();
            this.sortAfterAddOrEdit();
            }
        );

        this.userService.onUserUpdated.subscribe(() => {
            this.loadTheUsersDataGridTable();
            this.sortAfterAddOrEdit();
            }
        );
    }

    private loadTheUsersDataGridTable() {
        this.userService
            .getAll()
            .subscribe(
                users => this.table.items = users,
                error => {
                    this.errorServiceMessage = error;
                    if (error.status === 401) {
                        localStorage.removeItem('xAuthToken');
                        this.handleReAuthentication();
                    }
                });
    }

    sortAfterAddOrEdit() {
        if (this.table.sortBy == undefined) {
            this.sort('lastName', true);
        } else {
            this.sort(this.table.sortBy, true);
        }
    }

    // TODO
    // - Move to interceptor
    // - see /ibpworkbench/src/main/web/src/apps/ontology/app-services/bmsAuth.js
    handleReAuthentication() {
        alert('Site Admin needs to authenticate you again. Redirecting to login page.');
        window.top.location.href = '/ibpworkbench/logout';
    }

    isSorted(col): boolean {
        return col == this.table.sortBy;
    }

    sort(col, direction?, event?: MouseEvent): void {
        if (event) {
            event.preventDefault();
        }

        if (!direction) {
            if (this.table.sortBy == col) {
                this.table.sortAsc = !this.table.sortAsc;
            } else {
                this.table.sortAsc = true;
            }
        }

        this.table.sortBy = col;
    }

    changedActiveStatus() {
        var status = this.userSelected.status;

        // TODO Change status to boolean or int as in the backend
        if (this.userSelected.status === 'true') {
            this.userSelected.status = 'false';
        } else {
            this.userSelected.status = 'true';
        }

        this.userService
            .update(this.userSelected)
            .subscribe(
                resp => {
                    this.userSelected = null;
                },
                error => {
                    this.errorServiceMessage = error.json().ERROR.errors[0].message;
                    this.showErrorNotification = true;
                    this.userSelected.status = status;
                    this.userSelected = null;
                });

        this.showConfirmStatusDialog = false;

    }

    showUserStatusConfirmPopUp(e: any) {
        this.userSelected = e;
        this.showConfirmStatusDialog = true;
        this.confirmMessage = 'Please confirm that you would like to ';

        if (e.status === 'true') {
            this.confirmMessage = this.confirmMessage + 'deactivate this user account.';
        } else {
            this.confirmMessage = this.confirmMessage + 'activate this user account.';
        }
    }

    closeUserStatusConfirmPopUp() {
        this.showConfirmStatusDialog = false;
    }

    getCropsTitleFormat(crops) {
        return crops.map((crop) => crop.cropName).splice(1).join(' and ');
    }

    getRoleNamesTitleFormat(userRoles) {
        return userRoles.map((userRole) => userRole.role.name).splice(1).join(' and ');
    }

}
