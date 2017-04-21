package com.gu.thrift.serializer;

/* This is copied from com.fasterxml.jackson.core / jackson-databind (2.7.1) so we don't have to depend on the library */

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Simple {@link InputStream} implementation that exposes currently
 * available content of a {@link ByteBuffer}.
 */
public class ByteBufferBackedInputStream extends InputStream {
    protected final ByteBuffer _b;

    public ByteBufferBackedInputStream(ByteBuffer buf) { _b = buf; }

    @Override public int available() { return _b.remaining(); }

    @Override
    public int read() throws IOException { return _b.hasRemaining() ? (_b.get() & 0xFF) : -1; }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (!_b.hasRemaining()) return -1;
        len = Math.min(len, _b.remaining());
        _b.get(bytes, off, len);
        return len;
    }
}