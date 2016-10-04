/// <reference path="./../../../../../../typings/globals/jasmine/index.d.ts" />

import { MailService } from './mail.service';
import { By } from '@angular/platform-browser';
import { DebugElement, provide }    from '@angular/core';
import { addProviders, inject, async, TestBed, ComponentFixture } from "@angular/core/testing";
import { Response, XHRBackend, ResponseOptions, HTTP_PROVIDERS, Headers, Http} from "@angular/http";
import { MockConnection, MockBackend } from "@angular/http/testing";
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import { User } from './../models/user.model';

export function main()
{

    describe( 'Mail Service Test', () =>
    {

        let header: Headers;

        beforeEach(() =>
        {
            addProviders( [HTTP_PROVIDERS,
                provide( XHRBackend, { useClass: MockBackend }), MailService] );
            header = new Headers();
            header.append( 'Accept', 'application/json' );
            header.append( 'X-Auth-Token', 'token' )
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

                let user = new User("0", "first", "last", "username", "role", "email", "status");
                service.send(user).subscribe(
                  (data) => {
                      expect (data._body.success).toBe(true);
                  }
              );

            })
        );

    });
}
