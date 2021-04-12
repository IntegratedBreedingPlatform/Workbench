import { AfterViewInit, Component, ElementRef, isDevMode, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap, debounceTime, takeUntil, repeat } from 'rxjs/operators';
import { StudyInfo } from './study-info';
import { empty, fromEvent, Subject, of } from 'rxjs';

@Component({
    selector: 'jhi-my-studies',
    templateUrl: './my-studies.component.html'
})
export class MyStudiesComponent  {
    programUUID: string;
    mouseEnter = new Subject();
    mouseLeave = new Subject();

    // ngx-charts options

    /*
     * FIXME dev mode issues (not happening in prod)
     *  - responsive (view=undefined) (use default->600,400 as workaround)
     *   https://github.com/swimlane/ngx-charts/issues/374
     *  - ERROR TypeError: Cannot read property 'runOutsideAngular' of undefined
     *  - tooltip icons not rendering correctly
     *  - PLEASE NOTE in dev mode the chart will overflow in small screens
     */
    view: any[] = isDevMode() ? [600, 400] : undefined;
    showXAxis = true;
    showYAxis = true;
    gradient = false;
    showLegend = true;
    showXAxisLabel = true;
    xAxisLabel = 'environment';
    showYAxisLabel = true;
    yAxisLabel = 'count';
    animations = true;
    colorScheme = {
        domain: ['#f7912f', '#f7b62f', '#ffffff']
    };

    // TODO get from server
    studies: StudyInfo[] = [{
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: data
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: data2
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: data
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: data2
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: data
        }
    }];
    study: StudyInfo;

    constructor(
        private route: ActivatedRoute
    ) {
        this.route.queryParams.subscribe(((params) => this.programUUID = params['programUUID']));

        this.mouseEnter.pipe(
            debounceTime(100),
            takeUntil(this.mouseLeave),
            repeat()
        ).subscribe((study: StudyInfo) => {
            this.select(study);
        })

        this.load();
    }

    load() {
        // TODO get from server

        this.select(this.studies[0]);
    }

    onMouseEnter(study) {
        this.mouseEnter.next(study);
    }

    onMouseLeave() {
        this.mouseLeave.next();
    }

    select(study: StudyInfo) {
        this.studies.forEach((s) => s.selected = false);
        study.selected = true;
        this.study = study;
    }
}

const data = [
    {
        'name': 'Agua fria',
        'series': [
            {
                'name': 'confirmed',
                'value': 300
            },
            {
                'name': 'pending',
                'value': 300
            },
            {
                'name': 'unobserved',
                'value': 300
            },
        ]
    },
    {
        'name': 'El batan',
        'series': [
            {
                'name': 'confirmed',
                'value': 300
            },
            {
                'name': 'pending',
                'value': 150
            },
            {
                'name': 'unobserved',
                'value': 50
            },
        ]
    },
    {
        'name': 'Obregon',
        'series': [
            {
                'name': 'confirmed',
                'value': 50
            },
            {
                'name': 'pending',
                'value': 200
            },
            {
                'name': 'unobserved',
                'value': 350
            },
        ]
    },
];

const data2 = [
    {
        'name': 'Agua fria',
        'series': [
            {
                'name': 'confirmed',
                'value': 300
            },
            {
                'name': 'pending',
                'value': 100
            },
            {
                'name': 'unobserved',
                'value': 200
            },
        ]
    },
    {
        'name': 'El batan',
        'series': [
            {
                'name': 'confirmed',
                'value': 300
            },
            {
                'name': 'pending',
                'value': 250
            },
            {
                'name': 'unobserved',
                'value': 250
            },
        ]
    },
    {
        'name': 'Obregon',
        'series': [
            {
                'name': 'confirmed',
                'value': 450
            },
            {
                'name': 'pending',
                'value': 50
            },
            {
                'name': 'unobserved',
                'value': 100
            },
        ]
    },
];
