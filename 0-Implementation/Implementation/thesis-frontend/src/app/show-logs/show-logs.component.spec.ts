import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowLogsComponent } from './show-logs.component';

describe('ShowLogsComponent', () => {
  let component: ShowLogsComponent;
  let fixture: ComponentFixture<ShowLogsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowLogsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowLogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
