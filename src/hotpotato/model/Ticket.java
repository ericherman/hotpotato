/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
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
