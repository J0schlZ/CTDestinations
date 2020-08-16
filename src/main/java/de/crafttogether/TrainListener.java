package de.crafttogether;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class TrainListener implements Listener {
    @EventHandler
    void onVehicleEnter(VehicleEnterEvent e) {
        if (!(e.getEntered() instanceof Player)) return;
        if (!(e.getVehicle() instanceof Minecart)) return;

        Player p = (Player) e.getEntered();
        Minecart minecart = (Minecart) e.getVehicle();

        Bukkit.getLogger().info("Spieler " + p.getName() + " hat ein Minecart bestiegen!");

        MinecartMember<?> cart = MinecartMemberStore.getFromEntity(minecart);
        if (cart != null) {
            Bukkit.getLogger().info("Spieler " + p.getName() + " hat ein TrainCart bestiegen!");

            String enterMessage = cart.getProperties().getEnterMessage();

            if (enterMessage == null) {
                TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));

                TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e-------------- &c&lCraftBahn &e--------------"));
                message.addExtra((BaseComponent) newLine);
                p.spigot().sendMessage((BaseComponent) message);

                message = new TextComponent();
                message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» &eGuten Tag, Reisender!")));
                message.addExtra((BaseComponent) newLine);
                message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» &eVerstehst du nur ")));
                message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c/bahnhof")));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bahnhof"));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eInformationen zum Schienennetz"))).create()));
                message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e?")));
                p.spigot().sendMessage((BaseComponent) message);

                if (cart.getProperties().getDestination().isBlank()) {
                    message = new TextComponent();
                    message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» ")));
                    message.addExtra((BaseComponent) newLine);
                    message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» &c&lHinweis:")));
                    message.addExtra((BaseComponent) newLine);
                    message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» &cDieser Zug hat noch kein Fahrziel.")));
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fahrziel list"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eVerfügbare Fahrziele anzeigen"))).create()));
                    p.spigot().sendMessage((BaseComponent) message);
                }
                else {
                    message = new TextComponent();
                    message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» ")));
                    message.addExtra((BaseComponent) newLine);
                    message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» &eDieser Zug versucht, das Ziel:")));
                    message.addExtra((BaseComponent) newLine);
                    message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CraftBahn &8» &f'&6&l" + cart.getProperties().getDestination() + "&f' &ezu erreichen.")));
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fahrziel list"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&eAnderes Fahrziel auswählen"))).create()));
                    p.spigot().sendMessage((BaseComponent) message);
                }

                message = new TextComponent((BaseComponent) newLine);
                message.addExtra((BaseComponent) new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e----------------------------------------")));
                message.addExtra((BaseComponent) newLine);
                p.spigot().sendMessage((BaseComponent) message);
            }
            else
                TrainCarts.sendMessage(p, enterMessage);
        }
    }
}
