/// <reference path="./../../../../../../typings/globals/jasmine/index.d.ts" />

import { UserService } from './user.service';
import { inject, TestBed } from '@angular/core/testing';
import { BaseRequestOptions, Headers, Http, Response, ResponseOptions, XHRBackend } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import { User } from './../models/user.model'
import 'rxjs/add/operator/map';

export function main() {

    describe('User Service Test', () => {

      let header: Headers;

      beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [{ provide: XHRBackend, useClass: MockBackend }, UserService,
                {
                    provide: Http, useFactory: (backend, options) => {
                        return new Http(backend, options);
                    },
                    deps: [MockBackend, BaseRequestOptions]
                },
                MockBackend,
                BaseRequestOptions
            ]
        });
        header = new Headers();
        header.append('Accept', 'application/json');
        header.append('Authorization', 'Bearer ' + 'token');
      });

      it('Should get an User',
          inject([XHRBackend, UserService], (backend, service) => {
              backend.connections.subscribe(
                  (connection:MockConnection) => {
                      var options = new ResponseOptions({
                          body:
                              {
                                "id": 1,
                                "username": "username",
                                "firstName": "first",
                                "lastName": "last",
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
                      expect(user.active).toBe(true);
                      expect(user.email).toBe('test-ng2@leafnode.io');
                  }
              );
          })
      );

      it('Should get a list of user',
          inject([XHRBackend, UserService], (backend, service) => {
              backend.connections.subscribe(
                  (connection:MockConnection) => {
                      var options = new ResponseOptions({
                          body: [
                              {
                                "id": 1,
                                "username": "username",
                                "firstName": "first",
                                "lastName": "last",
                                "active": true,
                                "email": "test-ng2@leafnode.io"
                              },
                              {
                                "id": 2,
                                "username": "username2",
                                "firstName": "first2",
                                "lastName": "last2",
                                "active": false,
                                "email": "test-ng2-2@leafnode.io"
                              }
                          ]
                      });
                      var response = new Response(options);
                      connection.mockRespond(response);
                  }
              );

              spyOn(service, 'getHeaders').and.returnValue(header);

              service.getAll().subscribe(
                  (users) => {
                      expect(users[0].username).toBe('username');
                      expect(users[0].id).toBe(1);
                      expect(users[0].firstName).toBe('first');
                      expect(users[0].lastName).toBe('last');
                      expect(users[0].active).toBe(true);
                      expect(users[0].email).toBe('test-ng2@leafnode.io');
                      expect(users[1].username).toBe('username2');
                      expect(users[1].id).toBe(2);
                      expect(users[1].firstName).toBe('first2');
                      expect(users[1].lastName).toBe('last2');
                      expect(users[1].active).toBe(false);
                      expect(users[1].email).toBe('test-ng2-2@leafnode.io');
                  }
              );

          })
      );

      it('Should save an user',
          inject([XHRBackend, UserService], (backend, service) => {
              backend.connections.subscribe(
                  (connection:MockConnection) => {
                      var options = new ResponseOptions({
                          body: {
                            "id" : 1
                          }
                      });
                      var response = new Response(options);
                      connection.mockRespond(response);
                  }
              );

              spyOn(service, 'getHeaders').and.returnValue(header);
              let user = new User('0', 'first', 'last', 'username', [], [], 'email', true, true);

              service.save(user).subscribe(
                  (data) => {
                      expect (data._body.id).not.toBe(0);
                  }
              );
          })
      );

      it('Should update an user',
          inject([XHRBackend, UserService], (backend, service) => {
              backend.connections.subscribe(
                  (connection:MockConnection) => {
                      var options = new ResponseOptions({
                          body: {
                            "id" : 1
                          }
                      });
                      var response = new Response(options);
                      connection.mockRespond(response);
                  }
              );

              spyOn(service, 'getHeaders').and.returnValue(header);
              let user = new User('0', 'first', 'last', 'username', [], [], 'email', true, true);

              service.update(user).subscribe(
                  (data) => {
                      expect (data._body.id).toBe(1);
                  }
              );
          })
      );

    });
}
