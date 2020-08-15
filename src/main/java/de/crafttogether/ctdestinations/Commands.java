package de.crafttogether.ctdestinations;

import de.crafttogether.CTDestinations;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class Commands implements TabExecutor {
	private CTDestinations plugin = CTDestinations.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String st, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("fahrziel"))
			return true;

		Player p = null;

		if (sender instanceof Player)
			p = Bukkit.getPlayer(((Player)sender).getUniqueId());

		if (p == null)
			return false;

		if (args.length == 0) {
			TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));

			sendMessage(p, "&e-------------- &c&lCraftBahn &e--------------");

			TextComponent message = new TextComponent();
			message.addExtra((BaseComponent) newLine);
			message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6Willkommen bei der CraftBahn!")));
			message.addExtra((BaseComponent) newLine);

			message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eUnser Schienennetz erstreckt sich in alle Himmelsrichtungen.")));
			message.addExtra((BaseComponent) newLine);

			message.addExtra((BaseComponent) newLine);
			message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lAnleitung: ")));
			p.spigot().sendMessage((BaseComponent)message);

			message = new TextComponent();
			message.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c/bahnhof")));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bahnhof"));
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eHowTo: &6Bahnhof"))).create()));
			p.spigot().sendMessage((BaseComponent)message);

			message = new TextComponent();
			message.addExtra((BaseComponent) newLine);
			message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6&lFahrziele: ")));
			p.spigot().sendMessage((BaseComponent)message);

			message = new TextComponent();
			message.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c/fahrziel list")));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fahrziel list"));
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eAlle Fahrziele auflisten"))).create()));
			p.spigot().sendMessage((BaseComponent)message);

			message = new TextComponent();
			message.addExtra((BaseComponent) newLine);
			message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eGute Fahrt!")));
			p.spigot().sendMessage((BaseComponent)message);
		}

		else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("liste")) {
			TreeMap<String, Destination> destinations = this.plugin.getDestinations();
			if (destinations.size() < 1) {
				sendMessage(p, "&cEs ist noch kein Ziel in der Liste. Tippe &e/destination add <name> &czum hinzufügen.");
				return true;
			}
			sendMessage(p, "&6&lHauptbahnhof&f:");
			for (Map.Entry<String, Destination> entry : destinations.entrySet()) {
				Destination dest = entry.getValue();

				if (dest.getType() != Destination.DestinationType.MAIN_STATION)
					continue;

				String hoverText = "/fahrziel " + dest.getName();

				if (dest.getOwner() != null && p.hasPermission("ctdestination.see.owner")) {
					OfflinePlayer owner = Bukkit.getOfflinePlayer(dest.getOwner());

					if (owner.hasPlayedBefore())
						hoverText = hoverText + "\n&6Besitzer: &e" + Bukkit.getOfflinePlayer(dest.getOwner()).getName();
				}

				if (dest.getLocation() != null && p.hasPermission("ctdestination.see.location"))
					hoverText = hoverText + "\n&6Koordinaten: &e" + dest.getLocation().getX() + ", " + dest.getLocation().getY() + ", " + dest.getLocation().getZ();

				TextComponent message = new TextComponent();
				message.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7 > &e" + dest.getName())));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/train destination " + dest.getName()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverText))).create()));

				if (dest.getLocation() != null && p.hasPermission("ctdestination.see.location")) {
					Location loc = dest.getLocation();

					TextComponent tp = new TextComponent();
					tp.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', " &7[&fTP&7]")));
					tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minecraft:teleport " + loc.getX() + " " + loc.getY() + " " + loc.getZ()));
					tp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&6Teleportiere zum Zielort"))).create()));

					message.addExtra((BaseComponent)tp);
				}

				p.spigot().sendMessage((BaseComponent)message);
			}

			sendMessage(p, "&6&lBahnhof&f:");

			for (Map.Entry<String, Destination> entry : destinations.entrySet()) {
				Destination dest = entry.getValue();

				if (dest.getType() != Destination.DestinationType.STATION)
					continue;

				String hoverText = "/fahrziel " + dest.getName();

				if (dest.getOwner() != null && p.hasPermission("ctdestination.see.owner")) {
					OfflinePlayer owner = Bukkit.getOfflinePlayer(dest.getOwner());

					if (owner.hasPlayedBefore())
						hoverText = hoverText + "\n&6Besitzer: &e" + Bukkit.getOfflinePlayer(dest.getOwner()).getName();
				}

				if (dest.getLocation() != null && p.hasPermission("ctdestination.see.location"))
					hoverText = hoverText + "\n&6Koordinaten: &e" + dest.getLocation().getX() + ", " + dest.getLocation().getY() + ", " + dest.getLocation().getZ();

				TextComponent message = new TextComponent();
				message.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7 > &e" + dest.getName())));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/train destination " + dest.getName()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverText))).create()));

				if (dest.getLocation() != null && p.hasPermission("ctdestination.see.location")) {
					Location loc = dest.getLocation();

					TextComponent tp = new TextComponent();
					tp.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', " &7[&fTP&7]")));
					tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minecraft:teleport " + loc.getX() + " " + loc.getY() + " " + loc.getZ()));
					tp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&6Teleportiere zum Zielort"))).create()));

					message.addExtra((BaseComponent)tp);
				}

				p.spigot().sendMessage((BaseComponent)message);
			}

			sendMessage(p, "&6&lSpieler&f:");

			for (Map.Entry<String, Destination> entry : destinations.entrySet()) {
				Destination dest = entry.getValue();

				if (dest.getType() != Destination.DestinationType.PLAYER_STATION)
					continue;

				String hoverText = "/fahrziel " + dest.getName();

				if (dest.getOwner() != null && p.hasPermission("ctdestination.see.owner")) {
					OfflinePlayer owner = Bukkit.getOfflinePlayer(dest.getOwner());

					if (owner.hasPlayedBefore())
						hoverText = hoverText + "\n&6Besitzer: &e" + Bukkit.getOfflinePlayer(dest.getOwner()).getName();
				}

				if (dest.getLocation() != null && p.hasPermission("ctdestination.see.location"))
					hoverText = hoverText + "\n&6Koordinaten: &e" + dest.getLocation().getX() + ", " + dest.getLocation().getY() + ", " + dest.getLocation().getZ();

				TextComponent message = new TextComponent();
				message.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7 > &e" + dest.getName())));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/train destination " + dest.getName()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverText))).create()));

				if (dest.getLocation() != null && p.hasPermission("ctdestination.see.location")) {
					Location loc = dest.getLocation();

					TextComponent tp = new TextComponent();
					tp.addExtra((BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', " &7[&fTP&7]")));
					tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minecraft:teleport " + loc.getX() + " " + loc.getY() + " " + loc.getZ()));
					tp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&6Teleportiere zum Zielort"))).create()));

					message.addExtra((BaseComponent)tp);
				}

				p.spigot().sendMessage((BaseComponent)message);
			}

			return true;
		}

		else if (args[0].equalsIgnoreCase("add")) {

			if (!p.hasPermission("ctdestination.add")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2 || args[1].equals("") || args[1].length() < 1) {
				sendMessage(p, "&cBitte gebe den Namen des Ziel ein.");
				return true;
			}

			if (this.plugin.getDestination(args[1]) != null) {
				sendMessage(p, "&cEs existiert bereits ein Ziel mit diesem Namen in der Liste.");
				return true;
			}

			sendMessage(p, "&aZiel gespeichert.");
			this.plugin.addDestination(args[1], p.getUniqueId(), Destination.DestinationType.STATION, p.getLocation(), Boolean.valueOf(true));
		}

		else if (args[0].equalsIgnoreCase("remove")) {

			if (!p.hasPermission("ctdestination.remove")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2 || args[1].equals("") || args[1].length() < 1) {
				sendMessage(p, "&cBitte gebe den Namen des Ziel ein.");
				return true;
			}

			if (this.plugin.getDestination(args[1]) == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen.");
				return true;
			}

			sendMessage(p, "&aZiel gelöscht.");
			this.plugin.removeDestination(args[1]);
		}

		else if (args[0].equalsIgnoreCase("setowner")) {
			if (!p.hasPermission("ctdestination.setowner")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2) {
				sendMessage(p, "&cEs wurde kein Ziel angegeben.");
				sendMessage(p, "/destination setowner <destination> <player>");
				return true;
			}

			if (this.plugin.getDestination(args[1]) == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen");
				return true;
			}

			if (args.length < 3) {
				sendMessage(p, "&cEs wurde kein Spieler angegeben");
				sendMessage(p, "/destination setowner <destination> <player>");
				return true;
			}

			OfflinePlayer owner = Bukkit.getOfflinePlayer(args[2]);
			if (owner == null || !owner.hasPlayedBefore()) {
				sendMessage(p, "&cEs wurde kein Spieler mit dem Namen &e" + args[2] + " &cgefunden");
				return true;
			}

			Destination dest = (Destination) this.plugin.getDestinations().get(args[1]);
			this.plugin.setOwner(dest.getName(), owner.getUniqueId());
			sendMessage(p, "&e" + dest.getName() + " &6gehnun &e" + owner.getName());
		}

		else if (args[0].equalsIgnoreCase("settype")) {

			if (!p.hasPermission("ctdestination.settype")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2) {
				sendMessage(p, "&cEs wurde kein Ziel angegeben");
				sendMessage(p, "/destination settype <destination> <STATION|MAIN_STATION|PLAYER_STATION>");
				return true;
			}

			if (this.plugin.getDestination(args[1]) == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen");
				return true;
			}

			if (args.length < 3) {
				sendMessage(p, "&cEs wurde kein Stationstyp angegeben");
				sendMessage(p, "/destination settype <destination> <STATION|MAIN_STATION|PLAYER_STATION>");
				return true;
			}

			Destination.DestinationType type = null;
			try {
				type = Destination.DestinationType.valueOf(args[2].toUpperCase());
			} catch (Exception exception) {}

			if (type == null) {
				sendMessage(p, "&cUngültiger Stationstyp.");
				return true;
			}

			Destination dest = (Destination) this.plugin.getDestinations().get(args[1]);
			this.plugin.setType(dest.getName(), type);
			sendMessage(p, "&e" + dest.getName() + " &6ist nun eine &e" + type);
		}

		else if (args[0].equalsIgnoreCase("setlocation")) {

			if (!p.hasPermission("ctdestination.setlocation")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2) {
				sendMessage(p, "&cEs wurde kein Ziel angegeben");
				sendMessage(p, "/destination setlocation <destination>");
				return true;
			}

			if (this.plugin.getDestination(args[1]) == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen");
				return true;
			}

			Destination dest = (Destination)this.plugin.getDestinations().get(args[1]);
			this.plugin.setLocation(dest.getName(), p.getLocation());
			sendMessage(p, "&6Du hast die Position von &e" + dest.getName() + " &6aktualisiert.");
		}

		else if (args[0].equalsIgnoreCase("setprivate")) {

			if (!p.hasPermission("ctdestination.setprivate")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2) {
				sendMessage(p, "&cEs wurde kein Ziel angegeben");
				sendMessage(p, "/destination setprivate <destination>");
				return true;
			}

			if (this.plugin.getDestination(args[1]) == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen");
				return true;
			}

			Destination dest = (Destination)this.plugin.getDestinations().get(args[1]);
			this.plugin.setPublic(dest.getName(), Boolean.valueOf(false));
			sendMessage(p, "&6Das Ziel &e" + dest.getName() + " &6ist nun &eprivat");
		}

		else if (args[0].equalsIgnoreCase("setpublic")) {

			if (!p.hasPermission("ctdestination.setpublic")) {
				sendMessage(p, "&cDazu hast du keine Berechtigung.");
				return true;
			}

			if (args.length < 2) {
				sendMessage(p, "&cEs wurde kein Ziel angegeben");
				sendMessage(p, "/destination setpublic <destination>");
				return true;
			}

			if (this.plugin.getDestination(args[1]) == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen");
				return true;
			}

			Destination dest = (Destination)this.plugin.getDestinations().get(args[1]);
			this.plugin.setPublic(dest.getName(), Boolean.valueOf(true));
			sendMessage(p, "&6Das Ziel &e" + dest.getName() + " &6ist nun &2öffentlich.");
		}

		else {
			Destination dest = this.plugin.getDestination(args[0]);

			if (dest == null) {
				sendMessage(p, "&cEs existiert kein Ziel mit diesem Namen.");
				return true;
			}

			Bukkit.getServer().dispatchCommand((CommandSender)p, "train destination " + dest.getName());
		}

		return true;
	}

	private void sendMessage(Player p, String message) {
		if (p.isOnline())
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("fahrziel"))
			return null;

		ArrayList<String> newList = new ArrayList<>();
		ArrayList<String> proposals = new ArrayList<>();

		if (args.length < 2) {
			if (sender.hasPermission("ctdestination.add"))
				proposals.add("add");
			if (sender.hasPermission("ctdestination.remove"))
				proposals.add("remove");
			if (sender.hasPermission("ctdestination.setowner"))
				proposals.add("setowner");
			if (sender.hasPermission("ctdestination.setlocation"))
				proposals.add("setlocation");
			if (sender.hasPermission("ctdestination.settype"))
				proposals.add("settype");
			if (sender.hasPermission("ctdestination.setpublic"))
				proposals.add("setpublic");
			if (sender.hasPermission("ctdestination.setprivate"))
				proposals.add("setprivate");
		}

		if (args.length == 3)
			switch (args[0]) {

				case "settype":
					proposals.add("STATION");
					proposals.add("MAIN_STATION");
					proposals.add("PLAYER_STATION");
					break;

				case "setowner":
					for (Player p : Bukkit.getOnlinePlayers())
						proposals.add(p.getName());
					break;
			}

		if (args.length < 3) {
			TreeMap<String, Destination> destinations = this.plugin.getDestinations();

			for (Map.Entry<String, Destination> entry : destinations.entrySet())
				proposals.add(entry.getKey());
		}

		if (args.length < 1 || args[args.length - 1].equals("")) {
			newList = proposals;
		}

		else {
			for (String value : proposals) {
				if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					newList.add(value);
			}
		}

		return newList;
	}
}
