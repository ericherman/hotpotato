/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String prefix;
    private int number;
    private Callable<Serializable> order;
    private long time;

    public Ticket(String prefix, int number, long time, Callable<Serializable> order) {
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

    public Callable<Serializable> getOrder() {
        return order;
    }

}
