package fr.eraklys.social.groups;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketUpdateGroup
{
	public int playerID;
	public int action;
	
	public PacketUpdateGroup(int id, int act)
	{
		this.playerID = id;
		this.action = act;
	}
	
	public static void write(PacketUpdateGroup packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.playerID);
		buffer.writeInt(packet.action);
	}
	
	public static PacketUpdateGroup read(PacketBuffer buffer)
	{
		return new PacketUpdateGroup(buffer.readInt(), buffer.readInt());
	}
	
	public static void handle(PacketUpdateGroup packet, Supplier<NetworkEvent.Context> context)
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
	private static void handleClient(PacketUpdateGroup packet, NetworkEvent.Context context)
	{
		if(packet.action == -1)
		{
			ClientGroup.clearClientGroup();
		}

		if(packet.action == 0)
		{
			ClientGroup.addMember(packet.playerID);
		}
		
		if(packet.action == 1)
		{
			ClientGroup.removeMember(packet.playerID);
		}
		
		if(packet.action == 2)
		{
			ClientGroup.setOwnerID(packet.playerID);
		}
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	private static void handleServer(PacketUpdateGroup packet, NetworkEvent.Context context)
	{
		GroupSession group = GroupSession.getPlayerGroup(context.getSender());
		
		if(packet.action == 1)
		{
			if(group != null)
			{
				group.removePlayer(context.getSender());
			}
		}
		
		if(packet.action == 10)
		{					
			if(group != null)
			{
				Entity e = context.getSender().getServerWorld().getEntityByID(packet.playerID);
				
				if(e instanceof ServerPlayerEntity)
				{
					ServerPlayerEntity player = (ServerPlayerEntity)e;
					
					if(GroupSession.getPlayerGroup(player) == group)
					{
						group.setOwner(player);
					}
				}
			}
		}
	}
}
