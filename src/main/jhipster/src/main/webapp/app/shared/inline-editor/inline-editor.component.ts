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

    @Input() observationVariable: ObservationVariable;
    @Input() value: any;

    @Output() onApply = new EventEmitter();
    @Output() onCancel = new EventEmitter();

    @ViewChild('editorInput')
    editorInput: ElementRef;

    DataTypeIdEnum = DataTypeIdEnum;
    dateValue: NgbDate;

    constructor(
        private dateHelperService: DateHelperService
    ) {
    }

    ngOnInit(): void {
        try {
            if (this.value) {
                this.dateValue = this.dateHelperService.convertStringToNgbDate(this.value);
            }
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

    submitDate(f) {
        if (this.dateValue) {
            this.value = this.dateHelperService.convertNgbDateToString(this.dateValue);
        } else {
            /*
             * kind of hack to perform a delete value when clearing out the cell and hit enter.
             * The first keyup enter would cause a submit() with the initial value, which would be a noop in the caller component,
             * and will set ngbdatepicker empty.
             * Then the (closed) event in the datepicker will fire and set this value to null, which would fire a delete call in the caller
             */
            this.value = null;
        }
        this.submit(f);
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
