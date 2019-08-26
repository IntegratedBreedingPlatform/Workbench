import { Component, OnInit } from '@angular/core';
import { NgDataGridModel } from '../shared/components/datagrid/ng-datagrid.model';
import { Role } from '../shared/models/role.model';
import { RoleService } from '../shared/services/role.service';
import { RoleComparator } from './role-comparator.component';
import { Router } from '@angular/router';
import { scrollTop } from '../shared/utils/scroll-top';
import 'rxjs/add/operator/toPromise';

@Component({
    selector: 'roles-datagrid',
    templateUrl: 'roles-datagrid.component.html',
    styleUrls: [
        './roles-datagrid.component.css'
    ],
    moduleId: module.id
})

export class RolesDatagrid implements  OnInit {

    table: NgDataGridModel<Role>;
    private message: string;

    // TODO upgrade angular, use ngIf-else and async pipe
    private loading: boolean;

    constructor(private roleService: RoleService,
                private router: Router) {

        this.table = new NgDataGridModel<Role>([], 25, new RoleComparator(), <Role>{ active: true });
    }

    ngOnInit() {
        if (!this.table.sortBy) {
            this.table.sortBy = 'name';
        }

        this.roleService.onRoleAdded.subscribe((role: Role) => {
            this.message = `${role.name} role was successfully saved!`;
            this.loadTable();
        });
        this.loadTable();
    }

    private loadTable() {
        this.loading = true;
        this.roleService.getFilteredRoles(null).toPromise().then(roles => {
            this.loading = false;
            this.table.items = roles;
        }, error => {
            this.loading = false;
            this.errorServiceMessage = error;
            if (error.status === 401) {
                localStorage.removeItem('xAuthToken');
                this.handleReAuthentication();
            }

        };
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

    getPermissionsTitleFormat(permissions) {
        return permissions.map((permission) => permission.description).splice(1).join(' and ');
    }

    isSuperAdminRole(role: Role) {
        return role.name === 'SuperAdmin';
    }

    editRole(role: Role) {
        this.router.navigate(['role-card', role.id], { queryParams: { isEditing: false } });
        scrollTop();
    }
}