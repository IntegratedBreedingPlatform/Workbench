import { Injectable } from '@angular/core';
import { GERMPLASM_LIST_MANAGER_URL, STUDY_URL } from '../../app.constants';
import { NavbarMessageEvent } from '../model/navbar-message.event';
import { Program } from '../program/model/program';

@Injectable()
export class UrlService {

    openStudy(studyId: any, program: Program = null) {
        let message: NavbarMessageEvent;
        if (program) {
            message = {
                programSelected: program,
                toolSelected: STUDY_URL + studyId
            }
        } else {
            // TODO untested
            message = {
                toolSelected: STUDY_URL + studyId
            }
        }
        window.top.postMessage(message, '*');
        return false;
    }

    openList(listId: any, program: Program = null) {
        const queryParams = `?restartApplication&lists=${listId}`;
        let message: NavbarMessageEvent;
        if (program) {
            message = {
                programSelected: program,
                toolSelected: GERMPLASM_LIST_MANAGER_URL + queryParams
            }
        } else {
            // TODO untested
            message = {
                toolSelected: GERMPLASM_LIST_MANAGER_URL + queryParams
            }
        }
        window.top.postMessage(message, '*');
        return false;
    }
}
