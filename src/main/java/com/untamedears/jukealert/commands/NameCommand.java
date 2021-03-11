package com.untamedears.jukealert.commands;

import static com.untamedears.jukealert.util.JAUtility.findLookingAtOrClosestSnitch;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.util.JAUtility;
import com.untamedears.jukealert.util.JukeAlertPermissionHandler;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.util.JAUtility;
import com.untamedears.jukealert.util.JukeAlertPermissionHandler;

import net.md_5.bungee.api.chat.TextComponent;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.PermissionType;

@CivCommand(id = "janame")
public class NameCommand extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		String name = "";
		if (args[0].length() > 40) {
			name = args[0].substring(0, 40);
		} else {
			name = args[0];
		}
		PermissionType renamePerm = JukeAlert.getInstance().getPermissionHandler().getRenameSnitch();
		Snitch snitch = findLookingAtOrClosestSnitch(player, renamePerm);
		if (snitch == null) {
			player.sendMessage(
					ChatColor.RED + "You do not own any snitches nearby or lack permission to view their logs!");
			return true;
		}
		String prevName = snitch.getName();
		snitch.setName(name);
		TextComponent lineText = new TextComponent(ChatColor.AQUA + " Changed snitch name to ");
		lineText.addExtra(JAUtility.genTextComponent(snitch));
		lineText.addExtra(ChatColor.AQUA + " from " + ChatColor.GOLD + prevName);
		player.spigot().sendMessage(lineText);
		return true;

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new LinkedList<>();
	}
}
