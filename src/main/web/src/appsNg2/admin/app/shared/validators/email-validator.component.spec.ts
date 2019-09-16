/// <reference path="../../../../../../typings/globals/jasmine/index.d.ts" />

import { validateEmail } from './email-validator.component';
import { FormControl } from '@angular/forms';

export function main() {
    describe('validateEmail', () => {
        it('should validate email', () => {
            expect(validateEmail(<FormControl>({ value: 'test@test.com' }))).toBeNull();
            expect(validateEmail(<FormControl>({ value: 'test.company@test.com' }))).toBeNull();
            expect(validateEmail(<FormControl>({ value: 'test-2.company@test.com' }))).toBeNull();
            expect(validateEmail(<FormControl>({ value: 'test_2.company@test.com' }))).toBeNull();
            expect(validateEmail(<FormControl>({ value: 'test_company@test.com' }))).toBeNull();
            expect(validateEmail(<FormControl>({ value: 'test-company@test.com' }))).toBeNull();

            expect(validateEmail(<FormControl>({ value: 'test-2.@test.com' }))).toBeDefined();
            expect(validateEmail(<FormControl>({ value: 'test%@test.com' }))).toBeDefined();
            expect(validateEmail(<FormControl>({ value: 'test company@test.com' }))).toBeDefined();
        });
    });
}
