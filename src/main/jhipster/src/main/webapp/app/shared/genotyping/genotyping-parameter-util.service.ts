import { Injectable } from '@angular/core';
import { CropParameterService } from '../crop-parameter/service/crop-parameter.service';
import { CropGenotypingParameter } from '../crop/model/crop-genotyping-parameter';
import { Observable } from 'rxjs';
import { CropParameter } from '../crop-parameter/model/crop-parameter';
import { map } from 'rxjs/operators';

@Injectable()
export class GenotypingParameterUtilService {

    private readonly GENOTYPING_SERVER = 'gigwa';

    private readonly ENDPOINT = this.GENOTYPING_SERVER + '_endpoint';
    private readonly TOKEN_ENDPOINT = this.GENOTYPING_SERVER + '_token_endpoint';
    private readonly PROGRAM_ID = this.GENOTYPING_SERVER + '_program_id';
    private readonly USERNAME = this.GENOTYPING_SERVER + '_username';
    private readonly PASSWORD = this.GENOTYPING_SERVER + '_password';
    private readonly BASE_URL = this.GENOTYPING_SERVER + '_base_url';

    constructor(private cropParameterService: CropParameterService) {
    }

    /**
     * Convenience method to get the genotyping parameters required for connecting to Gigwa server. This will also
     * return the access token generated from Gigwa server.
     */
    getGenotypingParametersAndAuthenticate(): Observable<CropGenotypingParameter> {
        return new Observable<CropGenotypingParameter>((observer) => {
            this.getCropGenotypingParameter().toPromise().then((cropGenotypingParameter) => {
                if (this.isGenotypingParameterConfigured(cropGenotypingParameter)) {
                    this.cropParameterService.getGenotypingToken(this.GENOTYPING_SERVER).toPromise().then((accessToken) => {
                        cropGenotypingParameter.accessToken = accessToken;
                        observer.next(cropGenotypingParameter);
                    }, (error) => {
                        observer.error('genotyping.connection.error');
                    });
                } else {
                    observer.error('genotyping.database.not.configured');
                }
            });
        });
    }

    private getCropGenotypingParameter(): Observable<CropGenotypingParameter | undefined> {
        return this.cropParameterService.getByGroupName(this.GENOTYPING_SERVER).pipe(map((cropParameters) => this.createGenotypingParameters(cropParameters)));
    }

    private createGenotypingParameters(cropParameters: CropParameter[]): CropGenotypingParameter | undefined {

        const cropParameterMap = cropParameters.reduce((map1, row) => {
            map1[row.key] = row;
            return map1;
        }, {});

        // If the genotyping parameter values are incomplete, return undefined.
        if (cropParameterMap[this.ENDPOINT] && cropParameterMap[this.TOKEN_ENDPOINT] && cropParameterMap[this.PROGRAM_ID]
            && cropParameterMap[this.USERNAME] && cropParameterMap[this.PASSWORD] && cropParameterMap[this.BASE_URL]) {
            return new CropGenotypingParameter(cropParameterMap[this.ENDPOINT].value,
                cropParameterMap[this.TOKEN_ENDPOINT].value, cropParameterMap[this.USERNAME].value, cropParameterMap[this.PASSWORD].value,
                cropParameterMap[this.PROGRAM_ID].value, cropParameterMap[this.BASE_URL].value);
        }
    }

    isGenotypingParameterConfigured(cropGenotypingParameter: CropGenotypingParameter) {
        return cropGenotypingParameter && cropGenotypingParameter.endpoint && cropGenotypingParameter.tokenEndpoint && cropGenotypingParameter.userName
            && cropGenotypingParameter.password && cropGenotypingParameter.programId && cropGenotypingParameter.baseUrl;
    }

}
