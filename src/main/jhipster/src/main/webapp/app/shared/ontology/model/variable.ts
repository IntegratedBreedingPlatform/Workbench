import { TermSummary } from './term-summary';

/**
 * Represents Middleware/Variable, that some services return directly from Middleware
 * In the future this one and BMSAPI/VariableDetails (variable-details.ts) can be merged
 * Use variable-details preferably
 */
export class Variable extends TermSummary {
    constructor(
        public alias?: string,
        public minValue?: string,
        public maxValue?: string
        // TODO complete if necessary
    ) {
        super();
    }
}
