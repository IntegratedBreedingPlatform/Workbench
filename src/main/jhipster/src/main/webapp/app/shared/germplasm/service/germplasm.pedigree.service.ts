import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { GermplasmTreeNode } from '../model/germplasm-tree-node.model';
import { GermplasmDto } from '../model/germplasm.model';
import { GermplasmNeighborhoodTreeNode } from '../model/germplasm-neighborhood-tree-node.model';

@Injectable()
export class GermplasmPedigreeService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getGermplasmTree(gid: number, level: number, includeDerivativeLines: boolean): Observable<GermplasmTreeNode> {
        return this.http.get<GermplasmTreeNode>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/tree/${level}?programUUID=${this.context.programUUID}&includeDerivativeLines=${includeDerivativeLines}`);
    }

    getGenerationHistory(gid: number): Observable<GermplasmDto[]> {
        return this.http.get<GermplasmDto[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/generation-history`);
    }

    getManagementNeighbors(gid: number): Observable<GermplasmDto[]> {
        return this.http.get<GermplasmDto[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/management-neighbors`);
    }

    getGroupRelatives(gid: number): Observable<GermplasmDto[]> {
        return this.http.get<GermplasmDto[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/group-relatives`);
    }

    getDerivativeNeighborhood(gid: number, numberOfStepsBackward?: number, numberOfStepsForward?: number): Observable<GermplasmNeighborhoodTreeNode> {
        const params = {};
        if (numberOfStepsBackward) {
            params['numberOfStepsBackward'] = numberOfStepsBackward;
        }
        if (numberOfStepsForward) {
            params['numberOfStepsForward'] = numberOfStepsForward;
        }
        return this.http.get<GermplasmNeighborhoodTreeNode>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/derivative-neighbors`, {
            params
        });
    }

    getMaintenanceNeighborhood(gid: number, numberOfStepsBackward?: number, numberOfStepsForward?: number): Observable<GermplasmNeighborhoodTreeNode> {
        const params = {};
        if (numberOfStepsBackward) {
            params['numberOfStepsBackward'] = numberOfStepsBackward;
        }
        if (numberOfStepsForward) {
            params['numberOfStepsForward'] = numberOfStepsForward;
        }
        return this.http.get<GermplasmNeighborhoodTreeNode>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/maintenance-neighbors`, {
            params
        });
    }

}
