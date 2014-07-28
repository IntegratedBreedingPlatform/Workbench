package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Method;

/**
  * Created by cyrus on 4/7/14.
  */
 public class MethodView extends Method implements BeanFormState {
    private boolean active = false;
    private boolean isEnabled = true;

    public MethodView() {
        this.setMname("");
        this.setMdesc("");
        this.setMtype("");
        this.setMgrp("");
        this.setMcode("");
        this.setGeneq(null);
    }

    public boolean isBulk() {
        return getGeneq() == 1;
    }

    public void setBulk(boolean value) {
        this.setGeneq(value ? 1 : 0);

    }

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

    public Method copy() {
        return new Method(
            getMid(),
            getMtype(),
            getMgrp(),
            getMcode(),
            getMname(),
            getMdesc(),
            getReference(),
            getMprgn(),
            getMfprg(),
            getMattr(),
            getGeneq(),
            getUser(),
            getLmid(),
            getMdate()
        );
    }
    
    public MethodView copyMethodView(){
    	
    	MethodView methodView = new MethodView();
    	//methodView.setMid(getMid());
    	methodView.setMtype(getMtype());
    	methodView.setMgrp(getMgrp());
    	methodView.setMcode(getMcode());
    	methodView.setMname(getMname());
    	methodView.setMdesc(getMdesc());
    	methodView.setReference(getReference());
    	methodView.setMprgn(getMprgn());
    	methodView.setMfprg(getMfprg());
    	methodView.setMattr(getMattr());
    	methodView.setGeneq(getGeneq());
    	methodView.setUser(getUser());
    	methodView.setLmid(getLmid());
    	methodView.setMdate(getMdate());
    	
		return methodView;
    	
    }

}
