import { AfterViewInit, Component, ElementRef, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap, debounceTime, takeUntil, repeat } from 'rxjs/operators';
import { StudyInfo } from './study-info';
import { empty, fromEvent, Subject, of } from 'rxjs';

@Component({
    selector: 'jhi-my-studies',
    templateUrl: './my-studies.component.html'
})
export class MyStudiesComponent {
    programUUID: string;
    mouseEnter = new Subject();
    mouseLeave = new Subject();

    @ViewChildren('tr')
    private rows: QueryList<ElementRef>;

    // TODO get from server
    studies: StudyInfo[] = [{
        selected: true,
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: {
                env1: {
                    count: 2,
                    countPending: 5
                }
            }
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: {
                env1: {
                    count: 2,
                    countPending: 5
                }
            }
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: {
                env1: {
                    count: 2,
                    countPending: 5
                }
            }
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: {
                env1: {
                    count: 2,
                    countPending: 5
                }
            }
        }
    }, {
        name: 'study 1',
        type: 'study type 1',
        date: '2019',
        folder: 'folder 1',
        metadata: {
            observations: {
                env1: {
                    count: 2,
                    countPending: 5
                }
            }
        }
    }];

    constructor(
        private route: ActivatedRoute
    ) {
        this.route.queryParams.subscribe(((params) => this.programUUID = params['programUUID']));

        this.mouseEnter.pipe(
            debounceTime(300),
            takeUntil(this.mouseLeave),
            repeat()
        ).subscribe((study: StudyInfo) => {
            this.select(study);
        })

        this.load();
    }

    load() {
        // TODO get from server
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
    }
}
