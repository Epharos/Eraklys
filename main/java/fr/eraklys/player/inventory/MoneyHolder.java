package fr.eraklys.player.inventory;

public class MoneyHolder implements IMoney 
{
	private int money = 0;

	@Override
	public int getMoney() 
	{
		return money;
	}

	@Override
	public void setMoney(int value) throws Exception 
	{
		if(value < 0)
			throw new Exception("Money can't be negative");
		
		money = value;
	}
}
