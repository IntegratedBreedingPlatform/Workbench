import { Input, OnInit } from '@angular/core';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../service/param.context';
import { HttpErrorResponse } from '@angular/common/http';
import { ListModel } from '../list-builder/model/list.model';
import { ListType } from '../list-builder/model/list-type.model';
import { ListService } from './service/list.service';
import { formatErrorList } from '../alert/format-error-list';
import { AlertService } from '../alert/alert.service';
import { DateHelperService } from '../service/date.helper.service';
import { JhiEventManager } from 'ng-jhipster';

export class ListMetadataComponent implements OnInit {

    listTypes: ListType[];
    @Input() listId: number;
    model: ListModel;

    isLoading: boolean;
    selectedDate: NgbDate;

    constructor(public modal: NgbActiveModal,
                public eventManager: JhiEventManager,
                public paramContext: ParamContext,
                public alertService: AlertService,
                public listService: ListService,
                public dateHelperService: DateHelperService) {
        if (!this.paramContext.cropName) {
            this.paramContext.readParams();
        }
    }

    ngOnInit(): void {
        this.listService.getListTypes().subscribe((listTypes) => this.listTypes = listTypes);
        this.listService.getById(this.listId).subscribe((listModel) => {
            this.model = listModel;
            this.selectedDate =  this.dateHelperService.convertFormattedDateStringToNgbDate(this.model.date, 'yyyy-mm-dd');
        });
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading;
    }

    closeModal() {
        this.modal.dismiss();
    }

    save() {
        this.isLoading = true;
        this.model.date = `${this.selectedDate.year}-${this.selectedDate.month}-${this.selectedDate.day}`
        this.listService.updateListMetadata(this.listId, this.model)
            .subscribe(
                (res) => this.onSaveSuccess(),
                (res: HttpErrorResponse) => this.onError(res)
            );
    }

    onSaveSuccess() {
        this.isLoading = false;
        this.alertService.success('edit-list-metadata.success');
        this.eventManager.broadcast({ name: 'listMetadataUpdated', content: this.listId });
        this.modal.close();
    }

    onError(response: HttpErrorResponse) {
        this.isLoading = false;
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

}
