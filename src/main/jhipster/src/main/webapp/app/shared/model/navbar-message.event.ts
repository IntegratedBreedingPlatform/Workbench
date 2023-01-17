import { Program } from '../program/model/program';

export interface NavbarMessageEvent {
    programSelected?: Program,
    programUpdated?: Program,
    programDeleted?: any,
    toolSelectedUrl?: string,
    toolSelectedName?: string,
    userProfileChanged?: boolean
}
