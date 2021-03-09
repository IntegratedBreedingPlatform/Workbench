import { Component, Input } from '@angular/core';

@Component({
    selector: 'jhi-collapsible',
    template: `
		<label (click)="isCollapsed = !isCollapsed"
			   [attr.aria-expanded]="!isCollapsed">
			<i [ngClass]="isCollapsed?'fa fa-caret-right fa-lg':'fa fa-caret-down fa-lg'" aria-hidden="true"> </i>
			<strong class="d-inline-flex p-2">{{heading}}</strong>
		</label><br>
		<section id="{{heading}}" [hidden]="isCollapsed">
			<ng-content></ng-content>
		</section>
    `
})
export class CollapsibleComponent {
    isCollapsed = false;
    @Input() heading: string;
    @Input() collapsable = true;
}
