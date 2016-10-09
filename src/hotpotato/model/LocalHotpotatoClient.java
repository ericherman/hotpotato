/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoClient;
import hotpotato.HotpotatoServer;
import hotpotato.Request;

import java.io.Serializable;

public class LocalHotpotatoClient implements HotpotatoClient {
    HotpotatoServer restaurant;

    public LocalHotpotatoClient(HotpotatoServer alices) {
        this.restaurant = alices;
    }

    public synchronized Serializable send(Request request) {
        return request.exec(restaurant);
    }
}
