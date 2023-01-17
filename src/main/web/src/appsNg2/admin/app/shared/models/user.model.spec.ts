import { User } from './user.model';
import 'rxjs/add/operator/map';

export function main() {

    describe('User Model Test', () => {

      function createUser() {
          return new User('0', 'first', 'last', 'username', [], [], 'email', true, true);
      }

        it('should be created by constructor', () => {
        let user = createUser();
        expect(user.id).toBe("0");
        expect(user.firstName).toBe("first");
        expect(user.lastName).toBe("last");
        expect(user.username).toBe("username");
        expect(user.email).toBe("email");
        expect(user.active).toBe(true);
      });

    });

  }
