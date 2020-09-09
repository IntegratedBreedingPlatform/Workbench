/// <reference path="../../../../../typings/globals/jasmine/index.d.ts" />

import { PermissionTree } from './role-card.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Permission } from '../shared/models/permission.model';
import { FormsModule } from '@angular/forms';
import { RoleService } from '../shared/services/role.service';

export function main() {

    let onPermissionSelectedSpy = jasmine.createSpyObj('onPermissionSelected', ['next']);
    let onRoleAddedSpy = jasmine.createSpyObj('onRoleAdded', ['next']);

    class RoleServiceMock  {
        onPermissionSelected = onPermissionSelectedSpy;
        onRoleAdded = onRoleAddedSpy;
    }

    describe('RoleCardComponent', () => {
        describe('Permission tree', () => {

            let fixture: ComponentFixture<PermissionTree>;
            let permissionTree: PermissionTree;

            beforeEach(() => {
                TestBed.configureTestingModule({
                    declarations: [PermissionTree],
                    imports: [FormsModule],
                    providers: [{provide: RoleService, useClass: RoleServiceMock}]
                });
                fixture = TestBed.createComponent(PermissionTree);
                permissionTree = fixture.componentInstance;
            });

            /**
             * Test the class
             */
            describe('Class', () => {
                it('should hide checkbox if not selectable', () => {
                    expect(permissionTree.isShowCheckbox({ selectable: true })).toBe(true);
                    expect(permissionTree.isShowCheckbox({ selectable: false })).toBe(false);
                });

                it('should hide checkbox if not selectable', () => {
                    let permission = getPermissionMock();
                    permissionTree.onPermissionClick({ currentTarget: { checked: true } }, permission);
                    expect(permission.children[1].children[1].selected).toBe(true); // MANAGE_STUDIES
                    expect(permission.children[0].children[0].children[0].selected).toBe(true); // MANAGE_PROGRAM_SETTINGS
                    expect(onPermissionSelectedSpy.next).toHaveBeenCalledTimes(8);
                });
            });

            /**
             * Test the component
             */
            describe('Component', () => {

                it('should render recursively', () => {
                    permissionTree.permissions = [getPermissionMock()];
                    fixture.detectChanges();
                    expect(permissionTree).toBeDefined();
                    let treeCount = fixture.debugElement.nativeElement.querySelectorAll('permission-tree').length;
                    expect(treeCount).toBe(5); // root tree + 4 sub-trees (children)
                });

            });
        });
    });

    function getPermissionMock() {
        return <Permission>({
            id: '1',
            name: 'FULL',
            selectable: true,
            children: [
                {
                    id: '2',
                    name: 'CROP_MANAGEMENT',
                    description: 'Crop management',
                    selectable: false,
                    children: [
                        {
                            id: '4',
                            name: 'MANAGE_PROGRAM',
                            description: 'Manage program',
                            selectable: false,
                            children: [
                                {
                                    id: '13',
                                    name: 'MANAGE_PROGRAM_SETTINGS',
                                    description: 'Manage program settings',
                                    selectable: true,
                                }
                            ]
                        }
                    ],
                },
                {
                    id: '6',
                    name: 'STUDIES',
                    description: 'Breeding activities',
                    selectable: false,
                    children: [
                        {
                            id: '14',
                            name: 'MANAGE_GERMPLASM',
                            description: 'Manage Germplasm',
                            selectable: true,
                            children: [
                                {
                                    id: '14',
                                    name: 'DELETE_GERMPLASM',
                                    description: 'Delete Germplasm',
                                    selectable: true,
                                },
                            ]
                        },
                        {
                            id: '16',
                            name: 'MANAGE_STUDIES',
                            description: 'Manage Studies',
                            selectable: true,
                        },
                    ],
                },
            ]
        });
    }
}
