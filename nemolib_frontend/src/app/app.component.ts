import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material';
import {FileInput, FileValidator} from 'ngx-material-file-input';
import {HttpClient, HttpEventType, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import {NetworkMotif} from './Model/NetworkMotif';
import {ApiService} from './api.service';
import {NetworkMotifResults} from './Model/NetworkMotifResults';

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

    currentFileUpload: File;
    progress: { percentage: number } = {percentage: 0};
    formDoc: FormGroup;
    probSel: number;
    prob = [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5];
    results = '';
    maxSize = 104857600; // maximum size = 100mb
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
    response: NetworkMotifResults;
    invalidCount = 0;

    ngOnInit() {
        this.formDoc = this.fb.group({
            reFile: [
                undefined, [Validators.required, FileValidator.maxContentSize(this.maxSize)]
            ]
        });
    }

    validation(): boolean {
        const motifSize = this.mSControl.value;
        if (this.probSel == null || !this.mSControl.valid || !this.rSControl.valid || !this.formDoc.valid) {
            return false;
        } else {
            for (let i = 0; i < motifSize; i++) {
                if (this.prob[i] == null || this.prob[i] <= 0 || this.prob[i] > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    postData(): void {
        if (this.validation()) {
            this.invalidCount = 0;
            const formdata: FormData = new FormData();
            formdata.append('motifSize', this.mSControl.value);
            formdata.append('randSize', this.rSControl.value);
            formdata.append('file', this.formDoc.get('reFile').value.files[0]);
            this.currentFileUpload = this.formDoc.get('reFile').value.files[0];
            this.results += 'Uploading files...\n';
            this.apiService.submitNetworkMotif(formdata).subscribe(
                res => {
                    if (res.type === HttpEventType.UploadProgress) {
                        this.progress.percentage = Math.round(100 * res.loaded / res.total);
                    } else if (res instanceof HttpResponse) {
                        this.results += 'File is completely uploaded!\n';
                        this.response = JSON.parse(res.body.toString());
                        this.results += this.response.message;
                    } else {
                        console.log(res.type);
                    }
                },
                err => {
                    alert('An error occurred while saving the note ' + err);
                }
            );
        } else {
            this.invalidCount += 1;
            if (this.invalidCount >= 3) {
                alert('Please enter all parameters');
            }
            return;
        }
    }

    selFile() {
        this.progress.percentage = 0;
    }
}

