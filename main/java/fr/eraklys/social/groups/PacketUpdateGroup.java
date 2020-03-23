package fr.eraklys.social.groups;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
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
			});
		});
		
		context.get().setPacketHandled(true);
	}
}
