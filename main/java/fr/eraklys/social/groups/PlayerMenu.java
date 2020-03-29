package fr.eraklys.social.groups;

import fr.eraklys.screen.Menu;
import fr.eraklys.screen.entry.FriendInviteEntry;
import fr.eraklys.screen.entry.GroupInviteEntry;
import fr.eraklys.screen.entry.GuildInviteEntry;
import fr.eraklys.screen.entry.PrivateMessageEntry;
import fr.eraklys.screen.entry.SeparatorEntry;
import fr.eraklys.util.ClientPlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;

public class PlayerMenu extends Menu 
{
	public AbstractClientPlayerEntity target;
	
	public PlayerMenu(AbstractClientPlayerEntity e)
	{
		this.setTarget(e);
		this.addEntry(new PrivateMessageEntry(this, ent -> Minecraft.getInstance().displayGuiScreen(new ChatScreen("/mp " + target.getName().getString() + " "))));
		this.addEntry(new SeparatorEntry(this));
		if(!ClientGroup.isInGroup(e))
			this.addEntry(new GroupInviteEntry(this, ent ->  ClientPlayerUtil.groupInvite(target.getEntityId())));
		this.addEntry(new GuildInviteEntry(this, ent -> {}));
		this.addEntry(new SeparatorEntry(this));
		this.addEntry(new FriendInviteEntry(this, ent -> {}));
	}
	
	public PlayerMenu setTarget(AbstractClientPlayerEntity e)
	{
		this.target = e;
		return this;
	}
}
