import { TermSummary } from './term-summary';
import { MetadataDetails } from './metadata-details';

export class MethodDetails extends TermSummary {
    constructor(
        public metadata?: MetadataDetails
    ) {
        super();
    }

}
