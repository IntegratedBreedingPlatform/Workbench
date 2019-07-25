import { IObjectComparator } from './../shared/components/datagrid/object-comparator.interface';

export class RoleComparator implements IObjectComparator {

    constructor (){
    }

    public same (source, target): boolean {

        if (source === target) return true;
        if ( ! ( source instanceof Object ) || ! ( target instanceof Object ) ) return false;
        // if they are not strictly equal, they both need to be Objects
        for ( let prop in source ) {
            // console.log("source " + source[prop]);
            if (!source.hasOwnProperty(prop)) continue;
            if (source[prop] === undefined || source[prop] === null || source[prop] === '') continue;
            if (typeof source[prop] === 'object' && this.same(source[prop], target[prop])) continue;

            if (typeof source[prop] === 'string' && typeof target[prop] === 'string' && target[prop].startsWith(source[prop])) continue;
            if (typeof source[prop] === 'string' && target[prop].length && target[prop].indexOf(source[prop]) != -1) continue;
            if (source[prop] === target[prop]) continue;
            if (typeof target[prop] === 'string' && source[prop].toUpperCase() === target[prop].toUpperCase()) continue;
            if (source[prop] == "undefined") continue;
            //Option for status Active
            if(prop == "active" && source[prop] == "undefined" && target[prop] === true) continue;
            if((source[prop] == "undefined" && prop != "active") || source[prop] == "all") continue;
            return false;
        }

        if(source["active"] == undefined && target["active"] == false) {
            return false;
        }

        return true;
    };

    public equals ( x: any, y: any ): boolean {

        if ( x === y ) return true;
        // if both x and y are null or undefined and exactly the same

        if ( ! ( x instanceof Object ) || ! ( y instanceof Object ) ) return false;
        // if they are not strictly equal, they both need to be Objects

        if ( x.constructor !== y.constructor ) return false;
        // they must have the exact same prototype chain, the closest we can do is
        // test there constructor.

        for ( var p in x ) {
            if ( ! x.hasOwnProperty( p ) ) continue;
            // other properties were tested using x.constructor === y.constructor

            if ( ! y.hasOwnProperty( p ) ) return false;
            // allows to compare x[ p ] and y[ p ] when set to undefined

            if ( x[ p ] === y[ p ] ) continue;
            // if they have the same strict value or identity then they are equal

            if ( typeof( x[ p ] ) !== "object" ) return false;
            // Numbers, Strings, Functions, Booleans must be strictly equal

            if ( ! this.equals( x[ p ],  y[ p ] ) ) return false;
            // Objects and Arrays must be tested recursively
        }

        for ( p in y ) {
            if ( y.hasOwnProperty( p ) && ! x.hasOwnProperty( p ) ) return false;
            // allows x[ p ] to be set to undefined
        }
        return true;
    };
}
