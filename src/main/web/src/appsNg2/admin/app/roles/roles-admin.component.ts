import { Component, OnInit } from '@angular/core';
import { RoleService } from '../shared/services/role.service';

@Component({
    selector: 'roles-admin',
    templateUrl: './roles-admin.component.html',
    moduleId: module.id
})

export class RolesAdmin implements OnInit {
    constructor(private roleService: RoleService) {
    }

    ngOnInit() {
    }
}
