import { RevisionType } from './revision-type';
import { DateFormatEnum, formatDateToUTC } from '../../shared/util/date-utils';
import { GermplasmAudit } from './germplasm-audit.model';

export function getEventUser(germplasmAudit: GermplasmAudit): string {
    return (germplasmAudit.revisionType ===  RevisionType.CREATION) ? germplasmAudit.createdBy : germplasmAudit.modifiedBy;
}

export function getEventDate(germplasmAudit: GermplasmAudit): string {
    const date: number = (germplasmAudit.revisionType ===  RevisionType.CREATION) ? germplasmAudit.createdDate : germplasmAudit.modifiedDate;
    return formatDateToUTC(date, DateFormatEnum.ISO_8601_AND_TIME) + ' UTC';
}
