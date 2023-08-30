package org.koekepan.herobrineproxy.sps;

import java.util.HashMap;
import java.util.UUID;

public class EntityTracker {
    private final UUID uuid;
    // Fields for entity's position
    private double x;
    private double y;
    private double z;

    // Field for entity identification
    private final int entityId;

    // Static map to hold instances of EntityTracker by entity ID
    private static final HashMap<Integer, EntityTracker> entityTrackerMapByEntityId = new HashMap<>();

    // Constructor to initialize all fields
    public EntityTracker(double x, double y, double z, int entityId, UUID uuid) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
        this.uuid = uuid;

        entityTrackerMapByEntityId.put(this.entityId, this);
    }

    public static EntityTracker getEntityTrackerByEntityId(int entityId) {
        return entityTrackerMapByEntityId.get(entityId);
    }

    public static void removeEntityByEntityId(int entityId) {
        entityTrackerMapByEntityId.remove(entityId);
    }

    public static double getXByEntityId(int entityId) {
        EntityTracker et = entityTrackerMapByEntityId.get(entityId);
        return et != null ? et.getX() : null;
    }

    public static double getYByEntityId(int entityId) {
        EntityTracker et = entityTrackerMapByEntityId.get(entityId);
        return et != null ? et.getY() : null;
    }

    public static double getZByEntityId(int entityId) {
        EntityTracker et = entityTrackerMapByEntityId.get(entityId);
        return et != null ? et.getZ() : null;
    }

    public static void moveByEntityId(int entityId, double x_move, double y_move, double z_move, byte flag) {
        EntityTracker et = entityTrackerMapByEntityId.get(entityId);
        if (et != null) {
            et.move(x_move, y_move, z_move, flag);
        }
    }

    public static void moveByEntityId(int entityId, double x_move, double y_move, double z_move) {
        EntityTracker et = entityTrackerMapByEntityId.get(entityId);
        if (et != null) {
            et.move(x_move, y_move, z_move);
        }
    }

    public static boolean isEntity(int entityId) {
        return getEntityTrackerByEntityId(entityId) != null;
    }

    private void setX(double x) {
        this.x = x;
    }

    private void setZ(double z) {
        this.z = z;
    }

    private void setY(double y) {
        this.y = y;
    }

    private double getX() {
        return x;
    }

    private double getZ() {
        return z;
    }

    private double getY() {
        return y;
    }

    private void move(double x_move, double y_move, double z_move, byte flag) {
        // Flags as per the description given on the protocol wiki
        final byte FLAG_X = 0x01; // 0000 0001
        final byte FLAG_Y = 0x02; // 0000 0010
        final byte FLAG_Z = 0x04; // 0000 0100

        // Check each bit and set the position accordingly
        if ((flag & FLAG_X) == FLAG_X) {
            // X value is relative
            this.x += x_move;
        } else {
            // X value is absolute
            this.x = x_move;
        }

        if ((flag & FLAG_Y) == FLAG_Y) {
            // Y value is relative
            this.y += y_move;
        } else {
            // Y value is absolute
            this.y = y_move;
        }

        if ((flag & FLAG_Z) == FLAG_Z) {
            // Z value is relative
            this.z += z_move;
        } else {
            // Z value is absolute
            this.z = z_move;
        }
    }

    private void move(double x_move, double y_move, double z_move) { // Without Flag, assume relative move
        this.x += x_move;
        this.y += y_move;
        this.z += z_move;
    }
}
