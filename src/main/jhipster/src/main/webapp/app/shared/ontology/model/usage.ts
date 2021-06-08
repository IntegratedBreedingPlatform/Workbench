import { TermSummary } from './term-summary';

export class Usage {
    constructor(
        // observations of variable
        public observations?: number,
        // studies of variable
        public studies?: number,
        // datasets of variable
        public datasets?: number,
        public variables?: TermSummary[]
    ) {
    }
}
