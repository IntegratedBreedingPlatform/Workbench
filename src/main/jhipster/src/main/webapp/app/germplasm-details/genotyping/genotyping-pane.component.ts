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

@Component({
    selector: 'jhi-genotyping-pane',
    templateUrl: './genotyping-pane.component.html',
    providers: [{ provide: 'test', useValue: '' }]
})
export class GenotypingPaneComponent implements OnInit {

    totalCount = 10;
    page: number = 1;
    pageSize: number = 10;
    isLoading = false;

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

    constructor(
        private context: ParamContext,
        private cropGenotypingParameterService: CropGenotypingParameterService,
        private genotypingBrapiService: GenotypingBrapiService,
        public germplasmDetailsContext: GermplasmDetailsContext,
        public germplasmService: GermplasmService,
        public alertService: AlertService) {
    }

    cropGenotypingParameter: CropGenotypingParameter;


    ngOnInit(): void {
        this.cropGenotypingParameterService.getByCropName(this.context.cropName).pipe(flatMap((result) => {
            this.cropGenotypingParameter = result;
            this.genotypingBrapiService.baseUrl = this.cropGenotypingParameter.endpoint;
            return this.cropGenotypingParameterService.getToken(this.context.cropName);
        })).subscribe((accessToken) => {
            this.genotypingBrapiService.accessToken = accessToken;
            this.linkBySelectOnChange();
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
            this.germplasmService.getGermplasmSamplesByGid(this.germplasmDetailsContext.gid).toPromise().then((samples) => {
                this.germplasmSamples = samples;
            });
        } else if (this.selectedLinkBy === this.LINK_BY_GUID) {
            this.germplasmSearchValue = this.germplasmDetailsContext.germplasm.germplasmUUID;
            this.linkByChanged();
        }
    }

    linkByChanged() {
        this.resetForm();
        let searchGermplasmRequest: SearchGermplasmRequest;
        if (this.selectedLinkBy === this.LINK_BY_NAME) {
            searchGermplasmRequest = { germplasmNames: [this.germplasmSearchValue], programDbIds: [this.cropGenotypingParameter.programId] };
        } else if (this.selectedLinkBy === this.LINK_BY_GUID || this.selectedLinkBy === this.LINK_BY_SAMPLE_UID) {
            searchGermplasmRequest = { externalReferenceIds: [this.germplasmSearchValue], programDbIds: [this.cropGenotypingParameter.programId] };
        }

        this.genotypingBrapiService.searchGermplasm(searchGermplasmRequest).pipe(flatMap((response) => {
            if (response && response.result.data.length) {
                this.genotypingGermplasm = response.result.data[0];
                return this.genotypingBrapiService.searchStudies({
                    germplasmDbIds: [this.genotypingGermplasm.germplasmDbId],
                    programDbIds: [this.cropGenotypingParameter.programId]
                });
            } else {
                this.alertService.error('genotyping.no.genotyping.germplasm.found', { linkType: this.selectedLinkBy, germplasmSearchValue: this.germplasmSearchValue });
            }
            return Observable.empty();
        })).subscribe((brapiResponse) => {
            if (brapiResponse && brapiResponse.result.data.length) {
                this.genotypingStudies = brapiResponse.result.data;
            } else {
                this.alertService.error('genotyping.no.genotyping.studies.found');
            }
        }, (error) => {
            // FIXME: Gigwa server throws an http 500 error if the germplasm is not found.
            this.alertService.error('genotyping.no.genotyping.germplasm.found');
        });

    }

    selectStudyOnChange() {
        if (this.selectedGenotypingStudy) {
            this.genotypingBrapiService.searchVariantsets({ studyDbIds: [this.selectedGenotypingStudy.studyDbId] }).toPromise().then((brapiResponse) => {
                if (brapiResponse && brapiResponse.result.data.length) {
                    this.genotypingVariantsets = brapiResponse.result.data;
                } else {
                    this.alertService.error('genotyping.no.genotyping.variantsets.found');
                }
            });
        }
    }

    selectVariantsetOnChange() {
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

    loadGenotypingCalls() {
        if (this.genotypingCallSet) {
            this.isLoading = true;
            this.genotypingBrapiService.searchCalls({
                callSetDbIds: [this.genotypingCallSet.callSetDbId],
                pageSize: this.pageSize,
                pageToken: (this.page - 1).toString()
            }).subscribe((brapiResponse => {
                this.genotypingCalls = brapiResponse.result.data;
                this.totalCount = brapiResponse.metadata.pagination.totalCount;
                this.isLoading = false;
            }));
        }
    }

    resetForm() {
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
