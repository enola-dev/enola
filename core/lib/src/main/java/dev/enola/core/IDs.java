package dev.enola.core;

import dev.enola.core.proto.ID;

public final class IDs {
    private IDs() { }

    public static ID from(String s) {
        java.net.URI uri = java.net.URI.create(s);
        ID.Parts.Builder builder = ID.Parts.newBuilder();

        if (uri.getScheme() == null) {
            throw new IllegalArgumentException(s + " ID has no scheme: " + uri);
        }
        builder.setScheme(uri.getScheme());

        if (uri.getAuthority() != null) {
            throw new IllegalArgumentException(s + " ID cannot have an //authority: " + uri);
        }

        if (uri.getFragment() != null) {
            throw new IllegalArgumentException(s + " ID cannot have an #fragment: " + uri);
        }

        if (uri.getSchemeSpecificPart() == null) {
            throw new IllegalArgumentException(s + " ID has no authority: " + uri);
        }
        builder.setEntity(uri.getSchemeSpecificPart());

        return ID.newBuilder().setParts(builder).build();
    }

    public static ID struct(ID id) {
        if (id.hasParts()) {
            return id;
        }
        if (!id.hasText()) {
            throw new IllegalArgumentException("ID proto is empty and has neither text nor struct");
        }
        return from(id.getText());
    }
}