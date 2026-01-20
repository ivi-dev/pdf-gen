package com.pdfgen.reporting;

import com.pdfgen.reporting.modifiers.MessageModifier;
import com.pdfgen.reporting.prefixers.Prefixer;

public class BasicReporter implements Reporter {

    private final Prefixer[] prefixers;

    private final MessageModifier[] modifiers;

    public BasicReporter() {
        this(new Prefixer[0], new MessageModifier[0]);
    }

    public BasicReporter(Prefixer[] msgPrefixers) {
        this(msgPrefixers, new MessageModifier[0]);
    }

    public BasicReporter(MessageModifier[] msgModifiers) {
        this(new Prefixer[0], msgModifiers);
    }

    public BasicReporter(
        Prefixer[] msgPrefixers, 
        MessageModifier[] msgModifiers
    ) {
        this.prefixers = msgPrefixers;
        this.modifiers = msgModifiers;
    }

    private String prefix(Level level) {
        String prefix = "";
        for (var prefixer : prefixers)
            prefix += prefixer.get(level) + " ";
        prefix = prefix.trim();
        return prefix.length() > 0 ? prefix + " " : prefix;
    }

    private String message(String msg, Object ...args) {
        String message = msg;
        for (var modifier : modifiers)
            message = modifier.modify(message, args);
        return message;
    }

    private void report(String msg, Level level, Object ...args) {
        System.err.println(prefix(level) + message(msg, args));
    }

    @Override
    public void info(String msg, Object ...args) {
        report(msg, Level.INFO, args);
    }

    @Override
    public void error(String msg, Object ...args) {
        report(msg, Level.ERROR, args);
    }

    @Override
    public void success(String msg, Object ...args) {
        report(msg, Level.SUCCESS, args);
    }

}