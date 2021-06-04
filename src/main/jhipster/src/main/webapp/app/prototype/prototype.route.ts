import { Route } from '@angular/router';
import { VariableSelectComponent } from '../shared/variable-select/variable-select.component';
import { VariableSelectTestComponent } from './variable-select.test.component';

export const prototypeRoutes: Route[] = [
    {
        path: 'prototype/variable-select',
        component: VariableSelectTestComponent
    }
];
