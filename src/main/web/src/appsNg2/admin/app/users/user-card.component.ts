import { Component, OnInit, EventEmitter, Output, Input, ViewChild } from '@angular/core';
import {
    Validators, FormGroup, FormControl
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
    errorMessage: string = '';
    submitted = false;
    @Input() originalUser: User;
    @Input() userSaved: boolean = false;
    @Input() isEditing: boolean;
    @Input() model: User;
    @Output() onUserAdded = new EventEmitter<User>();
    @Output() onUserEdited = new EventEmitter<User>();
    @Output() onCancel = new EventEmitter<void>();

    @ViewChild('form') form: FormGroup;

    constructor(private userService: UserService, private roleService: RoleService) {

    }

    /**
     * XXX
     * Reset form hack
     * The first call to initUser() is needed
     * when coming from edit user
     * to clean the user loaded
     * The second call is to rebind
     * model.status to the form control
     * after reset
     *
     */
    resetForm() {
        this.initUser();
        this.form.reset();
        this.initUser();
    }

    initUser() {
        this.model = new User("0", "", "", "", "", "", "true");
    }

    onSubmit() { this.submitted = true; }
    cancel(form: FormGroup) {
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
                    this.errorMessage = '';
                    setTimeout(() => {
                        this.model.id = resp.json().id;
                        this.userSaved = false;
                        this.onUserAdded.emit(this.model);
                    }, 1000)
                },
                error =>  {this.errorMessage =  this.mapErrorUser(error.json().ERROR.errors);

              });
    }


    editUser() {
        this.userService
            .update(this.model)
            .subscribe(
                resp => {
                    this.userSaved = true;
                    this.errorMessage = '';
                    setTimeout(() => {
                        this.userSaved = false;
                        this.onUserEdited.emit(this.model);
                    }, 1000)
                },
                error =>  {this.errorMessage =  this.mapErrorUser(error.json().ERROR.errors);
            });
    }

    private mapErrorUser(response:any): string{
       return response.map(this.toErrorUser);
    }

    private toErrorUser(r:any): string{
      let msg ={
        fieldNames: r.fieldNames,
        message: r.message,
      }
      return " " + msg.fieldNames + " " + msg.message;
    }

}
