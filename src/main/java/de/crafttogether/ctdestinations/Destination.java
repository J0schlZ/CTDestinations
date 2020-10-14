package de.crafttogether.ctdestinations;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Destination {
    private String name = null;
    private String description = null;
    private ArrayList<OfflinePlayer> ownerList;
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

        PUBLIC_STATION {
            @Override
            public String toString() {
                return "Öffentliche";
            }
        },

        PLAYER_STATION {
            @Override
            public String toString() {
                return "Spielerbahnhof";
            }
        }
    }

    public Destination(String name, String description, Enum<?> type, Location location, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.isPublic = isPublic;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<OfflinePlayer> getOwners() { return this.ownerList; }

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

    public void setDescription(String description) {
        this.description = name;
    }

    public void setType(Enum<?> type) {
        if (findType(type) == null)
            return;
        this.type = type;
    }

    public void setType(String type) {
        if (findType(type) == null)
            return;

        this.type = DestinationType.valueOf(type);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean hasOwner(UUID uuid) {
        for (OfflinePlayer owner : ownerList) {
            if (owner.getUniqueId().equals(uuid))
                return true;
        }

        return false;
    }

    public boolean hasOwner (OfflinePlayer owner) {
        return hasOwner(owner.getUniqueId());
    }

    public void addOwner(OfflinePlayer owner) {
        if (!ownerList.contains(owner))
            ownerList.add(owner);
    }

    public void removeOwner(OfflinePlayer owner) {
        if (ownerList.contains(owner))
            ownerList.remove(owner);
    }

    public DestinationType findType(Enum<?> type) {
        return findType(type.name());
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
