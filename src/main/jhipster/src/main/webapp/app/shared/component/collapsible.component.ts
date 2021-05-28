import { Component, Input } from '@angular/core';

@Component({
    selector: 'jhi-collapsible',
    template: `
		<label (click)="isCollapsed = !isCollapsed"
			   [attr.aria-expanded]="!isCollapsed">
			<i [ngClass]="isCollapsed?'fa fa-caret-right fa-lg':'fa fa-caret-down fa-lg'" aria-hidden="true"> </i>
			<span class="d-inline-flex p-2 h4 font-weight-bold">{{heading}}</span>
		</label><br>
		<section id="{{heading}}" [hidden]="isCollapsed">
			<ng-content></ng-content>
		</section>
    `
})
export class CollapsibleComponent {
    @Input() isCollapsed = false;
    @Input() heading: string;
    @Input() collapsable = true;
}
