package net.hpclab.entities;

public enum StatusEnum {
    ACTIVO("Activo"), DESHABILITADO("Deshabilitado"), BLOQUEADO("Bloqueado"), INCOMPLETO("Incompleto"), COMPLETO("Completo");

    private final String status;

    private StatusEnum(String status) {
        this.status = status;
    }

    public String get() {
        return status;
    }
}
