import { NgModule } from '@angular/core';
import { BmsjHipsterSharedModule } from '../shared';
import { RouterModule } from '@angular/router';
import { ImportEntryDetailsComponent, ImportEntryDetailsPopupComponent } from './entry-details/import-entry-details.component';
import { STUDY_MANAGER_ROUTES } from './study-manager.route';
import { EntryDetailsImportContext } from '../shared/ontology/entry-details-import.context';
import { AdvanceStudyComponent } from './advance/advance-study.component';
import { GermplasmTreeService } from '../shared/tree/germplasm/germplasm-tree.service';
import { DatasetService } from '../shared/dataset/service/dataset.service';
import { StudyManagerComponent } from './study-manager.component';
import { StudySearchComponent } from './study-search.component';
import { StudySummaryComponent } from './study-summary.component';
import { StudyManagerTreeComponent } from './study-manager-tree.component';
import { StudySummaryDatasetComponent } from './study-summary-dataset.component';
import { AdvanceSamplesComponent } from './advance/advance-samples.component';
import { AttributesPropagationPresetService } from './advance/attributes-propagation-preset.service';

@NgModule({
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild([...STUDY_MANAGER_ROUTES]),
    ],
    declarations: [
        StudyManagerComponent,
        StudySearchComponent,
        StudySummaryComponent,
        StudySummaryDatasetComponent,
        StudyManagerTreeComponent,
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent,
        AdvanceStudyComponent,
        AdvanceSamplesComponent
    ],
    entryComponents: [
        StudyManagerComponent,
        StudySearchComponent,
        StudySummaryComponent,
        StudySummaryDatasetComponent,
        StudyManagerTreeComponent,
        ImportEntryDetailsComponent,
        ImportEntryDetailsPopupComponent,
        AdvanceStudyComponent,
        AdvanceSamplesComponent
    ],
    providers: [
        EntryDetailsImportContext,
        GermplasmTreeService,
        DatasetService,
        AttributesPropagationPresetService
    ]
})
export class StudyManagerModule {
}
