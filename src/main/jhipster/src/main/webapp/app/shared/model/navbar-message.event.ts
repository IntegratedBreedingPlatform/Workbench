import { Program } from '../program/model/program';

export interface NavbarMessageEvent {
    programSelected?: Program,
    programUpdated?: Program,
    programDeleted?: any,
    toolSelected?: string,
    userProfileChanged?: boolean
}
