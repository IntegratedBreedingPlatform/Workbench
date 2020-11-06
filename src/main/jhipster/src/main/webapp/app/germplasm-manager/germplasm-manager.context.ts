import { Injectable } from '@angular/core';
import { SearchComposite } from '../shared/model/search-composite';
import { GermplasmSearchRequest } from '../entities/germplasm/germplasm-search-request.model';

@Injectable()
export class GermplasmManagerContext {
    searchComposite: SearchComposite<GermplasmSearchRequest, number>;
}
