package me.F_o_F_1092.WeatherVote;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.F_o_F_1092.WeatherVote.PluginManager.CommandListener;
import me.F_o_F_1092.WeatherVote.PluginManager.HelpPageListener;
import me.F_o_F_1092.WeatherVote.PluginManager.UpdateListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

public class CommandWeatherVote implements CommandExecutor {

	private Main plugin;

	public CommandWeatherVote(Main plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!(cs instanceof Player) || !plugin.useVoteGUI) {
				String replaceCommand = plugin.msg.get("msg.22");
				replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv help (Page)").getColoredCommand());
				cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
			} else {
				Player p = (Player)cs;
				if (plugin.disabledWorlds.contains(p.getWorld().getName())) {
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.4"));
				} else {
					if (WeatherVoteManager.isVotingAtWorld(p.getWorld().getName())) {
						WeatherVote wv = WeatherVoteManager.getVotingAtWorld(p.getWorld().getName());
	
						if (wv.isTimeoutPeriod()) {
							p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.15"));
						} else {
							if (plugin.votingInventoryMessages) {
								cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.20"));
							}
							WeatherVoteManager.openVoteingGUI(p.getName(), p.getWorld().getName());
						}
					} else {
						if (plugin.votingInventoryMessages) {
							cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.20"));
						}
						WeatherVoteManager.openVoteingGUI(p.getName(), p.getWorld().getName());
					}
				}
			}
		} else {
			if (args[0].equalsIgnoreCase("help")) {
				if (!(args.length >= 1 && args.length <= 2)) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv help (Page)").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					if (!(cs instanceof Player)) {
						if (args.length != 1) {
							String replaceCommand = plugin.msg.get("msg.22");
							replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv help (Page)").getColoredCommand());
							cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
						} else {
							HelpPageListener.sendNormalMessage(cs);
						}
					} else {
						Player p = (Player)cs;
							if (args.length == 1) {
							HelpPageListener.sendMessage(p, 0);
						} else {
							if (!HelpPageListener.isNumber(args[1])) {
								String replaceCommand = plugin.msg.get("msg.22");
								replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv help (Page)").getColoredCommand());
								cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
							} else {
								if (Integer.parseInt(args[1]) <= 0 || Integer.parseInt(args[1]) > HelpPageListener.getMaxPlayerPages(p)) {
									String replaceCommand = plugin.msg.get("msg.22");
									replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv help (Page)").getColoredCommand());
									cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
								} else {
									HelpPageListener.sendMessage(p, Integer.parseInt(args[1]) - 1);
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("info")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv info").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					cs.sendMessage("�9-----�f[�9Weather�bVote�f]�9-----");
					cs.sendMessage("�9Version: �b" + UpdateListener.getUpdateStringVersion());
					cs.sendMessage("�9By: �bF_o_F_1092");
					cs.sendMessage("�9WeatherVote: �bhttps://fof1092.de/Plugins/WV");
				}
			} else if (args[0].equalsIgnoreCase("stats")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv stats").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					WeatherVoteStats wvs = new WeatherVoteStats();

					cs.sendMessage(plugin.msg.get("color.1") + "-----" + plugin.msg.get("[WeatherVote]") + plugin.msg.get("color.1") + "-----");
					cs.sendMessage(plugin.msg.get("statsText.1") + plugin.msg.get("color.2") + wvs.getDate());
					cs.sendMessage(plugin.msg.get("statsText.2") + plugin.msg.get("color.2") + wvs.getMoneySpent() + "$");
					cs.sendMessage(plugin.msg.get("statsText.3") + plugin.msg.get("color.2") + wvs.getSunnyVotes());
					cs.sendMessage(plugin.msg.get("statsText.4") + plugin.msg.get("color.2") + wvs.getSunnyYes());
					cs.sendMessage(plugin.msg.get("statsText.5") + plugin.msg.get("color.2") + wvs.getSunnyNo());
					cs.sendMessage(plugin.msg.get("statsText.6") + plugin.msg.get("color.2") + wvs.getSunnyWon());
					cs.sendMessage(plugin.msg.get("statsText.7") + plugin.msg.get("color.2") + wvs.getSunnyLost());
					cs.sendMessage(plugin.msg.get("statsText.8") + plugin.msg.get("color.2") + wvs.getRainyVotes());
					cs.sendMessage(plugin.msg.get("statsText.4") + plugin.msg.get("color.2") + wvs.getRainyYes());
					cs.sendMessage(plugin.msg.get("statsText.5") + plugin.msg.get("color.2") + wvs.getRainyNo());
					cs.sendMessage(plugin.msg.get("statsText.6") + plugin.msg.get("color.2") + wvs.getRainyWon());
					cs.sendMessage(plugin.msg.get("statsText.7") + plugin.msg.get("color.2") + wvs.getRainyLost());
				}
			} else if (args[0].equalsIgnoreCase("sun")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv sun").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					if (!(cs instanceof Player)) {
						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.1"));
					} else {
						Player p = (Player)cs;
						if (!p.hasPermission("WeatherVote.Sun")) {
							cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.2"));
						} else {
							if (plugin.disabledWorlds.contains(p.getWorld().getName())) {
								cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.4"));
							} else {
								if (WeatherVoteManager.isVotingAtWorld(p.getWorld().getName())) {
									WeatherVote wv = WeatherVoteManager.getVotingAtWorld(p.getWorld().getName());

									if (wv.isTimeoutPeriod()) {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.15"));
									} else {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.5"));
									}
								} else {
									WeatherVote wv = null;

									if (WeatherVoteManager.isVaultInUse()) {
										if (!WeatherVoteManager.getVault().has(p, plugin.price)) {
											String text = plugin.msg.get("msg.18");
											text = text.replace("[MONEY]", ((plugin.price * 100) - (WeatherVoteManager.getVault().getBalance(p) * 100)) / 100 + "");
											p.sendMessage(plugin.msg.get("[WeatherVote]") + text);
											
											if (WeatherVoteManager.containsOpenVoteingGUI(p.getName())) {
												WeatherVoteManager.closeVoteingGUI(p.getName(), true);
											}
										} else {
											String text1 = plugin.msg.get("msg.19");
											text1 = text1.replace("[MONEY]", plugin.price + "");
											p.sendMessage(plugin.msg.get("[WeatherVote]") + text1);

											WeatherVoteManager.getVault().withdrawPlayer(p, plugin.price);

											wv = new WeatherVote(p.getWorld().getName(), p.getName(), "Sunny", plugin.price);
										}
									} else {
										wv = new WeatherVote(p.getWorld().getName(), p.getName(), "Sunny", 0.00);

										if (plugin.price > 0.0) {
											System.out.println("\u001B[31m[WeatherVote] ERROR: 007 | The plugin Vault was not found, but a Voting-Price was set in the Config.yml file.\u001B[0m");
										}
									}	
									if (wv != null) {
										if (wv.getAllPlayersAtWorld().size() == 1 || plugin.checkForHiddenPlayers && wv.getAllPlayersAtWorld().size() - wv.getNumberOfHiddenPlayers() <= 1) {
											String text = plugin.msg.get("msg.23");
											text = text.replace("[WEATHER]", plugin.msg.get("text.1"));
											p.sendMessage(plugin.msg.get("[WeatherVote]") + text);
										} else {
											if (plugin.rawMessages) {
												String text2 = plugin.msg.get("rmsg.1");
												text2 = text2.replace("[WEATHER]", plugin.msg.get("text.1"));
												text2 = text2.replace("[\"\",", "[\"\",{\"text\":\"" + plugin.msg.get("[WeatherVote]") + "\"},");
												wv.sendRawMessage(text2);
											} else {
												String text2 = plugin.msg.get("msg.3");
												text2 = text2.replace("[WEATHER]", plugin.msg.get("text.1"));
												wv.sendMessage(plugin.msg.get("[WeatherVote]") + text2);
											}
										}
									}
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("rain")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv rain").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					if (!(cs instanceof Player)) {
						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.1"));
					} else {
						Player p = (Player)cs;
						if (!p.hasPermission("WeatherVote.Rain")) {
							p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.2"));
						} else {
							if (plugin.disabledWorlds.contains(p.getWorld().getName())) {
								cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.4"));
							} else {
								if (WeatherVoteManager.isVotingAtWorld(p.getWorld().getName())) {
									WeatherVote wv = WeatherVoteManager.getVotingAtWorld(p.getWorld().getName());

									if (wv.isTimeoutPeriod()) {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.15"));
									} else {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.5"));
									}
								} else {
									WeatherVote wv = null;

									if (WeatherVoteManager.isVaultInUse()) {
										if (!WeatherVoteManager.getVault().has(p, plugin.price)) {
											String text = plugin.msg.get("msg.18");
											text = text.replace("[MONEY]", ((plugin.price * 100) - (WeatherVoteManager.getVault().getBalance(p) * 100)) / 100 + "");
											p.sendMessage(plugin.msg.get("[WeatherVote]") + text);
											
											if (WeatherVoteManager.containsOpenVoteingGUI(p.getName())) {
												WeatherVoteManager.closeVoteingGUI(p.getName(), true);
											}
										} else {
											String text1 = plugin.msg.get("msg.19");
											text1 = text1.replace("[MONEY]", plugin.price + "");
											p.sendMessage(plugin.msg.get("[WeatherVote]") + text1);

											WeatherVoteManager.getVault().withdrawPlayer(p, plugin.price);

											wv = new WeatherVote(p.getWorld().getName(), p.getName(), "Rainy", plugin.price);
										}
									} else {
										wv = new WeatherVote(p.getWorld().getName(), p.getName(), "Rainy", 0.00);

										if (plugin.price > 0.0) {
											System.out.println("\u001B[31m[ote] ERROR: 007 | The plugin Vault was not found, but a Voting-Price was set in the Config.yml file.\u001B[0m");
										}
									}
									if (wv != null) {
										if (wv.getAllPlayersAtWorld().size() == 1 || plugin.checkForHiddenPlayers && wv.getAllPlayersAtWorld().size() - wv.getNumberOfHiddenPlayers() <= 1) {
											String text = plugin.msg.get("msg.23");
											text = text.replace("[WEATHER]", plugin.msg.get("text.2"));
											p.sendMessage(plugin.msg.get("[WeatherVote]") + text);
										} else {
											if (plugin.rawMessages) {
												String text2 = plugin.msg.get("rmsg.1");
												text2 = text2.replace("[WEATHER]", plugin.msg.get("text.2"));
												text2 = text2.replace("[\"\",", "[\"\",{\"text\":\"" + plugin.msg.get("[WeatherVote]") + "\"},");
												wv.sendRawMessage(text2);
											} else {
												String text2 = plugin.msg.get("msg.3");
												text2 = text2.replace("[WEATHER]", plugin.msg.get("text.2"));
												wv.sendMessage(plugin.msg.get("[WeatherVote]") + text2);
											}
										}
									}
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("yes")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv yes").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					if (!(cs instanceof Player)) {
						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.1"));
					} else {
						Player p = (Player)cs;
						if (!p.hasPermission("WeatherVote.Vote")) {
							p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.2"));
						} else {
							if (!WeatherVoteManager.isVotingAtWorld(p.getWorld().getName()) || (WeatherVoteManager.isVotingAtWorld(p.getWorld().getName()) && WeatherVoteManager.getVotingAtWorld(p.getWorld().getName()).isTimeoutPeriod())) {
								p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.6"));
							} else {
								if (WeatherVoteManager.hasVoted(p.getName(), p.getWorld().getName())) {
									p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.7"));
								} else {
									WeatherVote wv = WeatherVoteManager.getVotingAtWorld(p.getWorld().getName());

									if (wv.isTimeoutPeriod()) {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.6"));
									} else {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.8"));
										wv.voteYes(p.getName());
									}
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("no")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv no").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					if (!(cs instanceof Player)) {
						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.1"));
					} else {
						Player p = (Player)cs;
						if (!p.hasPermission("WeatherVote.Vote")) {
							p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.2"));
						} else {
							if (!WeatherVoteManager.isVotingAtWorld(p.getWorld().getName()) || (WeatherVoteManager.isVotingAtWorld(p.getWorld().getName()) && WeatherVoteManager.getVotingAtWorld(p.getWorld().getName()).isTimeoutPeriod())) {
								p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.6"));
							} else {
								if (WeatherVoteManager.hasVoted(p.getName(), p.getWorld().getName())) {
									p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.7"));
								} else {
									WeatherVote wv = WeatherVoteManager.getVotingAtWorld(p.getWorld().getName());

									if (wv.isTimeoutPeriod()) {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.6"));
									} else {
										p.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.9"));
										wv.voteNo(p.getName());
									}
								}
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("reload")) {
				if (args.length != 1) {
					String replaceCommand = plugin.msg.get("msg.22");
					replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv reload").getColoredCommand());
					cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
				} else {
					if (!cs.hasPermission("WeatherVote.Reload")) {
						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.2"));
					} else {
						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.10"));

						for (World w : Bukkit.getWorlds()) {
							if (plugin.useVoteGUI) {
								if (!plugin.votingGUI.isEmpty()) {
									WeatherVoteManager.closeAllVoteingGUIs(w.getName());
								}
							}

							if (WeatherVoteManager.isVotingAtWorld(w.getName())) {
								WeatherVote wv = WeatherVoteManager.getVotingAtWorld(w.getName());
								if (!wv.timeoutPeriod) {
									if (plugin.useScoreboard) {
										for (Player p : wv.getAllPlayersAtWorld()) {
											wv.removeScoreboard(p.getName());
										}
										
										if (plugin.useBossBarAPI) {
											for (Player p : wv.getAllPlayersAtWorld()) {
												wv.removeBossBar(p.getName());
											}
										}
										
										wv.cancelTimer(1);
										wv.cancelTimer(2);
										wv.cancelTimer(3);
									}
									wv.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.13"));
								}
							}
						}

						plugin.votes.clear();
						plugin.disabledWorlds.clear();

						CommandListener.clearCommands();
						
						File fileConfig = new File("plugins/WeatherVote/Config.yml");
						FileConfiguration ymlFileConfig = YamlConfiguration.loadConfiguration(fileConfig);

						if(!fileConfig.exists()) {
							plugin.disabledWorlds.add("world_nether");
							plugin.disabledWorlds.add("world_the_end");

							try {
								ymlFileConfig.save(fileConfig);
								ymlFileConfig.set("Version", UpdateListener.getUpdateDoubleVersion());
								ymlFileConfig.set("VotingTime", 35);
								ymlFileConfig.set("RemindingTime", 25);
								ymlFileConfig.set("TimeoutPeriod", 15);
								ymlFileConfig.set("UseScoreboard", true);
								ymlFileConfig.set("UseVoteGUI", true);
								ymlFileConfig.set("UseBossBarAPI", true);
								ymlFileConfig.set("UseTitleAPI", true);
								ymlFileConfig.set("PrematureEnd", true);
								ymlFileConfig.set("Price", 0.00);
								ymlFileConfig.set("RawMessages", true);
								ymlFileConfig.set("DisabledWorld", plugin.disabledWorlds);
								ymlFileConfig.set("VotingInventoryMessages", true);
								ymlFileConfig.save(fileConfig);
							} catch (IOException e1) {
								System.out.println("\u001B[31m[WeatherVote] Can't create the Config.yml. [" + e1.getMessage() +"]\u001B[0m");
							}

							plugin.disabledWorlds.clear();
						}

						plugin.votingTime = ymlFileConfig.getLong("VotingTime");
						plugin.remindingTime = ymlFileConfig.getLong("RemindingTime");
						plugin.timeoutPeriod = ymlFileConfig.getLong("TimeoutPeriod");
						plugin.useScoreboard = ymlFileConfig.getBoolean("UseScoreboard");
						plugin.useVoteGUI = ymlFileConfig.getBoolean("UseVoteGUI");
						plugin.prematureEnd = ymlFileConfig.getBoolean("PrematureEnd");
						plugin.price = ymlFileConfig.getDouble("Price");
						plugin.rawMessages = ymlFileConfig.getBoolean("RawMessages");
						plugin.disabledWorlds.addAll(ymlFileConfig.getStringList("DisabledWorld"));
						plugin.votingInventoryMessages = ymlFileConfig.getBoolean("VotingInventoryMessages");

						File fileMessages = new File("plugins/WeatherVote/Messages.yml");
						FileConfiguration ymlFileMessage = YamlConfiguration.loadConfiguration(fileMessages);

						if(!fileMessages.exists()) {
							try {
								ymlFileMessage.save(fileMessages);
								ymlFileMessage.set("Version", UpdateListener.getUpdateDoubleVersion());
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
								ymlFileMessage.set("Message.16", "There is a new update available for this plugin. &b( https://fof1092.de/Plugins/WV )&9");
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
								ymlFileMessage.set("RawMessage.1", "[\"\",{\"text\":\"There is a new voting for \",\"color\":\"blue\"},{\"text\":\"[WEATHER]\",\"color\":\"aqua\"},{\"text\":\" weather, vote with \",\"color\":\"blue\"},{\"text\":\"/wv yes\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/wv yes\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"/wv yes\",\"color\":\"aqua\"}]}}},{\"text\":\" or \",\"color\":\"blue\"},{\"text\":\"/wv no\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/wv no\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"/wv no\",\"color\":\"aqua\"}]}}},{\"text\":\".\",\"color\":\"blue\"}]");
								ymlFileMessage.save(fileMessages);
							} catch (IOException e1) {
								System.out.println("\u001B[31m[WeatherVote] Can't create the Messages.yml. [" + e1.getMessage() +"]\u001B[0m");
							}
						}

						plugin.msg.put("[WeatherVote]", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("[WeatherVote]")));
						plugin.msg.put("color.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("Color.1")));
						plugin.msg.put("color.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("Color.2")));
						plugin.msg.put("msg.1", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.1")));
						plugin.msg.put("msg.2", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.2")));
						plugin.msg.put("msg.3", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.3")));
						plugin.msg.put("msg.4", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.4")));
						plugin.msg.put("msg.5", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.5")));
						plugin.msg.put("msg.6", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.6")));
						plugin.msg.put("msg.7", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.7")));
						plugin.msg.put("msg.8", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.8")));
						plugin.msg.put("msg.9", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.9")));
						plugin.msg.put("msg.10", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.10")));
						plugin.msg.put("msg.11", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.11")));
						plugin.msg.put("msg.12", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.12")));
						plugin.msg.put("msg.13", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.13")));
						plugin.msg.put("msg.14", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.14")));
						plugin.msg.put("msg.15", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.15")));
						plugin.msg.put("msg.16", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.16")));
						plugin.msg.put("msg.17", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.17")));
						plugin.msg.put("msg.18", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.18")));
						plugin.msg.put("msg.19", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.19")));
						plugin.msg.put("msg.20", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.20")));
						plugin.msg.put("msg.21", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.21")));
						plugin.msg.put("msg.22", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.22")));
						plugin.msg.put("msg.23", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("Message.23")));
						plugin.msg.put("text.1", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.2") + ymlFileMessage.getString("Text.1")));
						plugin.msg.put("text.2", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.2") + ymlFileMessage.getString("Text.2")));
						plugin.msg.put("text.3", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.2") + ymlFileMessage.getString("Text.3")));
						plugin.msg.put("text.4", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.2") + ymlFileMessage.getString("Text.4")));
						plugin.msg.put("statsText.1", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.1")));
						plugin.msg.put("statsText.2", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.2")));
						plugin.msg.put("statsText.3", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.3")));
						plugin.msg.put("statsText.4", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.4")));
						plugin.msg.put("statsText.5", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.5")));
						plugin.msg.put("statsText.6", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.6")));
						plugin.msg.put("statsText.7", ChatColor.translateAlternateColorCodes('&', plugin.msg.get("color.1") + ymlFileMessage.getString("StatsText.7")));
						plugin.msg.put("votingInventoryTitle.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("VotingInventoryTitle.1")));
						plugin.msg.put("votingInventoryTitle.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("VotingInventoryTitle.2")));
						plugin.msg.put("bossBarAPIMessage", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("BossBarAPIMessage")));
						plugin.msg.put("titleAPIMessage.Title.1", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.1")));
						plugin.msg.put("titleAPIMessage.Title.2", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.2")));
						plugin.msg.put("titleAPIMessage.Title.3", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.3")));
						plugin.msg.put("titleAPIMessage.Title.4", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.Title.4")));
						plugin.msg.put("titleAPIMessage.SubTitle", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("TitleAPIMessage.SubTitle")));
						plugin.msg.put("rmsg.1", ymlFileMessage.getString("RawMessage.1"));

						
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv help (Page)", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.1"))));
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv info", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.2"))));
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv stats", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.3"))));
						if (plugin.useVoteGUI) {
							CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv", null, ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.4"))));
						}
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv sun", "WeatherVote.Sun", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.5"))));
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv rain", "WeatherVote.Rain", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.6"))));
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv yes", "WeatherVote.Vote", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.7"))));
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv no", "WeatherVote.Vote", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.8"))));
						CommandListener.addCommand(new me.F_o_F_1092.WeatherVote.PluginManager.Command("/wv reload", "WeatherVote.Reload", ChatColor.translateAlternateColorCodes('&', ymlFileMessage.getString("HelpText.9"))));
						
						
						File fileStats = new File("plugins/WeatherVote/Stats.yml");
						FileConfiguration ymlFileStats = YamlConfiguration.loadConfiguration(fileStats);

						if(!fileStats.exists()){
							try {
								ymlFileStats.save(fileStats);
								ymlFileStats.set("Version", UpdateListener.getUpdateDoubleVersion());
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
								System.out.println("\u001B[31m[WeatherVote] Can't create the Stats.yml. [" + e1.getMessage() +"]\u001B[0m");
							}
						}

						cs.sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.11"));
					}
				}
			} else {
				String replaceCommand = plugin.msg.get("msg.22");
				replaceCommand = replaceCommand.replace("[COMMAND]", CommandListener.getCommand("/wv help (Page)").getColoredCommand());
				cs.sendMessage(plugin.msg.get("[WeatherVote]") + replaceCommand); 
			}
		}
		return true;
	}

}
