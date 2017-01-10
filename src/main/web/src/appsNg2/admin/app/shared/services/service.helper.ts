import {Headers} from "@angular/http";

export default class ServiceHelper {

    static getHeaders(): Headers {
        let headers = new Headers();
        headers.append('Accept', 'application/json');
        headers.append('Authorization', 'Bearer ' + JSON.parse(localStorage["bms.xAuthToken"]).token);
        return headers;
    }
}
