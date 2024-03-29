import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';

import { BmsjHipsterSampleModule } from './sample/sample.module';
import { BreedingMethodModule } from './breeding-method/breeding-method.module';
import { LocationModule } from './location/location.module';
import { ProgramModule } from './program/program.module';
import { GermplasmProgenitorsModule } from './germplasm/progenitors/germplasm-progenitors.module';
import { GermplasmNameModule } from './germplasm/name/germplasm-name.module';
import { GermplasmAttributeModule } from './germplasm/attribute/germplasm-attribute.module';
import { GermplasmBasicDetailsModule } from './germplasm/basic-details/germplasm-basic-details.module';
import { GermplasmNameAuditModule } from '../germplasm-details/audit/names/germplasm-name-audit.module';
import { GermplasmAttributeAuditModule } from '../germplasm-details/audit/attributes/germplasm-attribute-audit.module';
import { GermplasmBasicDetailsAuditModule } from '../germplasm-details/audit/basic-details/germplasm-basic-details-audit.module';
import { GermplasmProgenitorsAuditModule } from '../germplasm-details/audit/progenitors/germplasm-progenitors-audit.module';
import { LotAttributeModule } from './lot/lot-attribute.module';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BmsjHipsterSampleModule,
        BreedingMethodModule,
        LocationModule,
        ProgramModule,
        GermplasmBasicDetailsModule,
        GermplasmNameModule,
        GermplasmAttributeModule,
        GermplasmProgenitorsModule,
        GermplasmNameAuditModule,
        GermplasmAttributeAuditModule,
        GermplasmBasicDetailsAuditModule,
        GermplasmProgenitorsAuditModule,
        LotAttributeModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterEntityModule {
}
