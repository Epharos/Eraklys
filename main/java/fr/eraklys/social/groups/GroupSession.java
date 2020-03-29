package fr.eraklys.social.groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import fr.eraklys.Eraklys;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

@OnlyIn(Dist.DEDICATED_SERVER)
public class GroupSession 
{
	public static Map<ServerPlayerEntity, GroupSession> groups = new HashMap<ServerPlayerEntity, GroupSession>();
	public GroupMembersList members;
	
	private GroupSession(final ServerPlayerEntity player)
	{
		this.members = new GroupMembersList(player, this);
	}
	
	public static GroupSession createGroup(final ServerPlayerEntity player)
	{
		return new GroupSession(player);
	}
	
	public void addPlayer(final ServerPlayerEntity player)
	{
		if(!this.members.addPlayerToGroup(player))
		{
			Eraklys.LOGGER.warn("Le groupe est plein, le joueur n'a pas pu �tre ajout�");
			return;
		}
		
		this.prompt(new TranslationTextComponent("group.join", player.getName().getString()));
	}
	
	public void removePlayer(final ServerPlayerEntity player)
	{
		if(!this.members.removePlayerFromGroup(player))
		{
			Eraklys.LOGGER.warn("Le joueur n'appartient pas � ce groupe, c'est pas normal d'avoir cette erreur ...");
			return;
		}
		
		this.prompt(new TranslationTextComponent("group.left", player.getName().getString()));
	}
	
	public static GroupSession getPlayerGroup(final ServerPlayerEntity player)
	{
		return groups.get(player);
	}
	
	public ServerPlayerEntity[] getMemberList()
	{
		return this.members.getMemberList();
	}
	
	public void prompt(@Nullable final ServerPlayerEntity playerSending, final ITextComponent text, @Nullable final ServerPlayerEntity playerReceiver)
	{
		if(playerReceiver != null)
		{
			playerReceiver.sendMessage(new TranslationTextComponent("group.label").appendSibling(text));
			return;
		}
		
		for(ServerPlayerEntity member : this.members.getMemberList())
		{
			if(member != null)
				member.sendMessage(new TranslationTextComponent("group.label.chat")
						.appendSibling(new StringTextComponent(playerSending != null ? " " + playerSending.getName().getFormattedText() + " : " : " "))
						.appendSibling(text));
		}
	}
	
	public void prompt(final ITextComponent text)
	{
		this.prompt(null, text, null);
	}
	
	private class GroupMembersList 
	{
		public static final int maxGroupSize = 6;
		private ServerPlayerEntity[] players = new ServerPlayerEntity[GroupMembersList.maxGroupSize];
		private GroupSession session;
		private ServerPlayerEntity owner;
		
		public GroupMembersList(ServerPlayerEntity player, GroupSession s)
		{
			this.session = s;
			this.setOwner(player);
			this.addPlayerToGroup(player);
		}
		
		public int memberCount()
		{
			int c = 0;
			
			for(int i = 0 ; i < maxGroupSize ; i++)
			{
				if(this.getMember(i) != null)
				{
					c++;
				}
			}
			
			return c;
		}
		
