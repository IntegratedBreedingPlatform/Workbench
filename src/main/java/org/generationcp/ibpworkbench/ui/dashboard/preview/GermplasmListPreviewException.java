package org.generationcp.ibpworkbench.ui.dashboard.preview;

/**
 * Created by cyrus on 10/10/14.
 */
public class GermplasmListPreviewException extends Exception {
    public static final String NOT_FOLDER = "Selected item is not a folder.";
    public static final String NO_PARENT = "Selected item is a root item, please choose another item on the list.";
    public static final String HAS_CHILDREN = "Folder has child items.";
    public static final String NO_SELECTION = "Please select a folder item";
    public static final String BLANK_NAME = "Folder name cannot be blank";
    public static final String INVALID_NAME = "Please choose a different name";
    public static final String NAME_NOT_UNIQUE = "Name is not unique";

    public GermplasmListPreviewException() {
    }

    public GermplasmListPreviewException(String message) {
        super(message);
    }

    public GermplasmListPreviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public GermplasmListPreviewException(Throwable cause) {
        super(cause);
    }
}
