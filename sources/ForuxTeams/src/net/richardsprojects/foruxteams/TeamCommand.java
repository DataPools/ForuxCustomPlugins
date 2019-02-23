package net.richardsprojects.foruxteams;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

	private ForuxTeams plugin;

	public TeamCommand(ForuxTeams plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] args) {
		if (args.length >= 1) {
			if (arg0 instanceof Player) {
				Player p = (Player) arg0;
				if (args[0].equals("create")) {
					if (plugin.getPlayersTeam(p.getUniqueId()) == null) {
						int teamId = plugin.createNewTeam(p.getUniqueId());
						p.sendMessage(ChatColor.GREEN + "Team created!");
					} else {
						String msg = ChatColor.RED + "You are already part of a team.";
						p.sendMessage(msg);
					}
					return true;
				} else if (args[0].equals("invite")) {
					Team cTeam = plugin.getPlayersTeam(p.getUniqueId());
					if (cTeam == null) {
						String msg = ChatColor.RED + "You are not part of a team.";
						p.sendMessage(msg);
					} else {
						if (cTeam.getLeader().equals(p.getUniqueId())) {
							if (args.length == 1) {
								String msg = ChatColor.RED + "You must specify a player to invite.";
								String msg2 = ChatColor.RED + "/team invite <player>";
								p.sendMessage(msg);
								p.sendMessage(msg2);
							} else if (args.length > 1) {
								Player invite = plugin.getServer().getPlayer(args[1]);
								if (invite != null) {
									if (plugin.getPlayersTeam(invite.getUniqueId()) != null) {
										String msg = ChatColor.RED + "That player is already on a team";
										p.sendMessage(msg);
									} else {
										// invite and notify everyone
										cTeam.addPendingMember(invite.getUniqueId());
										cTeam.sendMessage(plugin, invite.getName() + " has been invited to the team.");
										
										// notify player who has been invited
										String msg = ChatColor.GREEN + "You have been invited to " + ChatColor.RESET + p.getName() + "'s" + ChatColor.GREEN + " Team.";
										invite.sendMessage(msg);
										msg = ChatColor.GREEN + "This request expires in 30 seconds.";
										invite.sendMessage(msg);
										ComponentBuilder cb = new ComponentBuilder(" - ");
										cb.color(ChatColor.GREEN);
										cb.append(ChatColor.BOLD + "Accept")
										        .event(new ClickEvent(
										            ClickEvent.Action.RUN_COMMAND,
										            "/team accept"));
										cb.append(" ");
										cb.append(ChatColor.RED + "" + ChatColor.BOLD + "Reject")
												.event(new ClickEvent(
														ClickEvent.Action.RUN_COMMAND,
														"/team reject"));
										invite.spigot().sendMessage(cb.create());
									}
								} else {
									String msg = ChatColor.RED + "The player must be online.";
									p.sendMessage(msg);
								}
							}
						} else {
							String msg = ChatColor.RED + "You must be the team leader to invite other people to the team.";
							p.sendMessage(msg);
						}
					}
					return true;
				} else if (args[0].equals("teleport")) {
					if (args.length == 1) {
						String msg = ChatColor.RED + "You must specify a player to teleport to.";
						String msg2 = ChatColor.RED + "/team teleport <player>";
						p.sendMessage(msg);
						p.sendMessage(msg2);
					} else if (args.length > 1) {
						Player teleport = plugin.getServer().getPlayer(args[1]);
						if (teleport != null) {
							Team otherPlayersTeam = plugin.getPlayersTeam(teleport.getUniqueId());
							Team thisPlayersTeam = plugin.getPlayersTeam(p.getUniqueId());
							if (thisPlayersTeam == null || otherPlayersTeam == null) {
								p.sendMessage(ChatColor.RED + "Both of you must be on a team.");
								return true;
							} else {
								if (thisPlayersTeam.getId() == otherPlayersTeam.getId()) {
									thisPlayersTeam.sendMessage(plugin, "Teleporting " + p.getName() + " to " + teleport.getName() + "...");
									p.teleport(teleport);
									return true;
								} else {
									p.sendMessage(ChatColor.RED + "Both of you must be on the same team.");
									return true;
								}
							}
						} else {
							String msg = ChatColor.RED + "The player must be online.";
							p.sendMessage(msg);
						}
					}
				} else if (args[0].equals("leave")) {
					Team cTeam = plugin.getPlayersTeam(p.getUniqueId());
					if (cTeam == null) {
						String msg = ChatColor.RED + "You are not part of a team.";
						p.sendMessage(msg);
						return true;
					} else {
						if (cTeam.getLeader().equals(p.getUniqueId())) {
							// leader is leaving - disband team
							cTeam.sendMessage(plugin, p.getName() + " has left and the team has been disbanded.");
							plugin.disbandTeam(cTeam.getId());
						} else {
							// individual player is leaving
							if (cTeam.removeMember(p.getUniqueId())) {
								cTeam.sendMessage(plugin, p.getName() + " has left the team.");
							}
							p.sendMessage(ChatColor.GREEN + "[Team] " + ChatColor.RESET + "You have left your team.");
							return true;
						}
					}
				} else if (args[0].equals("kick")) {
					Team cTeam = plugin.getPlayersTeam(p.getUniqueId());
					if (args.length == 1) {
						String msg = ChatColor.RED + "You must specify a player.";
						p.sendMessage(msg);
					} else if (args.length > 1) {
						if (cTeam != null) {
							if (cTeam.getLeader().equals(p.getUniqueId())) {
								UUID kick = plugin.getPlayerUUID(args[1]);
								if (kick != null) {
									if (cTeam.hasMember(kick)) {
										if (cTeam.removeMember(kick)) {
											cTeam.sendMessage(plugin, args[1] + " has been kicked from the team.");
										}
										
										// notify player who has been kicked
										Player tmp = plugin.getServer().getPlayer(kick);
										if (tmp != null) {
											tmp.sendMessage(ChatColor.DARK_GREEN + "[Team] " + ChatColor.RESET + "You have been kicked by " + p.getName());
										}
									} else {
										String msg = ChatColor.RED + "That player is not a member of your team.";
										p.sendMessage(msg);
									}
								} else {
									String msg = ChatColor.RED + "That player has not been online before.";
									p.sendMessage(msg);
								}
							} else {
								String msg = ChatColor.RED + "You must be the team leader in order to kick someone.";
								p.sendMessage(msg);
							}
						} else {
							String msg = ChatColor.RED + "You are not part of a team.";
							p.sendMessage(msg);
						}
					}
					return true;
				} else if (args[0].equals("chat")) {
					Team cTeam = plugin.getPlayersTeam(p.getUniqueId());
					if (cTeam == null) {
						String msg = ChatColor.RED + "You are not part of a team.";
						p.sendMessage(msg);
					} else {
						// calculate chat message
						String cMsg = "";
						for (int i = 1; i < args.length; i++) {
							cMsg = cMsg + args[i] + " ";
						}
						if (cMsg.length() > 0) cMsg = cMsg.substring(0, cMsg.length() - 1);
						
						if (cMsg.equals("")) {
							String m = ChatColor.RED + "You must enter a message.";
							p.sendMessage(m);
						} else {
							String chatMsg = arg0.getName() + ": " + cMsg;
							cTeam.sendMessage(plugin, chatMsg);
						}
					}
					return true;
				} else if (args[0].equals("list")) {
					Team cTeam = plugin.getPlayersTeam(p.getUniqueId());
					if (cTeam == null) {
						String msg = ChatColor.RED + "You are not part of a team.";
						p.sendMessage(msg);
					} else {
						String line1 = ChatColor.DARK_GREEN + " ------ Your Team ------";
						String lName = plugin.getPlayerName(cTeam.getLeader());
						String line2 = ChatColor.DARK_GREEN + " Leader: " + ChatColor.WHITE + lName;
						String line3 = ChatColor.DARK_GREEN + " Members: " + ChatColor.WHITE;
						if (cTeam.getTotalMembers() == 0) {
							line3 = line3 + "None";
						} else {
							String mList = "";
							for (int i = 0; i < cTeam.getTotalMembers(); i++) {
								if (i < 3) {
									mList = mList + ", " + plugin.getPlayerName(cTeam.getMember(i));
								}
							}
							mList = mList.substring(1);
							line3 = line3 + mList;
						}
						
						p.sendMessage(line1);
						p.sendMessage(line2);
						p.sendMessage(line3);
					}
					return true;
				} else if (args[0].equals("help")) {
					String msg1 = ChatColor.DARK_GREEN + " ------ Team Help ------ ";
					String msg2 = "/team create - Creates a new team in which you are the leader";
					String msg3 = "/team chat <message> - Talk in your team's chat.";
					String msg4 = "/team help - Shows this help menu.";
					String msg5 = "/team invite <player> - Used to invite other players to your team.";
					String msg6 = "/team kick <player> - Kick a player from your team. Must be team leader.";
					String msg7 = "/team leave - Leave the team. If the leader leaves the team is disbanded.";
					String msg8 = "/team list - Lists all members on your team. Must be on a team.";
					String msg9 = "/team teleport <player> - Allows you to teleport to another teammate.";
					
					p.sendMessage(msg1);
					p.sendMessage(msg2);
					p.sendMessage(msg3);
					p.sendMessage(msg4);
					p.sendMessage(msg5);
					p.sendMessage(msg6);
					p.sendMessage(msg7);
					p.sendMessage(msg8);
					p.sendMessage(msg9);
					
				} else if (args[0].equals("reject")) {
					Team cTeam = plugin.getPlayersTeamInvite(p.getUniqueId());
					if (cTeam == null) {
						String msg = ChatColor.RED + "You have not been invited to a team.";
						p.sendMessage(msg);
					} else {
						cTeam.removePendingMember(p.getUniqueId()); // remove pending member
						cTeam.sendMessage(plugin, p.getName() + " has declined to join the team.");
					}
				} else if (args[0].equals("accept")) {
					Team cTeam = plugin.getPlayersTeamInvite(p.getUniqueId());
					if (cTeam == null) {
						String msg = ChatColor.RED + "You have not been invited to a team.";
						p.sendMessage(msg);
					} else {
						cTeam.removePendingMember(p.getUniqueId()); // remove pending member
						
						if (cTeam.getTotalMembers() < 3) {
							if (cTeam.addMember(p.getUniqueId())) {
								cTeam.sendMessage(plugin, p.getName() + " has joined the team.");
							}
						} else {
							p.sendMessage(ChatColor.RED + "Sorry! That team is now full.");
						}
					}
					
					return true;
				}
			} else {
				arg0.sendMessage(ChatColor.RED
						+ "Only players can use this command.");
				return true;
			}
		} else {
			arg0.sendMessage(ChatColor.RED + "Use /team help for a list of commands.");
			return true;
		}
		return false;
	}
}
