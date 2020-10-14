package de.crafttogether;

import com.bergerkiller.bukkit.tc.*;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import de.crafttogether.ctdestinations.Commands;
import de.crafttogether.ctdestinations.Destination;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

public class CTDestinations extends JavaPlugin implements Listener {
    private static CTDestinations plugin;

    private DynmapAPI dynmap = null;
    private TreeMap<String, Destination> destinations;

    public void onEnable() {
        plugin = this;

        if (!getServer().getPluginManager().isPluginEnabled("Train_Carts")) {
            plugin.getLogger().warning("Couln't find TrainCarts");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        if (!getServer().getPluginManager().isPluginEnabled("BKCommonLib")) {
            plugin.getLogger().warning("Couln't find BKCommonLib");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        if (!getServer().getPluginManager().isPluginEnabled("Dynmap")) {
            plugin.getLogger().warning("Couln't find Dynmap");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("Dynmap");

        readData();
        getServer().getPluginManager().registerEvents(new TrainListener(), this);
        TabExecutor cmdListener = new Commands();
        registerCommand("fahrziel", cmdListener);
        registerCommand("fahrziele", cmdListener);
        registerCommand("fahrzieledit", cmdListener);

        Bukkit.getServer().getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                plugin.getLogger().info("Setup MarkerSets...");
                plugin.createMarkerSets();

                plugin.getLogger().info("Setup Markers...");
                TreeMap<String, Destination> destinations = plugin.getDestinations();
                for (Destination dest : destinations.values())
                    plugin.setMarker(dest, true);
                
                plugin.getLogger().info("Setup complete.");
            }
        });

        plugin.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " enabled");
    }

    public void onDisable() {}

    private void registerCommand(String cmd, TabExecutor executor) {
        getCommand(cmd).setExecutor((CommandExecutor)executor);
        getCommand(cmd).setTabCompleter((TabCompleter)executor);
    }

    public void broadcast(String message) {
        for (Player p : Bukkit.getServer().getOnlinePlayers())
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void deleteMarker(Destination dest) {
        MarkerSet set = dynmap.getMarkerAPI().getMarkerSet("CT_" + dest.getType().name());
        if (set == null) return;

        Marker marker = set.findMarker(dest.getName());
        if (marker != null)
            marker.deleteMarker();
    }

    public void createMarkerSets() {
        if (dynmap == null)
            return;

        MarkerAPI markerApi = dynmap.getMarkerAPI();
        MarkerIcon iconRail = markerApi.getMarkerIcon("ct-rail");
        MarkerIcon iconMinecart = markerApi.getMarkerIcon("ct-minecart");

        /* Create Icons if not existing */
        if (iconRail == null) {
            iconRail = dynmap.getMarkerAPI().createMarkerIcon("ct-rail", "ct-rail", getResource("rail.png"));
            dynmap.getMarkerAPI().getMarkerIcons().add(iconRail);
        }

        if (iconMinecart == null) {
            iconMinecart = dynmap.getMarkerAPI().createMarkerIcon("ct-minecart", "ct-minecart", getResource("minecart.png"));
            dynmap.getMarkerAPI().getMarkerIcons().add(iconMinecart);
        }

        /* Create MarkerSets if not existing */
        for (Destination.DestinationType type : Destination.DestinationType.values()) {
            MarkerSet set = dynmap.getMarkerAPI().getMarkerSet("CT_" + type.name());

            String label = "Bahnhof";
            if (type.name().equals("MAIN_STATION"))
                label = "Hauptbahnhof";
            else if (type.name().equals("PLAYER_STATION"))
                label = "Bahnhof (Spieler)";

            if (set == null)
                set = dynmap.getMarkerAPI().createMarkerSet("CT_" + type.name(), label, null, true);
        }
    }

    public void setMarker(Destination dest) {
        setMarker(dest, false);
    }
    public void setMarker(Destination dest, boolean updateOnly) {
        if (dynmap == null)
            return;

        MarkerAPI markerApi = dynmap.getMarkerAPI();

        if (!updateOnly)
            createMarkerSets();

        getLogger().info("Create Marker for '"+dest.getName()+"' updateOnly: " + updateOnly);

        MarkerSet set = dynmap.getMarkerAPI().getMarkerSet("CT_" + dest.getType().name());
        MarkerIcon icon = null;
        String label = null;
        String owner = Bukkit.getOfflinePlayer(dest.getOwner()).getName();
        Boolean showOwner = true;

        switch (dest.getType().name()) {
            case "STATION":
                icon = markerApi.getMarkerIcon("ct-rail");
                label = "Bahnhof";
                showOwner = false;
                break;
            case "MAIN_STATION":
                icon = markerApi.getMarkerIcon("ct-rail");
                label = "Hauptbahnhof";
                showOwner = false;
                break;
            case "PLAYER_STATION":
                icon = markerApi.getMarkerIcon("ct-minecart");
                label = "Spielerbahnhof";
                showOwner = true;
                break;
        }

        if (owner == null)
            showOwner = false;

        label = "<div class=\"ctdestination\" id=\"" + dest.getName() + "\"><div style=\"padding:6px\"><h3 style=\"padding:0px;margin:0px;color:#ffaa00\">" + dest.getName() + " <span style=\"color:#aaaaaa\">(" + label + ")</span></h3>" + (showOwner ? "<span style=\"font-weight:bold;color:#aaaaaa;\">Besitzer:</span> " + owner + "<br>" : "") + "<span style=\"font-style:italic;font-weight:bold;color:#ff5555\">/fahrziel " + dest.getName() + "</span></div></div>";
        //label += " ("+dest.getName()+")";

        // Delete Marker if exists
        deleteMarker(dest);

        /* Create Marker */
        Location loc = dest.getLocation();
        set.createMarker(dest.getName(), label, true, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), icon, false);
    }

    public void readData() {
        HashMap<String, Destination> readed = new HashMap<>();

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        this.destinations = new TreeMap<>();
        File destinationFile = new File(getDataFolder(), "destinations.txt");

        try {
            if (!destinationFile.exists())
                destinationFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file destinations.txt", e);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(destinationFile), "UTF8"));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.equals("") && !line.startsWith("#")) {
                    String[] split = line.split(":");
                    String[] loc = split[3].split(",");
                    World world = Bukkit.getWorld(loc[0]);

                    if (world == null)
                        return;

                    Double x = Double.valueOf(loc[1]);
                    Double y = Double.valueOf(loc[2]);
                    Double z = Double.valueOf(loc[3]);
                    Float pitch = Float.valueOf(loc[4]);
                    Float yaw = Float.valueOf(loc[5]);
                    String name = split[0];
                    UUID owner = UUID.fromString(split[1]);
                    Location location = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue(), pitch.floatValue(), yaw.floatValue());
                    Boolean isPublic = Boolean.valueOf(split[4].equals("true"));

                    readed.put(name, new Destination(name, owner, (Enum)Destination.DestinationType.valueOf(split[2]), location, isPublic));
                }
            }

            this.destinations = new TreeMap<>(readed);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveDestinations() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File destinationFile = new File(getDataFolder(), "destinations.txt");

        try {
            if (!destinationFile.exists())
                destinationFile.createNewFile();
        }

        catch (IOException e) {
            throw new RuntimeException("Unable to create file destinations.txt", e);
        }

        String data = "";

        for (Map.Entry<String, Destination> entry : this.destinations.entrySet()) {
            Destination dest = entry.getValue();
            Location loc = dest.getLocation();
            String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw();
            data = data + dest.getName() + ":" + dest.getOwner() + ":" + dest.getType().name() + ":" + location + ":" + dest.isPublic() + "\r\n";
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(destinationFile);
            writer.write(data);
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }
        
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Destination getDestination(String name) {
        for (Map.Entry<String, Destination> entry : this.destinations.entrySet()) {
            Destination dest = entry.getValue();

            if (dest.getName().equalsIgnoreCase(name))
                return dest;
        }
        return null;
    }

    public TreeMap<String, Destination> getDestinations() {
        return this.destinations;
    }

    public static CTDestinations getInstance() {
        return plugin;
    }

    public void addDestination(String name, String description, Destination.DestinationType type, Location location, Boolean isPublic) {
        if (getDestination(name) != null)
            return;

        Destination dest = new Destination(name, description, (Enum)type, location, isPublic);

        // Create dynmap-marker
        setMarker(dest);

        this.destinations.put(name, dest);
        saveDestinations();
    }

    public void removeDestination(String name) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        this.destinations.remove(dest.getName());
        saveDestinations();
    }

    public void addOwner(String name, OfflinePlayer owner) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        dest.addOwner(owner);
        saveDestinations();
    }

    public void setType(String name, Destination.DestinationType type) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        dest.setType((Enum)type);
        saveDestinations();
    }

    public void setLocation(String name, Location location) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        dest.setLocation(location);
        saveDestinations();
    }

    public void setPublic(String name, Boolean isPublic) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        dest.setPublic(isPublic);
        saveDestinations();
    }
}
