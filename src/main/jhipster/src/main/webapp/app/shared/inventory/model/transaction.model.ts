import { BaseEntity } from '../../model/base-entity';
import { Lot } from './lot.model';

export class Transaction implements BaseEntity {
    constructor(
        public id?: number,
        public transactionId?: number,
        public createdByUsername?: string,
        public transactionType?: string,
        public transactionStatus?: string,
        public amount?: number,
        public notes?: string,
        public createdDate?: string,
        public lot?: Lot) {
    }
}

export class TransactionType implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string) {

    }
}

export class TransactionStatus implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string) {

    }
}
