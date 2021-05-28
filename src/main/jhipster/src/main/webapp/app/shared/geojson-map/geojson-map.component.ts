import { Component, Input, OnInit } from '@angular/core';
import { geoJSON, icon, marker, tileLayer } from 'leaflet';

@Component({
    selector: 'jhi-geojson-map',
    templateUrl: './geojson-map.component.html'
})
export class GeojsonMapComponent implements OnInit {

    options;
    @Input() geojson;

    ngOnInit(): void {
        this.options = {
            layers: [
                tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: '...' }),
                geoJSON(this.geojson),
                marker(geoJSON(this.geojson).getBounds().getCenter(), {
                    icon: icon({
                        iconSize: [25, 41],
                        iconAnchor: [13, 41],
                        iconUrl: '/ibpworkbench/main/app/content/scss/images/marker-icon.png',
                        shadowUrl: '/ibpworkbench/main/app/content/scss/images/marker-shadow.png'
                    })
                })
            ],
            zoom: 8,
            center: geoJSON(this.geojson).getBounds().getCenter()
        };

    }

}
