import { MethodDetails } from './method-details';
import { PropertyDetails } from './property-details';
import { ScaleDetails } from './scale-details';
import { VariableType } from './variable-type';
import { ExpectedRange } from './expected-range';
import { Formula } from './formula';
import { MetadataDetails } from './metadata-details';
import { TermSummary } from './term-summary';

export class VariableDetails extends TermSummary {
    constructor(
        public programUuid?: string,
        public alias?: string,
        public favourite?: boolean,
        public allowsFormula?: boolean,
        public method?: MethodDetails,
        public property?: PropertyDetails,
        public scale?: ScaleDetails,
        public variableTypes?: VariableType[],
        public expectedRange?: ExpectedRange,
        public formula?: Formula,
        public metadata?: MetadataDetails
    ) {
        super();
    }
}
