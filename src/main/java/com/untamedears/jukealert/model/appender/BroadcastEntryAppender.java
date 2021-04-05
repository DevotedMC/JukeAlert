package com.untamedears.jukealert.model.appender;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.NameAPI;
import com.github.maxopoly.artemis.rabbit.outgoing.RabbitSendPlayerTextComponent;
import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.abstr.LoggablePlayerAction;
import com.untamedears.jukealert.model.actions.abstr.SnitchAction;
import com.untamedears.jukealert.model.appender.config.LimitedActionTriggerConfig;
import com.untamedears.jukealert.util.JASettingsManager;
import java.util.UUID;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class BroadcastEntryAppender extends ConfigurableSnitchAppender<LimitedActionTriggerConfig> {

	public static final String ID = "broadcast";

	public BroadcastEntryAppender(Snitch snitch, ConfigurationSection config) {
		super(snitch, config);
	}

	@Override
	public void acceptAction(SnitchAction action) {
		if (action.isLifeCycleEvent() || !action.hasPlayer()) {
			return;
		}
		LoggablePlayerAction log = (LoggablePlayerAction) action;
		if (snitch.hasPermission(log.getPlayer(), JukeAlert.getInstance().getPermissionHandler().getSnitchImmune())) {
			return;
		}
		if (!config.isTrigger(action.getIdentifier())) {
			return;
		}
		for (UUID uuid : snitch.getGroup().getAllMembers()) {
			JASettingsManager settings = JukeAlert.getInstance().getSettingsManager();
			if (settings.doesIgnoreAllAlerts(uuid)) {
				continue;
			}
			if (settings.doesIgnoreAlert(snitch.getGroup().getName(), uuid)) {
				continue;
			}
			if (snitch.hasPermission(uuid, JukeAlert.getInstance().getPermissionHandler().getSnitchAlerts())) {
				TextComponent comp = log.getChatRepresentation(snitch.getLocation(), true);
				if (settings.monocolorAlerts(uuid)) {
					String raw = comp.toPlainText();
					raw = ChatColor.stripColor(raw);
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSendPlayerTextComponent(
							NameAPI.CONSOLE_UUID, uuid, new TextComponent(ChatColor.AQUA + raw)));
				}
				else {
					ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RabbitSendPlayerTextComponent(
							NameAPI.CONSOLE_UUID, uuid, new TextComponent(comp)));
				}
			}
		}
	}

	@Override
	public boolean runWhenSnitchInactive() {
		return false;
	}

	@Override
	public Class<LimitedActionTriggerConfig> getConfigClass() {
		return LimitedActionTriggerConfig.class;
	}

}
