import { Component, OnInit, Input, Output, OnChanges, EventEmitter, trigger, state, style, animate, transition } from '@angular/core';

@Component({
  selector: 'error-notification',
  templateUrl: 'error-notification.component.html',
  styleUrls: ['error-notification.component.css'],
  moduleId: module.id,
  animations: [
    trigger('errorNotificationTrigger', [
      state('in', style({ opacity: 1, transform: 'translateX(0)' })),
      transition('void => *', [
        style({
          opacity: 0,
          transform: 'translateY(50px)'
        }),
        animate('0.3s ease-in')
      ]),
      transition('* => void', [
        animate('0.3s 10 ease-out', style({
          opacity: 0,
          transform: 'translateY(-50px)'
        }))
      ])
    ])
  ]
})
export class ErrorNotification implements OnInit {
  @Input() closable = true;
  @Input() visible: boolean;
  @Input() title: string;
  @Input() classes: string = "error-notify";
  @Output() visibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit() { }

  close() {
    this.visible = false;
    this.visibleChange.emit(this.visible);
  }
}
