package fr.eraklys.social.notifications;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import fr.eraklys.screen.SimpleScrollBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenNotification extends Screen 
{
	public static List<Notification> notifications = new ArrayList<Notification>();
	private int theoricalHeight;
	private double factor;
	
	private int guiTop, guiLeft, h;
	private SimpleScrollBar scrollBar;
	
	public ScreenNotification() 
	{
		super(new TranslationTextComponent("notif.label"));
	}
	
	protected void init()
	{
		ScreenNotification.addNotification(new GroupNotification(Minecraft.getInstance().player.getEntityId()));
		super.init();
		this.guiLeft = this.width - Notification.NOTIF_WIDTH - 20;
		this.guiTop = this.height / 2;
		this.h = this.height / 2 - 20;
		
		int th = 0;
		
		for(Notification n : notifications)
		{
			this.addButton(n);
			th += n.getHeight();
		}
		
		this.setTheoricalHeight(th);
		
		this.factor = (double)(this.h) / (double)((double)this.getTheoricalHeight());
		
		this.scrollBar = new SimpleScrollBar(this.guiLeft + Notification.NOTIF_WIDTH, this.guiTop - 1, this.h + 1, this.getTheoricalHeight() - this.h );
		if(this.needsScroll())
			this.addButton(this.scrollBar);
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) 
	{
		int ch = 0;
		for(Notification n : ScreenNotification.notifications)
		{
			n.x = this.guiLeft;
			n.y = (int) (this.guiTop + ch - (this.needsScroll() ? ((this.getTheoricalHeight() - this.h) * this.scrollBar.getScrollValue()) : 0));
			ch += n.getHeight();
		}
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		scissorBox(this.guiLeft - 1, this.guiTop - 1, this.guiLeft + Notification.NOTIF_WIDTH + 5, this.guiTop + this.h + 1);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		super.render(mouseX, mouseY, partialTicks);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public static void addNotification(Notification n)
	{
		ScreenNotification.notifications.add(n);
	}
	
	public int getTheoricalHeight() 
	{
		return theoricalHeight;
	}

	public void setTheoricalHeight(int theoricalHeight) 
	{
		this.theoricalHeight = theoricalHeight;
	}
	
	public boolean needsScroll()
	{
		return this.factor < 1.0d;
	}
	
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) 
	{
		if(mouseX >= this.guiLeft && mouseY >= this.guiTop && mouseX <= this.guiLeft + Notification.NOTIF_WIDTH + (this.needsScroll() ? 5 : 0) && mouseY <= this.guiTop + this.h)
		{
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		else
		{
			this.onClose();
		}
		
		return false;
	}
	
	public static void scissorBox(float xStart, float yStart, float xEnd, float yEnd)
    {
        double factor = Minecraft.getInstance().func_228018_at_().getGuiScaleFactor();

        // Needed to prevent pixel bleeding when scissor is active
        // This must be properly checked after a binding update
        // TODO : Ensure there are only 1 - 2 - 3 and 4 scaleFactor for MC GUI
        int topOffset = 0;
        int bottomOffset = 0;

        int heightRatio = (int) ((double) Minecraft.getInstance().func_228018_at_().getFramebufferHeight() / factor);
        boolean doesHeightNeedOffset = (double) Minecraft.getInstance().func_228018_at_().getFramebufferHeight() / factor > (double) heightRatio;

        if (factor == 4)
        {
            topOffset = -4;
            bottomOffset = 1;
        }
        else if (factor == 3)
        {
            if (doesHeightNeedOffset)
            {
                topOffset = -2;
                bottomOffset = 1;
            }
            else
            {
                topOffset = -3;
                bottomOffset = 3;
            }
        }
        else if (factor == 2)
        {
            if (doesHeightNeedOffset)
            {
                topOffset = -2;
                bottomOffset = 1;
            }
            else
            {
                topOffset = -2;
                bottomOffset = 2;
            }
        }
        else if (factor == 1)
        {
            topOffset = -1;
            bottomOffset = 1;
        }

        int width = (int) (xEnd - xStart);
        int height = (int) (yEnd - yStart);
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        if (currentScreen != null)
        {
            int bottomY = (int) (currentScreen.height - yEnd);
            GL11.glScissor((int) (xStart * factor), (int) (bottomY * factor) + bottomOffset, (int) (width * factor), (int) (height * factor) + topOffset);
        }
    }
}
