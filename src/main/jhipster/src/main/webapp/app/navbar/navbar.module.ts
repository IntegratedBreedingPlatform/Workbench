import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { NavbarComponent } from './navbar.component';
import { BmsjHipsterSharedModule } from '../shared';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NavService } from './nav.service';
import { MatTreeModule } from '@angular/material/tree';
import { MatMenuModule } from '@angular/material/menu';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        MatSidenavModule,
        MatToolbarModule,
        MatIconModule,
        MatButtonModule,
        MatTreeModule,
        MatMenuModule
    ],
    declarations: [NavbarComponent],
    providers: [NavService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NavbarModule {
}
