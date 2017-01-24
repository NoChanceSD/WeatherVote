package me.F_o_F_1092.WeatherVote;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import com.connorlinfoot.titleapi.TitleAPI;

import net.md_5.bungee.api.chat.TextComponent;

public class WeatherVote {

	private static Main plugin = (Main) Bukkit.getPluginManager().getPlugin("WeatherVote");

	String worldName;
	ArrayList<String> players = new ArrayList<String>();
	String weather;
	int yes;
	int no;
	Integer task1;
	Integer task2;
	Integer task3;
	boolean timeoutPeriod;
	double moneySpend;
	boolean onePlayerVoting = false;
	BossBar bossBar;
	private Scoreboard previousScoreboard;

	WeatherVote(String worldName, String player, String time, double moneySpend) {
		if (plugin.useVoteGUI) {
			if (!plugin.votingGUI.isEmpty()) {
				WeatherVoteManager.closeAllVoteingGUIs(worldName);
			}
		}

		plugin.votes.put(worldName, this);
		previousScoreboard = Bukkit.getPlayer(player).getScoreboard();
		this.worldName = worldName;
		this.players.add(player);
		this.weather = time;
		timeoutPeriod = false;
		this.moneySpend = moneySpend;

		if (getAllPlayersAtWorld().size() == 1 || plugin.checkForHiddenPlayers && getAllPlayersAtWorld().size() - getNumberOfHiddenPlayers() <= 1) {
			this.onePlayerVoting = true;

			this.voteYes(player);

			startTimer(2, 0);
		} else {
			if (plugin.useScoreboard) {
				for (Player p : getAllPlayersAtWorld()) {
					setScoreboard(p.getName());
				}
				updateScore();
			}

			if (plugin.useBossBarAPI) {
				String timeString = plugin.msg.get("bossBarAPIMessage");
				if (getWeather().equals("Sunny")) {
					timeString = timeString.replace("[WEATHER]", plugin.msg.get("text.1"));
				} else {
					timeString = timeString.replace("[WEATHER]", plugin.msg.get("text.2"));
				}

				bossBar = BossBarAPI.addBar(getAllPlayersAtWorld(), new TextComponent(timeString), BossBarAPI.Color.BLUE, BossBarAPI.Style.NOTCHED_20, 1.0f, 20, plugin.votingTime);

			}

			if (plugin.useTitleAPI) {
				for (Player p : getAllPlayersAtWorld()) {
					String timeString = plugin.msg.get("titleAPIMessage.Title.1");
					if (getWeather().equals("Sunny")) {
						timeString = timeString.replace("[WEATHER]", plugin.msg.get("text.1"));
					} else {
						timeString = timeString.replace("[WEATHER]", plugin.msg.get("text.2"));
					}

					TitleAPI.sendTitle(p, 10, 60, 10, timeString, plugin.msg.get("titleAPIMessage.SubTitle"));
				}
			}

			this.voteYes(player);

			startTimer(1, plugin.remindingTime);
			startTimer(2, plugin.votingTime);
		}
		startTimer(3, (plugin.timeoutPeriod + plugin.votingTime));
	}

	void setScoreboard(String player) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = sb.registerNewObjective("WeatherVote", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		if (getWeather().equals("Sunny")) {
			try {
				objective.setDisplayName(plugin.msg.get("[WeatherVote]") + plugin.msg.get("text.1"));
			} catch (Exception e1) {
				objective.setDisplayName("§f[§9Weather§bVote§f] SUNNY");
				System.out.println("\u001B[31m[WeatherVote] The scoreboard name caused a problem. (Message: text.1) [" + e1.getMessage() + "]\u001B[0m");
			}
		} else {
			try {
				objective.setDisplayName(plugin.msg.get("[WeatherVote]") + plugin.msg.get("text.2"));
			} catch (Exception e1) {
				objective.setDisplayName("§f[§9Weather§bVote§f] RAINY");
				System.out.println("\u001B[31m[WeatherVote] The scoreboard name caused a problem. (Message: text.2) [" + e1.getMessage() + "]\u001B[0m");
			}
		}
		Bukkit.getPlayer(player).setScoreboard(sb);
	}

