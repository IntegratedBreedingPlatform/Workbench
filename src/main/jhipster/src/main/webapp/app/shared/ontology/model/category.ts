import { TermSummary } from './term-summary';

export class Category extends TermSummary {
    constructor(
        public editable?: boolean
    ) {
        super();
    }
}
