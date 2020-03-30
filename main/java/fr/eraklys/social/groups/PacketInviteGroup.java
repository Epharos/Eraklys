package fr.eraklys.social.groups;

import java.util.function.Supplier;

import fr.eraklys.Eraklys;
import fr.eraklys.social.notifications.GroupNotification;
import fr.eraklys.util.ClientPlayerUtil;
import fr.eraklys.util.ServerPlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketInviteGroup 
{
	public int playerID;
	public boolean action;
	
	public PacketInviteGroup(int id)
	{
		this(id, true);
	}
	
	public PacketInviteGroup(int id, boolean act)
	{
		this.playerID = id;
		this.action = act;
	}
	
	public static void write(PacketInviteGroup packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.playerID);
		buffer.writeBoolean(packet.action);
	}
	
	public static PacketInviteGroup read(PacketBuffer buffer)
	{
		return new PacketInviteGroup(buffer.readInt(), buffer.readBoolean());
	}
	
	public static void handle(PacketInviteGroup packet, Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() ->
		{
			if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT)
			{
				handleClient(packet, context.get());
			}
			else
			{
				handleServer(packet, context.get());
			}
		});
		
		context.get().setPacketHandled(true);
	}
	
	@OnlyIn(Dist.CLIENT)
	private static void handleClient(PacketInviteGroup packet, NetworkEvent.Context context)
	{
		if(packet.action)
		{
			Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("notif.group.invite", Minecraft.getInstance().world.getEntityByID(packet.playerID).getName().getString()));
			ClientPlayerUtil.addNotification(new GroupNotification(packet.playerID));
		}
		else
		{
			ClientPlayerUtil.deleteGroupNotification(packet.playerID);
		}
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	private static void handleServer(PacketInviteGroup packet, NetworkEvent.Context context)
	{
		Entity entity = context.getSender().getServerWorld().getEntityByID(packet.playerID);
		
		if(entity instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity)entity;
			
			if(GroupSession.getPlayerGroup(player) != null)
			{
				context.getSender().sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.invite.error.ingroup", player.getName().getString())));
			}
			else
			{
				if(GroupSession.getPlayerGroup(context.getSender()) == null)
					context.getSender().sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.invite.invited", player.getName().getString())));
				else
					GroupSession.getPlayerGroup(context.getSender()).prompt(new TranslationTextComponent("group.invite.invited.by", player.getName().getString(), context.getSender().getName().getString()));
				
				Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketInviteGroup(context.getSender().getEntityId()));
				ServerPlayerUtil.groupInvite(context.getSender(), player);
			}
		}
		else
		{
			context.getSender().sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.invite.error.missing")));
		}
	}
}
