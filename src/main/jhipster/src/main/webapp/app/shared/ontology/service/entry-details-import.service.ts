import { Injectable } from '@angular/core';
import { ParamContext } from '../../service/param.context';
import { EntryDetailsImportContext } from '../entry-details-import.context';
import { toUpper } from '../../util/to-upper';
import { AlertService } from '../../alert/alert.service';
import { formatErrorList } from '../../alert/format-error-list';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class EntryDetailsImportService {

    constructor(
        private paramContext: ParamContext,
        private alertService: AlertService,
        private translateService: TranslateService,
        private context: EntryDetailsImportContext
    ) {
    }

    initializeVariableMatches() {
        const rows = [];

        this.context.newVariables.forEach((variable) => {
            const variableName = variable.alias ? variable.alias : variable.name;
            const row = {
                id: variable.id, name: variableName,
                description: variable.description,
                isAlreadyExisting: false
            };

            if (variable.alias) {
                this.context.variableMatchesResult[toUpper(variable.alias)] = variable.id;
            }
            this.context.variableMatchesResult[toUpper(variable.name)] = variable.id;
            rows.push(row);
        });

        this.context.variablesOfTheList.forEach((variable) => {
            const variableName = variable.alias ? variable.alias : variable.name;
            const row = {
                id: variable.id,
                name: variableName, description: variable.description,
                isAlreadyExisting: true
            };

            if (variable.alias) {
                this.context.variableMatchesResult[toUpper(variable.alias)] = variable.id;
            }
            this.context.variableMatchesResult[toUpper(variable.name)] = variable.id;
            rows.push(row);
        });

        this.context.unknownVariableNames.forEach((variableName) => {
            const row = {
                id: null,
                name: variableName,
                description: '',
                isAlreadyExisting: false
            };
            rows.push(row);
        });
        return rows;
    }

    normalizeHeaders(rawData: any[]) {
        if (!rawData || rawData.filter((row) => row.some((column) => Boolean(column.trim()))).length <= 1) {
            this.alertService.error('germplasm-list.import.file.validation.file.empty');
            return false;
        }

        // normalize headers
        rawData[0] = rawData[0].map((header) => header.toUpperCase());
        const headers = rawData[0];
        this.context.data = rawData.slice(1).map((fileRow, rowIndex) => {
            return fileRow.reduce((map, col, colIndex) => {
                map[headers[colIndex]] = col;
                return map;
            }, {});
        });

        return true;
    }

    validateFile(headers: string[], data: any[]) {
        const errorMessage: string[] = [];
        this.validateHeader(headers, errorMessage);
        this.validateData(errorMessage, data);

        if (errorMessage.length) {
            this.alertService.error('error.custom', { param: formatErrorList(errorMessage) });
            return false;
        }

        return true;
    }

    private validateHeader(fileHeader: string[], errorMessage: string[]) {
        Object.keys(fileHeader
            // get duplicates
            .filter((header, i, self) => self.indexOf(header) !== i)
            // Show duplicates only once
            .reduce((dupesMap, header) => {
                dupesMap[header] = true;
                return dupesMap;
            }, {})
        ).forEach((header) => {
            errorMessage.push(this.translateService.instant('error.import.header.duplicated', { param: header }));
        });
    }

    private validateData(errorMessage: string[], data: any[]) {
        // row validations
        for (const row of data) {
            if (!row[HEADERS.ENTRY_NO]) {
                errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.no'));
                break;
            }

            if (row[HEADERS.ENTRY_NO] && (isNaN(row[HEADERS.ENTRY_NO])
                || !Number.isInteger(Number(row[HEADERS.ENTRY_NO])) || row[HEADERS.ENTRY_NO] < 0)) {
                errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.no.format'));
                break;
            }
        }

        if (!errorMessage.length && data.map((row) => row[HEADERS.ENTRY_NO]).some((cell, i, col) => cell.length && col.indexOf(cell) !== i)) {
            errorMessage.push(this.translateService.instant('germplasm-list.import.file.validation.entry.no.duplicates'));
        }
    }

}

export enum HEADERS {
    'ENTRY_NO' = 'ENTRY_NO'
}
