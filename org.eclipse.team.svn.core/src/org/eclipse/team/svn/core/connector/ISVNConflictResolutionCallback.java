/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Gurov (Polarion Software) - initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.connector;

/**
 * The call-back interface allows to provide connector library with information on how conflict should be solved
 * 
 * The JavaHL API's is the only way to interact between SVN and Java-based tools. At the same time JavaHL connector library
 * is not EPL compatible and we won't to pin plug-in with concrete connector implementation. So, the only way to do this is
 * providing our own connector interface which will be covered by concrete connector implementation.
 * 
 * @author Alexander Gurov
 */
public interface ISVNConflictResolutionCallback {
	/**
	 * Acquires conflict resolution method.
	 * 
	 * @param descriptor
	 *            a conflict description
	 * @return The conflict resolution method (see {@link SVNConflictResolution})
	 * @throws SVNConnectorException
	 *             If an error occurs.
	 */
	public SVNConflictResolution resolve(SVNConflictDescriptor descriptor) throws SVNConnectorException;

}
