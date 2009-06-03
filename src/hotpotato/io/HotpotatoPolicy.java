/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.security.*;

/**
 * I'd like to extend a super special thanks to Brett Neumeier for finding the
 * correct way to implement this very simple-seeming class.
 */
class HotpotatoPolicy extends Policy {
    private Permissions allPriv;
    private Permissions noPriv;

    public HotpotatoPolicy() {
        allPriv = new Permissions();
        allPriv.add(new AllPermission());
        noPriv = new Permissions();
    }

    public PermissionCollection getPermissions(CodeSource cs) {
        if (cs instanceof AlienCodeSource) {
            return noPriv;
        } else {
            return allPriv;
        }
    }

    public void refresh() { // required by api
    }
}
