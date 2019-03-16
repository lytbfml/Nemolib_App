import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NetworkMotif} from './Model/NetworkMotif';
import {NetworkMotifResults} from './Model/NetworkMotifResults';


@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private BASE_URL =  'http://34.221.211.106:8080' + '/compute';
    // private BASE_URL = 'http://localhost:8080' + '/compute';
    private SUBMIT_NETWORKMOTIF_URL = `${this.BASE_URL}/networkmotif/`;

    constructor(private http: HttpClient) {

    }

    submitNetworkMotif(networkMotif: FormData): Observable<HttpEvent<{}>> {
        const req = new HttpRequest('POST', this.SUBMIT_NETWORKMOTIF_URL, networkMotif, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(req);
    }

}
