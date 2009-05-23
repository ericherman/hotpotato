/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.testsupport;

import hotpotato.*;

import java.io.*;

public class LocalHotpotatoClient implements HotpotatoClient {
    HotpotatoServer restaurant;
    public LocalHotpotatoClient(HotpotatoServer alices) {
        this.restaurant = alices;
    }
    public Serializable send(Request request) {
        return request.exec(restaurant);
    }
}