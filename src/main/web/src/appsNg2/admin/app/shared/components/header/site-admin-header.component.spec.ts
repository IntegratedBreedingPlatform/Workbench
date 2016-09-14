/// <reference path="./../../../../../../../typings/globals/jasmine/index.d.ts" />

import { SiteAdminHeader } from './site-admin-header.component';
import { TestBed , async , ComponentFixture} from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';

export function main() {

    let comp:    SiteAdminHeader;
    let fixture: ComponentFixture<SiteAdminHeader>;
    let el:      DebugElement;

    describe('Site Admin Header Test', () => {

      beforeEach( async(() => {
        TestBed.configureTestingModule({
          declarations: [SiteAdminHeader],
        })
        .compileComponents();
      }));

      beforeEach(() => {
         fixture = TestBed.createComponent(SiteAdminHeader);
         comp    = fixture.componentInstance;
         el  = fixture.debugElement.query(By.css('.om-title'));
       });

      it('should load Admin Header', function() {
         expect(el.nativeElement.textContent).toContain('Site Administration');
      });

    });

}
