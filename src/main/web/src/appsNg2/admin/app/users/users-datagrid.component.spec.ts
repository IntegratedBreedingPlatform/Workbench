import { Component, OnInit, ViewChild } from '@angular/core';
import { NgDataGridModel } from './../shared/components/datagrid/ng-datagrid.model';
import { PaginationComponent } from './../shared/components/datagrid/pagination.component';
import { User } from './../shared/models/user.model';
import { Role } from './../shared/models/role.model';
import './../shared/utils/array.extensions';
import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { Dialog } from './../shared/components/dialog/dialog.component';
import {UserComparator} from './user-comparator.component';
import { UsersDatagrid } from './users-datagrid.component';
import { UserCard } from './user-card.component';
import { MailService } from './../shared/services/mail.service';
import { inject, async, TestBed , ComponentFixture } from "@angular/core/testing";

export function main() {

  describe('User Datagrid Test', () => {
      let items: User[];
      let grid : UsersDatagrid;
      let userCard : UserCard;
      let userService: UserService;
      let mailService : MailService;
      let roleService: RoleService
      let user : User;

      function createArrayOfUsers () {
          return [ new User("0", "Vanina", "Maletta", "vmaletta", "technician", "vanina@leafnode.io", "0"),
                    new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0")
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

      it('check usernames', () => {
        expect (grid.table.items[0].username).toBe('vmaletta');
        expect (grid.table.items[1].username).toBe('ctovar');
      });

      it ('Should match the total rows', function() {
        expect (grid.table.totalRows).toBe(items.length);
      });

      it ('Should get startRow equals to 0', function () {
        grid.table.currentPageIndex = 0;
        expect (grid.table.startRow).toBe(0);
      });

      it ('Should get items.length equals to 3', function () {
        expect (grid.table.items.length).toBe(2);
      });

      it ('Should get items.length equals to 2', function () {
        grid.table.items = [ new User("0", "Vanina", "Maletta", "vmaletta", "admin", "vanina@leafnode.io", "0"),
                  new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0")];
        expect (grid.table.items.length).toBe(2);
      });

      it ('Should filter by typeToSearch equals to A', function () {
        grid.table.sortBy = 'role';
        grid.table.searchValue = new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0");
        expect (grid.table.itemsFiltered.length).toBe(1);
      });

      it ('Should open add user popup', function () {
         userCard = new UserCard(userService, roleService, mailService);
         grid.showNewUserForm(userCard);
        expect (grid.showNewDialog).toBe(true);
      });

      it ('Should open edit user popup', function () {
         user = new User("2", "Clarysabel2", "Tovar2", "ctovar2", "admin2", "clarysabel2@leafnode.io", "0")        
         userCard = new UserCard(userService, roleService, mailService);
         grid.showEditUserForm(user, userCard);
         expect (grid.showEditDialog).toBe(true);
      });
    });
  }