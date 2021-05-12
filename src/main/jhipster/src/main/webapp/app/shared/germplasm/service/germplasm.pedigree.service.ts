import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { GermplasmTreeNode } from '../model/germplasm-tree-node.model';

@Injectable()
export class GermplasmPedigreeService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getGermplasmTree(gid: number, level: number, includeDerivativeLines: boolean): Observable<GermplasmTreeNode> {
        return this.http.get<GermplasmTreeNode>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/tree/${level}?programUUID=${this.context.programUUID}&includeDerivativeLines=${includeDerivativeLines}`);
    }

}
