package dev.enola.cli;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

// Inspired by https://github.com/mihnita/java-color-loggers (Apache License 2.0)
public class LoggingColorConsoleHandler extends ConsoleHandler {

    // TODO Add magic to turn off colors when "there is no ANSI"
    // Use https://picocli.info/#_heuristics_for_enabling_ansi
    // and consider https://no-color.org

    private static final String COLOR_RESET = "\u001b[0m";

    private static final String COLOR_SEVERE = "\u001b[91m";
    private static final String COLOR_WARNING = "\u001b[93m";
    private static final String COLOR_INFO = "\u001b[32m";
    private static final String COLOR_CONFIG = "\u001b[94m";
    private static final String COLOR_FINE = "\u001b[36m";
    private static final String COLOR_FINER = "\u001b[35m";
    private static final String COLOR_FINEST = "\u001b[90m";

    @Override
    public void publish(LogRecord record) {
        System.err.print(logRecordToString(record));
        System.err.flush();
    }

    private String logRecordToString(LogRecord record) {
        Formatter f = getFormatter();
        String msg = f.format(record);

        String prefix;
        Level level = record.getLevel();
        if (level == Level.SEVERE) prefix = COLOR_SEVERE;
        else if (level == Level.WARNING) prefix = COLOR_WARNING;
        else if (level == Level.INFO) prefix = COLOR_INFO;
        else if (level == Level.CONFIG) prefix = COLOR_CONFIG;
        else if (level == Level.FINE) prefix = COLOR_FINE;
        else if (level == Level.FINER) prefix = COLOR_FINER;
        else if (level == Level.FINEST) prefix = COLOR_FINEST;
        else
            // Unknown level, probably not possible, but if it happens it means it's bad :-)
            prefix = COLOR_SEVERE;

        return prefix + msg + COLOR_RESET;
    }
}
