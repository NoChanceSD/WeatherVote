package me.F_o_F_1092.WeatherVote;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.F_o_F_1092.WeatherVote.PluginManager.Command;
import me.F_o_F_1092.WeatherVote.PluginManager.CommandListener;
import me.F_o_F_1092.WeatherVote.PluginManager.HelpPageListener;

public class Main extends JavaPlugin {

	HashMap<String, WeatherVote> votes = new HashMap<String, WeatherVote>();
	HashMap<String, String> votingGUI = new HashMap<String, String>();
	public HashMap<String, String> msg = new HashMap<String, String>();
	long votingTime;
	long remindingTime;
	long timeoutPeriod;
	boolean useScoreboard;
	boolean useVoteGUI;
	boolean useBossBarAPI = false;
	boolean useTitleAPI = false;
	boolean checkForHiddenPlayers = false;
	boolean prematureEnd;
	double price;
	boolean rawMessages;
	boolean votingInventoryMessages;
	ArrayList<String> timeoutPeriodWorlds = new ArrayList<String>();
	ArrayList<String> disabledWorlds = new ArrayList<String>();
	boolean vault = false;
	boolean updateAvailable = false;

	@Override
	public void onEnable() {

		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			vault = true;
		}

		if (Bukkit.getPluginManager().getPlugin("BossBarAPI") != null) {
			useBossBarAPI = true;
		}

