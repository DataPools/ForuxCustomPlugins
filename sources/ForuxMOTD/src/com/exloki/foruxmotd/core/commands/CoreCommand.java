package com.exloki.foruxmotd.core.commands;

import com.exloki.foruxmotd.ForuxMOTD;
import com.exloki.foruxmotd.Msg;
import com.exloki.foruxmotd.core.utils.BukkitUtil;
import com.exloki.foruxmotd.core.utils.Runnables;
import com.exloki.foruxmotd.core.utils.Txt;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Concept & code from Redemptive-Core by Twister915
 * https://github.com/Twister915/redemptive
 */

@Data
public abstract class CoreCommand implements CommandExecutor, TabCompleter {
    /**
     * Holds a list of the sub-commands bound to their names used for quick access.
     */
    private final Map<String, CoreCommand> subCommands = new LinkedHashMap<>();
    /**
     * Holds the name of this command.
     */
    @Getter private final String name;
    @Setter(AccessLevel.PROTECTED) @Getter private CoreCommand superCommand = null;
    @Getter private CommandMeta meta = getClass().isAnnotationPresent(CommandMeta.class) ? getClass().getAnnotation(CommandMeta.class) : null;
    @Getter private CommandPermission permission = getClass().isAnnotationPresent(CommandPermission.class) ? getClass().getAnnotation(CommandPermission.class) : null;
    private ForuxMOTD plugin;

    /**
     * Main constructor without sub-commands.
     * @param name The name of the command.
     */
    protected CoreCommand(String name) {
        this.name = name;
    }

    /**
     * Main constructor with sub-commands.
     * @param name The name of the command.
     * @param subCommands The sub-commands you wish to register.
     */
    protected CoreCommand(final String name, CoreCommand... subCommands) {
        this.name = name;
        registerSubCommand(subCommands);
    }

    /**
     * Registers sub commands with the command and re-creates the help command.
     * @param subCommands The sub-commands you wish to register.
     */
    public final void registerSubCommand(CoreCommand... subCommands) {
        //Toss all the sub commands in the map
        for (CoreCommand subCommand : subCommands) {
            if (subCommand.getSuperCommand() != null) throw new IllegalArgumentException("The command you attempted to register already has a supercommand.");
            this.subCommands.put(subCommand.getName(), subCommand);
            CommandMeta meta = subCommand.getMeta();
            if (meta != null)
                for (String a : meta.aliases())
                    this.subCommands.put(a, subCommand);
            subCommand.setSuperCommand(this);
        }
        //Add a provided help command
        regenerateHelpCommand();
    }

    public final void unregisterSubCommand(CoreCommand... subCommands) {
        for (CoreCommand subCommand : subCommands) {
            //if (!subCommand.getSuperCommand().equals(this)) continue;
            this.subCommands.remove(subCommand.getName());
            subCommand.setSuperCommand(null);
        }
        regenerateHelpCommand();
    }

    public final ImmutableList<CoreCommand> getSubCommands() {
        return ImmutableList.copyOf(this.subCommands.values());
    }

    private void regenerateHelpCommand() {
        if (!shouldGenerateHelpCommand()) return;
        final Map<String, CoreCommand> subCommandsLV = this.subCommands;
        if(subCommandsLV.size() == 1 && subCommandsLV.get("help") != null) {
            // Only the help command, clear the list
            this.subCommands.clear();
            return;
        }

        final CoreCommand superHelpCommand = this;
        this.subCommands.put("help", new CoreCommand("help") {
            @Override
            public void handleCommandUnspecific(CommandSender sender, String[] args) {
                for(Map.Entry<String, CoreCommand> entry : subCommandsLV.entrySet()) {
                    if(entry.getKey().equalsIgnoreCase("help")) continue;
                    if(entry.getValue().getPermission() != null && !entry.getValue().getPermission().value().isEmpty() &&
                            !sender.hasPermission(permission.value()) && !(sender.isOp() && permission.isOpExempt())) continue;

                    if(entry.getValue().getMeta() != null && !entry.getValue().getMeta().description().isEmpty()) {
                        sender.sendMessage(Msg.CMD_PRIMARY.get() + "/" + superHelpCommand.getFormattedName() + " " + entry.getKey() + Msg.CMD_SECONDARY.get() + " - " + entry.getValue().getMeta().description());
                    } else {
                        sender.sendMessage(Msg.CMD_PRIMARY.get() + "/" + superHelpCommand.getFormattedName() + " " + entry.getKey());
                    }
                }
            }
        });
    }

