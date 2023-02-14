import { Directive } from '@angular/core';
import { NG_VALIDATORS, Validator } from '@angular/forms';
import { FormControl } from '@angular/forms';

export const EMAIL_LOCAL_PART_REGEX = '^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*';

@Directive({
    selector: '[jhiValidateEmail][ngModel]',
    providers: [
        { provide: NG_VALIDATORS, useExisting: EmailValidatorDirective, multi: true }
    ]
})
export class EmailValidatorDirective implements Validator {
  validate(c: FormControl) {
        const EMAIL_REGEXP = new RegExp(EMAIL_LOCAL_PART_REGEX + '@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$', 'i');

        return EMAIL_REGEXP.test(c.value) ? null : {
            validateEmail: {
                valid: false
            }
        };
    }
}
