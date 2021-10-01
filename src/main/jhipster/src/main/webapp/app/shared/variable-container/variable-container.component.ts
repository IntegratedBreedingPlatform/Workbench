import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { GermplasmListService } from '../germplasm-list/service/germplasm-list.service';
import { AlertService } from '../alert/alert.service';
import { Principal } from '../auth/principal.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { VariableSelectModalComponent } from './variable-select-modal.component';
import { VariableDetails } from '../ontology/model/variable-details';

@Component({
    selector: 'jhi-variable-container',
    templateUrl: './variable-container.component.html'
})
export class VariableContainerComponent implements OnInit {

    @Input() title: string;

    @Input() hideAddAndRemove = false;

    @Input() variables: VariableDetails[];

    @Output() onDeleteVariable = new EventEmitter<any[]>();

    @Output() onAddVariable = new EventEmitter<VariableDetails>();

    isCollapsed = false;

    selectedItems: { [key: number]: VariableDetails } = {};

    constructor(private activatedRoute: ActivatedRoute,
                private jhiLanguageService: JhiLanguageService,
                private eventManager: JhiEventManager,
                private germplasmListService: GermplasmListService,
                private router: Router,
                private alertService: AlertService,
                private principal: Principal,
                private modalService: NgbModal) {

    }

    ngOnInit(): void {

    }

    remove($event) {
        $event.preventDefault();
        const variablesIds = Object.keys(this.selectedItems);
        this.onDeleteVariable.emit(variablesIds);
    }

    openSelectVariable() {
        const modal = this.modalService.open(VariableSelectModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.result.then((variableSelected) => {
            if (variableSelected) {
                this.onAddVariable.emit(variableSelected);
            }
        });
    }

    isSelected(variable: VariableDetails) {
        return this.selectedItems[variable.id];
    }

    toggleSelect(index, variable: VariableDetails, checkbox = false) {
        let items;
        items = [variable];
        const isItemSelected = this.selectedItems[variable.id];
        for (const item of items) {
            if (isItemSelected) {
                delete this.selectedItems[item.id];
            } else {
                this.selectedItems[item.id] = item;
            }
        }
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    isAllSelected() {
        return this.size(this.selectedItems) && this.variables.every((v) => Boolean(this.selectedItems[v.id]));
    }

    onSelectAll() {
        if (this.isAllSelected()) {
            // remove all items
            this.variables.forEach((v) => delete this.selectedItems[v.id]);
        } else {
            // check remaining items
            this.variables.forEach((v) => this.selectedItems[v.id] = v);
        }
    }

}
