import { IObjectComparator } from './../shared/components/datagrid/object-comparator.interface';

export class UserComparator implements IObjectComparator {

    constructor() {
    }

    public same(source, target): boolean {
        if (source === target) return true;
        if (!(source instanceof Object) || !(target instanceof Object)) return false;
        // if they are not strictly equal, they both need to be Objects
        for (let prop in source) {
            // console.log("source " + source[prop] + target[prop]);
            if (!source.hasOwnProperty(prop)) continue;
            if (source[prop] === undefined || source[prop] === null || source[prop] === '') continue;
            if (source[prop] == 'undefined') continue;

            //Option for status Active
            if (prop == 'active') {
                if (source[prop] == 'all' || source[prop] == 'undefined') continue;
                if (source[prop] === target[prop]) continue;
                if (source[prop] === String(target[prop])) continue;
                if (String(source[prop]) === target[prop]) continue;
                if (String(source[prop]) === String(target[prop])) continue;
            }
            //Option for Role type
            if (prop == 'roleNames') {
                if (source[prop] == 'all' || source[prop] == 'undefined') continue;
                if (target.userRoles.some(userRole => userRole.role.name == source[prop])) continue;
            }
            return false;
        }
        return true;
    };

    public equals(x: any, y: any): boolean {

        if (x === y) return true;
        // if both x and y are null or undefined and exactly the same

        if (!(x instanceof Object) || !(y instanceof Object)) return false;
        // if they are not strictly equal, they both need to be Objects

        if (x.constructor !== y.constructor) return false;
        // they must have the exact same prototype chain, the closest we can do is
        // test there constructor.

        for (var p in x) {
            if (!x.hasOwnProperty(p)) continue;
            // other properties were tested using x.constructor === y.constructor

            if (!y.hasOwnProperty(p)) return false;
            // allows to compare x[ p ] and y[ p ] when set to undefined

            if (x[p] === y[p]) continue;
            // if they have the same strict value or identity then they are equal

            if (typeof (x[p]) !== 'object') return false;
            // Numbers, Strings, Functions, Booleans must be strictly equal

            if (!this.equals(x[p], y[p])) return false;
            // Objects and Arrays must be tested recursively
        }

        for (p in y) {
            if (y.hasOwnProperty(p) && !x.hasOwnProperty(p)) return false;
            // allows x[ p ] to be set to undefined
        }
        return true;
    };
}
