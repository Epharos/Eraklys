package fr.eraklys.social.notifications;

import java.util.List;

import fr.eraklys.screen.ClickableText;
import fr.eraklys.util.ClientPlayerUtil;
import fr.eraklys.util.FontRendererStringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;

public class GroupNotification extends Notification 
{
	final int senderID;
	
	public GroupNotification(int id) 
	{
		super(0, 0, Notification.NOTIF_WIDTH, 0, "");
		this.senderID = id;
		List<String> notifText = FontRendererStringUtil.splitStringMultiline(this.getWidth() - 20, this.getMessage());
		this.setHeight(notifText.size() * 9 + 17);
		this.setMessage(I18n.format("notif.group.invite", Minecraft.getInstance().world.getEntityByID(senderID).getName().getString()));
		this.addWidget(new ClickableText(4, this.getHeight() - 11, I18n.format("ui.accept"), ct -> ClientPlayerUtil.acceptTrade(this.senderID, true)));
		this.addWidget(new ClickableText(this.getWidth() / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(I18n.format("ui.refuse")) / 2, this.getHeight() - 11, I18n.format("ui.refuse"), ct -> ClientPlayerUtil.acceptTrade(this.senderID, false)));
		this.addWidget(new ClickableText(this.getWidth() - 3 - Minecraft.getInstance().fontRenderer.getStringWidth(I18n.format("ui.ignore")), this.getHeight() - 11, I18n.format("ui.ignore"), ct -> {}/*ClientPlayerUtil.ignorePlayer(this.senderID)*/));
	}

	public Widget defaultAction() 
	{
		return this.getWidgets().get(1);
	}
}
