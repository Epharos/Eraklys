package fr.eraklys.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class FontRendererStringUtil 
{
	public static List<String> splitStringMultiline(int width, String text)
	{
		if(Minecraft.getInstance().fontRenderer.getStringWidth(text) <= width)
		{
			new ArrayList<String>().add(text);
		}
		
		return FontRendererStringUtil.splitStringMultiline(width, text.split(" "));
	}
	
	public static List<String> splitStringMultiline(int width, String[] splitedText)
	{		
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		String temp = "";
		List<String> tr = new ArrayList<String>();
		int i = 0;
		
		for(String word : splitedText)
		{
			if(font.getStringWidth(word) > width)
			{
				throw new RuntimeException("Preventing Stackoverflow : the set width is too small for the text you printed");
			}
			
			if(font.getStringWidth(temp.concat(word)) < width)
			{
				temp = temp.concat(word.concat(" "));
				i++;
			}
			else
			{
				break;
			}
			
			if(i == splitedText.length)
			{
				tr.add(temp);
				return tr;
			}
		}
		
		String[] next = new String[splitedText.length - i];
		
		for(int a = i ; a < splitedText.length ; a++)
		{
			next[a - i] = splitedText[a];
		}
		
		tr.add(temp);
		tr.addAll(FontRendererStringUtil.splitStringMultiline(width, next));
		return tr;
	}
	
	public static void main(String[] args)
	{
		for(String s : FontRendererStringUtil.splitStringMultiline(50, "Haec infuso ad eventu permittunt nihil Romae admodum vel ardore mirum Romae similiaque nihil admodum agi est serium ardore redeundum haec est eventu redeundum vel curulium Romae textum textum quodam."))
			System.out.println(s);
	}
}
