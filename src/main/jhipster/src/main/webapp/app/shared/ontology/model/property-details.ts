import { TermSummary } from './term-summary';
import { MetadataDetails } from './metadata-details';

export class PropertyDetails extends TermSummary {
    constructor(
        public cropOntologyId?: string,
        public classes?: string[],
        public metadata?: MetadataDetails
    ) {
        super();
    }
}