    public final boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        //Handling commands can be done by the logic below, and all errors should be thrown using an exception.
        //If you wish to override the behavior of displaying that error to the player, it is discouraged to do that in
        //your command logic, and you are encouraged to use the provided method handleCommandException.
        try {
            //STEP ONE: Handle sub-commands
            CoreCommand subCommand = null;

            //Get the permission and test for it
            if (permission != null) {
                if (!sender.hasPermission(permission.value()) && !(sender.isOp() && permission.isOpExempt())) {
                    throw new PermissionException("You do not have permission for this command!");
                }
            }

            //Check if we HAVE to use sub-commands (a behavior this class provides)
            if (isUsingSubCommandsOnly()) {
                //Check if there are not enough args for there to be a sub command
                if (args.length < 1)
                    throw new ArgumentRequirementException("You must specify a sub-command for this command!");
                //Also check if the sub command is valid by assigning and checking the value of the resolved sub command from the first argument.
                if ((subCommand = getSubCommandFor(args[0])) == null)
                    throw new ArgumentRequirementException("The sub-command you have specified is invalid!");
            }
            if (subCommand == null && args.length > 0) subCommand = getSubCommandFor(args[0]); //If we're not requiring sub-commands but we can have them, let's try that
            //By now we have validated that the sub command can be executed if it MUST, now lets see if we can execute it
            //In this case, if we must execute the sub command, this check will always past. In cases where it's an option, this check will also pass.
            //That way, we can use this feature of sub commands without actually requiring it.
            if (subCommand != null) {
                String[] choppedArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
                preSubCommandDispatch(sender, choppedArgs, subCommand); //Notify the subclass that we are using a sub-command in case any staging needs to take place.
                subCommand.onCommand(sender, command, s, choppedArgs);
                try {
                    handlePostSubCommand(sender, args);
                } catch (EmptyHandlerException ignored) {}
                return true;
            }

            //Now that we've made it past the sub commands and permissions, STEP TWO: actually handle the command and it's args.
            if (getClass().isAnnotationPresent(AsyncCommand.class))
                Runnables.forPlugin(plugin).async().with(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            actualDispatch(sender, args);
                        } catch (CommandException e) {
                            handleCommandException(e, args, sender);
                        } catch (Exception e) {
                            handleCommandException(new UnhandledCommandExceptionException(e), args, sender);
                        }
                    }
                }).now();
            else
                actualDispatch(sender, args);
        } //STEP THREE: Check for any command exceptions (intended) and any exceptions thrown in general and dispatch a call for an unhandled error to the handler.
        catch (CommandException ex) {
            handleCommandException(ex, args, sender);
        } catch (Exception e) {
            handleCommandException(new UnhandledCommandExceptionException(e), args, sender);
        }
        //STEP FOUR: Tell Bukkit we're done!
        return true;
    }

    private void actualDispatch(CommandSender sender, String[] args) throws CommandException {
        try {
            if (sender instanceof Player) handleCommand((Player) sender, args);
            else if (sender instanceof ConsoleCommandSender) handleCommand((ConsoleCommandSender)sender, args);
            else if (sender instanceof BlockCommandSender)  handleCommand((BlockCommandSender)sender, args);
        } catch (EmptyHandlerException e) {
            handleCommandUnspecific(sender, args);
        }
    }

    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //Security for tab complete
        if (getClass().isAnnotationPresent(CommandPermission.class)) {
            CommandPermission annotation = getClass().getAnnotation(CommandPermission.class);
            if (!sender.hasPermission(annotation.value()) && !(sender.isOp() && annotation.isOpExempt())) return Collections.emptyList();
        }
        //Step one, check if we have to go a level deeper in the sub command system:
        if (args.length > 1) {
            //If so, check if there's an actual match for the sub-command to delegate to.
            CoreCommand possibleHigherLevelSubCommand;
            if ((possibleHigherLevelSubCommand = getSubCommandFor(args[0])) != null)
                return possibleHigherLevelSubCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            //NOW THINK. If there's not one, you'll reach this line, and exit this block of the if statement. The next statement is an else if, so it will skip that
            //And go to the very bottom "handleTabComplete."
        } else if (args.length == 1) { //So if we have exactly one argument, let's try and complete the sub-command for that argument
            //Grab some sub commands from the method we defined for this purpose
            List<String> subCommandsForPartial = getSubCommandsForPartial(args[0]);
            //And if we found some
            if (subCommandsForPartial.size() != 0) {
                //Get the command names
                subCommandsForPartial.addAll(handleTabComplete(sender, command, alias, args));
                //And return them
                return subCommandsForPartial;
            }
            //Otherwise, head to the delegated call at the bottom.
        }
        return handleTabComplete(sender, command, alias, args);
    }

    /**
     * This method <b>should</b> be overridden by any sub-classes as the functionality it provides is limited.
     *
     * The goal of this method should always be conveying an error message to a user in a friendly manner. The {@link com.exloki.foruxmotd.core.commands.CommandException} can be extended by your plugin to provide extended functionality.
     *
     * The {@code args} are the same args that would be passed to your handlers. Meaning, if this is a sub-command they will be cut to fit that sub-command, and if this is a root level command they will be all of the arguments.
     *
     * @param ex The exception used to hold the error message and any other details about the failure. If there was an exception during the handling of the command this will be an {@link com.exloki.foruxmotd.core.commands.UnhandledCommandExceptionException}.
     * @param args The arguments passed to the command.
     * @param sender The sender of the command, cannot be directly cast to {@link org.bukkit.entity.Player}.
     */
    @SuppressWarnings("UnusedParameters")
    protected void handleCommandException(CommandException ex, String[] args, CommandSender sender) {
        //Get the friendly message if supported
        if (ex instanceof FriendlyException) sender.sendMessage(((FriendlyException) ex).getFriendlyMessage(this));
        else sender.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "!");
        if (ex instanceof UnhandledCommandExceptionException) ((UnhandledCommandExceptionException) ex).getCausingException().printStackTrace();
    }

    @SuppressWarnings("UnusedParameters")
    protected void preSubCommandDispatch(CommandSender sender, String[] args, CoreCommand subCommand) {}

    public final CoreCommand getSubCommandFor(String s) {
        //If we have an exact match, case and all, don't waste the CPU cycles on the lower for loop.
        if (subCommands.containsKey(s)) return subCommands.get(s);
        //Otherwise, loop through the sub-commands and do a case insensitive check.
        for (String s1 : subCommands.keySet()) {
            if (s1.equalsIgnoreCase(s)) return subCommands.get(s1);
        }
        //And we didn't find anything, so let's return nothing.
        return null;
    }

    public List<String> getSubCommandsForPartial(String s) {
        List<String> commands = new ArrayList<>(); //Create a place to hold our possible commands
        CoreCommand subCommand;
        if ((subCommand = getSubCommandFor(s)) != null) { //Check if we can get an exact sub-command
            commands.add(subCommand.getName());
            return commands; //exact sub-command is all we need.
        }
        String s2 = s.toUpperCase(); //Get the case-insensitive comparator.
        for (String s1 : subCommands.keySet()) {
            if (s1.toUpperCase().startsWith(s2))
                commands.add(s1); //We found one that starts with the argument.
        }
        return commands;
    }

    /*
     * Player messaging
     */

    protected void msg(CommandSender sender, Msg message) {
        sender.sendMessage(message.toString());
    }

    protected void msg(CommandSender sender, Msg message, String... vars) {
        sender.sendMessage(message.with(vars));
    }

    protected void msg(CommandSender sender, String message) {
        sender.sendMessage(Txt.parseColor(message));
    }

    protected void msg(CommandSender sender, String[] messages) {
        for (String str : messages) {
            msg(sender, str);
        }
    }

    protected void msg(CommandSender sender, Collection<String> messages) {
        for (String str : messages) {
            msg(sender, str);
        }
    }

    protected void msg(CommandSender sender, String message, Object... args) {
        sender.sendMessage(Txt.parse(message, args));
    }

    protected void msg(CommandSender sender, String[] messages, Object... args) {
        for (String str : messages) {
            msg(sender, str, args);
        }
    }

    protected void msg(CommandSender sender, Collection<String> messages, Object... args) {
        for (String str : messages) {
            msg(sender, str, args);
        }
    }

    /*
	 * Finding Players
	 */

    protected Player getPlayer(UUID uuid) throws CommandException {
        return getPlayer(uuid, true);
    }

    protected Player getPlayer(UUID uuid, boolean throwException) throws CommandException {
        Player found = Bukkit.getPlayer(uuid);
        if (found == null) {
            if (throwException)
                throw new PlayerNotFoundException("player not found");
            return null;
        }

        return found;
    }

    protected Player getPlayer(final String searchTerm) throws CommandException {
        return getPlayer(searchTerm, true);
    }

    protected Player getPlayer(final String searchTerm, boolean throwException) throws CommandException {
        return getPlayer(Bukkit.getServer(), searchTerm, throwException);
    }

    protected Player getPlayer(final Server server, final String searchTerm, boolean throwException) throws CommandException {
        Player player = BukkitUtil.getPlayer(server, searchTerm);
        if(player != null)
            return player;

        if (throwException)
            throw new PlayerNotFoundException("player not found");
        return null;
    }

    protected OfflinePlayer getOfflinePlayer(final String searchTerm) throws CommandException {
        return getOfflinePlayer(searchTerm, false);
    }

    protected OfflinePlayer getOfflinePlayer(final String searchTerm, final boolean deep) throws CommandException {
        OfflinePlayer found = BukkitUtil.getOfflinePlayer(searchTerm, deep);
        if(found != null) {
            return found;
        }

        throw new PlayerNotFoundException("player not found");
    }

    public ForuxMOTD getPlugin() {
        if (getSuperCommand() != null)
            return getSuperCommand().getPlugin();
        return plugin;
    }

    //Default behavior is to do nothing, these methods can be overridden by the sub-class.
    protected void handleCommand(Player player, String[] args) throws CommandException {throw new EmptyHandlerException();}
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {throw new EmptyHandlerException();}
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {throw new EmptyHandlerException();}

    //Handles for all types in the event that no specific handler is overridden above.
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {throw new EmptyHandlerException();}
    protected void handlePostSubCommand(CommandSender sender, String[] args) throws CommandException {throw new EmptyHandlerException();}

    protected boolean shouldGenerateHelpCommand() {return true;}

    //Default behavior if we delegate the call to the sub-class
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (isUsingSubCommandsOnly() || subCommands.size() > 0) return Collections.emptyList();
        List<String> ss = new ArrayList<String>(); //Create a list to put possible names
        String arg = args.length > 0 ? args[args.length - 1].toLowerCase() : ""; //Get the last argument
        for (Player player : Bukkit.getOnlinePlayers()) { //Loop through all the players
            String name1 = player.getName(); //Get this players name (since we reference it twice)
            if (name1.toLowerCase().startsWith(arg)) ss.add(name1); //And if it starts with the argument we add it to this list
        }
        return ss; //Return what we found.
    }

    protected boolean isUsingSubCommandsOnly() {return false;}

    protected String getFormattedName() {return superCommand == null ? name : superCommand.getFormattedName() + " " + name;}

    public void setPlugin(Plugin plugin) {
        if(plugin instanceof ForuxMOTD) {
            this.plugin = (ForuxMOTD) plugin;
        } else {
            throw new IllegalStateException("plugin must be an instance of " + ForuxMOTD.class.getName());
        }
    }

    @Override
    public String toString() {
        return "Command -> " + getFormattedName();
    }
}
