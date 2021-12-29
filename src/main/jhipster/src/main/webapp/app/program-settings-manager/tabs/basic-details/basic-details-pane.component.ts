import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse } from '@angular/common/http';
import { Program } from '../../../shared/program/model/program';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: 'basic-details-pane.component.html',
    styleUrls: ['basic-details-panel.component.css']
})
export class BasicDetailsPaneComponent implements OnInit, OnDestroy {

    isLoading: boolean;
    program: Program = new Program();
    programOrg: Program;
    startDate: NgbDate;

    constructor() {
        this.program = {};
    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
    }

    delete() {
    }

    save() {

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.program.name && this.program.crop
            && this.startDate;
    }

    reset() {
    }

    private onError(response: HttpErrorResponse) {
    }
}
