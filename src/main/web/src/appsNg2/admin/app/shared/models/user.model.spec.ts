import { User } from './user.model';
import 'rxjs/add/operator/map';
import { Role } from './role.model';

export function main() {

    describe('User Model Test', () => {

      function createUser() {
        return new User("0", "first", "last", "username", [], new Role("1", "role"), "email", "status");
      }

      it('should be createable by constructor', () => {
        let user = createUser();
        expect(user.id).toBe("0");
        expect(user.firstName).toBe("first");
        expect(user.lastName).toBe("last");
        expect(user.username).toBe("username");
        expect(user.role.id).toBe("1");
        expect(user.email).toBe("email");
        expect(user.status).toBe("status");
      });

    });

  }
