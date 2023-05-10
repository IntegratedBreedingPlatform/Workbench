import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { Graphviz, graphviz } from 'd3-graphviz';
import { GermplasmTreeNode } from '../germplasm/model/germplasm-tree-node.model';
import { GermplasmPedigreeService } from '../germplasm/service/germplasm.pedigree.service';
import { Subject } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import * as d3 from 'd3';
import { GermplasmDetailsUrlService } from '../germplasm/service/germplasm-details.url.service';
import {TranslateService} from "@ngx-translate/core";
import {VariableDetails} from "../ontology/model/variable-details";
import {VariableTypeEnum} from "../ontology/variable-type.enum";
import {DataTypeEnum} from "../ontology/data-type.enum";
import {NgbDate} from "@ng-bootstrap/ng-bootstrap";
import {GermplasmSearchRequest} from "../../entities/germplasm/germplasm-search-request.model";
import {Germplasm} from "../../entities/germplasm/germplasm.model";
import {GermplasmService} from "../germplasm/service/germplasm.service";
import {DateHelperService} from "../service/date.helper.service";
import {SearchComposite} from "../model/search-composite";
import {GermplasmManagerContext} from "../../germplasm-manager/germplasm-manager.context";
import {Router} from "@angular/router";

@Component({
    selector: 'jhi-pedigree-graph',
    templateUrl: './pedigree-graph.component.html',
    styleUrls: ['./pedigree-graph.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class PedigreeGraphComponent implements OnInit {

    @Input() gid: number;
    level = 3;
    levelChanged: Subject<number> = new Subject<number>();
    includeDerivativeLines = false;
    includeBreedingMethod = false;
    isLoading = false;
    graphviz: Graphviz<any, any, any, any>;
    selectedVariable: VariableDetails = null;
    VARIABLE_TYPE_IDS = [VariableTypeEnum.GERMPLASM_ATTRIBUTE, VariableTypeEnum.GERMPLASM_PASSPORT];
    DataType = DataTypeEnum;
    categoricalValue: string = null;
    characterValue: string = null;
    fromDate: NgbDate = null;
    toDate: NgbDate = null;
    minNumberValue: number = null;
    maxNumberValue: number = null;
    pedigreTreeGIDs: number[] = [];
    selectedGermplasmList: Germplasm[] = [];
    selectedGermplasmGids: number[] = [];
    nodesMap: {} =  {};


    MAX_NAME_DISPLAY_SIZE = 30;

    constructor(public germplasmPedigreeService: GermplasmPedigreeService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private alertService: JhiAlertService,
                public translateService: TranslateService,
                public germplasmService: GermplasmService,
                public dateHelperService: DateHelperService,
                private germplasmManagerContext: GermplasmManagerContext,
                private router: Router) {
        this.levelChanged.debounceTime(500) // wait 500 milliseccond after the last event before emitting last event
            .distinctUntilChanged() // only emit if value is different from previous value
            .subscribe((model) => {
                this.level = model;
                this.render();
            });
    }

    ngOnInit(): void {
        this.initializeGraph();
        this.render();
    }

    levelFieldChanged(level: number) {
        if (level > 20) {
            this.alertService.error('pedigree.tree.max.level.error');
        } else {
            this.levelChanged.next(level);
        }
    }

    render() {
        if (this.graphviz && this.gid && this.level > 0) {
            this.isLoading = true;
            this.germplasmPedigreeService.getGermplasmTree(this.gid, this.level, this.includeDerivativeLines).subscribe((gemplasmTreeNode) => {
                try {
                    this.graphviz
                        .renderDot(this.createDot(gemplasmTreeNode), () => {
                            this.initializeNodes();
                        });
                    this.isLoading = false;
                } catch (e) {
                    this.alertService.error('pedigree.tree.pedigree.graph.reached.maxixum.level.error');
                    this.isLoading = false;
                }
            });
        }
    }

    selectVariable(variable: VariableDetails) {
        this.selectedVariable = variable;
    }

    highlightFilteredNodes() {
        const request = this.createGermplasmSearchRequest();
        this.germplasmService.search(request).subscribe((searchRequestId) => {
            this.germplasmService.getAllGermplasm(searchRequestId).toPromise().then((filteredGermplasm) => {
                filteredGermplasm.forEach((filtered) => {
                    if (!this.selectedGermplasmGids.includes(filtered.gid)) {
                        this.selectedGermplasmGids.push(filtered.gid);
                        this.selectedGermplasmList.push(filtered);
                        const node = this.nodesMap[Number(filtered.gid)];
                        node.selectAll('polygon').attr('stroke', '#FCAE1E');
                        node.selectAll('polygon').attr('stroke-width', '3');
                    }
                });
            });
        });
    }

    createGermplasmSearchRequest(): GermplasmSearchRequest {
        const request = new GermplasmSearchRequest();
        request.gids = this.pedigreTreeGIDs;
        request.includeGroupMembers = false;
        request.addedColumnsPropertyIds = ["PREFERRED NAME"];
        if (this.selectedVariable.scale.dataType.name === this.DataType.NUMERIC) {
            request.attributeRangeMap = {};
            request.attributeRangeMap[this.selectedVariable.name] = {
                "fromValue": this.minNumberValue.toString(),
                "toValue": this.maxNumberValue.toString()
            };
        } else if (this.selectedVariable.scale.dataType.name === this.DataType.CHARACTER) {
            request.attributes = {};
            request.attributes[this.selectedVariable.name] = this.characterValue;
        } else if (this.selectedVariable.scale.dataType.name === this.DataType.CATEGORICAL) {
            request.attributes = {};
            request.attributes[this.selectedVariable.name] = this.categoricalValue;
        } else if (this.selectedVariable.scale.dataType.name === this.DataType.DATE) {
            request.attributeRangeMap = {};
            request.attributeRangeMap[this.selectedVariable.name] = {
                "fromValue": this.dateHelperService.convertNgbDateToString(this.fromDate),
                "toValue": this.dateHelperService.convertNgbDateToString(this.toDate)
            };
        }
        return request;

    }

    initializeGraph() {

        this.graphviz = graphviz('#pedigree-graph', {
            useWorker: false
        }).totalMemory(Math.pow(2, 27)) // Increase memory available to avoid OOM
            .fit(true)
            .zoom(true)
            .attributer((obj) => {
                if (obj.tag === 'svg') {
                    // Make sure the svg render fits the container
                    obj.attributes.height = '100%';
                    obj.attributes.width = '100%';
                }
            })
            .transition(d3.transition().delay(750).duration(1000).ease(d3.easeLinear));
    }

    initializeNodes() {
        // Create a local nodesMap variable since function inside each can't access the component's nodesMap variable
        const nodesMap = {};
        const nodes = d3.selectAll('.node');
        nodes.each(function (d, i) {
            // Access the current node
            const currentNode = d3.select(this);
            nodesMap[Number(d.key)] = currentNode;
        });
        this.nodesMap = nodesMap;

        // click and mousedown on nodes
        nodes.on('click', (datum, i, group) => {
            this.stopPropagation();
            if (!this.isUnknownGermplasm(datum)) {
                const node = group[i];
                const selection = d3.select(node);
                selection.selectAll('polygon').attr('stroke', '#FCAE1E');
                selection.selectAll('polygon').attr('stroke-width', '3');

                // Show options upon click
                /*const gid = datum.key;
                window.open(this.germplasmDetailsUrlService.getUrlAsString(gid), '_blank');*/
            }
        });
        nodes.on('mouseover', (datum, i, group) => {
            this.stopPropagation();
            if (!this.isUnknownGermplasm(datum)) {
                const node = group[i];
                const selection = d3.select(node);
                selection.selectAll('polygon').attr('fill', '#337ab7');
                selection.selectAll('text').attr('fill', 'white');
            }
        });
        nodes.on('mouseout', (datum, i, group) => {
            this.stopPropagation();
            if (!this.isUnknownGermplasm(datum)) {
                const node = group[i];
                const selection = d3.select(node);
                selection.selectAll('polygon').attr('fill', 'none');
                selection.selectAll('text').attr('fill', '#000000');
            }
        });
    }

    isUnknownGermplasm(datum): boolean {
        return datum.key === '0';
    }

    stopPropagation() {
        const event = d3.event;
        event.preventDefault();
        event.stopPropagation();
    }

    createDot(germplasmTreeNode: GermplasmTreeNode) {

        const dot: string[] = [];
        dot.push('strict digraph G {');
        this.addNode(dot, germplasmTreeNode);
        dot.push('}');

        return dot.join('');
    }

    addNode(dot: string[], germplasmTreeNode: GermplasmTreeNode) {
        this.pedigreTreeGIDs.push(germplasmTreeNode.gid);
        dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode) + ';\n');

        if (this.isUnknownImmediateSource(germplasmTreeNode)) {
            if (germplasmTreeNode.maleParentNode) {
                dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.maleParentNode) + '->'
                    + germplasmTreeNode.gid + ';\n');
                this.addNode(dot, germplasmTreeNode.maleParentNode);
            }
            if (germplasmTreeNode.femaleParentNode) {
                dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.femaleParentNode) + '->'
                    + germplasmTreeNode.maleParentNode.gid + ';\n');
                this.addNode(dot, germplasmTreeNode.femaleParentNode);
            }
        } else {
            if (germplasmTreeNode.femaleParentNode) {
                dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.femaleParentNode) + '->'
                    + germplasmTreeNode.gid + ((germplasmTreeNode.numberOfProgenitors === -1 && germplasmTreeNode.maleParentNode === null) ? ';\n' :
                        ' [color=\"RED\", arrowhead=\"odottee\"];\n'));
                this.addNode(dot, germplasmTreeNode.femaleParentNode);
            }
            if (germplasmTreeNode.maleParentNode) {
                dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.maleParentNode) + '->'
                    + germplasmTreeNode.gid + ((germplasmTreeNode.numberOfProgenitors === -1 && germplasmTreeNode.femaleParentNode === null) ? ';\n' :
                        ' [color=\"BLUE\", arrowhead=\"veeodot\"];\n'));
                this.addNode(dot, germplasmTreeNode.maleParentNode);
            }
            if (germplasmTreeNode.otherProgenitors && germplasmTreeNode.otherProgenitors.length > 0) {
                germplasmTreeNode.otherProgenitors.forEach((otherProgenitorGermplasmTreeNode) => {
                    dot.push(this.createNodeTextWithFormatting(dot, otherProgenitorGermplasmTreeNode) + '->' + germplasmTreeNode.gid
                        + ' [color=\"BLUE\", arrowhead=\"veeodot\"];\n');
                    this.addNode(dot, otherProgenitorGermplasmTreeNode);
                });
            }
        }

    }

    isUnknownImmediateSource(germplasmTreeNode) {
        return germplasmTreeNode.numberOfProgenitors === -1 &&
            germplasmTreeNode.femaleParentNode && germplasmTreeNode.femaleParentNode.gid !== 0 &&
            germplasmTreeNode.maleParentNode && germplasmTreeNode.maleParentNode.gid === 0;
    }

    removeSelectedItem(item: Germplasm) {
        const index = this.selectedGermplasmList.findIndex(germplasm => germplasm.germplasmUUID === item.germplasmUUID);
        if (index !== -1) {
            this.selectedGermplasmList.splice(index, 1);
            const gidIndex = this.selectedGermplasmGids.indexOf(item.gid);
            this.selectedGermplasmGids.splice(gidIndex, 1);
            const node = this.nodesMap[Number(item.gid)];
            node.selectAll('polygon').attr('stroke', '#000000');
            node.selectAll('polygon').attr('stroke-width', '1');
        }
    }

    createNodeTextWithFormatting(dot: string[], germplasmTreeNode: GermplasmTreeNode) {

        const name: string[] = [];

        if (germplasmTreeNode.preferredName) {
            const preferredName = germplasmTreeNode.preferredName.length > this.MAX_NAME_DISPLAY_SIZE
                ? germplasmTreeNode.preferredName.substring(0, this.MAX_NAME_DISPLAY_SIZE) + '...' : germplasmTreeNode.preferredName;
            name.push(preferredName + '\n');
        }
        if (germplasmTreeNode.gid === 0) {
            dot.push(germplasmTreeNode.gid + ' [shape=box, style=dashed];\n');
        } else {
            name.push('GID: ' + germplasmTreeNode.gid);
            dot.push(`${germplasmTreeNode.gid} [shape=box];\n`);
            if (this.includeBreedingMethod && germplasmTreeNode.methodName && germplasmTreeNode.methodCode) {
                name.push(`\n\n${germplasmTreeNode.methodCode}: ${germplasmTreeNode.methodName}`);
            }
        }
        dot.push(germplasmTreeNode.gid + ' [label=\"' + name.join('') + '\", tooltip=\"' + germplasmTreeNode.preferredName
            + '\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];\n');

        return germplasmTreeNode.gid;
    }

    downloadSvg() {
        // Reset zoom so that the downloaded file will show the whole graph
        this.graphviz.resetZoom();
        const svgData = document.getElementsByTagName('svg')[0].outerHTML;
        const preface = '<?xml version="1.0" standalone="no"?>\r\n';
        const svgBlob = new Blob([preface, svgData], { type: 'image/svg+xml;charset=utf-8' });
        const svgUrl = URL.createObjectURL(svgBlob);
        const downloadLink = document.createElement('a');
        downloadLink.href = svgUrl;
        downloadLink.download = `pedigree-graph-${this.gid}.svg`;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }

    openCreateList() {
        const searchComposite = new SearchComposite<GermplasmSearchRequest, number>();
        searchComposite.itemIds = this.selectedGermplasmGids;
        this.germplasmManagerContext.searchComposite = searchComposite;

        this.router.navigate(['/', { outlets: { popup: 'germplasm-list-creation-dialog' }, }], {
            replaceUrl: true,
            queryParamsHandling: 'merge'
        });
    }

    disableHighlightButton(): boolean {
        if (this.selectedVariable) {
            if (this.selectedVariable.scale.dataType.name === this.DataType.NUMERIC) {
                return this.minNumberValue === null && this.maxNumberValue === null;
            } else if (this.selectedVariable.scale.dataType.name === this.DataType.CHARACTER) {
                return this.characterValue === null || this.characterValue.trim().length === 0;
            } else if (this.selectedVariable.scale.dataType.name === this.DataType.CATEGORICAL) {
                return this.categoricalValue === null || this.categoricalValue.trim().length === 0;
            } else if (this.selectedVariable.scale.dataType.name === this.DataType.DATE) {
                return this.fromDate === null && this.toDate === null;
            }
        }
        return true;
    }

}
