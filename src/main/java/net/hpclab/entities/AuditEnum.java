package net.hpclab.entities;

public enum AuditEnum {
    LOGIN("LOGIN"), LOGOUT("LOGOUT"), INSERT("INSERT"), UPDATE("UPDATE"), DELETE("DELETE"), STATUS_CHANGE("STATUS_CHANGE");

    private final String audit;

    private AuditEnum(String audit) {
        this.audit = audit;
    }

    public String get() {
        return audit;
    }
}
