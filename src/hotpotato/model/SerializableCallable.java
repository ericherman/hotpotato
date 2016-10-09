/**
 * Copyright (C) 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class SerializableCallable implements Serializable,
        Callable<Serializable> {

    private static final long serialVersionUID = 1L;

    private Runnable command;

    /** no arg constructor for over-riding call() */
    public SerializableCallable() {
        this.command = null;
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

    public String toString() {
        return "" + command;
    }

}
