/// <reference path="./../../../../../../typings/globals/jasmine/index.d.ts" />

import { MailService } from './mail.service';
import { inject, TestBed } from '@angular/core/testing';
import { BaseRequestOptions, Headers, Http, Response, ResponseOptions, XHRBackend } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import 'rxjs/add/operator/map';
import { User } from './../models/user.model';
import { Role } from '../models/role.model';

export function main()
{

    describe( 'Mail Service Test', () =>
    {

        let header: Headers;

        beforeEach(() =>
        {
            TestBed.configureTestingModule({
                providers: [{ provide: XHRBackend, useClass: MockBackend }, MailService,
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
            header.append( 'Accept', 'application/json' );
            header.append( 'Authorization', 'Bearer ' + 'token' )
        });

        it( 'Should send an email',
            inject( [XHRBackend, MailService], ( backend, service ) =>
            {
                backend.connections.subscribe(
                    ( connection: MockConnection ) =>
                    {
                        var options = new ResponseOptions( {
                            body:
                                {
                                    success: true
                                }
                        });
                        var response = new Response( options );
                        connection.mockRespond( response );
                    }
                );

                spyOn( service, 'getHeaders' ).and.returnValue( header );

                let user = new User('0', 'first', 'last', 'username', [], new Role('1', 'role', 'instance'), [], 'email', 'status');
                service.send(user).subscribe(
                  (data) => {
                      expect (data._body.success).toBe(true);
                  }
              );

            })
        );

    });
}
