import { AfterViewInit, Component, ElementRef, isDevMode, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap, debounceTime, takeUntil, repeat, map, finalize } from 'rxjs/operators';
import { MyStudy, MyStudyMetadata, NgChartsBarPlotMetadata, ObservationsMetadata } from './my-study';
import { empty, fromEvent, Subject, of } from 'rxjs';
import { MyStudiesService } from './my-studies.service';
import { Pageable } from '../../shared/model/pageable';
import { UrlService } from '../../shared/service/url.service';
import { ProgramContext } from './program.context';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-my-studies',
    templateUrl: './my-studies.component.html'
})
export class MyStudiesComponent {

    readonly MAX_ENVIRONMENTS_TO_SHOW = 10;

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

    studies: MyStudy[];
    study: MyStudy;

    page = 1;
    pageSize = 5;
    predicate = 'startDate';
    reverse = false;
    totalCount: any;
    isLoading = false;

    constructor(
        private route: ActivatedRoute,
        private myStudiesService: MyStudiesService,
        public urlService: UrlService,
        public context: ProgramContext,
        private translateService: TranslateService
    ) {
        this.route.queryParams.subscribe((params) => {
            // params['programUUID'] just to listen for changes
            this.load();
        });

        this.mouseEnter.pipe(
            debounceTime(100),
            takeUntil(this.mouseLeave),
            repeat()
        ).subscribe((study: MyStudy) => {
            this.select(study);
        })
    }

    load() {
        this.isLoading = true;
        this.myStudiesService.getMyStudies(
            <Pageable>({
                page: this.page - 1,
                size: this.pageSize,
                sort: this.getSort()
            }),
            this.context.program.crop,
            this.context.program.uniqueID
        ).pipe(map((resp) => {
            this.totalCount = resp.headers.get('X-Total-Count')
            return resp.body.map((study) => <MyStudy>({
                studyId: study.studyId,
                name: study.name,
                type: study.type,
                date: study.date,
                folder: study.folder,
                metadata: this.transformMetadata(study)
            }));
        })).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe((studies) => {
            this.studies = studies;
            this.select(this.studies[0]);
        });
    }

    sort() {
        this.page = 1;
        this.load();
    }

    getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    }

    private transformMetadata(study: MyStudy) {
        if (!(study.metadata && study.metadata.observations)) {
            return {};
        }
        const aggregatedDatasetsByEnvName: { [key: string]: ObservationsMetadata } =
            study.metadata.observations.reduce((prev: { [key: string]: ObservationsMetadata }, curr: ObservationsMetadata) => {
                const prevVal = prev[curr.instanceName];
                if (!prevVal) {
                    prev[curr.instanceName] = Object.assign({}, curr);
                } else {
                    prevVal.confirmedCount += curr.confirmedCount;
                    prevVal.pendingCount += curr.pendingCount;
                    prevVal.unobservedCount += curr.unobservedCount;
                }
                return prev;
            }, {});
        const hasMoreEnvironments = Object.values(aggregatedDatasetsByEnvName).length > this.MAX_ENVIRONMENTS_TO_SHOW;
        const observationSeries = Object.values(aggregatedDatasetsByEnvName)
            .slice(0, this.MAX_ENVIRONMENTS_TO_SHOW)
            .map((obs: ObservationsMetadata) => {
                return <NgChartsBarPlotMetadata>({
                    name: obs.instanceName,
                    series: [{
                        name: 'confirmed',
                        value: obs.confirmedCount
                    }, {
                        name: 'pending',
                        value: obs.pendingCount
                    }, {
                        name: 'unobserved',
                        value: obs.unobservedCount
                    }]
                });
            });
        return <MyStudyMetadata>({
            observations: observationSeries,
            hasMoreEnvironments
        });
    }

    onMouseEnter(study) {
        this.mouseEnter.next(study);
    }

    onMouseLeave() {
        this.mouseLeave.next();
    }

    select(study: MyStudy) {
        if (study) {
            this.studies.forEach((s) => s.selected = false);
            study.selected = true;
            this.study = study;
        }
    }
}
