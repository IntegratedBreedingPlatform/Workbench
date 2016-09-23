import { DebugElement, provide }    from '@angular/core';
import { addProviders, inject, async, TestBed , ComponentFixture } from "@angular/core/testing";
import { Observable } from 'rxjs/Rx';
import { NgDataGridModel } from './ng-datagrid.model';
import { DefaultObjectComparator } from './default-object-comparator.component';
import 'rxjs/add/operator/map';


export function main() {

  class GenericModel {
      typeToSearch: String;

      constructor (s: String) {
        this.typeToSearch = s;
      }
  }

  describe('Ng Datagrid Model Test', () => {

      let items: GenericModel[];

      let grid : NgDataGridModel<GenericModel>;

      let gridComp : NgDataGridModel<GenericModel>;

      function createArrayOfGenericModel () {
          return [ new GenericModel('A'),
                   new GenericModel('B'),
                   new GenericModel('A')
                 ];
      }

      beforeEach(() => {
        items = createArrayOfGenericModel();
        grid = new NgDataGridModel (items, 2);
        gridComp = new NgDataGridModel (items, 2, new DefaultObjectComparator());
      });

      it ('Should create a NgDatagrid Model', function() {
        expect (grid.items[0].typeToSearch).toBe('A');
        expect (grid.items[1].typeToSearch).toBe('B');
        expect (grid.items[2].typeToSearch).toBe('A');
        expect (grid.pageSize).toBe(2);
        expect (gridComp.items[0].typeToSearch).toBe('A');
        expect (gridComp.items[1].typeToSearch).toBe('B');
        expect (gridComp.items[2].typeToSearch).toBe('A');
        expect (gridComp.pageSize).toBe(2);
      });

      it ('Should match the total rows', function() {
        expect (grid.totalRows).toBe(items.length);
      });

      it ('Should filter by typeToSearch equals to A', function () {
        grid.sortBy = 'typeToSearch';
        grid.searchValue = new GenericModel('A');
        expect (grid.itemsFiltered.length).toBe(2);
      });

      it ('Should get totalFilteredRows equals to 2', function () {
        grid.sortBy = 'typeToSearch';
        grid.searchValue = new GenericModel('A');
        expect (grid.totalFilteredRows).toBe(2);
      });

      it ('Should get startRow equals to 0', function () {
        grid.currentPageIndex = 0;
        expect (grid.startRow).toBe(0);
      });

      it ('Should get startRow equals to 2', function () {
        grid.currentPageIndex = 2;
        expect (grid.startRow).toBe(2);
      });

      it ('Should get maxPageIndex equals to 2', function () {
        expect (grid.maxPageIndex).toBe(2);
      });

      it ('Should get itemsOnCurrentPage.length equals to 2', function () {
        expect (grid.itemsOnCurrentPage.length).toBe(2);
      });

      it ('Should get itemsOnCurrentPage.length equals to 1', function () {
        grid.currentPageIndex = 2;
        expect (grid.itemsOnCurrentPage.length).toBe(1);
      });

      it ('Should get items.length equals to 3', function () {
        expect (grid.items.length).toBe(3);
      });

      it ('Should get items.length equals to 2', function () {
        grid.items = [ new GenericModel('A'),
                       new GenericModel('B')];
        expect (grid.items.length).toBe(2);
      });

      it ('Should get totalFilteredRows equals to 2', function () {
        gridComp = new NgDataGridModel (items, 2, new DefaultObjectComparator(), new GenericModel('A'));
        gridComp.sortBy = 'typeToSearch';
        let i = [ new GenericModel('Ana'),
                  new GenericModel('B'),
                  new GenericModel('Augusto'),
                  new GenericModel('Abel'),
                  new GenericModel('Adolfo')
               ];
        gridComp.items = i;
        gridComp.sortAsc = false;
        expect (gridComp.totalFilteredRows).toBe(4);
      });

  });
}
