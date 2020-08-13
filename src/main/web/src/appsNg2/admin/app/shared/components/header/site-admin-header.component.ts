import { Component } from '@angular/core';
import { HelpService } from '../../services/help.service';


@Component({
   selector: 'site-admin-header',
   templateUrl: './site-admin-header.component.html',
   moduleId: module.id
})

export class SiteAdminHeader {
   helpLink: string;
   constructor(helpService: HelpService) {
      if (!this.helpLink || !this.helpLink.length)
      helpService.getOnlinHelpLink().toPromise().then((response)=>{
        let body = response.text();
        console.log(body);
        if(body) {
           this.helpLink = body.data || body;
        }
        console.log(this.helpLink);
      }).catch((error)=>{
         console.log(error);
      });
   }
}
