import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
    selector: '[appOnlyNumbers]'
})
export class OnlyNumbersDirective {
    constructor(private el: ElementRef) {
    }

    @Input() allowMultiLine: boolean = false;
    @Input() allowComma: boolean = false;
    @Input() maxLength: number = 0;
    regex: RegExp;

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
            this.regex = <RegExp>eval('/^[' + (this.allowComma ? ',' : '') + '0-9]+$/g');
        }
        var lines = this.allowMultiLine ? newValue.split('\n') : [newValue];
        for (let line of lines) {
            let lineText = line.replace('\r', '');
            if (this.maxLength && lineText.length > this.maxLength || !lineText.match(this.regex)) {
                event.preventDefault();
                return;
            }
        }
    }

}
