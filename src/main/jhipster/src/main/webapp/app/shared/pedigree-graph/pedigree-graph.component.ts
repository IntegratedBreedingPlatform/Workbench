import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { Graphviz, graphviz } from 'd3-graphviz';
import { GermplasmTreeNode } from '../germplasm/model/germplasm-tree-node.model';
import { GermplasmPedigreeService } from '../germplasm/service/germplasm.pedigree.service';
import { Subject } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import * as d3 from 'd3';
import { GermplasmDetailsUrlService } from '../germplasm/service/germplasm-details.url.service';

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

    constructor(public germplasmPedigreeService: GermplasmPedigreeService,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private alertService: JhiAlertService) {
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
        const nodes = d3.selectAll('.node');
        // click and mousedown on nodes
        nodes.on('click', (datum, i, group) => {
            this.stopPropagation();
            if (!this.isUnknownGermplasm(datum)) {
                const gid = datum.key;
                window.open(this.germplasmDetailsUrlService.getUrlAsString(gid), '_blank');
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
                    + germplasmTreeNode.gid + ((germplasmTreeNode.numberOfProgenitors === -1) ? ';\n' : ' [color=\"RED\", arrowhead=\"odottee\"];\n'));
                this.addNode(dot, germplasmTreeNode.femaleParentNode);
            }
            if (germplasmTreeNode.maleParentNode) {
                dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.maleParentNode) + '->'
                    + germplasmTreeNode.gid + ((germplasmTreeNode.numberOfProgenitors === -1) ? ';\n' : ' [color=\"BLUE\", arrowhead=\"veeodot\"];\n'));
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

    createNodeTextWithFormatting(dot: string[], germplasmTreeNode: GermplasmTreeNode) {

        const name: string[] = [];

        if (germplasmTreeNode.preferredName) {
            name.push(germplasmTreeNode.preferredName + '\n');
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
        dot.push(germplasmTreeNode.gid + ' [label=\"' + name.join('') + '\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];\n');

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

}
