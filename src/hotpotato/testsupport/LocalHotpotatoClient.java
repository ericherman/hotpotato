/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.testsupport;

import hotpotato.HotpotatoClient;
import hotpotato.HotpotatoServer;
import hotpotato.Request;

import java.io.Serializable;

public class LocalHotpotatoClient implements HotpotatoClient {
    HotpotatoServer restaurant;

    public LocalHotpotatoClient(HotpotatoServer alices) {
        this.restaurant = alices;
    }

    public Serializable send(Request request) {
        return request.exec(restaurant);
    }
}
