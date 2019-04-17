package com.faridkamizi.playershop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.fusesource.jansi.Ansi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Handlers implements Listener 
{
	public static String pluginFolder;
	
	public void sendDataFolder(String s)
	{
		pluginFolder = s;
		System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString() + "Data Folder Received as " + s + Ansi.ansi().reset());
	}
	
	/* Here we create a location array to save the location of shops
	 * So they don't get conflicted with vanilla chest.
	   And we can open the gui by getting the location of these chests.   */
	ArrayList<Location> shops = new ArrayList<Location>();
	
	/* Portal to Shop, GUI & Main */
	private Shop shopHandler = new Shop();
	private JSON json = new JSON();
	
	
	/* Private variables */

	
	/* Here we add a player journal to player when they join the server.
	 * We give the players the journal if they don't have one.
	 */
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		File playerFile = new File(pluginFolder + File.separator + "playerData" + File.separator + p.getDisplayName() + ".json");
		if(!playerFile.exists()) {
			try {
				playerFile.createNewFile(); 
			} catch(Exception ex) { ex.printStackTrace(); }
			
		}
		
		ItemStack pJournal = new ItemStack(Material.WRITTEN_BOOK);
		ItemMeta pJournalmeta = pJournal.getItemMeta();
		pJournalmeta.setDisplayName(ChatColor.GREEN + "Player Journal");
		
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add(ChatColor.GRAY + "by The DungeonRealms Team Tattered");
		Lore.add(ChatColor.WHITE + "Left-Click: " + ChatColor.GRAY + "Invite to party.");
		Lore.add(ChatColor.WHITE + "Sneak-Reft-Click: " + ChatColor.GRAY + "Setup shop.");
		pJournalmeta.setLore(Lore);
		
		pJournal.setItemMeta(pJournalmeta);
		
		if(e.getPlayer().getInventory().contains(pJournal))
		{}
		else {
			p.getInventory().addItem(pJournal);
		}
		

		if(Integer.toString(Integer.parseInt(json.readJSON("playerData", p.getDisplayName(), "shopSize"))) == null)
		{
			JSONObject obj = new JSONObject();
			obj.put("shopSize", new Integer(18));
			
			try(FileWriter fileWriter = new FileWriter(playerFile))
			{
				fileWriter.write(obj.toJSONString());
				fileWriter.flush();
				fileWriter.close();
				
			} catch(Exception ex) { ex.printStackTrace(); }			
			
		}
	}
	
	/* Here we save the player's shop when they log out */
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		if(shopHandler.playerHasShop(p))
		{
			JSONObject obj = new JSONObject();			
			JSONArray list = new JSONArray();
			
			String s = shopHandler.getPlayerShop(p).toString();
			
			System.out.print(s);
			
			String[] arg = s.split(" ,");
			
			list.add(arg[0]); list.add(arg[1]);
			
			obj.put("shopLocation", list);
			
			File playerFile = new File(pluginFolder + "playerData" + File.separator + p.getDisplayName() + ".json");
			if(!playerFile.exists()) { playerFile.mkdir(); }
			
			try(FileWriter fileWriter = new FileWriter(playerFile))
			{
				fileWriter.write(obj.toJSONString());
				fileWriter.flush();
				fileWriter.close();
				
			} catch(Exception ex) { ex.printStackTrace(); }
			
			System.out.print(Ansi.ansi().fg(Ansi.Color.BLUE).bold().toString() + p.getDisplayName() + "'s shop was saved." + Ansi.ansi().reset());
		}
	}
	
	
	
	/* Here we check if the player sneak right click to setup their shop */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(e.getPlayer().isSneaking() && e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(e.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Player Journal"))
			{
				e.setCancelled(true);
				Location chest = e.getClickedBlock().getLocation();
				chest.setY(chest.getY() + 1);
				chest.getBlock().setType(Material.CHEST);
				
				Location chestTwo = chest.getBlock().getRelative(BlockFace.WEST).getLocation();
				chestTwo.getBlock().setType(Material.CHEST);
				
				ArrayList<Location> loc = new ArrayList<Location>();
				loc.add(chest);
				loc.add(chestTwo);
				
				shopHandler.setPlayerShop(e.getPlayer(), loc);
				
				JSONObject obj = new JSONObject();
				JSONArray list = new JSONArray();
				
				list.add(chest); list.add(chestTwo);
				obj.put("shopLocation", list);
				
				File playerFile = new File(pluginFolder + File.separator + "playerData" + File.separator + e.getPlayer().getDisplayName() + ".json");
				try (FileWriter file = new FileWriter(playerFile))
				{
					file.write(obj.toJSONString());
				} catch (IOException ex) { ex.printStackTrace(); }
				
			}
		}
		else if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(e.getClickedBlock().getType() == Material.CHEST)
			{
				if(shopHandler.getPlayerShop(e.getPlayer()) == null)
				{
					e.setCancelled(true);
					e.getPlayer().sendMessage(shopHandler.getPlayerShop(e.getPlayer()) + " ");
				}
				else 
				{
					e.setCancelled(true);
					loadPlayerGUI(e.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();
			
		if(inv.getName().equals(ChatColor.DARK_GRAY + p.getDisplayName() + "'s Shop"))
		{
			
		}
		else 
		{
			
		}
	}
	

	@SuppressWarnings("deprecation")
	public void loadPlayerGUI(Player player)
	{
		int size = Integer.parseInt(json.readJSON("playerData", player.getDisplayName(), "shopSize"));
		Inventory chestShopGUI = Bukkit.createInventory(null, size, ChatColor.DARK_GRAY + player.getDisplayName() + "'s Shop");
		//for(int i = 0; i >= 8; i++)
		//{
		//	for(ItemStack j : shop.shopItems.get(i)) {
		//		chestShopGUI.setItem(i, j);
		//	}
		//}
		
		ItemStack pskull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullmeta = (SkullMeta) pskull.getItemMeta();
		skullmeta.setDisplayName(ChatColor.GREEN + player.getDisplayName() + "'s Shop");
		skullmeta.setOwner(player.getDisplayName());
		ArrayList<String> skullLore = new ArrayList<String>();
		skullLore.add(ChatColor.GRAY + "Click here to view the last items this shop sold.");
		skullmeta.setLore(skullLore);
		pskull.setItemMeta(skullmeta);
		
		ItemStack chestSFX = new ItemStack(Material.FIREWORK);
		ItemMeta firework = chestSFX.getItemMeta();
		
		firework.setDisplayName(ChatColor.GREEN + "ChestSFX");
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add(ChatColor.GRAY + "Click here to set your shop's particle display.");
		Lore.add(ChatColor.GRAY + "Only open shops display particles.");
		firework.setLore(Lore);
		chestSFX.setItemMeta(firework);
		

		ItemStack nameTag = new ItemStack(Material.NAME_TAG);
		ItemMeta nameTagMeta = nameTag.getItemMeta();
		nameTagMeta.setDisplayName(ChatColor.GREEN + "Rename Shop");
		ArrayList<String> tagLore = new ArrayList<String>();
		tagLore.add(ChatColor.GRAY + "Click here to rename your shop.");
		nameTagMeta.setLore(tagLore);
		nameTag.setItemMeta(nameTagMeta);
		
		ItemStack gray_panel = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta glassMeta = gray_panel.getItemMeta();
		glassMeta.setDisplayName("");
		gray_panel.setItemMeta(glassMeta);
		
		ItemStack barrier = new ItemStack(Material.BARRIER);
		ItemMeta barrierMeta = barrier.getItemMeta();
		barrierMeta.setDisplayName(ChatColor.RED + "Delete Shop");
		ArrayList<String> bLore = new ArrayList<String>();
		bLore.add(ChatColor.GRAY + "Click here to safety delete your shop.");
		bLore.add(ChatColor.GRAY + "Your item will be sent to your Collection Bin.");
		barrierMeta.setLore(bLore);
		barrier.setItemMeta(barrierMeta);
		
		ItemStack DYE = new ItemStack(35, 1);
		ItemMeta dyeMeta = DYE.getItemMeta();
		dyeMeta.setDisplayName(ChatColor.RED + "Click to " + ChatColor.BOLD + "CLOSE" + ChatColor.RESET + ChatColor.RED + " this shop.");
		ArrayList<String> dyeLore = new ArrayList<String>();
		dyeLore.add(ChatColor.GRAY + "Allows modifying shop stock.");
		dyeMeta.setLore(dyeLore);
		DYE.setItemMeta(dyeMeta);
		
		chestShopGUI.setItem(chestShopGUI.getSize() - 9, pskull);
		chestShopGUI.setItem(chestShopGUI.getSize() - 8, chestSFX);
		chestShopGUI.setItem(chestShopGUI.getSize() - 7, nameTag);
		
		chestShopGUI.setItem(chestShopGUI.getSize() - 6, gray_panel);
		chestShopGUI.setItem(chestShopGUI.getSize() - 5, gray_panel);
		chestShopGUI.setItem(chestShopGUI.getSize() - 4, gray_panel);
		chestShopGUI.setItem(chestShopGUI.getSize() - 3, gray_panel);
		
		chestShopGUI.setItem(chestShopGUI.getSize() - 2, barrier);
		chestShopGUI.setItem(chestShopGUI.getSize() - 1, DYE);
		
		player.openInventory(chestShopGUI);
	}
	
	
}
