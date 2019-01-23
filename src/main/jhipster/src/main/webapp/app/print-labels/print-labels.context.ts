import { Injectable } from '@angular/core';

@Injectable()
export class PrintLabelsContext {
    studyId: number;
    datasetId: number;
    printingLabelType: number;
}
