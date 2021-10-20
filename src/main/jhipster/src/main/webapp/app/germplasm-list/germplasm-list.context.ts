import { Injectable } from '@angular/core';
import { SearchComposite } from '../shared/model/search-composite';

@Injectable()
export class GermplasmListContext {
    searchComposite: SearchComposite<any, number>;
}
