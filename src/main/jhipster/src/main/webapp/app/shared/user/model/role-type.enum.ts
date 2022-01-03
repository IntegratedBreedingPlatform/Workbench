export class RoleTypeEnum {
    static readonly INSTANCE = new RoleTypeEnum(1, 'INSTANCE');
    static readonly CROP = new RoleTypeEnum(2, 'CROP');
    static readonly PROGRAM = new RoleTypeEnum(3, 'PROGRAM');

    constructor(
        readonly id: number,
        readonly name: string
    ) {
    }
}
