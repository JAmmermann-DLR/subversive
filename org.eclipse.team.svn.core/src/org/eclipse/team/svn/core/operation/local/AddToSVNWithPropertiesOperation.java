/*******************************************************************************
 * Copyright (c) 2005-2006 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elena Matokhina (Polarion Software) - initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.operation.local;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.Team;
import org.eclipse.team.svn.core.client.ClientWrapperException;
import org.eclipse.team.svn.core.client.ISVNClientWrapper;
import org.eclipse.team.svn.core.client.PropertyData;
import org.eclipse.team.svn.core.extension.CoreExtensionsManager;
import org.eclipse.team.svn.core.operation.SVNProgressMonitor;
import org.eclipse.team.svn.core.resource.IResourceProvider;
import org.eclipse.team.svn.core.utility.FileUtility;

/**
 * Set content properties operation
 * 
 * @author Elena Matokhina
 */
public class AddToSVNWithPropertiesOperation extends AddToSVNOperation {
	protected static final String BINARY_FILE = "application/octet-stream";
	
	public AddToSVNWithPropertiesOperation(IResource[] resources) {
		this(resources, false);
	}
	
	public AddToSVNWithPropertiesOperation(IResource[] resources, boolean isRecursive) {
		super(resources, isRecursive);
	}
	
	public AddToSVNWithPropertiesOperation(IResourceProvider provider, boolean isRecursive) {
		super(provider, isRecursive);
	}

	protected void doAdd(IResource current, final ISVNClientWrapper proxy, final IProgressMonitor monitor) throws Exception {
		super.doAdd(current, proxy, monitor);
		
		if (!this.isRecursive) {
			this.processResource(current, proxy, monitor);
		}
		else {
			FileUtility.visitNodes(current, new IResourceVisitor() {

				public boolean visit(IResource resource) throws CoreException {
					if (monitor.isCanceled()) {
						return false;
					}
					
					try {
						AddToSVNWithPropertiesOperation.this.processResource(resource, proxy, monitor);
					}
					catch (ClientWrapperException cwe) {
						AddToSVNWithPropertiesOperation.this.reportError(cwe);
						return false;
					}
					return true;
				}
				
			}, IResource.DEPTH_INFINITE);
		}
	}
	
	protected void processResource(IResource resource, ISVNClientWrapper proxy, IProgressMonitor monitor) throws ClientWrapperException {
		String path = resource.getLocation().toString();
		PropertyData[] properties = CoreExtensionsManager.instance().getOptionProvider().getAutomaticProperties(resource.getName());
		for (int pCount = 0; pCount < properties.length; pCount++) {
			proxy.propertySet(path, properties[pCount].name, properties[pCount].value, false, new SVNProgressMonitor(this, monitor, null));
		}
		if (resource.getType() == IResource.FILE) {
			this.processFile(resource, proxy, monitor);
		}
	}
	
	protected void processFile(IResource resource, ISVNClientWrapper proxy, IProgressMonitor monitor) throws ClientWrapperException {
		String path = resource.getLocation().toString();
		for (int i = 0; i < 2; i++) {
			int type = (i == 0) ?
					Team.getFileContentManager().getTypeForExtension(resource.getFileExtension() == null ? "" : resource.getFileExtension()) :
					Team.getFileContentManager().getTypeForName(resource.getName());
			if (type == Team.BINARY) {
				proxy.propertySet(path, PropertyData.MIME_TYPE, AddToSVNWithPropertiesOperation.BINARY_FILE, false, new SVNProgressMonitor(this, monitor, null));
			}
			else if (type == Team.TEXT) {
				PropertyData data = proxy.propertyGet(path, PropertyData.MIME_TYPE, new SVNProgressMonitor(this, monitor, null));
				if (data != null) {
					proxy.propertyRemove(path, PropertyData.MIME_TYPE, false, new SVNProgressMonitor(this, monitor, null));
				}
			}
		}
	}
	
}
