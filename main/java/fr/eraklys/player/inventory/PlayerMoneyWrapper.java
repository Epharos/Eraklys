package fr.eraklys.player.inventory;

import fr.eraklys.Eraklys;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerMoneyWrapper implements ICapabilitySerializable<INBT> 
{
	private IMoney holder = Eraklys.MONEY_CAPABILITY.getDefaultInstance();
	private final LazyOptional<IMoney> lazyOptional = LazyOptional.of(() -> this.holder);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) 
	{
		return Eraklys.MONEY_CAPABILITY.orEmpty(cap, this.lazyOptional);
	}

	@Override
	public INBT serializeNBT() 
	{
		return Eraklys.MONEY_CAPABILITY.writeNBT(this.holder, null);
	}

	@Override
	public void deserializeNBT(INBT nbt) 
	{
		Eraklys.MONEY_CAPABILITY.readNBT(this.holder, null, nbt);
	}
}
