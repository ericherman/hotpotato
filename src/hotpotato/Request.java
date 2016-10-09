/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato;

import java.io.Serializable;

public interface Request extends Serializable {
    Serializable exec(HotpotatoServer restaurant);
}
