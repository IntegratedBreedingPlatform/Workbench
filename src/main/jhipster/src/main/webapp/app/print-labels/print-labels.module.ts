import { NgModule } from '@angular/core';
import { PrintLabelsComponent } from './print-labels.component';
import { RouterModule } from '@angular/router';
import { BmsjHipsterSharedModule } from '../shared';
import { PRINT_LABELS_ROUTES } from './print-labels-route';

@NgModule({
  declarations: [PrintLabelsComponent],
  imports: [
    BmsjHipsterSharedModule,
    RouterModule.forChild(PRINT_LABELS_ROUTES)
  ]
})
export class PrintLabelsModule { }
