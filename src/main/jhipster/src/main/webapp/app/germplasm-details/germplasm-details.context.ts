import { Injectable } from '@angular/core';
import { GermplasmDto } from '../shared/germplasm/model/germplasm.model';

@Injectable()
export class GermplasmDetailsContext {
    gid: number;
    germplasm: GermplasmDto;
}
