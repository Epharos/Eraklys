package fr.eraklys.social.groups;

import fr.eraklys.util.ClientPlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientGroup 
{
	private static AbstractClientPlayerEntity[] otherMembersID = new AbstractClientPlayerEntity[6];
	private static int ownerID;
	
	public static int groupSize()
	{
		int i = 0;
		
		for(AbstractClientPlayerEntity e : ClientGroup.otherMembersID)
			if(e != null)
				i++;
		
		return i;
	}
	
	public static AbstractClientPlayerEntity getMember(int i)
	{
		return ClientGroup.otherMembersID[i];
	}
	
	public static void clearClientGroup()
	{
		for(int i = 0 ; i < ClientGroup.otherMembersID.length ; i++)
			ClientGroup.otherMembersID[i] = null;
		ClientPlayerUtil.rebootGroupScreen();
	}
	
	public static void addMember(int id)
	{
		for(int i = 0 ; i < ClientGroup.otherMembersID.length ; i++)
			if(ClientGroup.otherMembersID[i] == null)
			{
				ClientGroup.otherMembersID[i] = (AbstractClientPlayerEntity) Minecraft.getInstance().player.getEntityWorld().getEntityByID(id);
				ClientPlayerUtil.rebootGroupScreen();
				return;
			}
	}
	
	public static void removeMember(int id)
	{
		for(int i = 0 ; i < ClientGroup.otherMembersID.length ; i++)
			if(ClientGroup.otherMembersID[i].getEntityId() == id)
			{
				ClientGroup.otherMembersID[i] = null;
				ClientPlayerUtil.rebootGroupScreen();
				return;
			}
	}
	
	public static void setOwnerID(int i)
	{
		ClientGroup.ownerID = i;
		ClientPlayerUtil.rebootGroupScreen();
	}
	
	public static int getOwnerID()
	{
		return ClientGroup.ownerID;
	}
	
	public static boolean isInGroup(AbstractClientPlayerEntity ent)
	{
		for(AbstractClientPlayerEntity e : ClientGroup.otherMembersID)
		{
			if(e == ent)
			{
				return true;
			}
		}
		
		return false;
	}
}
