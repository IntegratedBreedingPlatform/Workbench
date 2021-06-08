import { ValidValues } from './valid-values';
import { MetadataDetails } from './metadata-details';
import { TermSummary } from './term-summary';
import { DataType } from './data-type';

export class ScaleDetails extends TermSummary {
    constructor(
        public dataType?: DataType,
        public validValues?: ValidValues,
        public metadata?: MetadataDetails
    ) {
        super();
    }
}
