import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'truncateWithEllipsis'
})

export class TruncateWithEllipsisPipe implements PipeTransform {

    transform(value: string, limit: number): string {
        return value && value.length > limit ? value.substring(0, limit) + '...' : value;
    }
}
