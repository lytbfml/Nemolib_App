import { Component, OnInit } from '@angular/core';
import {Inject} from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-example',
  templateUrl: './example.component.html',
  styleUrls: ['./example.component.css']
})
export class ExampleComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ExampleComponent>) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  

  ngOnInit() {
  }

}
