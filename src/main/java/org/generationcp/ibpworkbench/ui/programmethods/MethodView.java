package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Method;

/**
  * Created by cyrus on 4/7/14.
  */
 public class MethodView extends Method implements BeanFormState {
    private boolean active = false;
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(Boolean val) {
        this.active = val;
    }

}
