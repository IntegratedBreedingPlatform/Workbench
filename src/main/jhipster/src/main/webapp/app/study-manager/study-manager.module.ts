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
import { TemplateService } from './advance/template.service';
import { SaveTemplateComponent } from './advance/save-template.component'
import { PropagateDescriptorsComponent } from './advance/propagate-descriptors.component';

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
        AdvanceSamplesComponent,
        SaveTemplateComponent,
        PropagateDescriptorsComponent
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
        AdvanceSamplesComponent,
        SaveTemplateComponent,
        PropagateDescriptorsComponent
    ],
    providers: [
        EntryDetailsImportContext,
        GermplasmTreeService,
        DatasetService,
        TemplateService
    ]
})
export class StudyManagerModule {
}
