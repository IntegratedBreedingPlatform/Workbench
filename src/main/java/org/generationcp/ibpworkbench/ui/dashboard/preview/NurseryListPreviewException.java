package org.generationcp.ibpworkbench.ui.dashboard.preview;

/**
 * Created by cyrus on 10/9/14.
 */
public class NurseryListPreviewException extends Exception {
    public static final String NO_SELECTION = "Please select a folder item";
    public static final String NOT_FOLDER = "Selected item is not a folder.";
    public static final String HAS_CHILDREN = "Folder has child items.";
    public static final String BLANK_NAME = "Folder name cannot be blank";
    public static final String INVALID_NAME = "Please choose a different name";

    public NurseryListPreviewException() {
        // empty constructor
    }

    public NurseryListPreviewException(String message) {
        super(message);
    }

    public NurseryListPreviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public NurseryListPreviewException(Throwable cause) {
        super(cause);
    }
}
