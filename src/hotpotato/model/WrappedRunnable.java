/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

class WrappedRunnable<T> implements Callable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private Runnable task;
    private T result;

    public WrappedRunnable(Runnable task, T result) {
        this.task = task;
        this.result = (result instanceof Serializable) ? result : null;
    }

    public T call() throws Exception {
        task.run();
        return result;
    }
}
