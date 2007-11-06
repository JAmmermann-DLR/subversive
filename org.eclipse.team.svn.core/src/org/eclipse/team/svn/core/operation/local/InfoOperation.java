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

package org.eclipse.team.svn.core.operation.local;

import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.IStateFilter;
import org.eclipse.team.svn.core.client.Depth;
import org.eclipse.team.svn.core.client.EntryRevisionReference;
import org.eclipse.team.svn.core.client.ISVNClientWrapper;
import org.eclipse.team.svn.core.client.EntryInfo;
import org.eclipse.team.svn.core.client.Revision;
import org.eclipse.team.svn.core.operation.AbstractNonLockingOperation;
import org.eclipse.team.svn.core.operation.SVNProgressMonitor;
import org.eclipse.team.svn.core.resource.ILocalResource;
import org.eclipse.team.svn.core.resource.IRepositoryLocation;
import org.eclipse.team.svn.core.svnstorage.SVNRemoteStorage;
import org.eclipse.team.svn.core.utility.FileUtility;
import org.eclipse.team.svn.core.utility.SVNUtility;

/**
 * This operation provide Info2 information for local resource
 * 
 * @author Alexander Gurov
 */
public class InfoOperation extends AbstractNonLockingOperation {
    protected IResource resource;
    protected ILocalResource local;
    protected EntryInfo info;

    public InfoOperation(IResource resource) {
        super("Operation.Info");
        this.resource = resource;
    }

    public EntryInfo getInfo() {
        return this.info;
    }

    public ILocalResource getLocal() {
        return this.local;
    }

    protected void runImpl(IProgressMonitor monitor) throws Exception {
        this.info = null;
        this.local = SVNRemoteStorage.instance().asLocalResource(this.resource);
        
        if (this.local != null && IStateFilter.SF_ONREPOSITORY.accept(this.resource, this.local.getStatus(), this.local.getChangeMask())) {
            IRepositoryLocation location = SVNRemoteStorage.instance().getRepositoryLocation(this.resource);
            ISVNClientWrapper proxy = location.acquireSVNProxy();
            try {
//    			this.writeToConsole(IConsoleStream.LEVEL_CMD, "svn info \"" + this.local.getWorkingCopyPath() + "\"\n");
                EntryInfo []infos = SVNUtility.info(proxy, new EntryRevisionReference(FileUtility.getWorkingCopyPath(this.resource), null, Revision.WORKING), Depth.EMPTY, new SVNProgressMonitor(this, monitor, null));
                
                if (infos != null && infos.length > 0) {
                    this.info = infos[0];
                }
            }
            finally {
                location.releaseSVNProxy(proxy);
            }
        }
    }
    
    protected String getShortErrorMessage(Throwable t) {
		return MessageFormat.format(super.getShortErrorMessage(t), new String[] {this.resource.getName()});
	}

}
