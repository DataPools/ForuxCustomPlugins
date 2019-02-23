package com.exloki.core_foruxe.commands;

import com.exloki.core_foruxe.LPlugin;
import com.exloki.core_foruxe.exceptions.InvalidArgumentsException;
import com.exloki.core_foruxe.exceptions.PlayerNotFoundException;
import com.exloki.foruxe.TL;
import com.exloki.core_foruxe.utils.StringPair;
import com.exloki.core_foruxe.utils.Txt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class LCommand implements ICommand
{
	private final transient String name;
	protected transient LPlugin plugin;
	
	protected String usageString;
	protected String descString;
	protected List<StringPair> helpMessages;

	// Command specific variables
	protected CommandSender sender;
	protected String label;
	protected Player player;
	protected boolean isConsole;
	protected List<String> args;

	protected LCommand(final String name)
	{
		this.name = name;
	}
	
	protected LCommand(final String name, final String usage)
	{
		this.name = name;
		this.usageString = usage;
	}
	
	protected LCommand(final String name, final String usage, final String description)
	{
		this.name = name;
		this.usageString = usage;
		this.descString = description;
	}
	
	private void setUsageAndDesc()
	{
		Map<String, Object> commandMap = plugin.getDescription().getCommands().get(name);
		if(commandMap == null) return; // But why..
		
		Object match;
		if(usageString == null || usageString.isEmpty())
		{
			match = commandMap.get("usage");
			if(match != null)
				usageString = String.valueOf(match);
		}
		
		if(descString == null || descString.isEmpty())
		{
			match = commandMap.get("description");
			if(match != null)
				descString = String.valueOf(match);
		}
	}

	@Override
	public void setPlugin(final LPlugin plugin)
	{
		this.plugin = plugin;
		setUsageAndDesc(); // Setup after we have the plugin, for defaults
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return descString == null ? "*unavailable*" : descString;
	}

	@Override
	public String getUsage()
	{
		return usageString == null ? "*unavailable*" : usageString.replaceAll("<command>", label == null ? name : label);
	}
	
	@Override
	public boolean hasHelpMessages()
	{
		return helpMessages != null && !helpMessages.isEmpty();
	}
	
	@Override
	public List<StringPair> getHelpMessages()
	{
		return helpMessages;
	}
	
	public void setHelpMessages(List<StringPair> formattedMessages)
	{
		helpMessages = formattedMessages;
	}
	
	public void addHelpMessage(String message, String permission)
	{
		if(helpMessages == null) helpMessages = new ArrayList<StringPair>();
		helpMessages.add(new StringPair(Txt.parseColor(message), permission));
	}
	
	public void addHelpMessage(String message)
	{
		addHelpMessage(message, "");
	}
	
	/*
	 * Send usage / help
	 */
	
	protected void sendUsage()
	{
		sender.sendMessage(TL.ER_USAGE.withVars(getUsage()));
	}
	
	protected void sendHelpMessages()
	{
		if(helpMessages == null || helpMessages.size() < 1)
		{
			sendUsage();
			return;
		}
		
		for(int k = 0; k < helpMessages.size(); k++)
		{
			StringPair pair = helpMessages.get(k);
			if(pair.isOneSet() && !player.hasPermission(pair.getOne()))
				continue;
			
			player.sendMessage(pair.getZero());
		}
	}
	
	/*
	 * Command Processing / Execution
	 */

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final Command cmd, final String[] args) throws Exception
	{
		// Setup execution time specific variables
		this.sender = sender;
		this.label = commandLabel;
		this.isConsole = !(sender instanceof Player);
		this.player = isConsole ? null : (Player) sender;
		this.args = Arrays.asList(args);

		perform(commandLabel, cmd);
	}

	protected abstract void perform(final String commandLabel, final Command cmd) throws Exception;

	/*
	 * Finding Players
	 */
	
	protected Player getPlayer(UUID uuid) throws PlayerNotFoundException
	{
		return getPlayer(uuid, true);
	}
	
	protected Player getPlayer(UUID uuid, boolean throwException) throws PlayerNotFoundException
	{
		Player found = Bukkit.getPlayer(uuid);
		if(found == null)
		{
			if(throwException)
				throw new PlayerNotFoundException();
			return null;
		}

		return found;
	}

	protected Player getPlayer(final String searchTerm) throws PlayerNotFoundException
	{
		return getPlayer(searchTerm, true);
	}
	
	protected Player getPlayer(final String searchTerm, boolean throwException) throws PlayerNotFoundException
	{
		return getPlayer(Bukkit.getServer(), searchTerm, throwException);
	}

	protected Player getPlayer(final Server server, final String searchTerm, boolean throwException) throws PlayerNotFoundException
	{
		Player exPlayer;

		try
		{
			exPlayer = server.getPlayer(UUID.fromString(searchTerm));
		}
		catch (IllegalArgumentException ex)
		{
			exPlayer = server.getPlayer(searchTerm);
		}

		if (exPlayer != null)
			return exPlayer;

		final List<Player> matches = server.matchPlayer(searchTerm);

		if (matches.isEmpty())
		{
			final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
			for (Player player : Bukkit.getOnlinePlayers())
			{
				final String displayName = Txt.stripColor(player.getName()).toLowerCase(Locale.ENGLISH);
				if (displayName.contains(matchText))
				{
					return player;
				}
			}
		}
		else
		{
			for (Player player : matches)
			{
				if (player.getDisplayName().startsWith(searchTerm))
				{
					return player;
				}
			}
			
			return matches.get(0);
		}
		
		if(throwException)
			throw new PlayerNotFoundException();
		return null;
	}

	protected Player getPlayer(final int argIndex) throws PlayerNotFoundException, InvalidArgumentsException
	{
		if (args.size() <= argIndex)
		{
			throw new InvalidArgumentsException();
		}
		if (args.get(argIndex).isEmpty())
		{
			throw new PlayerNotFoundException();
		}
		
		return getPlayer(args.get(argIndex));
	}
	
	protected OfflinePlayer getOfflinePlayer(final String searchTerm) throws PlayerNotFoundException
	{
		return getOfflinePlayer(searchTerm, false);
	}
	
	protected OfflinePlayer getOfflinePlayer(final String searchTerm, final boolean deep) throws PlayerNotFoundException
	{
		Server server = Bukkit.getServer();
		OfflinePlayer exPlayer;
		
		try
		{
			return getPlayer(searchTerm);
		}
		catch(PlayerNotFoundException ex)
		{	}

		try
		{
			exPlayer = server.getOfflinePlayer(UUID.fromString(searchTerm));
		}
		catch (IllegalArgumentException ex)
		{
			exPlayer = server.getOfflinePlayer(searchTerm);
		}

		if (exPlayer != null) return exPlayer;
		if(!deep) throw new PlayerNotFoundException();
		
		final OfflinePlayer[] offlinePlayers = server.getOfflinePlayers();

		final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
		for (OfflinePlayer player : offlinePlayers)
		{
			if (player.getName().toLowerCase(Locale.ENGLISH).contains(matchText))
			{
				return player;
			}
		}

		throw new PlayerNotFoundException();
	}

	/*
	 * Argument Processing
	 */

	protected boolean argSet(int idx)
	{
		return this.args.size() >= idx + 1;
	}

	// String
	protected String arg(int idx, String def)
	{
		if (this.args.size() < idx + 1)
			return def;

		return this.args.get(idx);
	}

	protected String arg(int idx)
	{
		return this.arg(idx, null);
	}
	
	protected String argStr(int idx, String def)
	{
		return this.arg(idx, def);
	}

	protected String argStr(int idx)
	{
		return this.arg(idx, null);
	}

	// Integer
	protected Integer strAsInt(String str, Integer def)
	{
		if (str == null)
			return def;

		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return def;
		}
	}

	protected Integer argInt(int idx, Integer def)
	{
		return strAsInt(this.arg(idx), def);
	}

	protected Integer argInt(int idx)
	{
		return this.argInt(idx, null);
	}

	// Double
	protected Double strAsDouble(String str, Double def)
	{
		if (str == null)
			return def;

		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return def;
		}
	}

	protected Double argDouble(int idx, Double def)
	{
		return strAsDouble(this.arg(idx), def);
	}

	protected Double argDouble(int idx)
	{
		return this.argDouble(idx, null);
	}

	// Boolean
	protected Boolean strAsBool(String str)
	{
		str = str.toLowerCase();
		return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
	}

	protected Boolean argBool(int idx, boolean def)
	{
		String str = this.arg(idx);
		if (str == null)
			return def;

		return strAsBool(str);
	}

	protected Boolean argBool(int idx)
	{
		return this.argBool(idx, false);
	}
	
	protected void msg(String message)
	{
		sender.sendMessage(Txt.parseColor(message));
	}
	
	protected void msg(String[] messages)
	{
		for(String str : messages)
		{
			msg(str);
		}
	}
	
	protected void msg(Collection<String> messages)
	{
		for(String str : messages)
		{
			msg(str);
		}
	}
	
	protected void msg(String message, Object... args)
	{
		sender.sendMessage(Txt.parse(message, args));
	}
	
	protected void msg(String[] messages, Object... args)
	{
		for(String str : messages)
		{
			msg(str, args);
		}
	}
	
	protected void msg(Collection<String> messages, Object... args)
	{
		for(String str : messages)
		{
			msg(str, args);
		}
	}

	protected String loopArgs(final int startIndex)
	{
		final StringBuilder bldr = new StringBuilder();
		for (int i = startIndex; i < args.size(); i++)
		{
			if (i != startIndex)
			{
				bldr.append(" ");
			}
			bldr.append(args.get(i));
		}
		return bldr.toString();
	}
}