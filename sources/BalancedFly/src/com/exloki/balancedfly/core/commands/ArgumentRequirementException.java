package com.exloki.balancedfly.core.commands;

import com.exloki.balancedfly.Msg;

public final class ArgumentRequirementException extends CommandException implements FriendlyException {
    public ArgumentRequirementException(String message) {
        super(message);
    }

    public ArgumentRequirementException(CoreCommand command) {
        super(command.getSuperCommand() == null ? "/" + (command.getMeta() == null ? command.getName() : (command.getMeta().usage().isEmpty() ? command.getName() : command.getMeta().usage())) :
                "/" + (command.getMeta() == null ? command.getSuperCommand().getName() + " " + command.getName() : command.getMeta().usage().isEmpty() ? command.getSuperCommand().getName() + " " + command.getName() :
                        command.getSuperCommand().getName() + " " + command.getMeta().usage()));
    }

    public static ArgumentRequirementException from(CoreCommand command) {
        StringBuilder builder = new StringBuilder();
        if(command.getSuperCommand() == null) {
            if(command.getMeta() != null && !command.getMeta().usage().isEmpty()) {
                if(!command.getMeta().usage().startsWith("/")) {
                    builder.append("/");
                }
                builder.append(command.getMeta().usage());
            } else {
                builder.append("/").append(command.getName());
            }
        } else {
            builder.append("/").append(command.getSuperCommand().getName()).append(" ");
            if(command.getMeta() != null && !command.getMeta().usage().isEmpty()) {
                builder.append(command.getMeta().usage());
            } else {
                builder.append(command.getName());
            }
        }

        return new ArgumentRequirementException(builder.toString().trim());
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return Msg.ER_USAGE.with(getMessage());
    }
}
