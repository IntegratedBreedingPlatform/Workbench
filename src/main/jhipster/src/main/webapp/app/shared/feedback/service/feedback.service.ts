import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedbackFeatureEnum } from '../feedback-feature.enum';
import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class FeedbackService {

    private readonly resourceUrl: string;

    constructor(private http: HttpClient) {
        this.resourceUrl = SERVER_API_URL + 'feedback';
    }

    shouldShowFeedback(feature: FeedbackFeatureEnum): Observable<HttpResponse<boolean>> {
        return this.http.get<boolean>(this.resourceUrl + `/${feature}/should-show`, { observe: 'response' });
    }

    dontShowAgain(feature: FeedbackFeatureEnum): Observable<void> {
       return this.http.put<void>(this.resourceUrl + `/${feature}/dont-show-again`, {});
    }

}
