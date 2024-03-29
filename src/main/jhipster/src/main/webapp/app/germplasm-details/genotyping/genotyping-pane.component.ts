import { Component, OnInit } from '@angular/core';
import { ParamContext } from '../../shared/service/param.context';
import { flatMap } from 'rxjs/operators';
import { CropGenotypingParameter } from '../../shared/crop/model/crop-genotyping-parameter';
import { GenotypingBrapiService } from '../../shared/brapi/service/genotyping-brapi.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { Study } from '../../shared/brapi/model/study/study';
import { VariantSet } from '../../shared/brapi/model/variantsets/variantset';
import { Call } from '../../shared/brapi/model/calls/call';
import { Germplasm } from '../../shared/brapi/model/germplasm/germplasm';
import { CallSet } from '../../shared/brapi/model/callsets/callset';
import { Observable } from 'rxjs';
import { AlertService } from '../../shared/alert/alert.service';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { Sample } from '../../entities/sample';
import { SearchGermplasmRequest } from '../../shared/brapi/model/germplasm/search-germplasm-request';
import { JhiAlertService } from 'ng-jhipster';
import { SearchSamplesRequest } from '../../shared/brapi/model/samples/search-samples-request';
import { GigwaExportRequest } from '../../shared/brapi/model/export/gigwa-export-request';
import { HttpClient } from '@angular/common/http';
import { GenotypingParameterUtilService } from '../../shared/genotyping/genotyping-parameter-util.service';
import { GA4GHSearchRequest } from '../../shared/brapi/model/export/ga4gh-search-request';

const flapjack = require('flapjack-bytes/src/flapjack-bytes');

@Component({
    selector: 'jhi-genotyping-pane',
    templateUrl: './genotyping-pane.component.html',
    styleUrls: ['./genotyping-pane.component.css'],
    providers: [{ provide: 'test', useValue: '' }]
})
export class GenotypingPaneComponent implements OnInit {

    totalCount = 10;
    page = 1;
    pageSize = 10;
    isExportingFlapjack = false;
    isStudyLoading = false;
    isVariantSetLoading = false;
    isSamplesLoading = false;
    genotypesView: any;
    isGenotypingCallsLoading = false;

    public readonly LINK_BY_GUID = 'GUID';
    public readonly LINK_BY_SAMPLE_UID = 'SAMPLE_UID';
    public readonly LINK_BY_NAME = 'NAME';

    linkByOptions: string[] = [this.LINK_BY_GUID, this.LINK_BY_SAMPLE_UID, this.LINK_BY_NAME];

    selectedLinkBy: string = this.LINK_BY_GUID;
    germplasmSearchValue;
    selectedGenotypingStudy: Study;
    selectedVariantSet: VariantSet;
    genotypingStudies: Study[] = [];
    genotypingVariantsets: VariantSet[] = [];
    genotypingGermplasm: Germplasm;
    genotypingCallSet: CallSet;
    genotypingCalls: Call[];
    germplasmSamples: Sample[];

    cropGenotypingParameter: CropGenotypingParameter;

    constructor(
        private context: ParamContext,
        private genotypingBrapiService: GenotypingBrapiService,
        private genotypingParameterUtilService: GenotypingParameterUtilService,
        public germplasmDetailsContext: GermplasmDetailsContext,
        public germplasmService: GermplasmService,
        public alertService: AlertService,
        public jhiAlertService: JhiAlertService,
        public http: HttpClient) {
    }

    ngOnInit(): void {
        this.genotypesView = '1';
        this.genotypingParameterUtilService.getGenotypingParametersAndAuthenticate().subscribe(
            (cropGenotypingParameter) => {
                this.cropGenotypingParameter = cropGenotypingParameter;
                this.genotypingBrapiService.brapiEndpoint = cropGenotypingParameter.endpoint;
                this.genotypingBrapiService.baseUrl = cropGenotypingParameter.baseUrl;
                this.genotypingBrapiService.accessToken = cropGenotypingParameter.accessToken;
                this.linkBySelectOnChange();
            }, (error) => {
                this.alertService.error(error);
            });
    }

