import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { GermplasmImportModule } from '../germplasm-manager/import/germplasm-import.module';
import { GermplasmListComponent } from './germplasm-list.component';
import { GERMPLASM_LIST_ROUTES } from './germplasm-list.route';
import { GermplasmListSearchComponent } from './germplasm-list-search.component';
import { GermplasmListService } from '../shared/germplasm-list/service/germplasm-list.service';
import { ListComponent } from './list.component';
import { GermplasmListImportComponent } from './import/germplasm-list-import.component';
import { GermplasmListImportModule } from './import/germplasm-list-import.module';
import { GermplasmListImportContext } from './import/germplasm-list-import.context';
import { ListColumnsComponent } from './list-columns.component';
import { ListDataRowComponent } from './list-table-row-data.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(GERMPLASM_LIST_ROUTES),
        GermplasmListImportModule
    ],
    declarations: [
        GermplasmListComponent,
        GermplasmListSearchComponent,
        ListComponent,
        ListColumnsComponent,
        ListDataRowComponent
    ],
    entryComponents: [
        GermplasmListComponent,
        GermplasmListSearchComponent,
        ListComponent,
        ListColumnsComponent,
        ListDataRowComponent
    ],
    providers: [
        GermplasmListService,
        GermplasmListImportContext
    ]
})
export class GermplasmListModule {

}
