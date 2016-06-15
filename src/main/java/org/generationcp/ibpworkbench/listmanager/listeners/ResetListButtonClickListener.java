
package org.generationcp.ibpworkbench.listmanager.listeners;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.listmanager.ListBuilderComponent;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class ResetListButtonClickListener implements Button.ClickListener {

	private static final long serialVersionUID = -2641642996209640461L;

	private final ListBuilderComponent source;

	private final SimpleResourceBundleMessageSource messageSource;

	public ResetListButtonClickListener(ListBuilderComponent source, SimpleResourceBundleMessageSource messageSource) {
		this.source = source;
		this.messageSource = messageSource;
	}

	@Override
	public void buttonClick(ClickEvent event) {

		ConfirmDialog.show(this.source.getWindow(), "Reset List Builder",
				this.messageSource.getMessage(Message.CONFIRM_RESET_LIST_BUILDER_FIELDS), this.messageSource.getMessage(Message.OK),
				this.messageSource.getMessage(Message.CANCEL), new ConfirmDialog.Listener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							ResetListButtonClickListener.this.resetListBuilder();
						}
					}
				});
	}

	public void resetListBuilder() {
		this.source.resetList();
	}

}
