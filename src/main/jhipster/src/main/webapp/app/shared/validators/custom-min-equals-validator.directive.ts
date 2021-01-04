import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
    selector: '[jhi-customMinEqualsValidator][ngModel]',
    providers: [{ provide: NG_VALIDATORS, useExisting: CustomMinEqualsValidatorDirective, multi: true }]
})
export class CustomMinEqualsValidatorDirective implements Validator {
    @Input() min: string;

    validate(c: AbstractControl): ValidationErrors | null {
        return c.value >= parseFloat(this.min) ? null : {
            customMin: {
                valid: false
            }
        };
    }
}