	void removeScoreboard(String player) {
		try {
			Bukkit.getPlayer(player).getScoreboard().getObjective("WeatherVote").unregister();
			if (previousScoreboard != null)
				Bukkit.getPlayer(player).setScoreboard(previousScoreboard);
		} catch (Exception e1) {
			System.out.println("\u001B[31m[WeatherVote] The scoreboard could not be removed from the Player. [" + e1.getMessage() + "]\u001B[0m");
		}
	}

	void setBossBar(String player) {
		bossBar.addPlayer(Bukkit.getPlayer(player));
	}

	void removeBossBar(String player) {
		bossBar.removePlayer(Bukkit.getPlayer(player));
	}

	void updateScore() {
		for (Player p : getAllPlayersAtWorld()) {
			Player player = Bukkit.getPlayer(p.getName());
			if (player.getScoreboard() == null)
				continue;
			Objective objective = player.getScoreboard().getObjective("WeatherVote");
			Score scoreYes;
			try {
				scoreYes = objective.getScore(plugin.msg.get("text.3"));
			} catch (Exception e1) {
				scoreYes = objective.getScore(plugin.msg.get("text.2") + "YES");
				System.out.println("\u001B[31m[WeatherVote] The scoreboard text for YES caused a problem. (Message: text.3) [" + e1.getMessage() + "]\u001B[0m");
			}
			scoreYes.setScore(getYesVotes());
			Score scoreNo;
			try {
				scoreNo = objective.getScore(plugin.msg.get("text.4"));
			} catch (Exception e1) {
				scoreNo = objective.getScore(plugin.msg.get("text.2") + "NO");
				System.out.println("\u001B[31m[WeatherVote] The scoreboard text for NO caused a problem. (Message: text.4) [" + e1.getMessage() + "]\u001B[0m");
			}
			scoreNo.setScore(getNoVotes());
		}
	}

	void cancelTimer(int task) {
		if (task == 1) {
			if (task1 != null) {
				Bukkit.getServer().getScheduler().cancelTask(task1);
				task1 = null;
			}
		}

		if (task == 2) {
			if (task2 != null) {
				Bukkit.getServer().getScheduler().cancelTask(task2);
				task2 = null;
			}
		}

		if (task == 3) {
			if (task3 != null) {
				Bukkit.getServer().getScheduler().cancelTask(task3);
				task3 = null;
			}
		}
	}

