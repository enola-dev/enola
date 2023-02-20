package dev.enola.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import dev.enola.core.proto.ID;

import java.util.HashSet;
import java.util.Set;

public final class IDs {
    private IDs() { }

    private final static Splitter AMPERSAND_SPLITTER = Splitter.on('&').omitEmptyStrings().trimResults();
    private final static Splitter EQUALSIGN_SPLITTER = Splitter.on('=').omitEmptyStrings().trimResults();

    public static ID from(String s) {
        java.net.URI uri = java.net.URI.create(s);
        ID.Parts.Builder builder = ID.Parts.newBuilder();

        if (uri.getScheme() == null) {
            throw new IllegalArgumentException(s + " ID URI has no scheme: " + uri);
        }
        builder.setScheme(uri.getScheme());

        if (uri.getAuthority() != null) {
            throw new IllegalArgumentException(s + " ID URI cannot have an //authority: " + uri);
        }

        if (uri.getFragment() != null) {
            throw new IllegalArgumentException(s + " ID URI cannot have an #fragment: " + uri);
        }

        if (uri.getSchemeSpecificPart() == null) {
            throw new IllegalArgumentException(s + " ID URI has no path: " + uri);
        }
        String path = uri.getSchemeSpecificPart();
        int idx = path.indexOf('?');
        if (idx == -1)
            builder.setEntity(uri.getSchemeSpecificPart());
        else {
            String entity = path.substring(0, idx);
            builder.setEntity(entity);

            Set<String> queryParameterNames = new HashSet<>();
            String query = path.substring(idx+1);
            AMPERSAND_SPLITTER.split(query).forEach(pair -> {
                String[] nameValue = Iterables.toArray(EQUALSIGN_SPLITTER.split(pair), String.class);
                if (nameValue.length > 2) {
                    throw new IllegalArgumentException(s + " ID URI ?query has name/value with more than 1 '=' sign: " + pair);
                }
                if (nameValue.length > 1) {
                    if (queryParameterNames.contains(nameValue[0]))
                        throw new IllegalArgumentException(s + " ID URI ?query has duplicate names: " + query);
                    queryParameterNames.add(nameValue[0]);

                    ID.NameValue.Builder nameValueBuilder = ID.NameValue.newBuilder();
                    nameValueBuilder.setName(nameValue[0]);
                    if (nameValue.length == 2) {
                        nameValueBuilder.setValue(nameValue[1]);
                    }
                    builder.addQuery(nameValueBuilder);
                }
            });
        }

        return ID.newBuilder().setParts(builder).build();
    }

    public static ID struct(ID id) {
        if (id.hasParts()) {
            return id;
        }
        if (!id.hasText()) {
            throw new IllegalArgumentException("ID proto is empty and has neither text nor parts oneof");
        }
        return from(id.getText());
    }
}