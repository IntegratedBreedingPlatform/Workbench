/// <reference path="./../../../../../../typings/globals/jasmine/index.d.ts" />

import { RoleService } from './role.service';
import { By } from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';
import { inject, async, TestBed, ComponentFixture } from "@angular/core/testing";
import { Response, XHRBackend, ResponseOptions, Headers, Http, BaseRequestOptions } from "@angular/http";
import { MockConnection, MockBackend } from "@angular/http/testing";
import { Observable } from 'rxjs/Rx';
import { Role } from './../models/role.model'
import 'rxjs/add/operator/map';

export function main()
{

    describe( 'Role Service Test', () =>
    {

        let header: Headers;

        beforeEach(() =>
        {
            TestBed.configureTestingModule({
                providers: [{ provide: XHRBackend, useClass: MockBackend }, RoleService,
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

        it( 'Should get a list of role',
            inject( [XHRBackend, RoleService], ( backend, service ) =>
            {
                backend.connections.subscribe(
                    ( connection: MockConnection ) =>
                    {
                        var options = new ResponseOptions( {
                            body: [
                                {
                                    "id": 1,
                                    "name": "name"
                                },
                                {
                                    "id": 2,
                                    "name": "name1"
                                }
                            ]
                        });
                        var response = new Response( options );
                        connection.mockRespond( response );
                    }
                );

                spyOn( service, 'getHeaders' ).and.returnValue( header );

                service.getAll().subscribe(
                    ( roles ) =>
                    {
                        expect( roles[0].description ).toBe( 'description' );
                        expect( roles[0].id ).toBe( 1 );
                        expect( roles[1].description ).toBe( 'description2' );
                        expect( roles[1].id ).toBe( 2 );
                    }
                );

            })
        );

    });
}
