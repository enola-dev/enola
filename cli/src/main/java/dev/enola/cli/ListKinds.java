package dev.enola.cli;

import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;

import picocli.CommandLine;

@CommandLine.Command(name = "list-kinds", description = "List known Entity Kinds")
public class ListKinds extends CommandWithModel {
    @Override
    protected void run(EntityKindRepository ekr) throws Exception {
        ekr.listID().forEach(ekid -> out.println(IDs.toPath(ekid)));
    }
}
