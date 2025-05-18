package org.example.securityservice.model;

public enum Permission {
    CREATE_ACTION(1, "createAction"),
    UPDATE_ACTION(2, "updateAction"),
    DELETE_ACTION(3, "deleteAction"),
    CREATE_METRIC(4, "createMetric"),
    UPDATE_METRIC(5, "updateMetric"),
    DELETE_METRIC(6, "deleteMetric"),
    TRIGGER_SCAN(7, "triggerScan"),
    TRIGGER_PROCESS(8, "triggerProcess"),
    TRIGGER_EVALUATION(9, "triggerEvaluation"),
    READ(10, "read"),
    // Admin specific permissions
    CREATE_USER(11, "createUser"),
    DELETE_USER(12, "deleteUser"),
    GRANT_PERMISSION(13, "grantPermission"),
    REVOKE_PERMISSION(14, "revokePermission"),
    ADMIN(15, "admin"); // Special permission that includes all permissions

    private final int id;
    private final String permission;

    Permission(int id, String permission) {
        this.id = id;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public static Permission fromId(int id) {
        for (Permission p : values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown permission id: " + id);
    }

    public static Permission fromPermission(String permission) {
        for (Permission p : values()) {
            if (p.getPermission().equals(permission)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown permission: " + permission);
    }
}
