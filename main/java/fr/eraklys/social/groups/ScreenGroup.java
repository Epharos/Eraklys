package fr.eraklys.social.groups;

import java.util.Iterator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.eraklys.Eraklys;
import fr.eraklys.screen.ClickableText;
import fr.eraklys.screen.Menu;
import fr.eraklys.screen.PlayerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenGroup extends Screen 
{
	private static final ResourceLocation texture = new ResourceLocation(Eraklys.MODID, "textures/gui/groups.png");
	private int xSize, ySize, guiLeft, guiTop;
	
	public ScreenGroup() 
	{
		super(new TranslationTextComponent("group.label"));
	}
	
	protected void init()
	{
		if(ClientGroup.groupSize() < 2)
		{
			this.onClose();
			return;
		}
		
		super.init();
		this.xSize = 160 * (ClientGroup.groupSize() <= 3 ? ClientGroup.groupSize() : 3);
		this.ySize = 86 * (ClientGroup.groupSize() <= 3 ? 1 : 2);
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		for(int i = 0 ; i < Math.min(ClientGroup.groupSize(), 3) ; i++)
		{
			final int id = i;
			if(ClientGroup.getMember(i).getEntityId() == ClientGroup.getOwnerID())
			{
				this.addButton(new ClickableText(this.guiLeft + 73 + 160 * i, this.guiTop + 10, ClientGroup.getMember(i).getName().getString(), 
						ct -> { if(!this.isMenuActive()) this.setMenu(new PlayerMenu(ClientGroup.getMember(id)).setPos(ct.x + 2, ct.y + 2));}));
			}
			else
			{
				this.addButton(new ClickableText(this.guiLeft + 62 + 160 * i, this.guiTop + 10, ClientGroup.getMember(i).getName().getString(), 
						ct -> { if(!this.isMenuActive()) this.setMenu(new PlayerMenu(ClientGroup.getMember(id)).setPos(ct.x + 2, ct.y + 2));}));
			}
		}
		
		if(ClientGroup.groupSize() > 3)
		{
			for(int i = 0 ; i < ClientGroup.groupSize() - 3 ; i++)
			{			
				final int id = i + 3;
				if(ClientGroup.getMember(3 + i).getEntityId() == ClientGroup.getOwnerID())
				{
					this.addButton(new ClickableText(this.guiLeft + 73 + 160 * i, this.guiTop + 96, ClientGroup.getMember(3 + i).getName().getString(), 
							ct -> { if(!this.isMenuActive()) this.setMenu(new PlayerMenu(ClientGroup.getMember(id)).setPos(ct.x + 2, ct.y + 2));}));
				}
				else
				{
					this.addButton(new ClickableText(this.guiLeft + 62 + 160 * i, this.guiTop + 96, ClientGroup.getMember(3 + i).getName().getString(), 
							ct -> { if(!this.isMenuActive()) this.setMenu(new PlayerMenu(ClientGroup.getMember(id)).setPos(ct.x + 2, ct.y + 2));}));
				}
			}
		}
	}
	
	public void setMenu(Menu menu)
	{
		this.closeMenu();		
		this.addButton(menu);
	}
	
	public boolean isMenuActive()
	{
		for(Iterator<Widget> it = this.buttons.iterator() ; it.hasNext() ; )
		{
			Widget w = it.next();
			
			if(w instanceof Menu)
			{
				return ((Menu)w).active;
			}
		}
		
		return false;
	}
	
	public void closeMenu()
	{
		for(Iterator<Widget> it = this.buttons.iterator() ; it.hasNext() ; )
		{
			Widget w = it.next();
			
			if(w instanceof Menu)
			{
				it.remove();
			}
		}
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) 
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		
		this.getMinecraft().getTextureManager().bindTexture(texture);
		
		for(int i = 0 ; i < Math.min(ClientGroup.groupSize(), 3) ; i++)
		{
			this.blit(this.guiLeft + 160 * i, this.guiTop, 0, 0, 160, 86);
			if(ClientGroup.getMember(i).getEntityId() == ClientGroup.getOwnerID())
			{
				this.blit(this.guiLeft + 61 + 160 * i, this.guiTop + 9, 160, 0, 9, 8);
			}
			
			this.blit(this.guiLeft + 61 + 160 * i, this.guiTop + 42, 160, 16, 9, 9);
		}
		
		if(ClientGroup.groupSize() > 3)
		{
			for(int i = 0 ; i < ClientGroup.groupSize() - 3 ; i++)
			{
				this.blit(this.guiLeft + 160 * i, this.guiTop + 86, 0, 0, 160, 86);
				if(ClientGroup.getMember(3 + i).getEntityId() == ClientGroup.getOwnerID())
				{
					this.blit(this.guiLeft + 8 + 160 * i, this.guiTop + 89, 160, 0, 9, 8);
				}
			}
		}
		
		for(int i = 0 ; i < Math.min(ClientGroup.groupSize(), 3) ; i++)
		{
			this.renderEntity(this.guiLeft + 32 + 160 * i, this.guiTop + 71, 30, (float)(this.guiLeft + 8 + 160 * i) - mouseX, (float)(this.guiTop + 8 - 50) - mouseY, ClientGroup.getMember(i));
		}
		
		if(ClientGroup.groupSize() > 3)
		{
			for(int i = 0 ; i < ClientGroup.groupSize() - 3 ; i++)
			{
				this.renderEntity(this.guiLeft + 32 + 160 * i, this.guiTop + 127, 30, (float)(this.guiLeft + 8 + 160 * i) - mouseX, (float)(this.guiTop + 94 - 50) - mouseY, ClientGroup.getMember(3 + i));
			}
		}
		
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		
		for(int i = 0 ; i < Math.min(ClientGroup.groupSize(), 3) ; i++)
		{
			font.drawString(I18n.format("ui.level", 180), this.guiLeft + 62 + 160 * i, this.guiTop + 26, 0xe1e1e1);
			font.drawString((int)ClientGroup.getMember(i).getHealth() + "/" + (int)ClientGroup.getMember(i).getMaxHealth(), this.guiLeft + 73 + 160 * i, this.guiTop + 43, 0xdd0000);
		}
		
		if(ClientGroup.groupSize() > 3)
		{
			for(int i = 0 ; i < ClientGroup.groupSize() - 3 ; i++)
			{			
				font.drawString(I18n.format("ui.level", 180), this.guiLeft + 62 + 160 * i, this.guiTop + 112, 0xe1e1e1);
				font.drawString((int)ClientGroup.getMember(i).getHealth() + "/" + (int)ClientGroup.getMember(i).getMaxHealth(), this.guiLeft + 73 + 160 * i, this.guiTop + 129, 0xdd0000);
			}
		}
		
		super.render(mouseX, mouseY, partialTicks);
	}
	
	//TODO Revoir l'affichage
	private void renderEntity(int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity) 
	{
	      RenderSystem.pushMatrix();
	      RenderSystem.translatef((float)x, (float)y, 1050.0F);
	      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
	      MatrixStack matrixstack = new MatrixStack();
	      matrixstack.func_227861_a_(0.0D, 0.0D, 1000.0D);
	      matrixstack.func_227862_a_((float)scale, (float)scale, (float)scale);
	      Quaternion quaternion = Vector3f.field_229183_f_.func_229187_a_(180.0F);
	      matrixstack.func_227863_a_(quaternion);  
	      EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
	      entityrenderermanager.setRenderShadow(false);
	      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().func_228019_au_().func_228487_b_();
	      entityrenderermanager.func_229084_a_(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
	      irendertypebuffer$impl.func_228461_a_();
	      entityrenderermanager.setRenderShadow(true);
	      RenderSystem.popMatrix();
	   }
}