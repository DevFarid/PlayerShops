package com.faridkamizi.playershop;

import java.util.ArrayList;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Shop
{
	private JSON json = new JSON();
	/* Set player's shop
	 * Get player's shop
	   Add to player's shop	 */


	//private HashMap<ArrayList<Location>, ArrayList<String>> shopHistory = new HashMap<ArrayList<Location>, ArrayList<String>>();
	HashMap<Player, ArrayList<Location>> shop = new HashMap<Player, ArrayList<Location>>();
	HashMap<ArrayList<Location>, ArrayList<ItemStack>> shopItems = new HashMap<ArrayList<Location>, ArrayList<ItemStack>>();
	
	/* Here we set the player's shop */ 
	public void setPlayerShop(Player player, ArrayList<Location> loc) 
	{
		shop.put(player, loc);
	}
	
	
	/* Here we get the player's shop */ 
	public ArrayList<Location> getPlayerShop(Player p)
	{
		if(shop.containsKey(p))
		{
			return shop.get(p);
		}
		else if(json.readJSON("playerData", p.getDisplayName(), "shopLocation") != null)
		{
			String s = json.readJSON("playerData", p.getDisplayName(), "shopLocation");
			s = s.replace("[Location{world=CraftWorld{name=world},", "");
			s = s.replace("Location{world=CraftWorld{name=world},", "");
			s = s.replaceAll("}", ""); s = s.replaceAll("]", ""); 
			s = s.replace(",pitch=0.0,yaw=0.0", ""); s = s.replace("x", ""); s = s.replace("y", ""); s = s.replace("z", "");
			s = s.replaceAll("=", "");
			
			
			String[] one = s.split(", ");
			
			String joined = String.join(", ", one[0]);
			return translate(joined);
		}
		return null;
	}
	
	/* Boolean version of getPlayerShop */
	public boolean playerHasShop(Player p)
	{
		if(getPlayerShop(p) != null)
		{
			return true;
		}
		return false;
	}
	
	
	/* Here we add items to the chest */
	public void addShopItem(Player player, ArrayList<ItemStack> item)
	{
		shopItems.put(getPlayerShop(player), item);
	}
	
	
	/* Remove chest item & with collection bin if inv. full */
	public void removeShopItem(Player player, ItemStack[] item)
	{
		shopItems.remove(getPlayerShop(player), item);
		
		if(player.getInventory().getSize() >= 35)
		{
			player.sendMessage("Your inventory was full but the item was added to your Collection Bin.");
		}
		else {
			player.getInventory().addItem(item);
		}
	}
	
	/* Here is the method to close a player's shop */
	public void closeShop(Player player)
	{
		getPlayerShop(player).get(0).getBlock().setType(Material.AIR);
		getPlayerShop(player).get(1).getBlock().setType(Material.AIR);
	}

	
	/* Here we get the items of a shop */
	@SuppressWarnings("unlikely-arg-type")
	public ArrayList<ItemStack> getShopItems(Player player)
	{
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		for(int i = 0; i < shopItems.size(); i++)
		{
			items.addAll(shopItems.get(i));
		}
		
		return items;
	}
	
	/* Here we turn Strings into an ArrayList of Locations */
	public ArrayList<Location> translate(String s)
	{	
	    String[] arg = s.split(",");
	    
	    double x = Double.parseDouble(arg[0]);
	    double y = Double.parseDouble(arg[1]);
	    double z = Double.parseDouble(arg[2]);
	    
	    Location location = new Location(Bukkit.getWorld("world"), x, y , z, 0 ,0);
	    Location location_2 = location.getBlock().getRelative(BlockFace.WEST).getLocation();
	    
	    ArrayList<Location> loc = new ArrayList<Location>();
	    loc.add(location_2); loc.add(location);
	    
		return loc;
		
	}
	
	
	
	/* Here we add the buyer to the shop history of the seller */
	
	/*
	public void addToShopHistory(Player player, String[] seller)
	{
		shopHistory.put(getPlayerShop(player), seller);
	}	
	*/ 
	/* Here we get the shop history of any shop */
	/*
	public ArrayList<String> getShopHistory(Player player)
	{
		ArrayList<String> players = new ArrayList<String>();
		
		for(int i = 0; i >= shopHistory.size(); i++) {
			players.add(shopHistory.get(i).toString());
		}
		return players; 
	}
	
	*/
	
}
