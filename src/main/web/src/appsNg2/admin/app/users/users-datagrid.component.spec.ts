import { Component, OnInit, ViewChild } from '@angular/core';
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
import { UsersDatagrid } from './users-datagrid.component';

export function main() {

  describe('User Datagrid Test', () => {
      let items: User[];
      let grid : UsersDatagrid;

      let userService: UserService;

      let roleService: RoleService

      function createArrayOfUsers () {
          return [ new User("0", "", "", "", "", "", "")
                 ];
      }

      beforeEach(() => {
        items = createArrayOfUsers();
        grid = new UsersDatagrid (userService, roleService);
        grid.table.items = items;
      });

      it('should have items', () => {
        expect(grid.table.items).not.toBe(null);
      });

    });
  }
