import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material';
import {FileInput, FileValidator} from 'ngx-material-file-input';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {NetworkMotif} from './Model/NetworkMotif';
import {ApiService} from './api.service';

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
        const isSubmitted = form && form.submitted;
        return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
    }
}

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    constructor(private fb: FormBuilder, private apiService: ApiService) {
        this.mSControl.setValue('3');
        this.rSControl.setValue('10');
    }
    title = 'Nemolib-ng-app';
    formDoc: FormGroup;
    public probSel: number;
    public prob = [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5];
    private valid: boolean;
    public results = 'Results';
    readonly maxSize = 104857600; // maximum size = 100mb
    mSControl = new FormControl('', [
        Validators.required,
        Validators.min(3),
        Validators.max(8)
    ]);
    rSControl = new FormControl('', [
        Validators.required,
        Validators.min(10)
    ]);
    matcher = new MyErrorStateMatcher();
    good: string;

    ngOnInit() {
        this.formDoc = this.fb.group({
            reFile: [
                undefined, [Validators.required, FileValidator.maxContentSize(this.maxSize)]
            ]
        });
    }

    create() {
        const newNet: NetworkMotif = {
            motifSize: this.mSControl.value,
            randSize: this.rSControl.value
        };

        this.apiService.submitNetworkMotif(newNet).subscribe(
            res => {
                newNet.motifSize = res.motifSize;
            },
            err => {
                alert('An error occurred while saving the note');
            }
        );
    }

    postData(): void {
        this.valid = true;
        // const file = this.formDoc.value;
        let fileData = Object.assign({});
        fileData = Object.assign(fileData, this.formDoc.value);
        const motifSize = this.mSControl.value;
        const randSize = this.rSControl.value;
        if (this.probSel == null || !this.mSControl.valid || !this.rSControl.valid || !this.formDoc.valid) {
            this.valid = false;
        } else {
            for (let i = 0; i < motifSize; i++) {
                if (this.prob[i] == null || this.prob[i] <= 0 || this.prob[i] > 1) {
                    this.valid = false;
                }
            }
        }
        if (this.valid) {
            this.results = motifSize + ' \n' + this.prob + '\n' + randSize;
        } else {
            this.results = 'not valid';
        }
        console.log(fileData);
        const file = fileData.files;
        console.log(file);
        console.log(fileData);

        const formData: FormData = new FormData();
        formData.append('file', fileData);
        formData.append('motifSize', motifSize.toString());
        formData.append('motifSize', randSize.toString());
        const payload = new HttpParams()
            .set('file', fileData)
            .set('motifSize', motifSize)
            .set('motifSize', randSize);

        // this.http.post('http://34.221.211.106:8080/compute/networkmotif', payload);
        // action="http://34.221.211.106:8080/compute/networkmotif" method="post" enctype="multipart/form-data"
        // target="_blank"
        const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
        this.create();
    }
}

