import { CUSTOM_ELEMENTS_SCHEMA, NgModule, ApplicationRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from './navbar.component';
import { BmsjHipsterSharedModule } from '../shared';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NavService } from './nav.service';
import { MatListModule } from '@angular/material/list';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        MatSidenavModule,
        MatToolbarModule,
        MatIconModule,
        MatButtonModule,
        MatListModule
    ],
    declarations: [NavbarComponent],
    providers: [NavService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NavbarModule {
    // ngDoBootstrap(appRef: ApplicationRef) {
    //     appRef.bootstrap(NavbarComponent);
    // }
}