		if (Bukkit.getPluginManager().getPlugin("TitleAPI") != null) {
			useTitleAPI = true;
		}

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventListener(this), this);

		this.getCommand("WeatherVote").setExecutor(new CommandWeatherVote(this));
		this.getCommand("WeatherVote").setTabCompleter(new CommandWeatherVoteTabCompleter());

		File fileConfig = new File("plugins/WeatherVote/Config.yml");
		FileConfiguration ymlFileConfig = YamlConfiguration.loadConfiguration(fileConfig);

		if (!fileConfig.exists()) {
			disabledWorlds.add("world_nether");
			disabledWorlds.add("world_the_end");

			try {
				ymlFileConfig.save(fileConfig);
				ymlFileConfig.set("VotingTime", 35);
				ymlFileConfig.set("RemindingTime", 25);
				ymlFileConfig.set("TimeoutPeriod", 15);
				ymlFileConfig.set("UseScoreboard", true);
				ymlFileConfig.set("UseVoteGUI", true);
				ymlFileConfig.set("UseBossBarAPI", true);
				ymlFileConfig.set("UseTitleAPI", true);
				ymlFileConfig.set("CheckForHiddenPlayers", false);
				ymlFileConfig.set("PrematureEnd", true);
				ymlFileConfig.set("Price", 0.00);
				ymlFileConfig.set("RawMessages", true);
				ymlFileConfig.set("DisabledWorld", disabledWorlds);
				ymlFileConfig.set("VotingInventoryMessages", true);
				ymlFileConfig.save(fileConfig);
			} catch (IOException e1) {
				System.out.println("\u001B[31m[WeatherVote] Can't create the Config.yml. [" + e1.getMessage() + "]\u001B[0m");
			}

			disabledWorlds.clear();
		}

		votingTime = ymlFileConfig.getLong("VotingTime");
		remindingTime = ymlFileConfig.getLong("RemindingTime");
		timeoutPeriod = ymlFileConfig.getLong("TimeoutPeriod");
		useScoreboard = ymlFileConfig.getBoolean("UseScoreboard");
		useVoteGUI = ymlFileConfig.getBoolean("UseVoteGUI");

		if (useBossBarAPI) {
			if (!ymlFileConfig.getBoolean("UseBossBarAPI")) {
				useBossBarAPI = false;
			}
		}

		if (useTitleAPI) {
			if (!ymlFileConfig.getBoolean("UseTitleAPI")) {
				useTitleAPI = false;
			}
		}

		checkForHiddenPlayers = ymlFileConfig.getBoolean("CheckForHiddenPlayers");
		prematureEnd = ymlFileConfig.getBoolean("PrematureEnd");
		price = ymlFileConfig.getDouble("Price");
		rawMessages = ymlFileConfig.getBoolean("RawMessages");
		disabledWorlds.addAll(ymlFileConfig.getStringList("DisabledWorld"));
		votingInventoryMessages = ymlFileConfig.getBoolean("VotingInventoryMessages");

		File fileMessages = new File("plugins/WeatherVote/Messages.yml");
		FileConfiguration ymlFileMessage = YamlConfiguration.loadConfiguration(fileMessages);

		if (!fileMessages.exists()) {
			try {
				ymlFileMessage.save(fileMessages);
				ymlFileMessage.set("[WeatherVote]", "&f[&9Weather&bVote&f] ");
				ymlFileMessage.set("Color.1", "&9");
				ymlFileMessage.set("Color.2", "&b");
				ymlFileMessage.set("Message.1", "You have to be a player, to use this command.");
				ymlFileMessage.set("Message.2", "You do not have the permission for this command.");
				ymlFileMessage.set("Message.3", "There is a new voting for &b[WEATHER]&9 weather, vote with &b/wv yes&9 or &b/wv no&9.");
				ymlFileMessage.set("Message.4", "The voting is disabled in this world.");
				ymlFileMessage.set("Message.5", "There is already a voting in this world.");
				ymlFileMessage.set("Message.6", "There isn't a voting in this world.");
				ymlFileMessage.set("Message.7", "You have already voted.");
				ymlFileMessage.set("Message.8", "You have voted for &bYES&9.");
				ymlFileMessage.set("Message.9", "You have voted for &bNO&9.");
				ymlFileMessage.set("Message.10", "The plugin is reloading...");
				ymlFileMessage.set("Message.11", "Reloading completed.");
				ymlFileMessage.set("Message.12", "The voting is over, the weather has been changed.");
				ymlFileMessage.set("Message.13", "The voting is over, the weather hasn't been changed.");
				ymlFileMessage.set("Message.14", "The voting for &b[WEATHER]&9 weather is over in &b[SECONDS]&9 seconds.");
				ymlFileMessage.set("Message.15", "You have to wait a bit, until you can start a new voting.");
				ymlFileMessage.set("Message.16", "There is a new update available for this plugin. &b( https://fof1092.de/WV )&9");
				ymlFileMessage.set("Message.17", "All players have voted.");
				ymlFileMessage.set("Message.18", "You need &b[MONEY]$&9 more to start a voting.");
				ymlFileMessage.set("Message.19", "You payed &b[MONEY]$&9 to start a voting.");
				ymlFileMessage.set("Message.20", "You opend the voting-inventory.");
				ymlFileMessage.set("Message.21", "You'r voting-inventory has been closed.");
				ymlFileMessage.set("Message.22", "Try [COMMAND]");
				ymlFileMessage.set("Message.23", "You changed the weather to &b[WEATHER]&9.");
				ymlFileMessage.set("Text.1", "SUNNY");
				ymlFileMessage.set("Text.2", "RAINY");
				ymlFileMessage.set("Text.3", "YES");
				ymlFileMessage.set("Text.4", "NO");
				ymlFileMessage.set("StatsText.1", "Stats since: ");
				ymlFileMessage.set("StatsText.2", "Money spent: ");
				ymlFileMessage.set("StatsText.3", "Total sunny votes: ");
				ymlFileMessage.set("StatsText.8", "Total rainy votes: ");
				ymlFileMessage.set("StatsText.4", "  Yes votes: ");
				ymlFileMessage.set("StatsText.5", "  No votes: ");
				ymlFileMessage.set("StatsText.6", "  Won: ");
				ymlFileMessage.set("StatsText.7", "  Lost: ");
				ymlFileMessage.set("HelpTextGui.1", "&b[&9Click to use this command&b]");
				ymlFileMessage.set("HelpTextGui.2", "&b[&9Next page&b]");
				ymlFileMessage.set("HelpTextGui.3", "&b[&9Last page&b]");
				ymlFileMessage.set("HelpTextGui.4", "&7&oPage [PAGE]. &7Click on the arrows for the next page.");
				ymlFileMessage.set("HelpText.1", "This command shows you the help page.");
				ymlFileMessage.set("HelpText.2", "This command shows you the info page.");
				ymlFileMessage.set("HelpText.3", "This command shows you the stats page.");
				ymlFileMessage.set("HelpText.4", "This command opens the Voting-Inventory.");
				ymlFileMessage.set("HelpText.5", "This command allows you to start a sun voting.");
				ymlFileMessage.set("HelpText.6", "This command allows you to start a rain voting.");
				ymlFileMessage.set("HelpText.7", "This command allows you to vote for yes or no.");
				ymlFileMessage.set("HelpText.8", "' '");
				ymlFileMessage.set("HelpText.9", "This command is reloading the Config.yml and Messages.yml file.");
				ymlFileMessage.set("VotingInventoryTitle.1", "&f[&9W&bV&f] &bSunny&f/&bRainy");
				ymlFileMessage.set("VotingInventoryTitle.2", "&f[&9W&bV&f] &b[WEATHER]&9");
				ymlFileMessage.set("BossBarAPIMessage", "&f[&9W&bV&f] &9Voting for &b[WEATHER]&9 weather (&b/wv yes&9 or &b/wv no&9)");
				ymlFileMessage.set("TitleAPIMessage.Title.1", "&f[&9W&bV&f] &b[WEATHER]&9 time voting.");
				ymlFileMessage.set("TitleAPIMessage.Title.2", "&f[&9W&bV&f] &b[SECONDS]&9 seconds left.");
				ymlFileMessage.set("TitleAPIMessage.Title.3", "&f[&9W&bV&f] &9The weather has been changed.");
				ymlFileMessage.set("TitleAPIMessage.Title.4", "&f[&9W&bV&f] &9The weather hasn't been changed.");
				ymlFileMessage.set("TitleAPIMessage.SubTitle", "&9(&b/wv yes&9 or &b/wv no&9)");
				ymlFileMessage.set("RawMessage.1",
				        "[\"\",{\"text\":\"There is a new voting for \",\"color\":\"blue\"},{\"text\":\"[WEATHER]\",\"color\":\"aqua\"},{\"text\":\" weather, vote with \",\"color\":\"blue\"},{\"text\":\"/wv yes\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/wv yes\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"/wv yes\",\"color\":\"aqua\"}]}}},{\"text\":\" or \",\"color\":\"blue\"},{\"text\":\"/wv no\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/wv no\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"/wv no\",\"color\":\"aqua\"}]}}},{\"text\":\".\",\"color\":\"blue\"}]");
				ymlFileMessage.save(fileMessages);
			} catch (IOException e1) {
				System.out.println("\u001B[31m[WeatherVote] Can't create the Messages.yml. [" + e1.getMessage() + "]\u001B[0m");
			}
		}

		msg.put("[WeatherVote]", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("[WeatherVote]")));
		msg.put("color.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("Color.1")));
		msg.put("color.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("Color.2")));
		msg.put("msg.1", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.1")));
		msg.put("msg.2", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.2")));
		msg.put("msg.3", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.3")));
		msg.put("msg.4", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.4")));
		msg.put("msg.5", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.5")));
		msg.put("msg.6", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.6")));
		msg.put("msg.7", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.7")));
		msg.put("msg.8", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.8")));
		msg.put("msg.9", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.9")));
		msg.put("msg.10", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.10")));
		msg.put("msg.11", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.11")));
		msg.put("msg.12", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.12")));
		msg.put("msg.13", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.13")));
		msg.put("msg.14", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.14")));
		msg.put("msg.15", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.15")));
		msg.put("msg.16", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.16")));
		msg.put("msg.17", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.17")));
		msg.put("msg.18", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.18")));
		msg.put("msg.19", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.19")));
		msg.put("msg.20", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.20")));
		msg.put("msg.21", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.21")));
		msg.put("msg.22", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.22")));
		msg.put("msg.23", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("Message.23")));
		msg.put("text.1", ChatColor.translateAlternateColorCodes('&', msg.get("color.2") + ymlFileMessage.getString("Text.1")));
		msg.put("text.2", ChatColor.translateAlternateColorCodes('&', msg.get("color.2") + ymlFileMessage.getString("Text.2")));
		msg.put("text.3", ChatColor.translateAlternateColorCodes('&', msg.get("color.2") + ymlFileMessage.getString("Text.3")));
		msg.put("text.4", ChatColor.translateAlternateColorCodes('&', msg.get("color.2") + ymlFileMessage.getString("Text.4")));
		msg.put("helpTextGui.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpTextGui.1")));
		msg.put("helpTextGui.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpTextGui.2")));
		msg.put("helpTextGui.3", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpTextGui.3")));
		msg.put("helpTextGui.4", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpTextGui.4")));
		msg.put("statsText.1", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.1")));
		msg.put("statsText.2", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.2")));
		msg.put("statsText.3", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.3")));
		msg.put("statsText.4", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.4")));
		msg.put("statsText.5", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.5")));
		msg.put("statsText.6", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.6")));
		msg.put("statsText.7", ChatColor.translateAlternateColorCodes('&', msg.get("color.1") + ymlFileMessage.getString("StatsText.7")));
		msg.put("votingInventoryTitle.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("VotingInventoryTitle.1")));
		msg.put("votingInventoryTitle.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("VotingInventoryTitle.2")));
		msg.put("bossBarAPIMessage", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("BossBarAPIMessage")));
		msg.put("titleAPIMessage.Title.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.1")));
		msg.put("titleAPIMessage.Title.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.2")));
		msg.put("titleAPIMessage.Title.3", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.3")));
		msg.put("titleAPIMessage.Title.4", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.4")));
		msg.put("titleAPIMessage.SubTitle", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.SubTitle")));
		msg.put("rmsg.1", ymlFileMessage.getString("RawMessage.1"));

		HelpPageListener.setPluginNametag(msg.get("[WeatherVote]"));

		CommandListener.addCommand(new Command("/wv help (Page)", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.1"))));
		CommandListener.addCommand(new Command("/wv info", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.2"))));
		CommandListener.addCommand(new Command("/wv stats", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.3"))));
		if (useVoteGUI) {
			CommandListener.addCommand(new Command("/wv", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.4"))));
		}
		CommandListener.addCommand(new Command("/wv sun", "WeatherVote.Sun", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.5"))));
		CommandListener.addCommand(new Command("/wv rain", "WeatherVote.Rain", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.6"))));
		CommandListener.addCommand(new Command("/wv yes", "WeatherVote.Vote", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.7"))));
		CommandListener.addCommand(new Command("/wv no", "WeatherVote.Vote", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.8"))));
		CommandListener.addCommand(new Command("/wv reload", "WeatherVote.Reload", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.9"))));

		File fileStats = new File("plugins/WeatherVote/Stats.yml");
		FileConfiguration ymlFileStats = YamlConfiguration.loadConfiguration(fileStats);

		if (!fileStats.exists()) {
			try {
				ymlFileStats.save(fileStats);
				ymlFileStats.set("Date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
				ymlFileStats.set("Sunny.Yes", 0);
				ymlFileStats.set("Sunny.No", 0);
				ymlFileStats.set("Sunny.Won", 0);
				ymlFileStats.set("Sunny.Lost", 0);
				ymlFileStats.set("Rainy.Yes", 0);
				ymlFileStats.set("Rainy.No", 0);
				ymlFileStats.set("Rainy.Won", 0);
				ymlFileStats.set("Rainy.Lost", 0);
				ymlFileStats.set("MoneySpent", 0.00);
				ymlFileStats.save(fileStats);
			} catch (IOException e1) {
				System.out.println("\u001B[31m[WeatherVote] Can't create the Stats.yml. [" + e1.getMessage() + "]\u001B[0m");
			}
		}
	}

	@Override
	public void onDisable() {
		for (World w : Bukkit.getWorlds()) {
			if (useVoteGUI) {
				if (!votingGUI.isEmpty()) {
					WeatherVoteManager.closeAllVoteingGUIs(w.getName());
				}
			}

			if (WeatherVoteManager.isVotingAtWorld(w.getName())) {
				WeatherVote wv = WeatherVoteManager.getVotingAtWorld(w.getName());
				if (!wv.isTimeoutPeriod()) {
					if (useScoreboard) {
						for (Player p : wv.getAllPlayersAtWorld()) {
							wv.removeScoreboard(p.getName());
						}
					}
					if (useBossBarAPI) {
						for (Player p : wv.getAllPlayersAtWorld()) {
							wv.removeBossBar(p.getName());
						}
					}
					wv.sendMessage(msg.get("[TimeVote]") + msg.get("msg.13"));
				}
			}
		}
	}

}
