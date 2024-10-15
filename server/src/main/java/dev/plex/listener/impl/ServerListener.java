package dev.plex.listener.impl;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import dev.plex.listener.PlexListener;
import dev.plex.util.PlexUtils;
import dev.plex.util.RandomUtil;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class ServerListener extends PlexListener
{
    @EventHandler
    public void onServerPing(PaperServerListPingEvent event)
    {
        String baseMotd = plugin.config.getString("server.motd");
        baseMotd = baseMotd.replace("\\n", "\n");
        baseMotd = baseMotd.replace("%servername%", plugin.config.getString("server.name"));
        baseMotd = baseMotd.replace("%mcversion%", Bukkit.getBukkitVersion().split("-")[0]);
        if (plugin.config.getBoolean("server.colorize_motd"))
        {
            AtomicReference<Component> motd = new AtomicReference<>(Component.empty());
            for (final String word : baseMotd.split(" "))
            {
                motd.set(motd.get().append(Component.text(word).color(RandomUtil.getRandomColor())));
                motd.set(motd.get().append(Component.space()));
            }
            event.motd(motd.get());
        }
        else
        {
            event.motd(PlexUtils.mmDeserialize(baseMotd.trim()));
        }
        /* - Broken on 1.20.6
        if (plugin.config.contains("server.sample"))
        {
            List<String> samples = plugin.config.getStringList("server.sample");
            if (!samples.isEmpty())
            {
                event.getPlayerSample().clear();
                event.getPlayerSample().addAll(samples.stream().map(string -> string.replace("&", "§")).map(Bukkit::createProfile).toList());
            }
        }*/
    }
}
