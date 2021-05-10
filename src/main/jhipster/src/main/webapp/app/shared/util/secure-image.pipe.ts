import { Pipe, PipeTransform } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

@Pipe({
    name: 'secureImage'
})
export class SecureImagePipe implements PipeTransform {

    constructor(private http: HttpClient) {
    }

    transform(url: string) {

        return new Observable<string>((observer: any) => {
            /*
             * This is a tiny blank image
             * If we don't put something into the image immediately, the browser will still try to load the image using HTTP
             */
            observer.next('data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==');

            // The next and error callbacks from the observer
            const { next, error } = observer;

            this.http.get(url, { responseType: 'blob' }).subscribe((response) => {
                const reader = new FileReader();
                reader.readAsDataURL(response);
                reader.onloadend = () => {
                    observer.next(reader.result);
                };
            });

            return {
                unsubscribe() {
                }
            };
        });
    }
}
