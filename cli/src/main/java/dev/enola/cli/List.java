package dev.enola.cli;

import dev.enola.core.EnolaService;
import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.ID;

import picocli.CommandLine.Command;

@Command(name = "list", description = "List Entities")
public class List extends CommandWithEntityID {

    // TODO Implement, after adding a list() method to EnolaService
    // Similar to, but functionally separate from, the list() on EntityKindRepository.

    // With EntityKind name asks connector to list (first N?) entity IDs
    // With path asks connector, and behavior is connector specific; FileRepoConnector appends a *

    @Override
    protected void run(EntityKindRepository ekr, EnolaService service, ID id) throws Exception {
        throw new IllegalArgumentException("TODO Implement support to list: " + IDs.toPath(id));
    }
}
