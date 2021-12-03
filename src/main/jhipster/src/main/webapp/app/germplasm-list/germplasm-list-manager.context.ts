import { Injectable } from '@angular/core';
import { SearchComposite } from '../shared/model/search-composite';

@Injectable()
export class GermplasmListManagerContext {
    searchComposite: SearchComposite<any, number>;
    activeGermplasmListId: number;
}
