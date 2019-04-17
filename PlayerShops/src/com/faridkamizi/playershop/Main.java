package com.faridkamizi.playershop;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

public class Main extends JavaPlugin 
{
	private Handlers handle = new Handlers();
	private JSON json = new JSON();
	
	@Override
	public void onEnable()
	{
		System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString() + "PlayerShop enabled." + Ansi.ansi().reset());
		this.getServer().getPluginManager().registerEvents(handle, this);
		handle.sendDataFolder(getDataFolder().getAbsolutePath());
		json.sendDataFolder(getDataFolder().getAbsolutePath());
		File filePath = new File(getDataFolder().getAbsoluteFile() + File.separator + "playerData");
		
		if(!filePath.exists())
		{
			filePath.mkdirs();
		}
	}
	
	
	public void onDisable()
	{
		System.out.print(Ansi.ansi().fg(Ansi.Color.RED).bold().toString() + "PlayerShop disabled." + Ansi.ansi().reset());
	}
	
}
