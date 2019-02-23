package com.exloki.foruxe.commands;

import com.exloki.core_foruxe.commands.LCommand;
import com.exloki.foruxe.ForuxEffects;
import com.exloki.foruxe.TL;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;

public class Commandforuxeffects extends LCommand implements Listener
{

	public Commandforuxeffects()
	{
		super("foruxeffects", "/foruxeffects reload");
	}

	@Override
	protected void perform(final String commandLabel, final Command cmd) throws Exception
	{
		if(!argSet(0)) {
			sendUsage();
			return;
		}

		if(arg(0).equalsIgnoreCase("reload")) {
			ForuxEffects.getSettings().reloadConfig();
			msg(TL.SUCCESS + "Successfully reloaded plugin configuration");
			return;
		}

		sendUsage();
	}
}