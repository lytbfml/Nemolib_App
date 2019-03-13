import {Component} from '@angular/core';
import {FormControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material';

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
export class AppComponent {
    title = 'Nemolib-ng-app';
    public motifSize = 3;
    public randSize = 10;
    public probSel: number;
    public prob = [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5];
    private valid: boolean;
    public results = 'Results';
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
    file: any;

    postData(): void {
        this.valid = true;
        for (let i = 0; i < this.motifSize; i++) {
            if (this.prob[i] == null || this.prob[i] <= 0 || this.prob[i] > 1) {
                this.valid = false;
            }
        }
        if (this.probSel == null || this.motifSize == null || this.randSize == null) {
            this.valid = false;
        }
        if (this.valid) {
            this.results = this.motifSize + ' ,  ' + this.prob;
        } else{
            this.results = 'not valid';
        }
    }
}

