package de.crafttogether.ctdestinations;

import java.util.UUID;
import org.bukkit.Location;

public class Destination {
    private String name = null;

    private UUID owner = null;

    private Enum<?> type = null;

    private Location location = null;

    private Boolean isPublic = null;

    public enum DestinationType {
        STATION {
            @Override
            public String toString() {
                return "Bahnhof";
            }
        },

        MAIN_STATION {
            @Override
            public String toString() {
                return "Hauptbahnhof";
            }
        },

        PLAYER_STATION {
            @Override
            public String toString() {
                return "Spielerbahnhof";
            }
        }
    }

    public Destination(String name, UUID owner, Enum<?> type, Location location, Boolean isPublic) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.isPublic = isPublic;
    }

    public String getName() {
        return this.name;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public Enum<?> getType() {
        return this.type;
    }

    public Location getLocation() {
        return this.location;
    }

    public Boolean isPublic() {
        return this.isPublic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setType(Enum<?> type) {
        if (type != DestinationType.MAIN_STATION && type != DestinationType.PLAYER_STATION && type != DestinationType.STATION)
            return;
        this.type = type;
    }

    public void setType(String type) {
        if (!type.equalsIgnoreCase("MAIN_STATION") && !type.equalsIgnoreCase("PLAYER_STATION") && !type.equalsIgnoreCase("STATION"))
            return;
        this.type = DestinationType.valueOf(type);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public static DestinationType findType(String label) {
        switch (label.toLowerCase()) {
            case "bahnhof": return DestinationType.STATION;
            case "hauptbahnhof": return DestinationType.MAIN_STATION;
            case "spielerbahnhof": return DestinationType.PLAYER_STATION;
        }
        return null;
    }
}
