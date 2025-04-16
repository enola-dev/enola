package dev.enola.chat.jline;

import dev.enola.chat.IO;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

/** JLineIO is an {@link IO} implementation based on <a href="https://jline.org">JLine.org</a>. */
public class JLineIO implements IO {

    private final Terminal terminal;
    private final LineReader lineReader;

    public JLineIO() throws IOException {
        terminal = TerminalBuilder.terminal();
        lineReader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    @Override
    public @Nullable String readLine() {
        return lineReader.readLine();
    }

    @Override
    public @Nullable String readLine(String prompt) {
        return lineReader.readLine(prompt);
    }

    @Override
    public void printf(String format, Object... args) {
        terminal.writer().printf(format, args);
    }
}
