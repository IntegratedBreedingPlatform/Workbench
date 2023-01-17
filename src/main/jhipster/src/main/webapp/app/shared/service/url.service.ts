import { Injectable } from '@angular/core';
import { GERMPLASM_LIST_MANAGER_URL, STUDY_URL } from '../../app.constants';
import { NavbarMessageEvent } from '../model/navbar-message.event';
import { Program } from '../program/model/program';

@Injectable()
export class UrlService {

    openStudy(studyId: any, studyName: string, program: Program = null) {
        let message: NavbarMessageEvent;
        if (program) {
            message = {
                programSelected: program,
                toolSelectedUrl: STUDY_URL + studyId,
                toolSelectedName: 'Study: ' + studyName
            }
        } else {
            // TODO untested
            message = {
                toolSelectedUrl: STUDY_URL + studyId,
                toolSelectedName: 'Study: ' + studyName
            }
        }
        window.top.postMessage(message, '*');
        return false;
    }

    openList(listId: number, listName: string, program: Program = null) {
        const queryParams = `listId=${listId}&listName=${listName}`;
        const url = `${GERMPLASM_LIST_MANAGER_URL}/list/${listId}?${queryParams}`;
        let message: NavbarMessageEvent;
        if (program) {
            message = {
                programSelected: program,
                toolSelectedUrl: url,
                toolSelectedName: 'List: ' + listName
            }
        } else {
            // TODO untested
            message = {
                toolSelectedUrl: url,
                toolSelectedName: 'List: ' + listName
            }
        }
        window.top.postMessage(message, '*');
        return false;
    }
}
