import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DEBUG_INFO_ENABLED } from './app.constants';
import { errorRoute } from './layouts';
import { routes } from './app.route';

const ROUTES = [
    ...routes,
    ...errorRoute
];

@NgModule({
    imports: [
        RouterModule.forRoot(ROUTES, { useHash: true, enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [
        RouterModule
    ]
})
export class BmsjHipsterAppRoutingModule {
}
