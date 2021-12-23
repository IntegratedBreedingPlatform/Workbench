import { Component } from '@angular/core';
import { UserDetail } from '../../../shared/user/model/user-detail.model';
import { MembersService } from '../../../shared/user/service/members.service';
import { AlertService } from '../../../shared/alert/alert.service';
import { Pageable } from '../../../shared/model/pageable';
import { DEFAULT_PAGE_SIZE } from '../../../shared';

/**
 * TODO:
 *  - drag drop
 *  - select
 *  - shift click
 *  - members sub-table
 *  - roles
 *  - assign role modal
 *  - save
 *  - reset
 */
@Component({
    selector: 'jhi-members-pane',
    templateUrl: 'members-pane.component.html'
})
export class MembersPaneComponent {

    eligibleUsers: UserDetail[];
    programMembers: UserDetail[];

    constructor(
        private membersService: MembersService,
        private alertService: AlertService
    ) {
        this.loadEligibleUsers(1, DEFAULT_PAGE_SIZE);
    }

    loadEligibleUsers(page, pageSize) {
        this.membersService.getMembersEligibleUsers(
            <Pageable>({
                page: page - 1,
                size: pageSize,
                sort: null
            })
        ).subscribe((resp) => this.eligibleUsers = resp.body);
    }

    reset() {

    }

    save() {

    }
}
