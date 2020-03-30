package fr.eraklys.economy.trading;

import java.util.function.Supplier;

import fr.eraklys.Eraklys;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketUpdateTradingInventory 
{
	public int action;
	
	public PacketUpdateTradingInventory(int a)
	{
		action = a;
	}
	
	public static void write(PacketUpdateTradingInventory packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.action);
	}
	
	public static PacketUpdateTradingInventory read(PacketBuffer buffer)
	{
		return new PacketUpdateTradingInventory(buffer.readInt());
	}
	
	public static void handle(PacketUpdateTradingInventory packet, Supplier<NetworkEvent.Context> context)
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
	private static void handleClient(PacketUpdateTradingInventory packet, NetworkEvent.Context context)
	{
		switch(packet.action)
		{
		case 0 :
			if(Minecraft.getInstance().currentScreen instanceof ScreenTrading)
			{
				((ScreenTrading)Minecraft.getInstance().currentScreen).toggleTraderAcceptation();
			}
			break;
		case 1 :
			if(Minecraft.getInstance().currentScreen instanceof ScreenTrading)
			{
				((ScreenTrading)Minecraft.getInstance().currentScreen).toggleTraderAcceptation(false);
				((ScreenTrading)Minecraft.getInstance().currentScreen).togglePlayerAcceptation(false);
			}
			break;
		case 2 :
			Eraklys.CHANNEL.sendToServer(packet);
			break;
		}
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	private static void handleServer(PacketUpdateTradingInventory packet, NetworkEvent.Context context)
	{
		switch(packet.action)
		{
		case 0:
			TradeSession.toggleAcceptationFor(context.getSender());
			break;
		case 1:
			TradeSession.toggleBothAcceptations(context.getSender());
			break;
		case 2:
			TradeSession.destroySession(context.getSender());
			break;				
		}
	}
}
