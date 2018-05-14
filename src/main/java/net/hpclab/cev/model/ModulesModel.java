/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.model;

import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.Users;

/**
 * Este modelo permite tener un abstracción de la relación de un módulo y un
 * nivel de acceso, a partir de un arreglo de posiciones booleanas, y además
 * guarda el nivel de acceso inicial antes de la actualización
 * 
 * <p>
 * El nivel de acceso está definido por la siguiente tabla:<br>
 * <tt>DUCR </tt><br>
 * <tt>0000 -> 00 NONE </tt><br>
 * <tt>0001 -> 01 ONLY READ </tt><br>
 * <tt>0010 -> 02 ONLY CREATE </tt><br>
 * <tt>0011 -> 03 READ, CREATE </tt><br>
 * <tt>0100 -> 04 ONLY UPDATE </tt><br>
 * <tt>0101 -> 05 READ, UPDATE </tt><br>
 * <tt>0110 -> 06 CREATE, UPDATE </tt><br>
 * <tt>0111 -> 07 READ, CREATE, UPDATE </tt><br>
 * <tt>1000 -> 08 ONLY DELETE </tt><br>
 * <tt>1001 -> 09 READ, DELETE </tt><br>
 * <tt>1010 -> 10 CREATE, DELETE </tt><br>
 * <tt>1011 -> 11 READ, CREATE, DELETE </tt><br>
 * <tt>1100 -> 12 UPDATE, DELETE </tt><br>
 * <tt>1101 -> 13 READ, UPDATE, DELETE </tt><br>
 * <tt>1110 -> 14 CREATE, UPDATE, DELETE </tt><br>
 * <tt>1111 -> 15 READ, CREATE, UPDATE, DELETE<br></tt> Donde D: Delete, U:
 * Update, C: Create, R: Read, 0 es acceso denegado, y 1 concedido. El número
 * decimal que se visualiza es la representación decimal de la conjunción de los
 * números de la izquierda.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Users
 * @see Modules
 */

public class ModulesModel implements Comparable<ModulesModel> {

	/**
	 * Objeto que representa el módulo, con la llave única.
	 */
	private Modules modules;

	/**
	 * Nombre del rol que hereda el permiso al rol
	 */
	private String inherited;

	/**
	 * Codigo de acceso del nivel inicial antes de la edición
	 */
	private int initialAccessCode;

	/**
	 * Arreglo que permite distinguir los accesos que se determinan para este modulo
	 */
	private boolean[] accesses;

	/**
	 * Constructor que permite añadir los elementos que definen este modelo
	 * 
	 * @param modules
	 *            Objeto que representa el módulo, con la llave única.
	 * @param inherited
	 *            Nombre del rol que hereda el permiso al rol
	 * @param initialAccessCode
	 *            Codigo de acceso del nivel inicial antes de la edición
	 * @param accesses
	 *            Arreglo que permite distinguir los accesos que se determinan para
	 *            este modulo
	 */
	public ModulesModel(Modules modules, String inherited, int initialAccessCode, boolean[] accesses) {
		this.modules = modules;
		this.inherited = inherited;
		this.initialAccessCode = initialAccessCode;
		this.accesses = accesses;
	}

	/**
	 * @return Objeto que representa el módulo, con la llave única.
	 */
	public Modules getModules() {
		return modules;
	}

	/**
	 * @param modules
	 *            Objeto que representa el módulo, con la llave única a modificar.
	 */
	public void setModules(Modules modules) {
		this.modules = modules;
	}

	/**
	 * @return Nombre del rol que hereda el permiso al rol
	 */
	public String getInherited() {
		return inherited;
	}

	/**
	 * @param inherited
	 *            Nombre del rol que hereda el permiso al rol a definir
	 */
	public void setInherited(String inherited) {
		this.inherited = inherited;
	}

	/**
	 * @return Arreglo que permite distinguir los accesos que se determinan para
	 *         este modulo a definir
	 */
	public boolean[] getAccesses() {
		return accesses;
	}

	/**
	 * @return Permite determinar si se otorga el permiso de lectura
	 */
	public boolean isReadGranted() {
		return accesses[0];
	}

	/**
	 * @param value
	 *            Permite otorgar o revocar el permiso de lectura
	 */
	public void setReadGranted(boolean value) {
		accesses[0] = value;
	}

	/**
	 * @return Permite determinar si se otorga el permiso de creacion
	 */
	public boolean isCreateGranted() {
		return accesses[1];
	}

	/**
	 * @param value
	 *            Permite otorgar o revocar el permiso de creacion
	 */
	public void setCreateGranted(boolean value) {
		accesses[1] = value;
	}

	/**
	 * @return Permite determinar si se otorga el permiso de actualización
	 */
	public boolean isUpdateGranted() {
		return accesses[2];
	}

	/**
	 * @param value
	 *            Permite otorgar o revocar el permiso de actualizacion
	 */
	public void setUpdateGranted(boolean value) {
		accesses[2] = value;
	}

	/**
	 * @return Permite determinar si se otorga el permiso de eliminación
	 */
	public boolean isDeleteGranted() {
		return accesses[3];
	}

	/**
	 * @param value
	 *            Permite otorgar o revocar el permiso de eliminación
	 */
	public void setDeleteGranted(boolean value) {
		accesses[3] = value;
	}

	/**
	 * @return Codigo de nivel de acceso inicial
	 */
	public int getInitialAccessCode() {
		return initialAccessCode;
	}

	/**
	 * @param initialAccessCode
	 *            Define el nivel de acceso inicial
	 */
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
