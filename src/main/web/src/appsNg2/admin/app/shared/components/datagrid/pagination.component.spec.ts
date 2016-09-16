import { PaginationComponent } from './pagination.component';
import { TestBed, async, ComponentFixture} from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';

export function main() {

    let comp: PaginationComponent;
    let fixture: ComponentFixture<PaginationComponent>;
    let el: DebugElement;

    describe('Pagination component Test', () => {

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                declarations: [PaginationComponent],
            })
                .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(PaginationComponent);
            comp = fixture.componentInstance;
            el = fixture.debugElement;
        });

        it('should be on first page', function() {
            expect(comp.currentPageNumber).toBe(1);
        });

    });

}
