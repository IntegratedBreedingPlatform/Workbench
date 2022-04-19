import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { DomSanitizer } from '@angular/platform-browser';
import { BTypeEnum } from './cop.model';

@Component({
    selector: 'jhi-btype-selector',
    template: `
		<jhi-modal [title]="'BType'">
			<div class="modal-body word-wrap">
				<form>
					<div class="form-group row">
						<div class="col"><span jhiTranslate="cop.btype.select"></span>:</div>
					</div>
					<div class="form-group row required">
						<label class="col-sm-2 col-form-label font-weight-bold" jhiTranslate="cop.btype.label"></label>
						<div class="col-sm-8">
							<select class="form-control" id="btypeDropdown" name="btypeDropdown"
									[(ngModel)]="btype" #btypeDropdown="ngModel">
								<option [value]="BTypeEnum.CROSS_FERTILIZING"><span jhiTranslate="cop.btype.cross.fertilizing"></span>
									(btype={{BTypeEnum.CROSS_FERTILIZING}})
								</option>
								<option [value]="BTypeEnum.SELF_FERTILIZING"><span jhiTranslate="cop.btype.self.fertilizing"></span>
									(btype={{BTypeEnum.SELF_FERTILIZING}})
								</option>
								<option [value]="BTypeEnum.SELF_FERTILIZING_F4"><span jhiTranslate="cop.btype.self.fertilizing.f4"></span>
									(btype={{BTypeEnum.SELF_FERTILIZING_F4}})
								</option>
							</select>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button (click)="dismiss()" type="button" class="btn btn-secondary" data-dismiss="modal">
					<span class="fa fa-ban"></span>&nbsp;<span jhiTranslate="cancel"></span>
				</button>
				<button (click)="confirm()" class="btn btn-primary" data-test="modalConfirmButton" [disabled]="!btype">
					<span class="fa fa-save"></span>&nbsp;<span jhiTranslate="confirm"></span>
				</button>
			</div>
		</jhi-modal>
    `
})
export class BtypeSelectorModalComponent {
    BTypeEnum = BTypeEnum;
    btype: BTypeEnum;

    constructor(private modal: NgbActiveModal,
                private translateService: TranslateService,
                public sanitizer: DomSanitizer) {
    }

    confirm() {
        this.modal.close(this.btype);
    }

    dismiss() {
        this.modal.dismiss();
    }
}
