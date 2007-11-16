/*******************************************************************************
 * Copyright (c) 2005-2006 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Gurov - Initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.client;

/**
 * SVN client wrapper cancel exception
 * 
 * The JavaHL API's is the only way to interact between SVN and Java-based tools. At the same time JavaHL client library
 * is not EPL compatible and we won't to pin plug-in with concrete client implementation. So, the only way to do this is
 * providing our own client interface which will be covered by concrete client implementation.
 * 
 * @author Alexander Gurov
 */
public class SVNClientCancelException extends SVNClientException {
	private static final long serialVersionUID = -1431358791852025035L;

	public SVNClientCancelException() {
		super();
	}

	public SVNClientCancelException(String message) {
		super(message);
	}

	public SVNClientCancelException(Throwable cause) {
		super(cause, false);
	}

	public SVNClientCancelException(String message, Throwable cause) {
		super(message, cause, false);
	}

}