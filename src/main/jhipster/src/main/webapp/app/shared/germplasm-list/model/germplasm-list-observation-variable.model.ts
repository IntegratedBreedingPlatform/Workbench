import { ObservationVariable } from '../../model/observation-variable.model';
import { GermplasmListColumnCategory } from './germplasm-list-column-category.type';

export class GermplasmListObservationVariable extends ObservationVariable {

    constructor(public termId: number,
                public name: string,
                public alias: string,
                public columnCategory: GermplasmListColumnCategory) {
        super(termId, name, alias);
    }

}
