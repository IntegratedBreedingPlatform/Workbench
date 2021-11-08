import { Injectable } from '@angular/core';
import { LabelPrintingType } from './label-printing.component';

@Injectable()
export class LabelPrintingContext {
    studyId: number;
    datasetId: number;
    printingLabelType: LabelPrintingType;
    searchRequestId: number;
    listId: number;
}
