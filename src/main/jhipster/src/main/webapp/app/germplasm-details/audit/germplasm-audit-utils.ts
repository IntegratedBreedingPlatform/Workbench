import { RevisionType } from './revision-type';
import { parseDate } from '../../shared/util/date-utils';
import { GermplasmAudit } from './germplasm-audit.model';

export function getEventUser(germplasmAudit: GermplasmAudit): string {
    return (germplasmAudit.revisionType ===  RevisionType.CREATION) ? germplasmAudit.createdBy : germplasmAudit.modifiedBy;
}

export function getEventDate(germplasmAudit: GermplasmAudit): string {
    const date: number = (germplasmAudit.revisionType ===  RevisionType.CREATION) ? germplasmAudit.createdDate : germplasmAudit.modifiedDate;
    return parseDate(date);
}
