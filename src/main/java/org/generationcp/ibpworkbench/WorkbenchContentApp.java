package org.generationcp.ibpworkbench;

import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class WorkbenchContentApp extends SpringContextApplication {

	private static final long serialVersionUID = -3098125752010885259L;

	@Override
	public void close() {
		super.close();
	}

	@Override
	protected void initSpringApplication(final ConfigurableWebApplicationContext configurableWebApplicationContext) {
		this.setTheme("gcp-default");
		this.setMainWindow(new ContentWindow());
	}

}

