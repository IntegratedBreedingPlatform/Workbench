package org.generationcp.ibpworkbench;

/**
 * Interface for standardizing IBPWorkbench 
 * UI layout classes
 * 
 * @author Efficio
 *
 */
public interface IBPWorkbenchLayout {
	
	/**
	 * Instantiate UI elements for the layout
	 */
	void instantiateComponents();
	
	/**
	 * Initialize values and state of the UI elements
	 */
	void initializeValues();
	
	/**
	 * Add action listeners to UI elements
	 */
	void addListeners();
	
	/**
	 * Set styling and position UI elements in the layout
	 */
	void layoutComponents();
}
