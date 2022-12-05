import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { STUDY_MANAGER_ROUTES } from './study-manager.route';
import { EntryDetailsImportContext } from '../../shared/ontology/entry-details-import.context';
import { AdvanceStudyComponent } from './advance/advance-study.component';
import { GermplasmTreeService } from '../../shared/tree/germplasm/germplasm-tree.service';
import { DatasetService } from '../../shared/dataset/service/dataset.service';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...STUDY_MANAGER_ROUTES]),
    ],
    declarations: [
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent,
        AdvanceStudyComponent
    ],
    entryComponents: [
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent,
        AdvanceStudyComponent
    ],
    providers: [
        EntryDetailsImportContext,
        GermplasmTreeService,
        DatasetService
    ]
})
export class StudyManagerModule {
}
