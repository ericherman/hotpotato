package hotpotato.util;

import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
    public void write(int b) {
        // no op
    }
}