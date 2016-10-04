import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import {
    Validators, FormGroup, FormControl
} from '@angular/forms';
import { User } from '../shared/models/user.model';

import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { MailService } from './../shared/services/mail.service';
import { Role } from './../shared/models/role.model';
import { Response } from '@angular/http';


@Component({
    selector: 'user-card',
    templateUrl: './user-card.component.html',
    moduleId: module.id
})

export class UserCard implements OnInit {
    errorUserMessage: string = '';
    errorClass: string = 'alert alert-danger';
    submitted = false;
    sendingEmail: boolean = false;
    @Input() originalUser: User;
    @Input() userSaved: boolean = false;
    @Input() isEditing: boolean;
    @Input() model: User;
    @Input() roles: Role[];
    @Input() sendMail: boolean;
    @Output() onUserAdded = new EventEmitter<User>();
    @Output() onUserEdited = new EventEmitter<User>();
    @Output() onCancel = new EventEmitter<void>();

    constructor(private userService: UserService, private roleService: RoleService, private mailService: MailService) {
        this.model = new User("0", "", "", "", "", "", "");
        this.errorUserMessage = '';
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
  /*  resetForm() {
        // see https://angular.io/docs/ts/latest/guide/forms.html#!#add-a-hero-and-reset-the-form
        this.activeForm = false;
        setTimeout(() => this.activeForm = true, 0);
    }*/

    initialize() {
        this.errorUserMessage = '';
    }

    onSubmit() { this.submitted = true; }
    cancel(form: FormGroup) {
        form.reset();
        this.errorUserMessage = '';
        this.onCancel.emit();
    }

    ngOnInit() {
    }

    addUser(form: FormGroup) {
        this.userService
            .save(this.model)
            .subscribe(
                resp => {
                    this.userSaved = true;
                    this.errorUserMessage = '';
                    this.sendEmailToResetPassword(resp);
                },
                error =>  {this.errorUserMessage =  this.mapErrorUser(error.json().ERROR.errors);

              });
    }


    editUser() {
        this.userService
            .update(this.model)
            .subscribe(
                resp => {
                    this.userSaved = true;
                    this.errorUserMessage = '';
                    this.sendEmailToResetPassword(resp);
                },
                error =>  {this.errorUserMessage =  this.mapErrorUser(error.json().ERROR.errors);
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

    private sendEmailToResetPassword (respSaving: Response){
      if (this.sendMail) {
          this.sendingEmail = true;
          this.mailService
              .send(this.model)
              .subscribe(
                resp => {
                  setTimeout(() => {
                      this.sendingEmail = false;
                      this.userSaved = false;
                      this.sendMail = !this.isEditing;
                      if (!this.isEditing) {
                        this.model.id = respSaving.json().id;
                        this.onUserAdded.emit(this.model);
                      } else {
                        this.onUserEdited.emit(this.model);
                      }
                  }, 1000);
                },
                error => {
                    this.sendingEmail = false;
                    this.errorClass = 'alert alert-warning';
                    this.errorUserMessage = 'Email was not sent. Please contact your system administrator';
                    setTimeout(() => {
                        this.errorUserMessage ='';
                        this.errorClass = 'alert alert-danger';
                        this.userSaved = false;
                        this.sendMail = !this.isEditing;
                        if (!this.isEditing) {
                          this.model.id = respSaving.json().id;
                          this.onUserAdded.emit(this.model);
                        } else {
                          this.onUserEdited.emit(this.model);
                        }
                    }, 2000);
                }
              );
      } else {
        setTimeout(() => {
            this.userSaved = false;
            this.sendMail = !this.isEditing;
            if (!this.isEditing) {
              this.model.id = respSaving.json().id;
              this.onUserAdded.emit(this.model);
            } else {
              this.onUserEdited.emit(this.model);
            }
        }, 1000);
      }
    }

}
