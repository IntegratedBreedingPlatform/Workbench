import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { STUDY_MANAGER_ROUTES } from './study-manager.route';
import { EntryDetailsImportContext } from '../shared/ontology/entry-details-import.context';
import { AdvanceStudyComponent } from './advance/advance-study.component';
import { GermplasmTreeService } from '../shared/tree/germplasm/germplasm-tree.service';
import { DatasetService } from './service/datasetService';
import { StudyManagerComponent } from './study-manager.component';
import { StudySearchComponent } from './study-search.component';
import { StudySummaryComponent } from './study-summary.component';
import { StudyManagerTreeComponent } from './study-manager-tree.component';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...STUDY_MANAGER_ROUTES]),
    ],
    declarations: [
        StudyManagerComponent,
        StudySearchComponent,
        StudySummaryComponent,
        StudyManagerTreeComponent,
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent,
        AdvanceStudyComponent
    ],
    entryComponents: [
        StudyManagerComponent,
        StudySearchComponent,
        StudySummaryComponent,
        StudyManagerTreeComponent,
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
