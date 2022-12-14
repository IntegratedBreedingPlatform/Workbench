import { Injectable } from '@angular/core';
import { CREATE_STUDY_URL, GERMPLASM_LIST_MANAGER_URL, STUDY_URL } from '../../app.constants';
import { NavbarMessageEvent } from '../model/navbar-message.event';
import { Program } from '../program/model/program';

@Injectable()
export class UrlService {

    createStudy() {
        window.top.postMessage({ toolSelected: CREATE_STUDY_URL }, '*');
        return false;
    }

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

    openList(listId: number, listName: string, program: Program = null) {
        const queryParams = `listId=${listId}&listName=${listName}`;
        const url = `${GERMPLASM_LIST_MANAGER_URL}/list/${listId}?${queryParams}`;
        let message: NavbarMessageEvent;
        if (program) {
            message = {
                programSelected: program,
                toolSelected: url
            }
        } else {
            // TODO untested
            message = {
                toolSelected: url
            }
        }
        window.top.postMessage(message, '*');
        return false;
    }
}
