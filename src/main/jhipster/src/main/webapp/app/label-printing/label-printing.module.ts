import { NgModule } from '@angular/core';
import { FieldListFilterPipe, LabelPrintingComponent } from './label-printing.component';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../shared';
import { LABEL_PRINTING_ROUTES } from './label-printing.route';
import { LabelPrintingService } from './label-printing.service';
import { LabelPrintingContext } from './label-printing.context';
import { DragDropModule } from '@angular/cdk/drag-drop';

@NgModule({
    declarations: [
        LabelPrintingComponent,
        FieldListFilterPipe
    ],
    providers: [
        LabelPrintingService,
        LabelPrintingContext
    ],
    imports: [
        DragDropModule,
        BmsjHipsterSharedModule,
        RouterModule.forChild(LABEL_PRINTING_ROUTES)
    ]
})
export class LabelPrintingModule {
}
