import { FormulaVariable } from './formula-variable';

export class Formula {
    constructor(
        public formulaId?: number,
        public target?: FormulaVariable,
        public inputs?: FormulaVariable[],
        public definition?: string,
        public active?: boolean,
        public name?: string,
        public description?: string,
    ) {
    }
}
