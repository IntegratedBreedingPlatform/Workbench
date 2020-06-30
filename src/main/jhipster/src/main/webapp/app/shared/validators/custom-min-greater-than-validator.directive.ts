import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
    selector: '[jhi-customMinGreaterThanValidator][ngModel]',
    providers: [{ provide: NG_VALIDATORS, useExisting: CustomMinGreaterThanValidatorDirective, multi: true }]
})
export class CustomMinGreaterThanValidatorDirective implements Validator {
    @Input() min: string;

    validate(c: AbstractControl): ValidationErrors | null {
        return c.value > parseFloat(this.min) ? null : {
            customMin: {
                valid: false
            }
        };
    }
}
