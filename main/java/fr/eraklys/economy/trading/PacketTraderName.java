package fr.eraklys.economy.trading;

import java.util.function.Supplier;

import fr.eraklys.Eraklys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketTraderName 
{
	public int playerID;
	
	public PacketTraderName(int i)
	{
		playerID = i;
	}
	
	public static void write(PacketTraderName packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.playerID);
	}
	
	public static PacketTraderName read(PacketBuffer buffer)
	{
		return new PacketTraderName(buffer.readInt());
	}
	
	public static void handle(PacketTraderName packet, Supplier<NetworkEvent.Context> context)
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
	private static void handleClient(PacketTraderName packet, NetworkEvent.Context context)
	{
		if(Minecraft.getInstance().currentScreen instanceof ScreenTrading)
		{
			((ScreenTrading)Minecraft.getInstance().currentScreen).traderHead = ((AbstractClientPlayerEntity)Minecraft.getInstance().world.getEntityByID(packet.playerID)).getLocationSkin();
		}
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	private static void handleServer(PacketTraderName packet, NetworkEvent.Context context)
	{
		TradeSession playerSession = TradeSession.registeredSessions.get(context.getSender());
		Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> context.getSender()),
				new PacketTraderName(playerSession.trader != context.getSender() ?
						playerSession.trader.getEntityId() : playerSession.player.getEntityId()));
	}
}
