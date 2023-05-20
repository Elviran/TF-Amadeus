import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from '../_services/alert.service';
import { AuthenticationService } from '../_services/authentication.service';
import { Machine } from '../_models/machine';
import { Cluster } from '../_models/cluster';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  fileForm: FormGroup;
  returnUrl: string;
  machine:Machine;

  loading = false;
  submitted = false;
  startJob = false;
  
  cluster1:Machine[] = [
      {title: "ps0", log_dir:"../ps0/dir", tensorboard_url:"http://10.132.0.15:6006", docker_url:"http://172.0.0.1"},
      {title: "chief", log_dir:"../chief/dir", tensorboard_url:"http://10.132.0.15:6006", docker_url:"http://172.0.0.1"},
      {title: "eval", log_dir:"../eval/dir", tensorboard_url:"http://10.132.0.15:6006", docker_url:"http://172.0.0.1"},
      {title: "w0", log_dir:"../w0/dir", tensorboard_url:"http://10.132.0.15:6006", docker_url:"http://172.0.0.1"},
      {title: "w1", log_dir:"../w1/dir", tensorboard_url:"http://10.132.0.15:6006", docker_url:"http://172.0.0.1"}
  ];

  cluster2:Machine[] = [
    {title: "ps0", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "chief", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "eval", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "w0", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "w1", log_dir:"", tensorboard_url:"", docker_url:""}
  ];

  cluster3:Machine[] = [
    {title: "ps0", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "chief", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "eval", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "w0", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "w1", log_dir:"", tensorboard_url:"", docker_url:""}
  ];

  cluster4:Machine[] = [
    {title: "ps0", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "chief", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "eval", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "w0", log_dir:"", tensorboard_url:"", docker_url:""},
    {title: "w1", log_dir:"", tensorboard_url:"", docker_url:""}
  ];

  public clusters: Cluster[] = [
    {title:"Cluster 1", machines: this.cluster1},
    {title:"Cluster 2", machines: this.cluster2},
    {title:"Cluster 3", machines: this.cluster3},
    {title:"Cluster 4", machines: this.cluster4},
  ]

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private alertService: AlertService
  ) {
      // // redirect to home if already logged in
      // if (!this.authenticationService.currentUserValue) {
      //     this.router.navigate(['/']);
      // }
  }

  ngOnInit() {
    this.fileForm = this.formBuilder.group({
        file: ['', Validators.required]
    });

    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onSubmit(){
    return null;
  }

  setMachine(machine:Machine){
    this.machine = machine;
  }



}
