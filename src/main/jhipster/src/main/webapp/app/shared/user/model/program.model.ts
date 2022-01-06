import { Crop } from '../../model/crop.model';

// Added here in user folder to mirror backend structure
export class Program {
    id: number;
    name: string;
    uuid: string;
    crop: Crop
}