    linkBySelectOnChange() {
        this.resetForm();
        if (this.selectedLinkBy === this.LINK_BY_SAMPLE_UID) {
            this.isSamplesLoading = true;
            this.germplasmService.getGermplasmSamplesByGid(this.germplasmDetailsContext.gid).toPromise().then((samples) => {
                this.germplasmSamples = samples;
                this.isSamplesLoading = false;
            });
        } else if (this.selectedLinkBy === this.LINK_BY_GUID) {
            this.germplasmSearchValue = this.germplasmDetailsContext.germplasm.germplasmUUID;
            this.linkByChanged();
        }
    }

    linkByChanged() {
        this.resetForm();
        this.isStudyLoading = true;
        let searchGermplasmRequest: SearchGermplasmRequest;
        if (this.selectedLinkBy === this.LINK_BY_NAME) {
            searchGermplasmRequest = { germplasmNames: [this.germplasmSearchValue], programDbIds: [this.cropGenotypingParameter.programId] };
            this.loadGenotypingStudy(searchGermplasmRequest);
        } else if (this.selectedLinkBy === this.LINK_BY_GUID) {
            searchGermplasmRequest = { externalReferenceIds: [this.germplasmSearchValue], programDbIds: [this.cropGenotypingParameter.programId] };
            this.loadGenotypingStudy(searchGermplasmRequest);
        } else if (this.selectedLinkBy === this.LINK_BY_SAMPLE_UID) {
            const searchSamplesRequest: SearchSamplesRequest = { externalReferenceIds: [this.germplasmSearchValue], programDbIds: [this.cropGenotypingParameter.programId] };
            this.searchSamples(searchSamplesRequest)
        }
    }

    searchSamples(searchSamplesRequest: SearchSamplesRequest) {
        // Search the Sample first by externalReferenceID,
        // If sample is available, we should get the germplasm associated to it, and use the germplasmDbId to
        // load the Gigwa study and calls.
        this.genotypingBrapiService.searchSamples(searchSamplesRequest).toPromise().then((response) => {
            if (response && response.result.data.length) {
                this.loadGenotypingStudy({ germplasmDbIds: [response.result.data[0].germplasmDbId], programDbIds: [this.cropGenotypingParameter.programId] });
            } else {
                this.isStudyLoading = false;
                this.alertService.error('genotyping.no.genotyping.germplasm.found', { linkType: this.selectedLinkBy, germplasmSearchValue: this.germplasmSearchValue });
            }
        });
    }

    loadGenotypingStudy(searchGermplasmRequest: SearchGermplasmRequest) {
        this.genotypingBrapiService.searchGermplasm(searchGermplasmRequest).pipe(flatMap((response) => {
            if (response && response.result.data.length) {
                this.genotypingGermplasm = response.result.data[0];
                return this.genotypingBrapiService.searchStudies({
                    germplasmDbIds: [this.genotypingGermplasm.germplasmDbId],
                    programDbIds: [this.cropGenotypingParameter.programId]
                });
            } else {
                this.isStudyLoading = false;
                this.alertService.error('genotyping.no.genotyping.germplasm.found', { linkType: this.selectedLinkBy, germplasmSearchValue: this.germplasmSearchValue });
            }
            return Observable.empty();
        })).subscribe((brapiResponse) => {
            if (brapiResponse && brapiResponse.result.data.length) {
                this.genotypingStudies = brapiResponse.result.data;
            } else {
                this.alertService.error('genotyping.no.genotyping.studies.found');
            }
            this.isStudyLoading = false;
        }, (error) => {
            // FIXME: Gigwa server throws an http 500 error if the germplasm is not found.
            this.alertService.error('genotyping.no.genotyping.germplasm.found');
            this.isStudyLoading = false;
        });
    }

    selectStudyOnChange() {
        if (this.selectedGenotypingStudy) {
            this.isVariantSetLoading = true;
            this.genotypingBrapiService.searchVariantsets({ studyDbIds: [this.selectedGenotypingStudy.studyDbId] }).toPromise().then((brapiResponse) => {
                if (brapiResponse && brapiResponse.result.data.length) {
                    this.genotypingVariantsets = brapiResponse.result.data;
                } else {
                    this.alertService.error('genotyping.no.genotyping.variantsets.found');
                }
                this.isVariantSetLoading = false;
            });
        }
    }

    selectVariantsetOnChange() {
        if (this.genotypesView === '2') {
            this.loadFlapjackBytes();
        } else {
            this.loadGenotypesTable();
        }
    }

