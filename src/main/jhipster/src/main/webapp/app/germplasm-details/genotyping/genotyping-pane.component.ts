import { Component, OnInit } from '@angular/core';
import { CropGenotypingParameterService } from '../../shared/crop/service/crop-genotyping-parameter.service';
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
import { ExportFlapjackRequest } from '../../shared/brapi/model/export/export-flapjack-request';
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
    isGenotypingCallsLoading = false;
    isStudyLoading = false;
    isVariantSetLoading = false;
    isSamplesLoading = false;

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
        private cropGenotypingParameterService: CropGenotypingParameterService,
        private genotypingBrapiService: GenotypingBrapiService,
        public germplasmDetailsContext: GermplasmDetailsContext,
        public germplasmService: GermplasmService,
        public alertService: AlertService,
        public jhiAlertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.cropGenotypingParameterService.getByCropName(this.context.cropName).pipe(flatMap((result) => {
            this.cropGenotypingParameter = result;
            this.genotypingBrapiService.brapiEndpoint = this.cropGenotypingParameter.endpoint;
            this.genotypingBrapiService.baseUrl = this.cropGenotypingParameter.baseUrl;
            return this.cropGenotypingParameterService.getToken(this.context.cropName);
        })).subscribe((accessToken) => {
            this.genotypingBrapiService.accessToken = accessToken;
            if (this.isGenotypingParameterConfigured()) {
                this.linkBySelectOnChange();
            }
        }, (error) => {
            this.alertService.error('genotyping.connection.error');
        });
    }

    isGenotypingParameterConfigured() {
        return this.cropGenotypingParameter && this.cropGenotypingParameter.cropName && this.cropGenotypingParameter.endpoint && this.cropGenotypingParameter.tokenEndpoint
            && this.cropGenotypingParameter.userName && this.cropGenotypingParameter.password && this.cropGenotypingParameter.programId;
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
        const exportFlapjackRequest = new ExportFlapjackRequest([], [], 'FLAPJACK', [this.germplasmSearchValue], true,
            100, this.selectedVariantSet.referenceSetDbId);
        this.genotypingBrapiService.exportFlapjack(exportFlapjackRequest).subscribe((response) => {
            let file = response.replace('.fjzip', '').replace('/gigwaV2', '');
            file = this.cropGenotypingParameter.baseUrl + file;

            const renderer = flapjack.default();
            renderer.renderGenotypesUrl({
                domParent: '#flapjack-div',
                width: 750,
                height: 300,
                mapFileURL: file + '.map',
                genotypeFileURL: file + '.genotype',
                phenotypeFileURL: file + '.phenotype',
                overviewWidth: 750,
                overviewHeight: 50,
                dataSetId: this.cropGenotypingParameter.programId,
            });
        });
    }

    loadGenotypingCalls() {
        if (this.genotypingCallSet) {
            this.isGenotypingCallsLoading = true;
            this.genotypingBrapiService.searchCalls({
                callSetDbIds: [this.genotypingCallSet.callSetDbId],
                pageSize: this.pageSize,
                pageToken: (this.page - 1).toString()
            }).subscribe(((brapiResponse) => {
                this.genotypingCalls = brapiResponse.result.data;
                this.totalCount = brapiResponse.metadata.pagination.totalCount;
                this.isGenotypingCallsLoading = false;
            }));
        }
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