	void startTimer(int task, long remindingTime) {
		if (task == 1) {
			if (plugin.remindingTime > 0) {
				task1 = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						String text = plugin.msg.get("msg.14");
						if (getWeather().equals("Sunny")) {
							text = text.replace("[WEATHER]", plugin.msg.get("text.1"));
						} else {
							text = text.replace("[WEATHER]", plugin.msg.get("text.2"));
						}
						text = text.replace("[SECONDS]", (plugin.votingTime - plugin.remindingTime) + "");

						sendMessage(plugin.msg.get("[WeatherVote]") + text);

						if (plugin.useTitleAPI) {
							for (Player p : getAllPlayersAtWorld()) {
								String secondsLeftString = plugin.msg.get("titleAPIMessage.Title.2");
								secondsLeftString = secondsLeftString.replace("[SECONDS]", (plugin.votingTime - plugin.remindingTime) + "");

								TitleAPI.sendTitle(p, 10, 60, 10, secondsLeftString, plugin.msg.get("titleAPIMessage.SubTitle"));
							}
						}

						task1 = null;
					}
				}, remindingTime * 20L);
			}
		}

		if (task == 2) {
			task2 = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					WeatherVoteStats wvs = new WeatherVoteStats();

					if (yes > no) {
						if (!onePlayerVoting) {
							sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.12"));
						}

						if (weather.equals("Sunny")) {
							Bukkit.getWorld(worldName).setStorm(false);
							wvs.setSunnyStats(getYesVotes(), getNoVotes(), true, moneySpend);
						} else {
							Bukkit.getWorld(worldName).setStorm(true);
							wvs.setRainyStats(getYesVotes(), getNoVotes(), true, moneySpend);
						}
					} else {
						sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.13"));

						if (weather.equals("Sunny")) {
							wvs.setSunnyStats(getYesVotes(), getNoVotes(), false, moneySpend);
						} else {
							wvs.setRainyStats(getYesVotes(), getNoVotes(), false, moneySpend);
						}
					}

					if (!onePlayerVoting) {
						if (plugin.useScoreboard) {
							for (Player p : getAllPlayersAtWorld()) {
								removeScoreboard(p.getName());
							}
						}
					}

					if (plugin.useBossBarAPI) {
						for (Player p : getAllPlayersAtWorld()) {
							removeBossBar(p.getName());
						}
					}

					if (plugin.useTitleAPI) {
						for (Player p : getAllPlayersAtWorld()) {
							String endingString;
							if (yes > no) {
								endingString = plugin.msg.get("titleAPIMessage.Title.3");
							} else {
								endingString = plugin.msg.get("titleAPIMessage.Title.4");
							}

							TitleAPI.sendTitle(p, 10, 60, 10, endingString, null);
						}
					}

					if (plugin.useVoteGUI) {
						if (!plugin.votingGUI.isEmpty()) {
							WeatherVoteManager.closeAllVoteingGUIs(worldName);
						}
					}

					timeoutPeriod = true;

					task2 = null;
				}
			}, remindingTime * 20L);
		}

		if (task == 3) {
			task3 = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.votes.remove(worldName);
					timeoutPeriod = false;

					task3 = null;
				}
			}, remindingTime * 20L);
		}
	}

	boolean hasVoted(String player) {
		return this.players.contains(player);
	}

	String getWorld() {
		return this.worldName;
	}

	String getWeather() {
		return this.weather;
	}

	boolean checkPrematureEnd() {
		for (Player p : getAllPlayersAtWorld()) {
			if (!players.contains(p.getName())) {
				return false;
			}
		}
		return true;
	}

	void prematureEnd() {
		sendMessage(plugin.msg.get("[WeatherVote]") + plugin.msg.get("msg.17"));

		cancelTimer(1);
		cancelTimer(2);
		cancelTimer(3);

		startTimer(2, 0L);
		startTimer(3, (plugin.timeoutPeriod + plugin.votingTime));
	}

	void voteYes(String player) {
		this.players.add(player);
		this.yes++;

		if (!this.onePlayerVoting) {
			if (plugin.useScoreboard) {
				updateScore();
			}

			if (plugin.prematureEnd) {
				if (checkPrematureEnd()) {
					prematureEnd();
				}
			}
		}
	}

	void voteNo(String player) {
		this.players.add(player);
		this.no++;

		if (!this.onePlayerVoting) {
			if (plugin.useScoreboard) {
				updateScore();
			}

			if (plugin.prematureEnd) {
				if (checkPrematureEnd()) {
					prematureEnd();
				}
			}
		}
	}

	int getYesVotes() {
		return this.yes;
	}

	int getNoVotes() {
		return this.no;
	}

	boolean isTimeoutPeriod() {
		return timeoutPeriod;
	}

	List<Player> getAllPlayersAtWorld() {
		List<Player> players = new ArrayList<Player>();
		for (Player p : Bukkit.getWorld(this.worldName).getPlayers()) {
			if (!players.contains(p.getName()) && !plugin.checkForHiddenPlayers || !players.contains(p.getName()) && plugin.checkForHiddenPlayers && !isHidden(p)) {
				players.add(p);
			}
		}
		return players;
	}

	void sendMessage(String message) {
		for (Player p : getAllPlayersAtWorld()) {
			p.sendMessage(message);
		}
	}

	void sendRawMessage(String message) {
		for (Player p : getAllPlayersAtWorld()) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " " + message);
		}
	}

	int getNumberOfHiddenPlayers() {
		int hiddenPlayers1 = 0;
		for (Player p1 : getAllPlayersAtWorld()) {
			int hiddenPlayers2 = 0;
			for (Player p2 : getAllPlayersAtWorld()) {
				if (!p2.canSee(p1)) {
					hiddenPlayers2++;
				}
			}

			if (hiddenPlayers2 >= getAllPlayersAtWorld().size() / 2) {
				hiddenPlayers1++;
			}
		}
		return hiddenPlayers1;
	}

	boolean isHidden(Player p1) {
		int hiddenPlayers2 = 0;

		for (Player p2 : getAllPlayersAtWorld()) {
			if (!p2.canSee(p1)) {
				hiddenPlayers2++;
			}
		}

		if (hiddenPlayers2 >= getAllPlayersAtWorld().size() / 2) {
			return true;
		} else {
			return false;
		}
	}
}
