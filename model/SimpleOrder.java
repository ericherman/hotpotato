/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

/** The simplest implementation of Order which honors the intent of Order */
public class SimpleOrder implements Order {
    private String id;
    protected boolean complete = false;

    public void exec() {
        complete = true;
    }

    public String getId() {
        return id;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setId(String id) {
        this.id = id;
    }
}
