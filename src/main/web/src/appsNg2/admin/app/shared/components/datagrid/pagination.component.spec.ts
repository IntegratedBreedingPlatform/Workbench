import { PaginationComponent } from './pagination.component';
import { TestBed, async, ComponentFixture} from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';

export function main() {

    describe('Pagination component Test', () => {
        
        let pc = new PaginationComponent();

        beforeEach(() => {
            let pc = new PaginationComponent();
        });

        it('should be on first page', function() {
            expect(pc.currentPageNumber).toBe(1);
        });

        it('should be on first page if set on 0', function() {
            pc.setCurrentPage(0)
            expect(pc.currentPageNumber).toBe(1);
        });

        it('should be on first page if set on page bigger than maxPageIndex', function() {
            pc.maxPageIndex = 10;
            pc.setCurrentPage(11)
            expect(pc.currentPageNumber).toBe(1);
            pc.setCurrentPage(9)
            expect(pc.currentPageNumber).toBe(9);
        });

        it('should change page number', function() {
            pc.setCurrentPage(2)
            expect(pc.currentPageNumber).toBe(2);
        });

        it('should return range', function() {
            let range = pc.range(2, 9)
            expect(range).toEqual([2, 3, 4, 5, 6, 7, 8, 9]);
        });

        it('should show 3 pages before', function() {
            pc.maxPageIndex = 35;
            pc.setCurrentPage(31)
            expect(pc.pageStartNumber).toBe(28);
        });

        it('should show 3 pages after ', function() {
            pc.maxPageIndex = 35;
            pc.setCurrentPage(25)
            expect(pc.pageEndNumber).toBe(28);
            
            pc.maxPageIndex = 26;
            expect(pc.pageEndNumber).toBe(26);
        });

    });

}
