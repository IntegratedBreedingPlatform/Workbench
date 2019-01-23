import { NgModule } from '@angular/core';
import { AllLabelsPipe, PrintLabelsComponent } from './print-labels.component';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../shared';
import { PRINT_LABELS_ROUTES } from './print-labels-route';
import { PrintLabelsService } from './print-labels.service';
import { PrintLabelsContext } from './print-labels.context';

@NgModule({
    declarations: [
        PrintLabelsComponent,
        AllLabelsPipe
    ],
    providers: [
        PrintLabelsService,
        PrintLabelsContext
    ],
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(PRINT_LABELS_ROUTES)
    ]
})
export class PrintLabelsModule {
}
