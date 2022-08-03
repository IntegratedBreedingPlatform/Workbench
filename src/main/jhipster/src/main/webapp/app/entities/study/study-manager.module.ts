import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { STUDY_MANAGER_ROUTES } from './study-manager.route';
import { EntryDetailsImportContext } from '../../shared/ontology/entry-details-import.context';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...STUDY_MANAGER_ROUTES]),
    ],
    declarations: [
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent
    ],
    entryComponents: [
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent
    ],
    providers: [
        EntryDetailsImportContext
    ]
})
export class StudyManagerModule {
}
