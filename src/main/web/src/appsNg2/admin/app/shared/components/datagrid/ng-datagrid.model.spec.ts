import { DebugElement, provide }    from '@angular/core';
import { addProviders, inject, async, TestBed , ComponentFixture } from "@angular/core/testing";
import { Observable } from 'rxjs/Rx';
import { NgDataGridModel } from './ng-datagrid.model'
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

      function createArrayOfGenericModel () {
          return [ new GenericModel('A'),
                   new GenericModel('B'),
                   new GenericModel('A')
                 ];
      }

      beforeEach(() => {
        items = createArrayOfGenericModel();
      });

      it ('Should create a NgDatagrid Model', function() {
        grid = new NgDataGridModel (items, 2);
        expect (grid.items[0].typeToSearch).toBe('A');
        expect (grid.items[1].typeToSearch).toBe('B');
        expect (grid.items[2].typeToSearch).toBe('A');
        expect (grid.pageSize).toBe(2);
      });

      it ('Should match the total rows', function() {
        grid = new NgDataGridModel (items, 2);
        expect (grid.totalRows).toBe(items.length);
      });

      it ('Should filter by typeToSearch equals to A', function () {
        grid = new NgDataGridModel (items, 2);
        grid.sortBy = 'typeToSearch';
        grid.searchValue = new GenericModel('A');
        expect (grid.itemsFiltered.length).toBe(2);
      });

      it ('Should get totalFilteredRows equals to 2', function () {
        grid = new NgDataGridModel (items, 2);
        grid.sortBy = 'typeToSearch';
        grid.searchValue = new GenericModel('A');
        expect (grid.totalFilteredRows).toBe(2);
      });


  });
}
