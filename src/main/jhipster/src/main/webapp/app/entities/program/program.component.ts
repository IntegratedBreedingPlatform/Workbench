import { Component } from '@angular/core';
import { ProgramService } from '../../shared/program/service/program.service';
import { Observable } from 'rxjs';
import { Program } from '../../shared/program/model/program';

@Component({
    selector: 'jhi-program',
    templateUrl: './program.component.html',
    // TODO migrate IBP-4093
    styleUrls: ['../../../content/css/global-bs4.scss']
})
export class ProgramComponent {

    programs: Observable<Program[]>;

    constructor(
        private programService: ProgramService
    ) {
        this.programs = this.programService.getPrograms();
    }

    onProgramSelect(program: Program) {
        window.parent.postMessage({programSelected: program}, '*');
    }
}
