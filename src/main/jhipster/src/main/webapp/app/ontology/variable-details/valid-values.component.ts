import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { VariableDetails } from '../../shared/ontology/model/variable-details';

@Component({
    selector: 'jhi-valid-values',
    templateUrl: './valid-values.component.html',
})
export class ValidValuesComponent implements OnInit {

    variableDetails: VariableDetails;

    private variableId: number;

    private isLoading: boolean;

    constructor(private activatedRoute: ActivatedRoute,
                private jhiAlertService: JhiAlertService,
                private jhiLanguageService: JhiLanguageService,
                private router: Router
    ) {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.variableId = queryParams.variableId;
    }

    ngOnInit(): void {
    }

}
