export class CrossPlanDesignInput {
    constructor(
        public femaleList?: number[],
        public maleList?: number[],
        public makeReciprocalCrosses?: boolean,
        public excludeSelfs?: boolean,
        public crossingMethod?: CrossingMethod
    ) {
    }
}

export enum CrossingMethod {
    PLEASE_CHOOSE = 'PLEASE_CHOOSE',
    CROSS_EACH_SELECTED_FEMALE_WITH_EACH_SELECTED_MALE = 'CROSS_EACH_SELECTED_FEMALE_WITH_EACH_SELECTED_MALE',
    CROSS_MATCHED_PAIRS_OF_SELECTED_FEMALE_AND_MALE_LINES_IN_TOP_TO_BOTTOM_ORDER = 'CROSS_MATCHED_PAIRS_OF_SELECTED_FEMALE_AND_MALE_LINES_IN_TOP_TO_BOTTOM_ORDER',
    CROSS_EACH_FEMALE_WITH_AN_UNKNOWN_MALE_PARENT = 'CROSS_EACH_FEMALE_WITH_AN_UNKNOWN_MALE_PARENT',
    CROSS_EACH_FEMALE_WITH_ALL_MALE_PARENTS = 'CROSS_EACH_FEMALE_WITH_ALL_MALE_PARENTS'
}

