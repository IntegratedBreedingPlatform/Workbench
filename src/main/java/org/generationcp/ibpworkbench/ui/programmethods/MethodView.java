package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Method;

/**
  * Created by cyrus on 4/7/14.
  */
 public class MethodView extends Method implements BeanFormState {
    private boolean active = false;
    private boolean isEnabled = true;
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(Boolean val) {
        this.active = val;
    }

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return isEnabled;
	}

	@Override
	public void setEnabled(Boolean val) {
		this.isEnabled = val;
		
	}

}
