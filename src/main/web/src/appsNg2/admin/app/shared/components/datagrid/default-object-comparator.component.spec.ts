import { DebugElement }    from '@angular/core';
import { inject, async, TestBed , ComponentFixture } from "@angular/core/testing";
import { Observable } from 'rxjs/Rx';
import { DefaultObjectComparator } from './default-object-comparator.component'
import 'rxjs/add/operator/map';


export function main() {

  let comparator: DefaultObjectComparator;

  class GenericModelA {
      toCompareA: String;

      constructor (s: String) {
        this.toCompareA = s;
      }
  }

  class GenericModelB {
      toCompareA: String;

      constructor (s: String) {
        this.toCompareA = s;
      }
  }

  class GenericModelC  extends GenericModelA{
    toCompareC: String;

    constructor (s: String) {
      super(s);
    }
  }

  class GenericModelD{
    toCompareA: Object;

    constructor (s: Object) {
      this.toCompareA = s;
    }
  }

  class GenericModelE{
    toCompareA: Object;

    constructor (s: Object) {
      this.toCompareA = s;
    }
  }

  describe('Default Object Comparator Test', () => {

    beforeEach(() => {
      comparator = new DefaultObjectComparator();
    });

    it('same should return true', function() {
      let a: GenericModelA = new GenericModelA ('A');
      let b: GenericModelA = new GenericModelA ('A');
      let c: GenericModelB = new GenericModelB ('A');
      let obj: Object  = new GenericModelA ('A');
      let num: number = 1;

      let arr1 = [2,3];
      let arr2 = [2,3];
      expect(comparator.same(a,a)).toBe(true);
      expect(comparator.same(a,b)).toBe(true);
      expect(comparator.same(a,c)).toBe(true);
      expect(comparator.same(arr1,arr1)).toBe(true);
      expect(comparator.same(arr2,arr2)).toBe(true);
      expect(comparator.same(new GenericModelD(obj), new GenericModelE(obj))).toBe(true);
      expect(comparator.same(new GenericModelD(num), new GenericModelE(num))).toBe(true);
      expect(comparator.same(new GenericModelD(null), new GenericModelE('A'))).toBe(true);
      
    });

    it('same should return false', function() {
      let a: GenericModelA = new GenericModelA ('A');
      let b: GenericModelA = new GenericModelA ('B');
      let c: GenericModelB = new GenericModelB ('B');

      expect(comparator.same(a,b)).toBe(false);
      expect(comparator.same(a,c)).toBe(false);
      expect(comparator.same(1,2)).toBe(false);

    });

    it('equals should return true', function() {
      let a: GenericModelA = new GenericModelA ('A');
      let b: GenericModelA = new GenericModelA ('A');

      let arr1 = [2,3];
      let arr2 = [2,3];
      expect(comparator.equals(a,a)).toBe(true);
      expect(comparator.equals(a,b)).toBe(true);
      expect(comparator.equals(arr1,arr1)).toBe(true);
      expect(comparator.equals(arr2,arr2)).toBe(true);

    });

    it('equals should return false', function() {
      let a: GenericModelA = new GenericModelA ('A');
      let b: GenericModelA = new GenericModelA ('B');
      let c: GenericModelB = new GenericModelB ('A');
      let d: GenericModelC = new GenericModelC ('A');

      expect(comparator.equals(a,b)).toBe(false);
      expect(comparator.equals(a,c)).toBe(false);
      expect(comparator.equals(1,2)).toBe(false);
      expect(comparator.equals(d,a)).toBe(false);

    });


  });

}
