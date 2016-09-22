import { Component, OnInit, EventEmitter, Output, Input, AfterViewInit } from '@angular/core';
import {
    FormBuilder,
    FormGroup,
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

export class UserCard implements OnInit, AfterViewInit {
    registerForm: FormGroup;
    submitted = false;
    @Input() model: User;
    @Input() userSaved: boolean = false;
    @Output() onUserAdded = new EventEmitter<User>();
    @Output() onCancel = new EventEmitter<void>();

    constructor(private userService: UserService, private roleService: RoleService) {
        this.initialize();
    }

    private initialize() {
        this.userSaved = false;
        this.model = new User("0", "", "", "", "", "", "");
    }

    onSubmit() { this.submitted = true; }
    cancel() { this.onCancel.emit() }

    ngOnInit() {
    }

    ngAfterViewInit() {
        this.initialize();
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
                    }, 3000)
                },
                err => console.log(err)
            )
    }
}
