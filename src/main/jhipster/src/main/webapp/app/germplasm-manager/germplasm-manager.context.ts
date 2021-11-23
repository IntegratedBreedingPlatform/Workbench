import { Injectable } from '@angular/core';
import { SearchComposite } from '../shared/model/search-composite';

@Injectable()
export class GermplasmManagerContext {
    searchComposite: SearchComposite<any, number>;
    sourceListId: number;
}
