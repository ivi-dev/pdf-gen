package com.pdfgen.reporting;

import java.util.function.Supplier;

import com.pdfgen.reporting.modifiers.I18NMessageModifier;
import com.pdfgen.reporting.modifiers.MessageModifier;
import com.pdfgen.reporting.prefixers.I18NLevelPrefixer;
import com.pdfgen.reporting.prefixers.Prefixer;
import com.pdfgen.reporting.prefixers.TimestampPrefixer;

public class ConditionalBasicReporter 
    extends BasicReporter 
    implements ConditionalReporter {

    private static final Supplier<MessageModifier[]> DEFAULT_MESSAGE_MODIFIERS = 
        () -> new MessageModifier[] { new I18NMessageModifier() };

    private boolean verbose;

    public ConditionalBasicReporter() { 
        this(false);
    }

    public ConditionalBasicReporter(boolean verbose) { 
        this(verbose, new Prefixer[0], new MessageModifier[0]);
    }

    public ConditionalBasicReporter(Prefixer[] msgPrefixers) {
        this(false, msgPrefixers, new MessageModifier[0]);
    }

    public ConditionalBasicReporter(
        boolean verbose, 
        Prefixer[] msgPrefixers
    ) {
        this(verbose, msgPrefixers, new MessageModifier[0]);
    }

    public ConditionalBasicReporter(MessageModifier[] msgModifiers) {
        this(false, new Prefixer[0], msgModifiers);
    }

    public ConditionalBasicReporter(
        boolean verbose, 
        MessageModifier[] msgModifiers
    ) {
        this(verbose, new Prefixer[0], msgModifiers);
    }

    public ConditionalBasicReporter(
        Prefixer[] msgPrefixers, 
        MessageModifier[] msgModifiers
    ) {
        this(false, msgPrefixers, msgModifiers);
    }

    public ConditionalBasicReporter(
        boolean verbose,
        Prefixer[] msgPrefixers, 
        MessageModifier[] msgModifiers
    ) {
        super(msgPrefixers, msgModifiers);
        this.verbose = verbose;
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void info(String msg, Object ...args) {
        if (verbose) super.info(msg, args);
    }

    @Override
    public void error(String msg, Object ...args) {
        if (verbose) super.error(msg, args);
    }

    @Override
    public void success(String msg, Object ...args) {
        if (verbose) super.success(msg, args);
    }

    public static ConditionalReporter leveledI18N() {
        return new ConditionalBasicReporter(
            new Prefixer[] { new I18NLevelPrefixer() },
            DEFAULT_MESSAGE_MODIFIERS.get()
        );
    }

    public static ConditionalReporter leveledTimestampedI18N() {
        return new ConditionalBasicReporter(
            new Prefixer[] { new I18NLevelPrefixer(), new TimestampPrefixer() },
            DEFAULT_MESSAGE_MODIFIERS.get()
        );
    }
        
}
