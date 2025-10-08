package dev.enola.todo.config;

import dev.enola.common.io.resource.FileResource;
import dev.enola.todo.ToDoRepository;
import dev.enola.todo.file.ToDoRepositoryFile;

import java.io.IOException;
import java.util.function.Supplier;

public final class ToDoRepositorySupplier implements Supplier<ToDoRepository> {

    @Override
    public ToDoRepository get() {
        var homeDir = System.getProperty("user.home");
        var toDoFile = new java.io.File(homeDir, "ToDo.yaml").toURI();
        var toDoResource = new FileResource(toDoFile);
        try {
            return new ToDoRepositoryFile(toDoResource);
        } catch (IOException e) {
            throw new IllegalArgumentException(toDoResource.toString(), e);
        }
    }
}
