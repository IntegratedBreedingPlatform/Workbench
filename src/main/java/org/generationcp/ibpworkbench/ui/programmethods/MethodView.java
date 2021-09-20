
package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Method;

/**
 * Created by cyrus on 4/7/14.
 */
public class MethodView extends Method implements BeanFormState {

	/**
	 *
	 */
	private static final long serialVersionUID = -7781564594804285349L;
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
		return this.getGeneq() == 1;
	}

	public void setBulk(final boolean value) {
		this.setGeneq(value ? 1 : 0);

	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(final Boolean val) {
		this.active = val;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public void setEnabled(final Boolean val) {
		this.isEnabled = val;
	}

	public Method copy() {
		final Method method = new Method(this.getMid(), this.getMtype(), this.getMgrp(), this.getMcode(), this.getMname(), this.getMdesc(),
				this.getReference(), this.getMprgn(), this.getMfprg(), this.getMattr(), this.getGeneq(), this.getUser(), this.getLmid(),
				this.getMdate());
		method.setSnametype(this.getSnametype());
		method.setSeparator(this.getSeparator());
		method.setPrefix(this.getPrefix());
		method.setCount(this.getCount());
		method.setSuffix(this.getSuffix());
		return method;
	}

	public MethodView copyMethodView() {

		final MethodView methodView = new MethodView();
		methodView.setMtype(this.getMtype());
		methodView.setMgrp(this.getMgrp());
		methodView.setMcode(this.getMcode());
		methodView.setMname(this.getMname());
		methodView.setMdesc(this.getMdesc());
		methodView.setReference(this.getReference());
		methodView.setMprgn(this.getMprgn());
		methodView.setMfprg(this.getMfprg());
		methodView.setMattr(this.getMattr());
		methodView.setGeneq(this.getGeneq());
		methodView.setUser(this.getUser());
		methodView.setLmid(this.getLmid());
		methodView.setMdate(this.getMdate());
		methodView.setSnametype(this.getSnametype());
		methodView.setSeparator(this.getSeparator());
		methodView.setPrefix(this.getPrefix());
		methodView.setCount(this.getCount());
		methodView.setSuffix(this.getSuffix());

		return methodView;

	}

}
