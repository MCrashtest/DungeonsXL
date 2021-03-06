/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import io.github.dre2n.dungeonsxl.world.DWorlds;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class EditCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DWorlds worlds = plugin.getDWorlds();

    public EditCommand() {
        setCommand("edit");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessages.HELP_CMD_EDIT.getMessage());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        String mapName = args[1];

        if (!worlds.exists(mapName)) {
            MessageUtil.sendMessage(player, DMessages.ERROR_DUNGEON_NOT_EXIST.getMessage(mapName));
            return;
        }

        DResourceWorld resource = worlds.getResourceByName(mapName);
        if (resource == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_MAP.getMessage(mapName));
            return;
        }

        DEditWorld editWorld = resource.instantiateAsEditWorld();
        DGroup dGroup = DGroup.getByPlayer(player);
        DGlobalPlayer dPlayer = plugin.getDPlayers().getByPlayer(player);

        if (!(resource.isInvitedPlayer(player) || DPermissions.hasPermission(player, DPermissions.EDIT))) {
            MessageUtil.sendMessage(player, DMessages.ERROR_NO_PERMISSIONS.getMessage());
            return;
        }

        if (dPlayer instanceof DInstancePlayer) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        if (dGroup != null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        DEditPlayer.create(player, editWorld);
    }

}
