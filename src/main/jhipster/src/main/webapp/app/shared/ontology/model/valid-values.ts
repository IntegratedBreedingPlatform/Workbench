import { Category } from './category';

export class ValidValues {
    constructor(
        public min?: number,
        public max?: number,
        public categories?: Category[]
    ) {
    }
}
