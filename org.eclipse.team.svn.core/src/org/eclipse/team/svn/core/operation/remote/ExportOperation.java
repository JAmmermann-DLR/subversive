/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sergiy Logvin - Initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.operation.remote;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.operation.SVNProgressMonitor;
import org.eclipse.team.svn.core.resource.IRepositoryLocation;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.core.resource.IRepositoryResourceProvider;
import org.eclipse.team.svn.core.utility.SVNUtility;

/**
 * Operation organize export local folder to repository
 * 
 * @author Sergiy Logvin
 */
public class ExportOperation extends AbstractRepositoryOperation {
	protected String path;
	protected int depth;
	
	public ExportOperation(IRepositoryResource resource, String path, int depth) {
		super("Operation.ExportRevision", new IRepositoryResource[] {resource});
		this.path = path;
		this.depth = depth;
	}
	
	public ExportOperation(IRepositoryResourceProvider provider, String path, int depth) {
		super("Operation.ExportRevision", provider);
		this.path = path;
		this.depth = depth;
	}
	
	protected void runImpl(IProgressMonitor monitor) throws Exception {
		IRepositoryResource resource = this.operableData()[0];
		IRepositoryLocation location = resource.getRepositoryLocation();
		ISVNConnector proxy = location.acquireSVNProxy();
		try {
			String path = this.path + "/" + resource.getName();
//			this.writeToConsole(IConsoleStream.LEVEL_CMD, "svn export \"" + resource.getUrl() + "@" + resource.getPegRevision() + "\" -r " + resource.getSelectedRevision() + " \"" + FileUtility.normalizePath(path) + "\" --force" + FileUtility.getUsernameParam(location.getUsername()) + "\n");
			proxy.doExport(SVNUtility.getEntryRevisionReference(resource), path, null, this.depth, ISVNConnector.Options.FORCE, new SVNProgressMonitor(this, monitor, null));
		}
		finally {
			location.releaseSVNProxy(proxy);
		}
	}
	
	protected String getShortErrorMessage(Throwable t) {
		return MessageFormat.format(super.getShortErrorMessage(t), new Object[] {this.operableData()[0].getUrl()});
	}
	
}
