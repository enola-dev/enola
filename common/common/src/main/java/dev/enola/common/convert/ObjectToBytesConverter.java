package dev.enola.common.convert;

import com.google.common.io.ByteSink;

public interface ObjectToBytesConverter<T> extends ConverterInto<T, ByteSink> {}
