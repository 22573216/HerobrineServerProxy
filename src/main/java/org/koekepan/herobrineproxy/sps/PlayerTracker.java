package org.koekepan.herobrineproxy.sps;

import org.koekepan.herobrineproxy.ConsoleIO;

import java.util.HashMap;
import java.util.UUID;

public class PlayerTracker {
    // Fields for player's position
    private double x;
    private double y;
    private double z;

    // Fields for player identification
    private final String username;
    private final UUID uuid;
    private int entityId;

    // Static maps to hold instances of PlayerTracker by username and entity ID
    private static final HashMap<String, PlayerTracker> playerTrackerMapByUsername = new HashMap<>();
    private static final HashMap<Integer, PlayerTracker> playerTrackerMapByEntityId = new HashMap<>();
//    private static final HashMap<Integer, PlayerTracker> playerTrackerMapByUUID = new HashMap<>();

    // Constructor to initialize all fields
    public PlayerTracker(double x, double y, double z, String username, UUID uuid) throws Exception {
//        PlayerTracker existingByUsername = playerTrackerMapByUsername.get(username);
//        PlayerTracker existingByEntityId = playerTrackerMapByEntityId.get(entityId);

//        if (existingByUsername != null || existingByEntityId != null) {
//            if (existingByUsername.equals(existingByEntityId)) {
//                existingByUsername.x = x;
//                existingByUsername.y = y;
//                existingByUsername.z = z;
//                this.username = null; // Dummy values to satisfy final keyword
//                this.entityId = 0;    // Dummy values to satisfy final keyword
//                return;
//            } else {
//                throw new Exception("Mismatch between username and entityId");
//            }
//        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.username = username;
//        this.entityId = Integer.parseInt(null);
        this.uuid = uuid;
//        this.entityId = Integer.parseInt(null);

        if (this.username != null) {
            playerTrackerMapByUsername.put(this.username, this);
        }
//        if (this.entityId != 0) {
//            playerTrackerMapByEntityId.put(this.entityId, this);
//        }
    }

    public PlayerTracker(String username, UUID uuid) throws Exception {
       this(0,0,0, username, uuid);
    }

    public static void setEntityId(int entityId, UUID uuid) throws Exception { // Sets entityID based on UUID
        PlayerTracker playerTracker = playerTrackerMapByEntityId.get(entityId);

        if (playerTracker != null) {
            if (playerTracker.uuid.equals(uuid)) {
                ConsoleIO.println("PlayerTracker with specified entityID found, can't change entityID");
                return;
            } else {
                throw new Exception("PlayerTracker with specified entityID found, can't change entityID - ALSO UUID mismatch!");
            }
        } else {
            playerTracker = getPlayerTrackerByUUID(uuid); // Only check this after check for entityID to save on O(n) efficiency - HashMap is O(1)
            if (playerTracker == null) {
                throw new Exception("PlayerTracker with specified UUID not found when setting entityID");
            } else {
                playerTracker.entityId = entityId;
                playerTrackerMapByEntityId.put(playerTracker.entityId, playerTracker);
            }
        }
    }

    private static PlayerTracker getPlayerTrackerByUUID(UUID uuidToFind) {
        for (PlayerTracker playerTracker : playerTrackerMapByUsername.values()) {
            if (uuidToFind.equals(playerTracker.uuid)){
                return playerTracker;
            }
        }
        return null; // Return null if no matching PlayerTracker is found
    }


    public static PlayerTracker getPlayerTrackerByUsername(String username) {
        return playerTrackerMapByUsername.get(username);
    }

    public static PlayerTracker getPlayerTrackerByEntityId(int entityId){
        return playerTrackerMapByEntityId.get(entityId);
    }

    public static boolean isPlayer(int entityId){
        return getPlayerTrackerByEntityId(entityId) != null;
    }

    public static void removePlayerByUsername(String username) {
        int entityID = playerTrackerMapByUsername.get(username).entityId;
        playerTrackerMapByUsername.remove(username);
        playerTrackerMapByEntityId.remove(entityID);
    }

    public static void removePlayerByEntityId(int entityId) {
        String username = playerTrackerMapByEntityId.get(entityId).username;
        playerTrackerMapByEntityId.remove(entityId);
        playerTrackerMapByUsername.remove(username);
    }

    public static double getXByUsername(String username) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        return pt != null ? pt.getX() : null;
    }

    public static double getYByUsername(String username) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        return pt != null ? pt.getY() : null;
    }

    public static double getZByUsername(String username) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        return pt != null ? pt.getZ() : null;
    }

    public static void setXByUsername(String username, double x) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        pt.setX(x);
        return;
    }

    public static void setYByUsername(String username, double y) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        pt.setY(y);
        return;
    }

    public static void setZByUsername(String username, double z) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        pt.setZ(z);
        return;
    }
    public static double getXByEntityId(int entityId) {
        PlayerTracker pt = playerTrackerMapByEntityId.get(entityId);
        return pt != null ? pt.getX() : null;
    }

    public static double getYByEntityId(int entityId) {
        PlayerTracker pt = playerTrackerMapByEntityId.get(entityId);
        return pt != null ? pt.getY() : null;
    }

    public static double getZByEntityId(int entityId) {
        PlayerTracker pt = playerTrackerMapByEntityId.get(entityId);
        return pt != null ? pt.getZ() : null;
    }

    // Static method to move a PlayerTracker by username, with flag parameter
    public static void moveByUsername(String username, double x_move, double y_move, double z_move, byte flag) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        if (pt != null) {
            pt.move(x_move, y_move, z_move, flag);
        }
    }

    // Static method to move a PlayerTracker by username, assuming relative movement
    public static void moveByUsername(String username, double x_move, double y_move, double z_move) {
        PlayerTracker pt = playerTrackerMapByUsername.get(username);
        if (pt != null) {
            pt.move(x_move, y_move, z_move);
        }
    }

    // Static method to move a PlayerTracker by entity ID, with flag parameter
    public static void moveByEntityId(int entityId, double x_move, double y_move, double z_move, byte flag) {
        PlayerTracker pt = playerTrackerMapByEntityId.get(entityId);
        if (pt != null) {
            pt.move(x_move, y_move, z_move, flag);
        }
    }

    // Static method to move a PlayerTracker by entity ID, assuming relative movement
    public static void moveByEntityId(int entityId, double x_move, double y_move, double z_move) {
        PlayerTracker pt = playerTrackerMapByEntityId.get(entityId);
        if (pt != null) {
            pt.move(x_move, y_move, z_move);
        }
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
