package net.hpclab.cev.enums;

public enum StatusEnum {
    Activo("Activo"), Deshabilitado("Deshabilitado"), Bloqueado("Bloqueado"), Incompleto("Incompleto"), Completo("Completo");

    private final String status;

    private StatusEnum(String status) {
        this.status = status;
    }

    public String get() {
        return status;
    }
}
