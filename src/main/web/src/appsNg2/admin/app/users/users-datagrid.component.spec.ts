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
import { inject, async, TestBed, ComponentFixture } from "@angular/core/testing";
import { Observable } from 'rxjs/Rx';
import { Http, Response, ResponseOptions, Headers } from '@angular/http';

export function main() {

  class MockUserService extends UserService {
    constructor() {
      super(null);
    }

    getAll(): Observable<User[]> {
      return Observable.of([new User("0", "Vanina", "Maletta", "vmaletta", "technician", "vanina@leafnode.io", "0"),
        new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0")]);
    }

    update(user: User): Observable<Response> {
      var options = new ResponseOptions({
        body: {
          "id": "1"
        }
      });
      var response = new Response(options);
      return Observable.of(response);
    }

  }

  class MockRoleService extends RoleService {
    constructor() {
      super(null);
    }

    getAll(): Observable<Role[]> {
      return Observable.of([new Role("0", "admin"),
        new Role("1", "breeder"),
        new Role("2", "technician")]);
    }

  }

  describe('User Datagrid Test', () => {
    let items: User[];
    let grid: UsersDatagrid;
    let userCard: UserCard;
    let userService: UserService;
    let mailService: MailService;
    let roleService: RoleService;
    let user: User;
    let mockRoleService: MockRoleService;
    let mockUserService: MockUserService;

    function createArrayOfUsers() {
      return [new User("0", "Vanina", "Maletta", "vmaletta", "technician", "vanina@leafnode.io", "0"),
        new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0")
      ];
    }

    beforeEach(() => {
      items = createArrayOfUsers();
      mockRoleService = new MockRoleService();
      mockUserService = new MockUserService();
      user = new User("3", "Diego", "Cuenya", "dcuenya", "breeder", "dcuenya@leafnode.io", "0");
      grid = new UsersDatagrid(mockUserService, mockRoleService);
      grid.table.items = items;
    });

    it('should have items', () => {
      expect(grid.table.items).not.toBe(null);
    });

    it('check usernames', () => {
      expect(grid.table.items[0].username).toBe('vmaletta');
      expect(grid.table.items[1].username).toBe('ctovar');
    });

    it('Should match the total rows', function () {
      expect(grid.table.totalRows).toBe(items.length);
    });

    it('Should get startRow equals to 0', function () {
      grid.table.currentPageIndex = 0;
      expect(grid.table.startRow).toBe(0);
    });

    it('Should get items.length equals to 3', function () {
      expect(grid.table.items.length).toBe(2);
    });

    it('Should get items.length equals to 2', function () {
      grid.table.items = [new User("0", "Vanina", "Maletta", "vmaletta", "admin", "vanina@leafnode.io", "0"),
        new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0")];
      expect(grid.table.items.length).toBe(2);
    });

    it('Should filter by typeToSearch equals to A', function () {
      grid.table.sortBy = 'role';
      grid.table.searchValue = new User("1", "Clarysabel", "Tovar", "ctovar", "admin", "clarysabel@leafnode.io", "0");
      expect(grid.table.itemsFiltered.length).toBe(1);
    });

    it('Should open add user popup', function () {
      userCard = new UserCard(userService, roleService, mailService);
      grid.showNewUserForm(userCard);
      expect(grid.showNewDialog).toBe(true);
    });

    it('Should open edit user popup', function () {
      userCard = new UserCard(userService, roleService, mailService);
      grid.showEditUserForm(user, userCard);
      expect(grid.showEditDialog).toBe(true);
    });

    it('Should close add user popup', function () {
      grid.onUserAdded(user);
      expect(grid.showNewDialog).toBe(false);
    });

    it('Should close edit user popup', function () {
      grid.onUserEdited(user);
      expect(grid.showEditDialog).toBe(false);
    });

    it('Should init user', function () {
      grid.initUser();
      expect(grid.user.id).toBe("0");
    });

    it('Should return is sorted by specific column', function () {
      grid.table.sortBy = "firstName";
      expect(grid.isSorted("firstName")).toBe(true);
    });

    it('Should sort by specific column', function () {
      grid.sort("username");
      expect(grid.isSorted("username")).toBe(true);
    });

    it('Should sort after edit or add user', function () {
      grid.sortAfterAddOrEdit();
      expect(grid.isSorted("lastName")).toBe(true);

      grid.table.sortBy = "username";
      grid.sortAfterAddOrEdit();
      expect(grid.isSorted("username")).toBe(true);
    });

    it('Should get all users and all roles', function () {
      grid.ngOnInit();
      expect(grid.table.items.length).toBe(2);
      expect(grid.roles.length).toBe(3);
    });

    /*it ('Should handleReAuthentication', function () {
       grid.handleReAuthentication();
       expect(window.location).toBeDefined();
       expect (window.top.location.href).toContain('/ibpworkbench/logout');
    });*/

    it('Should open user confirm status popup', function () {
      user = new User("2", "Clarysabel2", "Tovar2", "ctovar2", "admin2", "clarysabel2@leafnode.io", "true")
      grid.showUserStatusConfirmPopUp(user);
      expect(grid.showConfirmStatusDialog).toBe(true);
    });

    it('Should say the dialog popup', function () {
      let userChangeStatus = new User("2", "Clarysabel2", "Tovar2", "ctovar2", "admin2", "clarysabel2@leafnode.io", "true")
      grid.showUserStatusConfirmPopUp(userChangeStatus);
      expect(grid.confirmMessage).toBe("Please confirm that you would like to deactivate this user account.");
    });

    it('Should close user confirm status popup', function () {
      grid.closeUserStatusConfirmPopUp();
      expect(grid.showConfirmStatusDialog).toBe(false);
    });

    it('Should update user status when accept confirm status popup', function () {
      let userChangeStatus = new User("2", "Clarysabel2", "Tovar2", "ctovar2", "admin2", "clarysabel2@leafnode.io", "true")
      grid.showUserStatusConfirmPopUp(userChangeStatus);
      grid.changedActiveStatus();
      expect(grid.showConfirmStatusDialog).toBe(false);
    });
  });
}
