/**
 * Similar to server-side Util.buildErrorMessageFromList
 * @param list
 */
export function listPreview(list: (string | number)[]) {
    if (!list || !list.length) {
        return '';
    }
    let preview = list.slice(0, 3).join(', ');
    if (list.length > 3) {
        preview += ' and ' + (list.length - 3) + ' more';
    }
    return preview;
}
