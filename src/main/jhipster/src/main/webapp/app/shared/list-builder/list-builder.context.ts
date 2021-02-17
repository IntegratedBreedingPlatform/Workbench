import { Injectable } from '@angular/core';
import { BaseEntity } from '..';

@Injectable()
export class ListBuilderContext {
    visible = true;
    data: BaseEntity[] = [];
}
