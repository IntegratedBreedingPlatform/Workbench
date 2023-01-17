/// <reference path="../../../../../typings/globals/jasmine/index.d.ts" />

import { User } from './../shared/models/user.model';
import { Role } from './../shared/models/role.model';
import './../shared/utils/array.extensions';
import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { UsersDatagrid } from './users-datagrid.component';
import { UserCard } from './user-card.component';
import { MailService } from './../shared/services/mail.service';
import { Observable } from 'rxjs/Rx';
import { Response, ResponseOptions } from '@angular/http';
import { Crop } from '../shared/models/crop.model';
import { CropService } from '../shared/services/crop.service';

export function main() {

    class MockUserService extends UserService {
        constructor() {
            super(null);
        }

        getAll(): Observable<User[]> {
            return Observable.of([new User('0', 'Vanina', 'Maletta', 'vmaletta', [], [], 'vanina@leafnode.io', true, true),
                new User('1', 'Clarysabel', 'Tovar', 'ctovar', [], [], 'clarysabel@leafnode.io', true, true)]);
        }

        update(user: User): Observable<Response> {
            var options = new ResponseOptions({
                body: {
                    'id': '1'
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

        getFilteredRoles(): Observable<Role[]> {
            return Observable.of([new Role(0, 'admin', 'instance'),
                new Role(1, 'breeder', 'crop'),
                new Role(2, 'technician', 'program')]);
        }

    }

    class MockCropsService extends CropService {
        constructor() {
            super(null);
        }

        getAll() {
            return Observable.of([new Crop('maize'),
                new Crop('wheat')]);
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
        let mockCropsService: MockCropsService;

        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
        const routeSpy = jasmine.createSpy('Route');

        function createArrayOfUsers() {
            return [new User('0', 'Vanina', 'Maletta', 'vmaletta', [], [], 'vanina@leafnode.io', true, true),
                new User('1', 'Clarysabel', 'Tovar', 'ctovar', [], [], 'clarysabel@leafnode.io', false, true)
            ];
        }

        beforeEach(() => {
            items = createArrayOfUsers();
            mockRoleService = new MockRoleService();
            mockUserService = new MockUserService();
            user = new User('3', 'Diego', 'Cuenya', 'dcuenya', [], [], 'dcuenya@leafnode.io', true, true);
            grid = new UsersDatagrid(mockUserService, mockRoleService, new MockCropsService(), routerSpy, routeSpy);
            grid.table.items = items;
        });

        it('should have users', () => {
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

        it('Should get number of users in grid equals to 3', function () {
            expect(grid.table.items.length).toBe(2);
        });

        it('Should get number of users in grid equals to 2', function () {
            grid.table.items = [new User('0', 'Vanina', 'Maletta', 'vmaletta', [], [], 'vanina@leafnode.io', true, true),
                new User('1', 'Clarysabel', 'Tovar', 'ctovar', [], [], 'clarysabel@leafnode.io', true, true)];
            expect(grid.table.items.length).toBe(2);
        });

        it('Should filter by typeToSearch equals to A', function () {
            grid.table.sortBy = 'lastName';
            grid.table.searchValue = <User>{ active: true };
            expect(grid.table.itemsFiltered.length).toBe(1);
        });

        it('Should open add user popup', function () {
            grid.showNewUserForm();
            pending(); // TODO implement using router
        });

        it('Should open edit user popup', function () {
            // initialize to retrieve list of roles
            grid.ngOnInit();
            grid.showEditUserForm(user);
            pending(); // TODO implement using router
        });

        it('Should close add user popup', function () {
            pending(); // TODO implement using router
        });

        it('Should close edit user popup', function () {
            pending(); // TODO implement using router
        });

        it('Should init user', function () {
            pending(); // TODO implement in user-card
        });

        it('Should return is sorted by specific column', function () {
            grid.table.sortBy = 'firstName';
            expect(grid.isSorted('firstName')).toBe(true);
        });

        it('Should sort by specific column', function () {
            grid.sort('username');
            expect(grid.isSorted('username')).toBe(true);
        });

        it('Should sort after edit or add user', function () {
            grid.sortAfterAddOrEdit();
            expect(grid.isSorted('lastName')).toBe(true);

            grid.table.sortBy = 'username';
            grid.sortAfterAddOrEdit();
            expect(grid.isSorted('username')).toBe(true);
        });

        it('Should get all users and all roles', function () {
            grid.ngOnInit();
            expect(grid.table.items.length).toBe(2);
            expect(grid.roles.length).toBe(3);
        });

        it('Should open user confirm status popup', function () {
            user = new User('2', 'Clarysabel2', 'Tovar2', 'ctovar2', [], [], 'clarysabel2@leafnode.io', true, true);
            grid.showUserStatusConfirmPopUp(user);
            expect(grid.showConfirmStatusDialog).toBe(true);
        });

        it('Should say the dialog popup', function () {
            let userChangeStatus = new User('2', 'Clarysabel2', 'Tovar2', 'ctovar2', [], [], 'clarysabel2@leafnode.io', true, true);
            grid.showUserStatusConfirmPopUp(userChangeStatus);
            expect(grid.confirmMessage).toBe('Please confirm that you would like to deactivate this user account.');
        });

        it('Should close user confirm status popup', function () {
            grid.closeUserStatusConfirmPopUp();
            expect(grid.showConfirmStatusDialog).toBe(false);
        });

        it('Should update user status when accept confirm status popup', function () {
            let userChangeStatus = new User('2', 'Clarysabel2', 'Tovar2', 'ctovar2', [], [], 'clarysabel2@leafnode.io', true, true);
            grid.showUserStatusConfirmPopUp(userChangeStatus);
            grid.changedActiveStatus();
            expect(grid.showConfirmStatusDialog).toBe(false);
        });
    });
}
