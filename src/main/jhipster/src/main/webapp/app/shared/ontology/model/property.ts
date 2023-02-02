import { Term } from './term';

export class Property extends Term {
    constructor(
        public cropOntologyId?: string,
        public classes?: string[]
    ) {
        super();
    }
}