		public boolean addPlayerToGroup(ServerPlayerEntity player)
		{
			for(int i = 0 ; i < maxGroupSize ; i++)
				if(this.getMember(i) == null)
				{
					players[i] = player;
					GroupSession.groups.put(player, this.session);
					
					for(int k = 0 ; k < GroupMembersList.maxGroupSize ; k++)
					{
						if(this.getMember(k) != null)
						{
							final ServerPlayerEntity target = this.getMember(k);
							Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), new PacketUpdateGroup(player.getEntityId(), 0));
							
							if(target != player)
								Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketUpdateGroup(target.getEntityId(), 0));
						}
					}
					
					Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketUpdateGroup(this.getOwner().getEntityId(), 2));
					
					return true;
				}
			
			return false;
		}
		
		public boolean removePlayerFromGroup(ServerPlayerEntity player)
		{
			for(int i = 0 ; i < maxGroupSize ; i++)
				if(this.getMember(i) == player)
				{
					players[i] = null;
					GroupSession.groups.put(player, null);
					
					if(this.memberCount() < 2)
					{
						this.disbandGroup();
						return true;
					}
					
					if(player == this.getOwner())
					{
						this.newOwner();
					}
					
					for(int k = 0 ; k < GroupMembersList.maxGroupSize ; k++)
					{
						if(this.getMember(k) != null)
						{
							final ServerPlayerEntity target = this.getMember(k);
							Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), new PacketUpdateGroup(player.getEntityId(), 1));
							Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), new PacketUpdateGroup(this.getOwner().getEntityId(), 2));
							
							if(target != player)
								Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketUpdateGroup(target.getEntityId(), 1));
						}
					}
					
					return true;
				}
			
			return false;
		}
		
		public void disbandGroup()
		{
			for(int i = 0 ; i < maxGroupSize ; i++)
			{
				if(this.getMember(i) != null)
				{
					final ServerPlayerEntity target = this.getMember(i);
					GroupSession.groups.put(target, null);
					Eraklys.CHANNEL.send(PacketDistributor.PLAYER.with(() -> target), new PacketUpdateGroup(0, -1));
				}
			}
		}
		
		public void newOwner() 
		{
			for(int i = 0 ; i < maxGroupSize ; i++)
			{
				if(this.getMember(i) != null)
				{
					this.setOwner(this.getMember(i));
					return;
				}
			}
		}
		
		@SuppressWarnings("unused")
		public boolean isPlayerInGroup(ServerPlayerEntity player)
		{
			for(ServerPlayerEntity p : players)
				if(p == player)
					return true;
			
			return false;
		}
		
		public ServerPlayerEntity getMember(int i)
		{
			return this.players[i];
		}
		
		public ServerPlayerEntity[] getMemberList()
		{
			return this.players;
		}
		
		public void setOwner(ServerPlayerEntity player)
		{
			this.owner = player;
		}
		
		public ServerPlayerEntity getOwner()
		{
			return this.owner;
		}
	}
	
	public static class PendingRequest
	{
		public ServerPlayerEntity sender, receiver;
		public int timeOut;
		
		public static List<PendingRequest> pendings = new ArrayList<PendingRequest>();
		
		public PendingRequest(ServerPlayerEntity s, ServerPlayerEntity r)
		{
			this.sender = s;
			this.receiver = r;
			this.timeOut = 1200; //30 secondes
			
			for(Iterator<PendingRequest> it = PendingRequest.pendings.iterator() ; it.hasNext() ;)
			{
				PendingRequest pr = it.next();
				
				if(((pr.sender == s || pr.sender == r) && (pr.receiver == s || pr.receiver == r)) || r == s)
					return;
			}
			
			PendingRequest.pendings.add(this);
		}
		
		public void timeIsRunningOut()
		{
			this.timeOut--;
		}
		
		public static void checkPendings()
		{
			for(Iterator<PendingRequest> it = PendingRequest.pendings.iterator() ; it.hasNext() ;)
			{
				PendingRequest pr = it.next();
				pr.timeIsRunningOut();
				
				if(pr.timeOut <= 0)
				{
					pr.receiver.sendMessage(new TranslationTextComponent("group.invite.expired.receiver", pr.sender.getName().getString()));
					pr.sender.sendMessage(new TranslationTextComponent("group.invite.expired.sender", pr.receiver.getName().getString()));
					it.remove();
				}
			}
		}
		
		public static void acceptTrade(ServerPlayerEntity player, ServerPlayerEntity send)
		{
			for(Iterator<PendingRequest> it = PendingRequest.pendings.iterator() ; it.hasNext() ;)
			{
				PendingRequest pr = it.next();
				
				if(pr.sender == player)
				{
					pr.receiver.sendMessage(new TranslationTextComponent("group.invite.expired.receiver", pr.sender.getName().getString()));
					it.remove();
				}
				
				if(pr.receiver == player)
				{
					if(pr.sender == send)
					{
						GroupSession.getPlayerGroup(send).addPlayer(player);
						it.remove();
					}
					else
					{
						pr.sender.sendMessage(new TranslationTextComponent("group.invite.expired.sender", pr.receiver.getName().getString()));
						it.remove();
					}
				}
			}
		}
		
		public static void refuseTrade(ServerPlayerEntity player, ServerPlayerEntity send)
		{
			for(Iterator<PendingRequest> it = PendingRequest.pendings.iterator() ; it.hasNext() ;)
			{
				PendingRequest pr = it.next();
				
				if(pr.receiver == player)
				{
					if(pr.sender == send)
					{
						pr.sender.sendMessage(new TranslationTextComponent("group.invite.refused.sender", pr.receiver.getName().getString()));
						pr.receiver.sendMessage(new TranslationTextComponent("group.invite.refused.sender", pr.sender.getName().getString()));
						it.remove();
						return;
					}
				}
			}
		}
		
		public static void destroyPendingsFor(ServerPlayerEntity player)
		{
			for(Iterator<PendingRequest> it = PendingRequest.pendings.iterator() ; it.hasNext() ;)
			{
				PendingRequest pr = it.next();
				
				if(pr.sender == player)
				{
					pr.receiver.sendMessage(new TranslationTextComponent("group.invite.expired.receiver", pr.sender.getName().getString()));
					it.remove();
				}
				
				if(pr.receiver == player)
				{
					pr.sender.sendMessage(new TranslationTextComponent("group.invite.expired.sender", pr.receiver.getName().getString()));
					it.remove();
				}
			}
		}
	}
}
