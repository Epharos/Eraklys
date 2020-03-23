package fr.eraklys.player.inventory;

public interface IMoney 
{
	int getMoney();
	void setMoney(int value) throws Exception;
	
	default void addMoney(int value)
	{
		if(value < 0)
			value = -value;
		
		try 
		{
			this.setMoney(this.getMoney() + value);
			//TODO update client money
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	default void removeMoney(int value)
	{
		if(value > 0)
			value = -value;
		
		try 
		{
			this.setMoney(this.getMoney() - value);
			//TODO update client money
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