    loadFlapjackBytes() {
        if (this.selectedVariantSet) {
            this.isExportingFlapjack = true;

            const gigwaExportRequest = new GigwaExportRequest();
            gigwaExportRequest.variantSetId = this.selectedVariantSet.referenceSetDbId;
            gigwaExportRequest.exportFormat = 'FLAPJACK';
            gigwaExportRequest.exportedIndividuals = [this.genotypingGermplasm.germplasmName];
            gigwaExportRequest.keepExportOnServer = true;

            const ga4GHVariantSearchRequest = new GA4GHSearchRequest();
            ga4GHVariantSearchRequest.variantSetId = this.selectedVariantSet.referenceSetDbId;

            // Before exporting the data into Flapjack format, invoking Variants Search first is required in order to be able to export
            // because this is what tells the Gigwa which variants to include.
            this.genotypingBrapiService.ga4GhVariantsSearch(ga4GHVariantSearchRequest).toPromise().then((searchResult) => {
                if (searchResult.count) {
                    this.genotypingBrapiService.gigwaExportData(gigwaExportRequest).toPromise().then(async(response) => {
                        this.isExportingFlapjack = false;
                        let file = response.replace('.fjzip', '');
                        file = this.extractHostName(this.cropGenotypingParameter.baseUrl) + file;

                        const flapjackDiv = '#flapjack-div';
                        const renderer = flapjack.default();
                        renderer.renderGenotypesUrl({
                            domParent: flapjackDiv,
                            width: document.querySelector(flapjackDiv).getBoundingClientRect().width,
                            height: 250,
                            mapFileURL: file + '.map',
                            genotypeFileURL: file + '.genotype',
                            phenotypeFileURL: await this.getFileUrl(file + '.phenotype'),
                            overviewWidth: document.querySelector(flapjackDiv).getBoundingClientRect().width,
                            overviewHeight: 25,
                            dataSetId: this.cropGenotypingParameter.programId,
                        });
                    });
                } else {
                    this.isExportingFlapjack = false;
                    this.alertService.error('genotyping.no.genotyping.callsets.found');
                }
            });
        }
    }

    loadGenotypesTable() {
        if (this.selectedVariantSet) {
            this.genotypingBrapiService.searchCallsets({
                variantSetDbIds: [this.selectedVariantSet.variantSetDbId],
                germplasmDbIds: [this.genotypingGermplasm.germplasmDbId]
            }).subscribe((brapiResponse) => {
                if (brapiResponse && brapiResponse.result.data.length) {
                    this.genotypingCallSet = brapiResponse.result.data[0];
                    this.loadGenotypingCalls();
                } else {
                    this.alertService.error('genotyping.no.genotyping.callsets.found');
                }
            });
        }
    }

    loadGenotypingCalls() {
        if (this.genotypingCallSet) {
            this.isGenotypingCallsLoading = true;
            this.genotypingBrapiService.searchCalls({
                callSetDbIds: [this.genotypingCallSet.callSetDbId],
                pageSize: this.pageSize,
                page: (this.page - 1)
            }).subscribe(((brapiResponse) => {
                this.genotypingCalls = brapiResponse.result.data;
                this.totalCount = brapiResponse.metadata.pagination.totalCount;
                this.isGenotypingCallsLoading = false;
            }));
        }
    }

    getFileUrl(url): Promise<string> {
        return new Promise<string>((resolve) => {
            this.http.head(url, { observe: 'response' }).subscribe(
                (response) => {
                    // If file doesn’t exist on the server, return undefined.
                    if (response.status === 200) {
                        resolve(url);
                    } else {
                        resolve(undefined);
                    }
                },
                () => {
                    resolve(undefined);
                }
            );
        });
    }

    extractHostName(baseUrl) {
        const { hostname, protocol } = new URL(baseUrl);
        return protocol + '//' + hostname;
    }

    resetForm() {
        this.jhiAlertService.clear();
        this.genotypingGermplasm = null;
        this.genotypingStudies = [];
        this.genotypingVariantsets = [];
        this.selectedGenotypingStudy = null;
        this.selectedVariantSet = null;
        this.genotypingCallSet = null;
        this.genotypingCalls = null;
    }

    getCallsetAndVariantCountText() {
        return `${this.selectedVariantSet.callSetCount} individuals; ${this.selectedVariantSet.variantCount} markers`;
    }

}
