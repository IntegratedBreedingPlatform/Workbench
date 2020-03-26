import { animate, state, style, transition, trigger } from '@angular/animations';

export const ModalAnimation = [
    trigger('ModalAnimation', [
        state('in', style({ opacity: 1, transform: 'translateX(0)' })),
        transition('void => *', [
            style({
                opacity: 0,
                transform: 'translateY(50px)'
            }),
            animate('0.3s ease-in')
        ]),
        transition('* => void', [
            animate('0.3s ease-out', style({
                opacity: 0,
                transform: 'translateY(-50px)'
            }))
        ])
    ])
];
