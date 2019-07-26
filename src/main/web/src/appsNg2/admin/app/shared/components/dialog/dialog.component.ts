import { Component, OnInit, Input, Output, OnChanges, EventEmitter, trigger, state, style, animate, transition } from '@angular/core';

/*
 * TODO Migrate to ng-bootstrap when animations are done
 *  https://github.com/ng-bootstrap/ng-bootstrap/issues/295
 *  This custom solution has problems like popup scrolling
 */
@Component({
  selector: 'app-dialog',
  templateUrl: 'dialog.component.html',
  styleUrls: ['dialog.component.css'],
  moduleId: module.id,
  animations: [
    trigger('dialog', [
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
export class Dialog implements OnInit {
  @Input() closable = true;
  @Input() visible: boolean;
  @Input() title: string;
  @Output() visibleChange: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() onClose = new EventEmitter();

  constructor() { }

  ngOnInit() { }

  close() {
    this.onClose.emit();
  }
}