package com.untamedears.jukealert.model.appender;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.abstr.SnitchAction;
import com.untamedears.jukealert.model.actions.internal.DestroySnitchAction;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.NameAPI;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.abstr.SnitchAction;
import com.untamedears.jukealert.model.actions.internal.DestroySnitchAction;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class ShowOwnerOnDestroyAppender extends AbstractSnitchAppender {

	public static final String ID = "showownerondestroy";

	public ShowOwnerOnDestroyAppender(Snitch snitch) {
		super(snitch);
	}

	@Override
	public boolean runWhenSnitchInactive() {
		return true;
	}

	@Override
	public void acceptAction(SnitchAction action) {
		if (!action.isLifeCycleEvent()) {
			return;
		}
		if (!(action instanceof DestroySnitchAction)) {
			return;
		}
		DestroySnitchAction dsa = ((DestroySnitchAction) action);
		UUID destroyerUUID = dsa.getPlayer();
		Player player = Bukkit.getPlayer(destroyerUUID);
		if (player == null) {
			return;
		}
		Group group = snitch.getGroup();
		if (group == null) {
			sendMessage(player, "unknown", "unknown");
		} else {
			GroupRank ownerRank = group.getGroupRankHandler().getOwnerRank();
			UUID owner = group.getAllTrackedByType(ownerRank).iterator().next(); //guaranteed not empty
			NameAPI.consumeNameSync(owner, name -> sendMessage(player, group.getName(), name));
		}
	}
	
	private void sendMessage(Player player, String groupName, String ownerName) {
		player.sendMessage(String.format("%s%s %swas reinforced on %s%s%s owned by %s%s", ChatColor.GOLD,
				snitch.getType().getName(), ChatColor.YELLOW, ChatColor.GREEN, groupName, ChatColor.YELLOW,
				ChatColor.LIGHT_PURPLE, ownerName));
	}

}
