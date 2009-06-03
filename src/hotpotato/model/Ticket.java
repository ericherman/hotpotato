/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.*;

import java.io.*;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Order order;

    public Ticket(String id, Order order) {
        this.id = id;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

}
