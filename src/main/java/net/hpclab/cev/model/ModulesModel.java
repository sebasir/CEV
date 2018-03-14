package net.hpclab.cev.model;

import net.hpclab.cev.entities.Modules;

public class ModulesModel {
	private Modules modules;
	private String inherited;
	private boolean[] accesses;

	public ModulesModel(Modules modules, String inherited, boolean[] accesses) {
		this.modules = modules;
		this.inherited = inherited;
		this.accesses = accesses;
	}

	public Modules getModules() {
		return modules;
	}

	public void setModules(Modules modules) {
		this.modules = modules;
	}

	public String getInherited() {
		return inherited;
	}

	public void setInherited(String inherited) {
		this.inherited = inherited;
	}

	public boolean[] getAccesses() {
		return accesses;
	}

	public boolean isReadGranted() {
		return accesses[0];
	}

	public void setReadGranted(boolean value) {
		accesses[0] = value;
	}

	public boolean isCreateGranted() {
		return accesses[1];
	}

	public void setCreateGranted(boolean value) {
		accesses[1] = value;
	}

	public boolean isUpdateGranted() {
		return accesses[2];
	}

	public void setUpdateGranted(boolean value) {
		accesses[2] = value;
	}

	public boolean isDeleteGranted() {
		return accesses[3];
	}

	public void setDeleteGranted(boolean value) {
		accesses[3] = value;
	}

	@Override
	public int hashCode() {
		return 11231;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ModulesModel))
			return false;

		ModulesModel m = (ModulesModel) o;
		return m != null && m.modules != null && m.modules.getIdModule() != null && this.modules != null
				&& this.modules.getIdModule() != null && this.modules.getIdModule().equals(m.modules.getIdModule());
	}

}
