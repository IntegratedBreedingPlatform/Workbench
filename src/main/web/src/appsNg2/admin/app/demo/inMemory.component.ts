import { Component, OnInit } from '@angular/core';
import { NgDataGridModel } from './../datagrid/ng-datagrid.model';
import { PaginationComponent } from './../datagrid/pagination.component';
import { User } from './inMemory.model';
import { FORM_DIRECTIVES } from '@angular/forms'; 
import './../utils/array.extensions';

@Component({
    selector: 'in-memory-demo',
    templateUrl: 'inMemory.component.html',
    directives: [ PaginationComponent, FORM_DIRECTIVES],
    moduleId: module.id
})
export class InMemoryComponent implements OnInit {
    table: NgDataGridModel<User>;
    recentlyRemoveUsers: any[];

    constructor() {
        this.table = new NgDataGridModel<User>([]);
        this.table.pageSize = 10;
        var json = {
            "data": [
                {
                    "username": "user1",
                    "firstName": "name1",
                    "lastName": "lastName1",
                    "email": "name1@gmail.com",
                    "status": "false",
                    "role": "Breeder"
                },
                {
                    "username": "user2",
                    "firstName": "name2",
                    "lastName": "lastName2",
                    "email": "name2@gmail.com",
                    "status": "true",
                    "role": "Breeder"
                },
                {
                    "username": "user3",
                    "firstName": "name3",
                    "lastName": "lastName3",
                    "email": "name3@gmail.com",
                    "status": "true",
                    "role": "Technician"
                },
                {
                    "username": "user4",
                    "firstName": "name4",
                    "lastName": "lastName4",
                    "email": "name4@gmail.com",
                    "status": "true",
                    "role": "Admin"
                },
                {
                    "username": "user1",
                    "firstName": "name1",
                    "lastName": "lastName1",
                    "email": "name1@gmail.com",
                    "status": "false",
                    "role": "Admin"
                },
                {
                    "username": "user2",
                    "firstName": "name2",
                    "lastName": "lastName2",
                    "email": "name2@gmail.com",
                    "status": "true",
                    "role": "Breeder"
                },
                {
                    "username": "user3",
                    "firstName": "name3",
                    "lastName": "lastName3",
                    "email": "name3@gmail.com",
                    "status": "false",
                    "role": "Technician"
                },
                {
                    "username": "user4",
                    "firstName": "name4",
                    "lastName": "lastName4",
                    "email": "name4@gmail.com",
                    "status": "true",
                    "role": "Admin"
                },
                {
                    "username": "user1",
                    "firstName": "name1",
                    "lastName": "lastName1",
                    "email": "name1@gmail.com",
                    "status": "true",
                    "role": "Breeder"
                },
                {
                    "username": "user2",
                    "firstName": "name2",
                    "lastName": "lastName2",
                    "email": "name2@gmail.com",
                    "status": "true",
                    "role": "Breeder"
                },
                {
                    "username": "user3",
                    "firstName": "name3",
                    "lastName": "lastName3",
                    "email": "name3@gmail.com",
                    "status": "true",
                    "role": "Technician"
                },
                {
                    "username": "user4",
                    "firstName": "name4",
                    "lastName": "lastName4",
                    "email": "name4@gmail.com",
                    "status": "true",
                    "role": "Admin"
                }
            ]
        };
        json.data.forEach(item=> {
          this.table.items.push(new User(item.firstName, item.lastName, item.username,
                                        item.role, item.email, item.status));

        });
    }

    ngOnInit() { }

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
