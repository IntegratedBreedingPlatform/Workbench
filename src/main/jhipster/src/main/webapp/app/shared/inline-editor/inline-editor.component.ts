import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ObservationVariable, ValueReference } from '../model/observation-variable.model';
import { DataTypeIdEnum } from '../ontology/data-type.enum';
import { DateHelperService } from '../service/date.helper.service';
import { NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { toUpper } from '../util/to-upper';

@Component({
    selector: 'jhi-inline-editor',
    templateUrl: './inline-editor.component.html'
})
export class InlineEditorComponent implements OnInit, AfterViewInit {

    /*
     * TODO:
     *  - fix date escape cancel
     *  - no way to delete date values
     */

    @Input() observationVariable: ObservationVariable;
    @Input() value: any;

    @Output() onApply = new EventEmitter();
    @Output() onCancel = new EventEmitter();

    @ViewChild('editorInput')
    editorInput: ElementRef;

    DataTypeIdEnum = DataTypeIdEnum;
    dateValue: NgbDate;

    constructor(
        public dateHelperService: DateHelperService
    ) {
    }

    ngOnInit(): void {
        try {
            this.dateValue = this.dateHelperService.convertStringToNgbDate(this.value);
        } catch (e) {

        }
    }

    ngAfterViewInit(): void {
        if (this.editorInput.nativeElement) {
            this.editorInput.nativeElement.focus();
        }
    }

    submit(form) {
        if (!form.valid) {
            return;
        }
        this.onApply.emit(this.value);
    }

    cancel() {
        this.onCancel.emit();
    }

    searchCategorical(term: string, item: ValueReference) {
        const termUpper = toUpper(term);
        return toUpper(item.name).includes(termUpper)
            || toUpper(item.description).includes(termUpper);
    }
}
