package net.hpclab.cev.model;

import net.hpclab.cev.entities.Modules;

public class ModulesModel implements Comparable<ModulesModel> {
	private Modules modules;
	private String inherited;
	private int initialAccessCode;
	private boolean[] accesses;

	public ModulesModel(Modules modules, String inherited, int initialAccessCode, boolean[] accesses) {
		this.modules = modules;
		this.inherited = inherited;
		this.initialAccessCode = initialAccessCode;
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

	public int getInitialAccessCode() {
		return initialAccessCode;
	}

	public void setInitialAccessCode(int initialAccessCode) {
		this.initialAccessCode = initialAccessCode;
	}

	@Override
	public int hashCode() {
		return 11231 * this.modules.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ModulesModel))
			return false;

		ModulesModel m = (ModulesModel) o;
		return m != null && m.modules != null && m.modules.getIdModule() != null && this.modules != null
				&& this.modules.getIdModule() != null && this.modules.getIdModule().equals(m.modules.getIdModule());
	}

	@Override
	public int compareTo(ModulesModel o) {
		return this.modules.getIdModule().compareTo(o.getModules().getIdModule());
	}
}
