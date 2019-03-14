import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NetworkMotif} from './Model/NetworkMotif';
import {NetworkMotifResults} from './Model/NetworkMotifResults';



@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private BASE_URL =  'http://34.221.211.106:8080' + '/compute';
    private SUBMIT_NETWORKMOTIF_URL = `${this.BASE_URL}/networkmotif/`;

    constructor(private http: HttpClient) {

    }

    submitNetworkMotif(networkMotif: NetworkMotif): Observable<NetworkMotif> {
        return this.http.post<NetworkMotifResults>(this.SUBMIT_NETWORKMOTIF_URL, networkMotif);
    }
}

