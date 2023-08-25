import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ParamContext} from '../../service/param.context';
import {SERVER_API_URL} from '../../../app.constants';
import {CrossPlanDesignInput} from '../model/cross-plan-design-input';
import {Observable} from 'rxjs';

@Injectable()
export class CrossPlanDesignService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    generateCrossPreview(crossPlanDesignInput?: CrossPlanDesignInput): Observable<any> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/crossPlan/design/generation`;
        return this.http.post<any>(url, crossPlanDesignInput);
    }
}