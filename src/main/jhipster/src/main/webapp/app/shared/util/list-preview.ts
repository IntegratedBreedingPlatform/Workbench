/**
 * Similar to server-side Util.buildErrorMessageFromList
 * @param list
 */
export function listPreview(list: (string | number)[], numberOfItemsToShow = 3) {
    if (!list || !list.length) {
        return '';
    }
    let preview = list.slice(0, numberOfItemsToShow).join(', ');
    if (list.length > numberOfItemsToShow) {
        preview += ' and ' + (list.length - numberOfItemsToShow) + ' more';
    }
    return preview;
}
