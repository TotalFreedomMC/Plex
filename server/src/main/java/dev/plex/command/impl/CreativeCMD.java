package dev.plex.command.impl;

import com.google.common.collect.ImmutableList;
import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.command.exception.CommandFailException;
import dev.plex.command.source.RequiredCommandSource;
import dev.plex.event.GameModeUpdateEvent;
import dev.plex.util.PlexUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandPermissions(permission = "plex.gamemode.creative", source = RequiredCommandSource.ANY)
@CommandParameters(name = "creative", aliases = "gmc,egmc,ecreative,eecreative,creativemode,ecreativemode", description = "Set your own or another player's gamemode to creative mode")
public class CreativeCMD extends PlexCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, String[] args)
    {
        if (args.length == 0)
        {
            if (isConsole(sender))
            {
                throw new CommandFailException(messageString("consoleMustDefinePlayer"));
            }
            if (!(playerSender == null))
            {
                Bukkit.getServer().getPluginManager().callEvent(new GameModeUpdateEvent(sender, playerSender.getPlayer(), GameMode.CREATIVE));
            }
            return null;
        }

        checkPermission(sender, "plex.gamemode.creative.others");
        if (args[0].equals("-a"))
        {
            for (Player targetPlayer : Bukkit.getServer().getOnlinePlayers())
            {
                targetPlayer.setGameMode(GameMode.CREATIVE);
                messageComponent("gameModeSetTo", "creative");
            }
            PlexUtils.broadcast(messageComponent("setEveryoneGameMode", sender.getName(), "creative"));
            return null;
        }

        Player nPlayer = getNonNullPlayer(args[0]);
        Bukkit.getServer().getPluginManager().callEvent(new GameModeUpdateEvent(sender, nPlayer, GameMode.CREATIVE));
        return null;
    }

    @Override
    public @NotNull List<String> smartTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        if (silentCheckPermission(sender, "plex.gamemode.creative.others"))
        {
            return PlexUtils.getPlayerNameList();
        }
        return ImmutableList.of();
    }
}
