import { Pipe } from '@angular/core';
import { PipeTransform } from '@angular/core';

@Pipe({ name: 'keyvalue' })
export class KeyValuePipe implements PipeTransform {
    transform(value, args: string[]): any {
        const keys = [];
        for (const key of Object.keys(value)) {
            keys.push({ key, value: value[key] });
        }
        return keys;
    }
}
