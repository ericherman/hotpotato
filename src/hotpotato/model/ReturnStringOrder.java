/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class ReturnStringOrder implements Callable<String>, Serializable {
    private static final long serialVersionUID = 1L;

    private String val;

    public ReturnStringOrder(String val) {
        this.val = val;
    }

    public String call() {
        return val;
    }
}
