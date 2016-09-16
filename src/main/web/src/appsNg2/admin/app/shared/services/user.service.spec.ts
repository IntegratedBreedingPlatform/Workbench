/// <reference path="./../../../../../../typings/globals/jasmine/index.d.ts" />

import { UserService } from './user.service';
import { By } from '@angular/platform-browser';
import { DebugElement, provide }    from '@angular/core';
import { addProviders, inject, async, TestBed , ComponentFixture } from "@angular/core/testing";
import { Response, XHRBackend, ResponseOptions, HTTP_PROVIDERS , Headers, Http} from "@angular/http";
import { MockConnection, MockBackend } from "@angular/http/testing";
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';


export function main() {


    describe('User Service Test', () => {

      let header: Headers;

      beforeEach(() => {
        addProviders([HTTP_PROVIDERS,
                      provide(XHRBackend, {useClass: MockBackend}), UserService]);

        header = new Headers();
        header.append('Accept', 'application/json');
        header.append('X-Auth-Token', 'token')

      });

      it('Should get an User',
          inject([XHRBackend, UserService], (backend, service) => {
              backend.connections.subscribe(
                  (connection:MockConnection) => {
                      var options = new ResponseOptions({
                          body:
                              {
                                "userId": 1,
                                "username": "username",
                                "firstName": "first",
                                "lastName": "last",
                                "role": "role",
                                "status": "true",
                                "email": "test-ng2@leafnode.io"
                              }

                      });

                      var response = new Response(options);

                      connection.mockRespond(response);
                  }
              );

              spyOn(service, 'getHeaders').and.returnValue(header);

              service.get(1).subscribe(
                  (user) => {
                      expect(user.username).toBe('username');
                      expect(user.id).toBe(1);
                      expect(user.firstName).toBe('first');
                      expect(user.lastName).toBe('last');
                      expect(user.role).toBe('role');
                      expect(user.status).toBe('true');
                      expect(user.email).toBe('test-ng2@leafnode.io');
                  }
              );

          })
      );

    });

}
