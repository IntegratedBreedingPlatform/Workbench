package org.generationcp.ibpworkbench;

import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.print.DocFlavor;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(MockitoJUnitRunner.class)
public class ContentWindowTest {

	public static final String HTTP_EXAMPLE_COM = "http://example.com/";
	@Mock
	private OpenProgramLocationsAction openProgramLocationsAction;

	@Mock
	private OpenProgramMethodsAction openProgramMethodsAction;

	private ContentWindow contentWindow;

	@Before
	public void init() {

		this.contentWindow = new ContentWindow();
		this.contentWindow.setOpenProgramLocationsAction(openProgramLocationsAction);
		this.contentWindow.setOpenProgramMethodsAction(openProgramMethodsAction);

	}

	@Test
	public void testHandleURIProgramLocations() throws MalformedURLException {

		final URL url = new URL(HTTP_EXAMPLE_COM);

		this.contentWindow.handleURI(url, ContentWindow.PROGRAM_LOCATIONS);

		Mockito.verify(openProgramLocationsAction).doAction(this.contentWindow, "/" + ContentWindow.PROGRAM_LOCATIONS, false);

	}

	@Test
	public void testHandleURIProgramMethods() throws MalformedURLException {

		final URL url = new URL(HTTP_EXAMPLE_COM);

		this.contentWindow.handleURI(url, ContentWindow.PROGRAM_METHODS);

		Mockito.verify(openProgramMethodsAction).doAction(this.contentWindow, "/" + ContentWindow.PROGRAM_METHODS, false);

	}

}
