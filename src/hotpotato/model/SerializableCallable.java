/**
 * 
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class SerializableCallable implements Serializable,
        Callable<Serializable> {

    private static final long serialVersionUID = 1L;

    private Runnable command;

    public SerializableCallable() {
        // no arg constructor for over-riding
    }

    public SerializableCallable(Runnable command) {
        this.command = command;
    }

    public Serializable call() {
        if (command != null) {
            command.run();
        }
        return null;
    }

}
