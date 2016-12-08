import { inject, async, TestBed , ComponentFixture } from "@angular/core/testing";
import { Role } from './role.model';
import 'rxjs/add/operator/map';

export function main() {

    describe('Role Model Test', () => {

      function createRole() {
        return new Role("0", "description");
      }

      it('should be createable by constructor', () => {
        let role = createRole();
        expect(role.id).toBe("0");
        expect(role.description).toBe("description");
      });

    });

  }