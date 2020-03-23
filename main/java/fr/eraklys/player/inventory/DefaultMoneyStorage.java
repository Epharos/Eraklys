package fr.eraklys.player.inventory;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;

public class DefaultMoneyStorage implements Capability.IStorage<IMoney> 
{
	@Override
	public INBT writeNBT(Capability<IMoney> capability, IMoney instance, Direction side) 
	{
		return IntNBT.func_229692_a_(instance.getMoney());
	}

	public void readNBT(Capability<IMoney> capability, IMoney instance, Direction side, INBT nbt) 
	{
		try 
		{
			instance.setMoney(((IntNBT)nbt).getInt());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
