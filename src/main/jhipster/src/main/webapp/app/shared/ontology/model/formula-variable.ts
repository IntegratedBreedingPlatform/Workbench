import { Term } from './term';

export class FormulaVariable extends Term {
    constructor(
        public targetTermId?: number
    ) {
        super();
    }
}
