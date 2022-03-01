package dev.plex.command.impl;

import dev.plex.cache.DataUtils;
import dev.plex.cache.PlayerCache;
import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.command.exception.PlayerNotFoundException;
import dev.plex.command.source.RequiredCommandSource;
import dev.plex.player.PlexPlayer;
import dev.plex.player.PunishedPlayer;
import dev.plex.punishment.Punishment;
import dev.plex.punishment.PunishmentType;
import dev.plex.rank.enums.Rank;
import dev.plex.util.PlexUtils;
import java.time.LocalDateTime;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandParameters(name = "kick", description = "Kicks a player", usage = "/<command> <player>")
@CommandPermissions(level = Rank.ADMIN, permission = "plex.kick", source = RequiredCommandSource.ANY)
public class KickCMD extends PlexCommand
{
    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, String[] args)
    {
        if (args.length == 0)
        {
            return usage();
        }

        UUID targetUUID = PlexUtils.getFromName(args[0]);
        String reason = "No reason provided";

        if (targetUUID == null || !DataUtils.hasPlayedBefore(targetUUID))
        {
            throw new PlayerNotFoundException();
        }
        PlexPlayer plexPlayer = DataUtils.getPlayer(targetUUID);
        Player player = Bukkit.getPlayer(targetUUID);

        if (player == null)
        {
            throw new PlayerNotFoundException();
        }

        PunishedPlayer punishedPlayer = PlayerCache.getPunishedPlayer(targetUUID) == null ? new PunishedPlayer(targetUUID) : PlayerCache.getPunishedPlayer(targetUUID);
        Punishment punishment = new Punishment(targetUUID, getUUID(sender));
        punishment.setType(PunishmentType.KICK);
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
            punishment.setReason(reason);
        }

        punishment.setPunishedUsername(plexPlayer.getName());
        punishment.setEndDate(LocalDateTime.now());
        punishment.setCustomTime(false);
        punishment.setActive(false);
        plugin.getPunishmentManager().doPunishment(punishedPlayer, punishment);
        PlexUtils.broadcast(messageComponent("kickedPlayer", sender.getName(), plexPlayer.getName()));
        player.kick(componentFromString(reason));
        return null;
    }
}