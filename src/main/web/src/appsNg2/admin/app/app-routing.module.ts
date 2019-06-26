import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DEBUG_INFO_ENABLED } from './app.constants';
import { errorRoute } from './layouts/error/error.route';
import { routes } from './app.routes';

const LAYOUT_ROUTES = [
    ...errorRoute,
    ...routes
];

@NgModule({
    imports: [
        RouterModule.forRoot(LAYOUT_ROUTES, { useHash: true , enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule {}
