import { Component, OnInit, EventEmitter, Output } from '@angular/core';
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

export class UserCard implements OnInit {
    registerForm: FormGroup;
    submitted = false;
    model: User;
    @Output() onUserAdded = new EventEmitter<boolean>();

    constructor(private userService: UserService, private roleService: RoleService) {
        this.model = new User("0", "", "", "", "", "", "true");
    }

    onSubmit() { this.submitted = true; }

    ngOnInit() {
    }

    addUser() {
        // this.model = new Hero(42, '', '');
        // this.active = false;
        // setTimeout(() => this.active = true, 0);
        this.userService
            .save(this.model)
            .subscribe(
                resp => {
                    // TODO this.model.id
                    // TODO emit
                    this.onUserAdded.emit();
                },
                err => console.log(err)
            )
    }
}
