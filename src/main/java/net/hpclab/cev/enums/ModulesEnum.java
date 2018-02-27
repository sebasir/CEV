package net.hpclab.cev.enums;

public enum ModulesEnum {
	LOGIN("Login"), COLLECTION("Colecciones y Catalogos"), CONTENT("Contenido Gráfico"), USR_INS(
			"Usuarios e Instituciones"), AUDIT("Auditoria"), PROFILE("Editar Perfil"), SPECIMEN("Especímenes"), AUTHOR(
					"Autores"), LOCATION("Ubicaciones"), TAXONOMY("Clasificaciones"), WIZARD(
							"Asistente de Creación"), REPORTER("Reporteador"), SETTINGS("Configuración");

	private final String status;

	private ModulesEnum(String status) {
		this.status = status;
	}

	public String get() {
		return status;
	}
}
