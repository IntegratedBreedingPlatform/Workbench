import { Injectable } from '@angular/core';
import { SearchComposite } from '../shared/model/search-composite';
import { GermplasmList } from '../shared/germplasm-list/model/germplasm-list.model';

@Injectable()
export class GermplasmManagerContext {
    searchComposite: SearchComposite<any, number>;
    sourceGermplasmList: GermplasmList;
}
