package fr.eraklys.screen;

import fr.eraklys.Eraklys;
import fr.eraklys.screen.entry.FriendInviteEntry;
import fr.eraklys.screen.entry.GroupInviteEntry;
import fr.eraklys.screen.entry.GroupOwnerEntry;
import fr.eraklys.screen.entry.GuildInviteEntry;
import fr.eraklys.screen.entry.KickPlayerGroupEntry;
import fr.eraklys.screen.entry.PrivateMessageEntry;
import fr.eraklys.screen.entry.QuitGroupEntry;
import fr.eraklys.screen.entry.SeparatorEntry;
import fr.eraklys.social.groups.ClientGroup;
import fr.eraklys.social.groups.PacketKickGroupPlayer;
import fr.eraklys.social.groups.PacketUpdateGroup;
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
		
		if(this.target != Minecraft.getInstance().player)
		{
			this.addEntry(new PrivateMessageEntry(this, 
					ent -> { 
						Minecraft.getInstance().displayGuiScreen(new ChatScreen("/mp " + target.getName().getString() + " "));
					}));
			
			this.addEntry(new SeparatorEntry(this));
			
			if(ClientGroup.isInGroup(Minecraft.getInstance().player) && ClientGroup.isInGroup(target))
			{
				if(ClientGroup.getOwnerID() == Minecraft.getInstance().player.getEntityId())
				{
					this.addEntry(new KickPlayerGroupEntry(this, 
							ent -> { 
								Eraklys.CHANNEL.sendToServer(new PacketKickGroupPlayer(target.getEntityId()));
							}));
					this.addEntry(new GroupOwnerEntry(this,
							ent -> {
								Eraklys.CHANNEL.sendToServer(new PacketUpdateGroup(target.getEntityId(), 10));
							}));
					this.addEntry(new SeparatorEntry(this));
				}
			}
			
			if(!ClientGroup.isInGroup(e) && ClientGroup.groupSize() < 6)
				this.addEntry(new GroupInviteEntry(this, ent ->  ClientPlayerUtil.groupInvite(target.getEntityId())));
			this.addEntry(new GuildInviteEntry(this, ent -> {}));
			this.addEntry(new SeparatorEntry(this));
			
			this.addEntry(new FriendInviteEntry(this, ent -> {}));
		}
		else
		{
			if(ClientGroup.isInGroup(Minecraft.getInstance().player))
			{
				this.addEntry(new QuitGroupEntry(this, 
						ent -> {
							Eraklys.CHANNEL.sendToServer(new PacketUpdateGroup(target.getEntityId(), 1));
						}));
			}
		}
	}
	
	public PlayerMenu setTarget(AbstractClientPlayerEntity e)
	{
		this.target = e;
		return this;
	}
}
