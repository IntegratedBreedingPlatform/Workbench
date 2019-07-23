export class Role {

    constructor(public id?: number,
                public name?: string,
                public description?: string,
                public type?: string,
                public active?: boolean,
                public editable?: boolean,
                public assignable?: boolean) {
    }
}