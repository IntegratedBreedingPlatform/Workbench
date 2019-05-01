import { NgModule } from '@angular/core';
import { AllLabelsPipe, LabelPrintingComponent } from './label-printing.component';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../shared';
import { LABEL_PRINTING_ROUTES } from './label-printing.route';
import { LabelPrintingService } from './label-printing.service';
import { LabelPrintingContext } from './label-printing.context';

@NgModule({
    declarations: [
        LabelPrintingComponent,
        AllLabelsPipe
    ],
    providers: [
        LabelPrintingService,
        LabelPrintingContext
    ],
    imports: [
        BmsjHipsterSharedModule,
        RouterModule.forChild(LABEL_PRINTING_ROUTES)
    ]
})
export class LabelPrintingModule {
}
