import { animate, Component, EventEmitter, Input, OnInit, Output, state, style, transition, trigger } from '@angular/core';

// TODO close non-error type after n seconds
@Component({
  selector: 'notification',
  templateUrl: 'notification.component.html',
  styleUrls: ['notification.component.css'],
  moduleId: module.id,
  animations: [
    trigger('notificationTrigger', [
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
export class NotificationComponent implements OnInit {

  @Input() closable = true;
  _visible: boolean;
  @Input() title: string;
  @Input() classes: string;
  @Output() visibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit() { }

  close() {
    this._visible = false;
    this.visibleChange.emit(this._visible);
  }

  // TODO FIXME for error notifications.
  @Input('visible') set visible(value: boolean) {
    this._visible = value;
    if (value) {
      setTimeout(() => {
        this.close();
      }, 3000);
    }
  }

  get visible(): boolean {
    return this._visible;
  }
}
