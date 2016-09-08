/// <reference path="../../../../../typings/globals/node/index.d.ts" />

import { Component, OnInit } from '@angular/core';
import { NgDataGridModel } from './../datagrid/ng-datagrid.model';
import { PaginationComponent } from './../datagrid/pagination.component';
import { User } from './inMemory.model';
import { FORM_DIRECTIVES } from '@angular/forms';
import './../utils/array.extensions';
import { UserService } from './user.service';

@Component({
    selector: 'in-memory-demo',
    templateUrl: 'inMemory.component.html',
    styleUrls: [
        './inMemory.component.css'
    ],
    directives: [PaginationComponent, FORM_DIRECTIVES],
    moduleId: module.id
})
export class InMemoryComponent implements OnInit {
    table: NgDataGridModel<User>;
    recentlyRemoveUsers: any[];
    errorMessage: string = '';

    constructor(private userService : UserService) {
        this.table = new NgDataGridModel<User>([]);
        this.table.pageSize = 5;
        

    }

    ngOnInit() { 
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
