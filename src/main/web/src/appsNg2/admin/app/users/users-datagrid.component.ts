import { Component, OnInit } from '@angular/core';
import { NgDataGridModel } from './../shared/components/datagrid/ng-datagrid.model';
import { PaginationComponent } from './../shared/components/datagrid/pagination.component';
import { User } from './../shared/models/user.model';
import { Role } from './../shared/models/role.model';
import { FORM_DIRECTIVES } from '@angular/forms';
import './../shared/utils/array.extensions';
import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { Dialog } from './../shared/components/dialog/dialog.component';
import {UserComparator} from './user-comparator.component';
import { UserCard } from './user-card.component';

@Component({
    selector: 'users-datagrid',
    templateUrl: 'users-datagrid.component.html',
    styleUrls: [
        './users-datagrid.component.css'
    ],
    directives: [PaginationComponent, FORM_DIRECTIVES, Dialog, UserCard],
    moduleId: module.id
})

export class UsersDatagrid implements OnInit {
    showDialog = false;
    dialogTitle: string = "Add User";
    table: NgDataGridModel<User>;
    recentlyRemoveUsers: any[];
    errorMessage: string = '';
    private roles: Role[];

    constructor(private userService : UserService, private roleService : RoleService) {
        this.table = new NgDataGridModel<User>([], 25, new UserComparator());
     }

    ngOnInit() {
        //get all users
        this.userService
            .getAll()
            .subscribe(
                users => this.table.items = users,
                error => {
                    this.errorMessage = error;
                    if (error.status === 401) {
                        localStorage.removeItem('xAuthToken');
                        this.handleReAuthentication();
                    }
            });

        // get all roles
        this.roleService
            .getAll()
            .subscribe(
                roles => this.roles = roles,
                error => {
                    // TODO
            });
    }

    // TODO
    // - Move to a shared component
    // - see /ibpworkbench/src/main/web/src/apps/ontology/app-services/bmsAuth.js
    handleReAuthentication() {
        alert('Site Admin needs to authenticate you again. Redirecting to login page.');
        window.top.location.href = '/ibpworkbench/logout';
    }

    isSorted(col): boolean {
        return col == this.table.sortBy;
    }

    sort(col, event?: MouseEvent): void {
        if (event) {
            event.preventDefault();
        }

        if (this.table.sortBy == col) {
            this.table.sortAsc = !this.table.sortAsc;
        } else {
            this.table.sortAsc = true;
        }

        this.table.sortBy = col;
    }

    addRecordPlugin() {
        //let userId = this.userId++;
        //this.table.items.push(new User(userId, `user ${userId}`, `username${userId}`));
    }

    removeRecordPlugin(item) {
        //this.recentlyRemoveUsers = this.table.items.remove(item);
    }

    removeAllEvenIdPlugin() {
        //this.recentlyRemoveUsers = this.table.items
        //    .remove(item => item.userId % 2 === 0);
    }

    changedActiveStatus(e: any) {
      //  if (confirm('Do you want to include all filtered items?')) {
    //        this.table.itemsFiltered.forEach(user => user.active = e.target.checked);
    //    } else {
    //        this.table.itemsOnCurrentPage.forEach(user => user.active = e.target.checked);
    //    }
    }
}
