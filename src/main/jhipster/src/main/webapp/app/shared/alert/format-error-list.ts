export function formatErrorList(errors: any[]) {
    if (!errors || !errors.length) {
        return '';
    }
    if (errors.length === 1) {
        return errors[0].message || errors[0];
    }
    return '<div class="alert-overflow"><ul>' + errors.reduce((prev, error) => prev += `<li>${error.message || error}</li>`, '') + '</ul></div>';
}
