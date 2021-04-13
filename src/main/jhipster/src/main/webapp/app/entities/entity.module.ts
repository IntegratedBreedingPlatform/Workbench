import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';

import { BmsjHipsterSampleModule } from './sample/sample.module';
import { BreedingMethodModule } from './breeding-method/breeding-method.module';
import { LocationModule } from './location/location.module';
import { ProgramModule } from './program/program.module';
import { GermplasmNameModule } from './germplasm/name/germplasm-name.module';

/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BmsjHipsterSampleModule,
        BreedingMethodModule,
        LocationModule,
        ProgramModule,
        GermplasmNameModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BmsjHipsterEntityModule {
}
