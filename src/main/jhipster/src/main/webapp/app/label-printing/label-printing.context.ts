import { Injectable } from '@angular/core';
import { LabelPrintingType } from './label-printing.component';

@Injectable()
export class LabelPrintingContext {
    programId: string;
    studyId: number;
    datasetId: number;
    printingLabelType: LabelPrintingType;
    searchRequestId: number;
}
