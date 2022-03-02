export class GermplasmMatchRequest {
    constructor(
        public germplasmPUIs?: Array<string>,
        public germplasmUUIDs?: Array<string>,
        public gids?: Array<number>,
        public names?: Array<string>,
        public locationName?: any,
        public locationAbbreviation?: any,
        public methods?: Array<string>,
        public nameTypes?: Array<string>
    ) {
    }
}