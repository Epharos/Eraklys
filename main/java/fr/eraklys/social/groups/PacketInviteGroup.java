package fr.eraklys.social.groups;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketInviteGroup 
{
	public int playerID;
	
	public PacketInviteGroup(int id)
	{
		this.playerID = id;
	}
	
	public static void write(PacketInviteGroup packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.playerID);
	}
	
	public static PacketInviteGroup read(PacketBuffer buffer)
	{
		return new PacketInviteGroup(buffer.readInt());
	}
	
	public static void handle(PacketInviteGroup packet, Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() ->
		{
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
			{				
				
			});
			
			DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () ->
			{
				Entity entity = context.get().getSender().getServerWorld().getEntityByID(packet.playerID);
				
				if(entity instanceof ServerPlayerEntity)
				{
					ServerPlayerEntity player = (ServerPlayerEntity)entity;
					
					if(GroupSession.getPlayerGroup(player) != null)
					{
						context.get().getSender().sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.invite.error.ingroup", player.getName().getString())));
					}
					else
					{
						context.get().getSender().sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.invite.error.invited", player.getName().getString())));
						//TODO Créer l'invitation
					}
				}
				else
				{
					context.get().getSender().sendMessage(new TranslationTextComponent("group.label.chat").appendSibling(new TranslationTextComponent("group.invite.error.missing")));
				}
			});
		});
		
		context.get().setPacketHandled(true);
	}
}
