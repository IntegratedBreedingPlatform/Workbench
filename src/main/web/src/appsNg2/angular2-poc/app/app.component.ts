import { Component } from '@angular/core';

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    moduleId: module.id
})
export class AppComponent {
    users = [
        {
            'Username': 'jean',
            'First Name': 'Jean',
            'Last Name': 'Phillips',
            'Email': 'jean@leafnode.io',
            'Role': 'Admin',
            'Status': 'Deactivate'
        }, {
            'Username': 'matthew',
            'First Name': 'Matthew',
            'Last Name': 'Berrigan',
            'Email': 'matthew@leafnode.io',
            'Role': 'Breeder',
            'Status': 'Deactivate'
        }, {
            'Username': 'rachita',
            'First Name': 'Rachita',
            'Last Name': 'Sharma',
            'Email': 'rachita@leafnode.io',
            'Role': 'Technician',
            'Status': 'Deactivate'
        }
    ];
    columns = ['Username', 'First Name', 'Last Name', 'Email', 'Role', 'Status'];
}


/*
Copyright 2016 Google Inc. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at http://angular.io/license
*/
