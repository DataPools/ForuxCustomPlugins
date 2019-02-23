package com.exloki.balancedfly.core.commands;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommandException extends Exception {
    private final String message;
}
