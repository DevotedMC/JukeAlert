package com.untamedears.jukealert.commands;

import com.google.common.base.Strings;
import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.gui.SnitchOverviewGUI;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.SnitchTypeManager;
import com.untamedears.jukealert.model.appender.DormantCullingAppender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.commands.NameLayerTabCompletion;

@CivCommand(id = "jalist")
public class ListCommand extends StandaloneCommand {

	@Override
	public boolean execute(final CommandSender sender, final String[] arguments) {
		final Player player = (Player) sender;
		boolean playerProvidedGroups = true;
		List<String> groupNames = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(arguments)) {
			groupNames = Arrays.asList(arguments);
			groupNames.removeIf(Strings::isNullOrEmpty);
		}
		if (CollectionUtils.isEmpty(groupNames)) {
			Set<Group> groups = NameLayerPlugin.getInstance().getGroupTracker().getGroupsForPlayer(player.getUniqueId());
			if (groups.isEmpty()) {
				player.sendMessage(ChatColor.RED + "You are not a member of any groups");
				return true;
			}
			for (Group group : groups) {
				groupNames.add(group.getName());
			}
			playerProvidedGroups = false;
		}
		final var groupIds = new ArrayList<Integer>();
		for (final String groupName : groupNames) {
			final Group group = GroupAPI.getGroup(groupName);
			if (group == null) {
				if (playerProvidedGroups) {
					sender.sendMessage(ChatColor.RED + "The group " + groupName + " does not exist");
				}
				continue;
			}
			if (!GroupAPI.hasPermission(player.getUniqueId(), group,
					JukeAlert.getInstance().getPermissionHandler().getListSnitches())) {
				if (playerProvidedGroups) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to list snitches "
							+ "for the group " + group.getName());
				}
				continue;
			}
			groupIds.add(group.getPrimaryId());
		}
		if (groupIds.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + "You do not have access to any group's snitches.");
			return true;
		}
		sender.sendMessage(ChatColor.GREEN + "Retrieving snitches for a total of " + groupNames.size()
				+ " group instances. This may take a moment.");
		new BukkitRunnable() {
			@Override
			public void run() {
				final List<Snitch> snitches = JukeAlert.getInstance().getDAO().loadSnitchesByGroupID(groupIds);
				snitches.removeIf(snitch -> !snitch.hasAppender(DormantCullingAppender.class));
				snitches.sort((lhs, rhs) -> {
					/** These should be present, if not look at {@link SnitchTypeManager#registerAppenderTypes()}! */
					final var thisAppender = lhs.getAppender(DormantCullingAppender.class);
					final var thatAppender = rhs.getAppender(DormantCullingAppender.class);
					// Since the time decreases the closer a snitch gets to culling, the values are flipped
					return Long.compare(
							thisAppender.getTimeUntilCulling(),
							thatAppender.getTimeUntilCulling());
				});
				new BukkitRunnable() {
					@Override
					public void run() {
						new SnitchOverviewGUI(player, snitches, "Your snitches",
								player.hasPermission("jukealert.admin")).showScreen();
					}
				}.runTask(JukeAlert.getInstance());
			}
		}.runTaskAsynchronously(JukeAlert.getInstance());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		String last;
		if (args.length == 0) {
			last = "";
		} else {
			last = args[args.length - 1];
		}
		return NameLayerTabCompletion.completeGroupName(last, (Player) sender);
	}

}
