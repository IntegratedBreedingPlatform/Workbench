import { Component, OnInit } from '@angular/core';
import {
    FormBuilder,
    FormGroup,
    Validators
} from '@angular/forms';
import { User } from '../shared/models/user.model';

@Component({
    selector: 'user-card',
    templateUrl: './user-card.component.html',
    moduleId: module.id
})

export class UserCard implements OnInit {
    registerForm: FormGroup;
    submitted = false;
    model: User = new User("-1", "", "", "", "", "", "");

    constructor() {
    }

    onSubmit() { this.submitted = true; }

    ngOnInit() {
    }

    addUser() {
        // this.model = new Hero(42, '', '');
        // this.active = false;
        // setTimeout(() => this.active = true, 0);
    }
}
