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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CTDestinations extends JavaPlugin implements Listener {
    private static CTDestinations plugin;

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

        readData();
        getServer().getPluginManager().registerEvents(new TrainListener(), this);
        registerCommand("fahrziel", (TabExecutor)new Commands());
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

        BufferedReader read = null;
        try {
            read = new BufferedReader(new InputStreamReader(new FileInputStream(destinationFile), "UTF8"));
            String line;

            while ((line = read.readLine()) != null) {
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
            read.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if (read != null)
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file destinations.txt", e);
        }

        String data = "";

        for (Map.Entry<String, Destination> entry : this.destinations.entrySet()) {
            Destination dest = entry.getValue();
            Location loc = dest.getLocation();
            String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw();
            data = data + dest.getName() + ":" + dest.getOwner() + ":" + dest.getType().name() + ":" + location + ":" + dest.isPublic() + "\r\n";
        }

        try {
            FileWriter fw = new FileWriter(destinationFile);
            fw.write(data);
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public void addDestination(String name, UUID owner, Destination.DestinationType type, Location location, Boolean isPublic) {
        if (getDestination(name) != null)
            return;

        this.destinations.put(name, new Destination(name, owner, (Enum)type, location, isPublic));
        saveDestinations();
    }

    public void removeDestination(String name) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        this.destinations.remove(dest.getName());
        saveDestinations();
    }

    public void setOwner(String name, UUID ownerUUID) {
        Destination dest = getDestination(name);

        if (dest == null)
            return;

        dest.setOwner(ownerUUID);
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
