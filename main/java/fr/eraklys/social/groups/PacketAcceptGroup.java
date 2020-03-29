package fr.eraklys.social.groups;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketAcceptGroup
{
	public int playerID;
	public boolean acceptance;
	
	public PacketAcceptGroup(int i, boolean b)
	{
		this.playerID = i;
		this.acceptance = b;
	}
	
	public static void write(PacketAcceptGroup packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.playerID);
		buffer.writeBoolean(packet.acceptance);
	}
	
	public static PacketAcceptGroup read(PacketBuffer buffer)
	{
		return new PacketAcceptGroup(buffer.readInt(), buffer.readBoolean());
	}
	
	public static void handle(PacketAcceptGroup packet, Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() ->
		{
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () ->
			{				
				if(packet.acceptance)
				{
					if(context.get().getSender().getServerWorld().getEntityByID(packet.playerID) instanceof ServerPlayerEntity)
						GroupSession.PendingRequest.acceptTrade(context.get().getSender(), (ServerPlayerEntity)context.get().getSender().getServerWorld().getEntityByID(packet.playerID));
					else
						context.get().getSender().sendMessage(new TranslationTextComponent("error.badentityid"));
				}
				else
				{
					if(context.get().getSender().getServerWorld().getEntityByID(packet.playerID) instanceof ServerPlayerEntity)
						GroupSession.PendingRequest.refuseTrade(context.get().getSender(), (ServerPlayerEntity)context.get().getSender().getServerWorld().getEntityByID(packet.playerID));
					else
						context.get().getSender().sendMessage(new TranslationTextComponent("error.badentityid"));
				}
			});
		});
		
		context.get().setPacketHandled(true);
	}
}
