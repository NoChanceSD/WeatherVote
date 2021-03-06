package me.F_o_F_1092.WeatherVote;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;

public class WeatherVoteManager {

	private static Main plugin = (Main)Bukkit.getPluginManager().getPlugin("WeatherVote");

	public static boolean isVotingAtWorld(String worldName) {
		return plugin.votes.containsKey(worldName);
	}

	public static WeatherVote getVotingAtWorld(String worldName) {
		return plugin.votes.get(worldName);
	}

	public static boolean hasVoted(String player, String worldName) {
		return getVotingAtWorld(worldName).hasVoted(player);
	}

	public static Economy getVault() {
		return Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	}

	public static boolean isVaultInUse() {
		return plugin.vault && plugin.price > 0.00;
	}

	public static void openVoteingGUI(String player, String worldName) {
		Inventory voteGUI;
		ItemStack voteGUIItemOne;
		ItemStack voteGUIItemTwo;

		if (!isVotingAtWorld(worldName)) {
			try {
				voteGUI = Bukkit.createInventory(null, 9, plugin.msg.get("votingInventoryTitle.1"));
			} catch (Exception e1) {
				voteGUI = Bukkit.createInventory(null, 9, "�f[�9W�bV�f]" + plugin.msg.get("color.2") + " Voting..");
				System.out.println("\u001B[31m[WeatherVote] The Voring-Inventory text caused a problem. [" + e1.getMessage() +"]\u001B[0m");
			}

			voteGUIItemOne = new ItemStack(Material.STAINED_CLAY, 1, (byte)4);
			ItemMeta voteGUIItemOneMeta = voteGUIItemOne.getItemMeta();
			voteGUIItemOneMeta.setDisplayName("§e" + plugin.msg.get("text.1").replace(plugin.msg.get("color.2"), ""));
			voteGUIItemOne.setItemMeta(voteGUIItemOneMeta);

			voteGUIItemTwo = new ItemStack(Material.STAINED_CLAY, 1, (byte)11);
			ItemMeta voteGUIItemTwoMeta = voteGUIItemTwo.getItemMeta();
			voteGUIItemTwoMeta.setDisplayName("§9" + plugin.msg.get("text.2").replace(plugin.msg.get("color.2"), ""));
			voteGUIItemTwo.setItemMeta(voteGUIItemTwoMeta);
		} else {
			WeatherVote wv = getVotingAtWorld(worldName);
			try {
				String replace = plugin.msg.get("votingInventoryTitle.2");
				replace = replace.replace("[WEATHER]", wv.getWeather());
				voteGUI = Bukkit.createInventory(null, 9, replace);
			} catch (Exception e1) {
				voteGUI = Bukkit.createInventory(null, 9, "�f[�9W�bV�f] " + plugin.msg.get("color.2") + wv.getWeather());
				System.out.println("\u001B[31m[WeatherVote] The Voring-Inventory text caused a problem. [" + e1.getMessage() +"]\u001B[0m");
			}

			voteGUIItemOne = new ItemStack(Material.STAINED_CLAY, 1, (byte)5);
			ItemMeta voteGUIItemOneMeta = voteGUIItemOne.getItemMeta();
			voteGUIItemOneMeta.setDisplayName("§a" + plugin.msg.get("text.3").replace(plugin.msg.get("color.2"), ""));
			voteGUIItemOne.setItemMeta(voteGUIItemOneMeta);

			voteGUIItemTwo = new ItemStack(Material.STAINED_CLAY, 1, (byte)14);
			ItemMeta voteGUIItemTwoMeta = voteGUIItemTwo.getItemMeta();
			voteGUIItemTwoMeta.setDisplayName("§c" + plugin.msg.get("text.4").replace(plugin.msg.get("color.2"), ""));
			voteGUIItemTwo.setItemMeta(voteGUIItemTwoMeta);
		}

		ItemStack blockedItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)0);
		ItemMeta blockedItemMeta = voteGUIItemOne.getItemMeta();
		blockedItemMeta.setDisplayName("");
		blockedItem.setItemMeta(blockedItemMeta);
		
		
		voteGUI.setItem(0, blockedItem);
		voteGUI.setItem(1, voteGUIItemOne);
		voteGUI.setItem(2, voteGUIItemOne);
		voteGUI.setItem(3, voteGUIItemOne);
		voteGUI.setItem(4, blockedItem);
		voteGUI.setItem(5, voteGUIItemTwo);
		voteGUI.setItem(6, voteGUIItemTwo);
		voteGUI.setItem(7, voteGUIItemTwo);
		voteGUI.setItem(8, blockedItem);

		plugin.votingGUI.put(player, worldName);

		Bukkit.getPlayer(player).openInventory(voteGUI);
	}

	public static boolean containsOpenVoteingGUI(String player) {
		return plugin.votingGUI.containsKey(player);
	}

	public static void closeVoteingGUI(String player, boolean closeInventory) {
		plugin.votingGUI.remove(player);

		if (closeInventory) {
			Bukkit.getPlayer(player).closeInventory();
		}
		
		if (plugin.votingInventoryMessages) {
			Bukkit.getPlayer(player).sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.21"));
		}

	}

	public static void closeAllVoteingGUIs(String worldName) {
		HashMap<String, String> votingGUICopy = new HashMap<String, String>();
		for (String player : plugin.votingGUI.keySet()) {
			if (plugin.votingGUI.get(player).equals(worldName)) {
				votingGUICopy.put(player, worldName);
			}
		}
		for (String player : votingGUICopy.keySet()) {
			closeVoteingGUI(player, true);
		}
	}
}
