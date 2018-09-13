import { JhiAlertService } from 'ng-jhipster';

export function convertErrorResponse(resp, alertService: JhiAlertService) {
    if (!resp || !resp.error || !resp.error.errors.length) {
        alertService.error('error.generic');
    } else if (resp.error.errors.length === 1) {
        alertService.error('error.custom', { param: resp.error.errors[0].message });
    } else {
        alertService.error('error.custom', {
            param: '<ul>'
            + resp.error.errors.map((err) => '<li>' + err.message + '</li>')
            + '</ul>'
        });
    }
}
