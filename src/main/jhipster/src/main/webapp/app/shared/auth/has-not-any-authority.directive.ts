/**
 * @whatItDoes Conditionally includes an HTML element if current user has not
 * the authorities passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *jhiHasNotAnyAuthority="'ROLE_ADMIN'">...</some-element>
 *
 *     <some-element *jhiHasNotAnyAuthority="['ROLE_ADMIN', 'ROLE_USER']">...</some-element>
 * ```
 */
import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { Principal } from './principal.service';

@Directive({
    selector: '[jhiHasNotAnyAuthority]'
})
export class HasNotAnyAuthorityDirective {

    private authorities: string[];

    constructor(private principal: Principal, private templateRef: TemplateRef<any>, private viewContainerRef: ViewContainerRef) {
    }

    @Input()
    set jhiHasNotAnyAuthority(value: string | string[]) {
        this.authorities = typeof value === 'string' ? [<string>value] : <string[]>value;
        this.updateView();
        // Get notified each time authentication state changes.
        this.principal.getAuthenticationState().subscribe((identity) => this.updateView());
    }

    private updateView(): void {
        this.principal.hasAnyAuthority(this.authorities).then((result) => {
            this.viewContainerRef.clear();
            if (!result) {
                this.viewContainerRef.createEmbeddedView(this.templateRef);
            }
        });
    }
}
