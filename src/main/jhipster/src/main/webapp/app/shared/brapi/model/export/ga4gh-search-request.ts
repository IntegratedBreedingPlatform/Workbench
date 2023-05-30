export class GA4GHSearchRequest {

    alleleCount = '';
    annotationFieldThresholds?: AnnotationFieldThresholds = {};
    annotationFieldThresholds2?: AnnotationFieldThresholds = {};
    callSetIds = [];
    callSetIds2 = [];
    discriminate = false;
    end = -1;
    geneName = '';
    getGT = false;
    gtPattern = 'Any';
    gtPattern2 = 'Any';
    maxHeZ = 100;
    maxHeZ2 = 100;
    maxMaf = 50;
    maxMaf2 = 50;
    maxMissingData = 100;
    maxMissingData2 = 100;
    minHeZ = 0;
    minHeZ2 = 0;
    minMaf = 0;
    minMaf2 = 0;
    minMissingData = 0;
    minMissingData2 = 0;
    mostSameRatio = '100';
    mostSameRatio2 = '100';
    pageSize = 100;
    pageToken = '0';
    referenceName = '';
    searchMode = 3;
    selectedVariantIds = '';
    selectedVariantTypes = '';
    sortBy = '';
    sortDir = 'asc';
    start = -1;
    variantEffect = '';
    variantSetId: string;
}

export interface AnnotationFieldThresholds {
}
