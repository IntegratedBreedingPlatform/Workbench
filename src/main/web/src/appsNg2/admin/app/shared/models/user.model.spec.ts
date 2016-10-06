import { provide }    from '@angular/core';
import { addProviders, inject, async, TestBed , ComponentFixture } from "@angular/core/testing";
import { User } from './user.model'
import 'rxjs/add/operator/map';

export function main() {

    describe('User Model Test', () => {

      function createUser() {
        return new User("0", "first", "last", "username", "role", "email", "status");
      }

      it('should be createable by constructor', () => {
        let user = createUser();
        expect(user.id).toBe("0");
        expect(user.firstName).toBe("first");
        expect(user.lastName).toBe("last");
        expect(user.username).toBe("username");
        expect(user.role).toBe("role");
        expect(user.email).toBe("email");
        expect(user.status).toBe("status");
      });

    });

  }
