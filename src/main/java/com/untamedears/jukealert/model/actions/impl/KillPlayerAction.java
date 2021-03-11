package com.untamedears.jukealert.model.actions.impl;

import com.github.maxopoly.artemis.NameAPI;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.abstr.LoggablePlayerVictimAction;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import vg.civcraft.mc.civmodcore.inventory.items.ItemUtils;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

public class KillPlayerAction extends LoggablePlayerVictimAction {
	
	public static final String ID = "KILL_PLAYER";

	public KillPlayerAction(long time, Snitch snitch, UUID player, Location location, UUID victim) {
		super(time, snitch, player, location, victim.toString());
	}
	
	public String getVictimName() {
		return NameAPI.getNameLocal(getVictimUUID());
	}
	
	public UUID getVictimUUID() {
		return UUID.fromString(victim);
	}

	@Override
	public IClickable getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
		ItemUtils.addGlow(is);
		super.enrichGUIItem(is);
		return new DecorationStack(is);
	}
	
	@Override
	protected String getChatRepresentationIdentifier() {
		return "Killed " + getVictim();
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
