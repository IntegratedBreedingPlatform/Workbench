import { Crop } from './crop.model';

export class Program {

    constructor(public id: number,
                public name: string,
                public uuid: string,
                public crop: Crop) {

    }
}