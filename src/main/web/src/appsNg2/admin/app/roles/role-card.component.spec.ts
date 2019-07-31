/// <reference path="../../../../../typings/globals/jasmine/index.d.ts" />

import { PermissionTree } from './role-card.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Permission } from '../shared/models/permission.model';
import { FormsModule } from '@angular/forms';

export function main() {

    describe('RoleCardComponent', () => {
        describe('Permission tree', () => {

            /**
             * Test the class
             */
            describe('Class', () => {

                let permissionTree: PermissionTree;
                let permission: Permission;

                beforeEach(() => {
                    permissionTree = new PermissionTree();
                    permission = getPermissionMock();
                });

                it('should hide checkbox if not selectable', () => {
                    expect(permissionTree.isShowCheckbox({ selectable: true })).toBe(true);
                    expect(permissionTree.isShowCheckbox({ selectable: false })).toBe(false);
                });

                it('should hide checkbox if not selectable', () => {
                    permissionTree.onPermissionClick({ currentTarget: { checked: true } }, permission);
                    expect(permission.children[1].children[1].selected).toBe(true); // MANAGE_STUDIES
                    expect(permission.children[0].children[0].children[0].selected).toBe(true); // MANAGE_PROGRAM_SETTINGS
                });
            });

            /**
             * Test the component
             */
            describe('Component', () => {

                let fixture: ComponentFixture<PermissionTree>;
                let component: PermissionTree;

                beforeEach(() => {
                    TestBed.configureTestingModule({
                        declarations: [PermissionTree],
                        imports: [FormsModule]
                    });
                    fixture = TestBed.createComponent(PermissionTree);
                    component = fixture.componentInstance;
                });

                it('should render recursively', () => {
                    component.permissions = [getPermissionMock()];
                    fixture.detectChanges();
                    expect(component).toBeDefined();
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
                    name: 'BREEDING_ACTIVITIES',
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
