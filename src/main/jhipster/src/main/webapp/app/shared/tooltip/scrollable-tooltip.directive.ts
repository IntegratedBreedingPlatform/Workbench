import { Directive, ElementRef, HostListener, Input, Renderer2 } from '@angular/core';

@Directive({
    selector: '[scrollable-tooltip]'
})
export class ScrollableTooltipDirective {
    @Input('tooltip') tooltipText: string;
    @Input() placement: string = 'bottom';
    @Input() displayTimeout: number = 5000;
    @Input() showDelay: number = 1500;
    @Input() hideDelay: number = 1500;
    tooltip: HTMLElement;
    offset = 0;

    displayTimeoutHandlerId: number;
    hideDelayHandlerId: number;
    showDelayHandlerId: number;

    spanMouseOutUnlisten: () => void;
    spanMouseOverUnlisten: () => void;

    constructor(private el: ElementRef, private renderer: Renderer2) {
    }

    @HostListener('click', ['$event']) onMouseClick(event: any) {
        event.stopPropagation();
        if (!this.tooltip) {
            this.show();
        } else {
            this.hide();
        }
    }

    @HostListener('mouseover', ['$event']) onMouseOver(event: any) {
        event.stopPropagation();
        this.showDelayHandlerId = window.setTimeout(() => {
            if (!this.tooltip) {
                this.show();
            }
        }, this.showDelay);
    }

    @HostListener('mouseout', ['$event']) onMouseOut(event: any) {
        event.stopPropagation();
        window.clearTimeout(this.showDelayHandlerId);
        this.hideDelayHandlerId = window.setTimeout(() => {
            if (this.tooltip) {
                this.hide();
            }
        }, this.hideDelay);
    }

    show() {
        this.create();
        this.setPosition();
        this.renderer.addClass(this.tooltip, 'ng-tooltip-show');

        this.displayTimeoutHandlerId = window.setTimeout(() => {
            this.hide();
        }, this.displayTimeout);
    }

    hide() {
        if (this.tooltip) {
            window.clearTimeout(this.displayTimeoutHandlerId);
            window.clearTimeout(this.showDelayHandlerId);
            window.clearTimeout(this.hideDelayHandlerId);
            this.renderer.removeClass(this.tooltip, 'ng-tooltip-show');
            this.renderer.removeChild(document.body, this.tooltip);
            this.tooltip = null;
            if (this.spanMouseOutUnlisten) {
                this.spanMouseOutUnlisten();
            }
            if (this.spanMouseOverUnlisten) {
                this.spanMouseOverUnlisten();
            }
        }
    }

    create() {

        this.tooltip = this.renderer.createElement('span');

        this.renderer.appendChild(
            this.tooltip,
            this.renderer.createText(this.tooltipText)
        );

        this.renderer.appendChild(document.body, this.tooltip);

        this.renderer.addClass(this.tooltip, 'ng-tooltip');
        this.renderer.addClass(this.tooltip, `ng-tooltip-${this.placement}`);

        this.spanMouseOverUnlisten = this.renderer.listen(this.tooltip, 'mouseover', (event) => {
            window.clearTimeout(this.displayTimeoutHandlerId);
            window.clearTimeout(this.hideDelayHandlerId);
        });

        this.spanMouseOutUnlisten = this.renderer.listen(this.tooltip, 'mouseout', (event) => {
            this.hide();
        });

    }

    setPosition() {
        const hostPos = this.el.nativeElement.getBoundingClientRect();
        const tooltipPos = this.tooltip.getBoundingClientRect();

        const scrollPos = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;

        let top, left;

        if (this.placement === 'top') {
            top = hostPos.top - tooltipPos.height - this.offset;
            left = hostPos.left + (hostPos.width - tooltipPos.width) / 2;
        }

        if (this.placement === 'bottom') {
            top = hostPos.bottom + this.offset;
            left = hostPos.left + (hostPos.width - tooltipPos.width) / 2;
        }

        if (this.placement === 'left') {
            top = hostPos.top + (hostPos.height - tooltipPos.height) / 2;
            left = hostPos.left - tooltipPos.width - this.offset;
        }

        if (this.placement === 'right') {
            top = hostPos.top + (hostPos.height - tooltipPos.height) / 2;
            left = hostPos.right + this.offset;
        }

        this.renderer.setStyle(this.tooltip, 'top', `${top + scrollPos}px`);
        this.renderer.setStyle(this.tooltip, 'left', `${left}px`);
    }
}
