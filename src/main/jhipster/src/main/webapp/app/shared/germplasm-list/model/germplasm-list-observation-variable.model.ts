import { ObservationVariable } from '../../model/observation-variable.model';
import { GermplasmListColumnCategory } from './germplasm-list-column-category.type';
import { VariableTypeEnum } from '../../ontology/variable-type.enum';

export class GermplasmListObservationVariable extends ObservationVariable {

    constructor(public termId: number,
                public name: string,
                public alias: string,
                public columnCategory: GermplasmListColumnCategory,
                public variableType?: VariableTypeEnum) {
        super(termId, name, alias);
    }

}
