/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String prefix;
    private int number;
    private Callable<? extends Serializable> order;
    private long time;

    public Ticket(String prefix, int number, long time,
            Callable<? extends Serializable> order) {
        this.prefix = prefix;
        this.number = number;
        this.time = time;
        this.order = order;
    }

    public String getId() {
        return prefix + number;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getNumber() {
        return number;
    }

    public long getTime() {
        return time;
    }

    public Callable<? extends Serializable> getOrder() {
        return order;
    }

    public String toStirng() {
        return prefix + number + " " + new Date(time) + " " + order;
    }
}
