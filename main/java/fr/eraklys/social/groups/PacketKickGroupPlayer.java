package fr.eraklys.social.groups;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketKickGroupPlayer 
{
	public int playerID;
	
	public PacketKickGroupPlayer(int id)
	{
		this.playerID = id;
	}
	
	public static void write(PacketKickGroupPlayer packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.playerID);
	}
	
	public static PacketKickGroupPlayer read(PacketBuffer buffer)
	{
		return new PacketKickGroupPlayer(buffer.readInt());
	}
	
	public static void handle(PacketKickGroupPlayer packet, Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() ->
		{
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () ->
			{		
				GroupSession group = GroupSession.getPlayerGroup(context.get().getSender());
				
				if(group != null)
				{
					if(group.isOwner(context.get().getSender()))
					{
						Entity e = context.get().getSender().getServerWorld().getEntityByID(packet.playerID);
						
						if(e instanceof ServerPlayerEntity)
						{
							ServerPlayerEntity player = (ServerPlayerEntity)e;
							
							if(GroupSession.getPlayerGroup(player) == group)
							{
								group.removePlayer(player);
								group.prompt(new TranslationTextComponent("group.kick", player.getName().getString()));
								player.sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.kicked")));
							}
						}
					}
				}
			});
		});
		
		context.get().setPacketHandled(true);
	}
}
