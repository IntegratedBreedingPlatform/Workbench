import { Component, Input, OnInit } from '@angular/core';
import { Graphviz, graphviz } from 'd3-graphviz';
import { GermplasmTreeNode } from '../germplasm/model/germplasm-tree-node.model';
import { GermplasmPedigreeService } from '../germplasm/service/germplasm.pedigree.service';
import { Subject } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

@Component({
    selector: 'jhi-pedigree-graph',
    templateUrl: './pedigree-graph.component.html',
    styleUrls: ['./pedigree-graph.component.css']
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
                private alertService: JhiAlertService) {
        this.levelChanged.debounceTime(500) // wait 500 milliseccond after the last event before emitting last event
            .distinctUntilChanged() // only emit if value is different from previous value
            .subscribe(model => {
                this.level = model;
                this.showGraph();
            });
    }

    ngOnInit(): void {
        this.showGraph();
    }

    levelFieldChanged(level: number) {
        if (level > 20) {
            this.alertService.error('pedigree.tree.max.level.error');
        } else {
            this.levelChanged.next(level);
        }
    }

    showGraph() {
        this.reset();
        if (this.gid && this.level > 0) {
            this.isLoading = true;
            this.germplasmPedigreeService.getGermplasmTree(this.gid, this.level, this.includeDerivativeLines).subscribe((gemplasmTreeNode) => {
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
                    .renderDot(this.createDot(gemplasmTreeNode));
                this.isLoading = false;
            });
        }

    }

    reset() {
        if (this.graphviz) {
            this.graphviz.resetZoom(null);
        }
    }

    createDot(germplasmTreeNode: GermplasmTreeNode) {

        const dot: string[] = [];
        dot.push('strict digraph G {');
        this.addNode(dot, germplasmTreeNode);
        dot.push('}');

        return dot.join('');
    }

    addNode(dot: string[], germplasmTreeNode: GermplasmTreeNode) {

        dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode) + ';');

        if (germplasmTreeNode.femaleParentNode) {
            dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.femaleParentNode) + '->' + germplasmTreeNode.gid + ((germplasmTreeNode.numberOfProgenitors === -1) ? ';' : ' [color=\"RED\", arrowhead=\"odottee\"];'));
            this.addNode(dot, germplasmTreeNode.femaleParentNode);
        }
        if (germplasmTreeNode.maleParentNode) {
            dot.push(this.createNodeTextWithFormatting(dot, germplasmTreeNode.maleParentNode) + '->' + germplasmTreeNode.gid + ((germplasmTreeNode.numberOfProgenitors === -1) ? ';' : ' [color=\"BLUE\", arrowhead=\"odottee\"];'));
            this.addNode(dot, germplasmTreeNode.maleParentNode);
        }
        if (germplasmTreeNode.otherProgenitors && germplasmTreeNode.otherProgenitors.length > 0) {
            germplasmTreeNode.otherProgenitors.forEach((otherProgenitorGermplasmTreeNode) => {
                dot.push(this.createNodeTextWithFormatting(dot, otherProgenitorGermplasmTreeNode) + '->' + germplasmTreeNode.gid + ' [color=\"BLUE\", arrowhead=\"odottee\"];');
                this.addNode(dot, otherProgenitorGermplasmTreeNode);
            });
        }

    }

    createNodeTextWithFormatting(dot: string[], germplasmTreeNode: GermplasmTreeNode) {

        const name: string[] = [];

        if (germplasmTreeNode.preferredName) {
            name.push(germplasmTreeNode.preferredName + '\n');
        }
        if (germplasmTreeNode.gid === 0) {
            dot.push(germplasmTreeNode.gid + ' [shape=box, style=dashed];');
        } else {
            name.push('GID: ' + germplasmTreeNode.gid);
            dot.push(`${germplasmTreeNode.gid} [shape=box];`);
            if (this.includeBreedingMethod && germplasmTreeNode.methodName && germplasmTreeNode.methodCode) {
                name.push(`\n\n${germplasmTreeNode.methodCode}: ${germplasmTreeNode.methodName}`);
            }
        }
        dot.push(germplasmTreeNode.gid + ' [label=\"' + name.join('') + '\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];');

        return germplasmTreeNode.gid;
    }

}