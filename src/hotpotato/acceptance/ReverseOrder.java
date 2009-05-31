/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import java.io.Serializable;

import hotpotato.Order;

public class ReverseOrder implements Order {

	private static final long serialVersionUID = 1L;

	private final String message;

	public ReverseOrder(String message) {
		this.message = message;
	}

	public Serializable exec() {
		return new StringBuffer(message).reverse().toString();
	}

}
