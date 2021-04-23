import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
    selector: '[jhiApplyOnlyNumbers]'
})
export class OnlyNumbersDirective {

    @Input() allowMultiLine = false;
    @Input() allowComma = false;
    @Input() maxLength = 0;
    regex: RegExp;
    constructor(private el: ElementRef) {
    }

    @HostListener('keypress', ['$event'])
    onKeyPress(event: KeyboardEvent) {
        this.validate(event, event.key === 'Enter' ? '\n' : event.key);
    }

    @HostListener('paste', ['$event'])
    onPaste(event: Event) {
        const pastedText = (<any>window).clipboardData && (<any>window).clipboardData.getData('Text') // If IE, use window
            || <ClipboardEvent>event && (<ClipboardEvent>event).clipboardData.getData('text/plain'); // Non-IE browsers
        this.validate(event, pastedText);
    }

    @HostListener('cut', ['$event'])
    onCut(event: Event) {
        this.validate(event, '');
    }

    validate(event: Event, text: string) {
        const txtInput = this.el.nativeElement;
        const newValue = (txtInput.value.substring(0, txtInput.selectionStart)
            + text + txtInput.value.substring(txtInput.selectionEnd));
        if (!this.regex) {
            this.regex = new RegExp('^[' + (this.allowComma ? ',' : '') + '0-9]+$');
        }
        const lines = this.allowMultiLine ? newValue.split('\n') : [newValue];
        for (const line of lines) {
            const lineText = line.replace('\r', '');
            if (this.maxLength && lineText.length > this.maxLength || !this.regex.test(lineText)) {
                event.preventDefault();
                return;
            }
        }
    }

}
