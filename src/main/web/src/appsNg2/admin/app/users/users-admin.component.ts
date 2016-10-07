import { Component, OnInit } from '@angular/core';
import { UsersDatagrid } from './users-datagrid.component';

@Component({
    selector: 'users-admin',
    templateUrl: './users-admin.component.html',
    moduleId: module.id
})

export class UsersAdmin implements OnInit {
    recentlyRemoveUsers: any[];
    recentlyRemoveUsersPluginServer: any[];
    private userId: number = 0;

    constructor() {
    }

    ngOnInit() {
    }
}
