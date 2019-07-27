import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NetworkMotif} from './Model/NetworkMotif';
import {NetworkMotifResults} from './Model/NetworkMotifResults';


@Injectable({
    providedIn: 'root'
})
export class ApiService {
    // private BASE_URL = 'http://bioresearch02p.uwb.edu:8082/' + '/compute';
    private BASE_URL = 'http://localhost:8082' + '/compute';
    private SUBMIT_NETWORKMOTIF_URL = `${this.BASE_URL}/networkmotif/`;
    private SUBMIT_NEMOPROFILE_URL = `${this.BASE_URL}/nemoprofile/`;
    private SUBMIT_NEMOCOLLECTION_URL = `${this.BASE_URL}/nemocollect/`;

    constructor(private http: HttpClient) {
    }

    submitNetworkMotif(networkMotif: FormData): Observable<HttpEvent<{}>> {
        const req = new HttpRequest('POST', this.SUBMIT_NETWORKMOTIF_URL, networkMotif, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(req);
    }

    submitNemoProfile(networkMotif: FormData): Observable<HttpEvent<{}>> {
        const req = new HttpRequest('POST', this.SUBMIT_NEMOPROFILE_URL, networkMotif, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(req);
    }

    submitNemoCollection(networkMotif: FormData): Observable<HttpEvent<{}>> {
        const req = new HttpRequest('POST', this.SUBMIT_NEMOCOLLECTION_URL, networkMotif, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(req);
    }
}
