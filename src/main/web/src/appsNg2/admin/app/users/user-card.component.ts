import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import {
    Validators
} from '@angular/forms';
import { User } from '../shared/models/user.model';
import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';

@Component({
    selector: 'user-card',
    templateUrl: './user-card.component.html',
    moduleId: module.id
})

export class UserCard implements OnInit {
    submitted = false;
    @Input() originalUser: User;
    @Input() isEditing: boolean;
    @Input() model: User;
    @Input() userSaved: boolean;
    @Output() onUserAdded = new EventEmitter<User>();
    @Output() onUserEdited = new EventEmitter<User>();
    @Output() onCancel = new EventEmitter<void>();

    constructor(private userService: UserService, private roleService: RoleService) {
        this.model = new User("0", "", "", "", "", "", "");
    }

    /**
     * Init state except the model
     */
    initialize() {
        this.userSaved = false;
    }

    /*
    resetForm() {
        // see https://angular.io/docs/ts/latest/guide/forms.html#!#add-a-hero-and-reset-the-form
        this.activeForm = false;
        setTimeout(() => this.activeForm = true, 0);
    }
    */

    onSubmit() { this.submitted = true; }
    cancel() { 
            this.onCancel.emit();
             }

    ngOnInit() {
    }

    addUser() {
        this.userService
            .save(this.model)
            .subscribe(
                resp => {
                    this.userSaved = true;
                    setTimeout(() => {
                        this.model.id = resp.json().id;
                        this.onUserAdded.emit(this.model);
                    }, 1000)
                },
                err => console.log(err)
            )
    }


    editUser() {
        this.userService
            .update(this.model)
            .subscribe(
                resp => {
                    this.userSaved = true;
                    setTimeout(() => {
                        this.onUserEdited.emit(this.model);
                    }, 1000)
                },
                err => console.log(err)
            )
    }
}
