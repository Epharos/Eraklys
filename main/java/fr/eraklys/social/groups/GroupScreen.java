package fr.eraklys.social.groups;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.eraklys.Eraklys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GroupScreen extends Screen 
{
	private static final ResourceLocation texture = new ResourceLocation(Eraklys.MODID, "textures/gui/groups.png");
	private int xSize, ySize, guiLeft, guiTop;
	
	public GroupScreen() 
	{
		super(new TranslationTextComponent("group.label"));
	}
	
	protected void init()
	{
		super.init();
		this.xSize = 160 * (ClientGroup.groupSize() <= 3 ? ClientGroup.groupSize() : 3);
		this.ySize = 86 * (ClientGroup.groupSize() <= 3 ? 1 : 2);
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
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
			if(ClientGroup.getMember(i).getEntityId() == ClientGroup.getOwnerID())
			{
				this.font.drawStringWithShadow(ClientGroup.getMember(i).getName().getString(), this.guiLeft + 73 + 160 * i, this.guiTop + 10, 0xE1E1E1);
			}
			else
			{
				this.font.drawStringWithShadow(ClientGroup.getMember(i).getName().getString(), this.guiLeft + 62 + 160 * i, this.guiTop + 10, 0xE1E1E1);
			}
		}
		
		if(ClientGroup.groupSize() > 3)
		{
			for(int i = 0 ; i < ClientGroup.groupSize() - 3 ; i++)
			{			
				if(ClientGroup.getMember(3 + i).getEntityId() == ClientGroup.getOwnerID())
				{
					this.font.drawStringWithShadow(ClientGroup.getMember(3 + i).getName().getString(), this.guiLeft + 73 + 160 * i, this.guiTop + 96, 0xE1E1E1);
				}
				else
				{
					this.font.drawStringWithShadow(ClientGroup.getMember(3 + i).getName().getString(), this.guiLeft + 62 + 160 * i, this.guiTop + 96, 0xE1E1E1);
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
	}
	
	//TODO Revoir l'affichage
	private void renderEntity(int x, int y, int scale, float mouseX, float mouseY, LivingEntity entity) 
	{
	      float f = (float)Math.atan((double)(mouseX / 40.0F));
	      float f1 = (float)Math.atan((double)(mouseY / 40.0F));
	      RenderSystem.pushMatrix();
	      RenderSystem.translatef((float)x, (float)y, 1050.0F);
	      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
	      MatrixStack matrixstack = new MatrixStack();
	      matrixstack.func_227861_a_(0.0D, 0.0D, 1000.0D);
	      matrixstack.func_227862_a_((float)scale, (float)scale, (float)scale);
	      Quaternion quaternion = Vector3f.field_229183_f_.func_229187_a_(180.0F);
	      Quaternion quaternion1 = Vector3f.field_229179_b_.func_229187_a_(f1 * 20.0F);
	      quaternion.multiply(quaternion1);
	      matrixstack.func_227863_a_(quaternion);
	      float f2 = entity.renderYawOffset;
	      float f3 = entity.rotationYaw;
	      float f4 = entity.rotationPitch;
	      float f5 = entity.prevRotationYawHead;
	      float f6 = entity.rotationYawHead;
	      entity.setCustomNameVisible(false);
	      entity.renderYawOffset = 180.0F + f * 20.0F;
	      entity.rotationYaw = 180.0F + f * 40.0F;
	      entity.rotationPitch = -f1 * 20.0F;
	      entity.rotationYawHead = entity.rotationYaw;
	      entity.prevRotationYawHead = entity.rotationYaw;	      
	      EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
	      quaternion1.conjugate();
	      entityrenderermanager.func_229089_a_(quaternion1);
	      entityrenderermanager.setRenderShadow(false);
	      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().func_228019_au_().func_228487_b_();
	      entityrenderermanager.func_229084_a_(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
	      irendertypebuffer$impl.func_228461_a_();
	      entityrenderermanager.setRenderShadow(true);
	      entity.renderYawOffset = f2;
	      entity.rotationYaw = f3;
	      entity.rotationPitch = f4;
	      entity.prevRotationYawHead = f5;
	      entity.rotationYawHead = f6;
	      entity.setCustomNameVisible(true);
	      RenderSystem.popMatrix();
	   }
}