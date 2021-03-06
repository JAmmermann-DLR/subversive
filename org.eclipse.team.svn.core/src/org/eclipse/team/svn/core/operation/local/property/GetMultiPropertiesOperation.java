/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sergiy Logvin (Polarion Software) - initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.core.operation.local.property;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.IStateFilter;
import org.eclipse.team.svn.core.SVNMessages;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.connector.SVNEntryRevisionReference;
import org.eclipse.team.svn.core.connector.SVNProperty;
import org.eclipse.team.svn.core.connector.SVNRevision;
import org.eclipse.team.svn.core.operation.AbstractActionOperation;
import org.eclipse.team.svn.core.operation.IUnprotectedOperation;
import org.eclipse.team.svn.core.operation.SVNProgressMonitor;
import org.eclipse.team.svn.core.resource.ILocalResource;
import org.eclipse.team.svn.core.resource.IRepositoryLocation;
import org.eclipse.team.svn.core.resource.IResourceProvider;
import org.eclipse.team.svn.core.svnstorage.SVNRemoteStorage;
import org.eclipse.team.svn.core.utility.FileUtility;
import org.eclipse.team.svn.core.utility.ProgressMonitorUtility;
import org.eclipse.team.svn.core.utility.SVNUtility;

/**
 * Operation to get properties for multiple resources
 * 
 * @author Sergiy Logvin
 */
public class GetMultiPropertiesOperation extends AbstractActionOperation implements IResourceProvider, IPropertyProvider {
	protected IResource []resources;
	protected HashMap<IResource, SVNProperty []> properties;
	protected String propertyName;
	protected int depth;
	protected IStateFilter filter;
	
	/**
	 * @param resources the resources for which the properties are requested 
	 * @param propertyName the name of a property which value is requested. 
	 * 		If null then all the properties will be computed for the resources.
	 */
	public GetMultiPropertiesOperation(IResource []resources, int depth, IStateFilter filter, String propertyName) {
		super("Operation_GetMultiProperties", SVNMessages.class); //$NON-NLS-1$
		this.resources = resources;
		this.propertyName = propertyName;
		this.filter = filter != null ? filter : IStateFilter.SF_VERSIONED;
		this.properties = new HashMap<IResource, SVNProperty []>();
		this.depth = depth;
	}
	
	public IResource []getResources() {
		Set<IResource> resources = this.properties.keySet();
		return resources.toArray(new IResource[resources.size()]);
	}
	
	public SVNProperty []getProperties(IResource resource) {
		return this.properties.get(resource);
	}
	
	protected void runImpl(IProgressMonitor monitor) throws Exception {
		for (int i = 0; i < this.resources.length && !monitor.isCanceled(); i++) {
			final IResource current = this.resources[i];
			IRepositoryLocation location = SVNRemoteStorage.instance().getRepositoryLocation(current);
			final ISVNConnector proxy = location.acquireSVNProxy();
			try {
				this.protectStep(new IUnprotectedOperation() {
					public void run(final IProgressMonitor monitor) throws Exception {
						FileUtility.visitNodes(current, new IResourceVisitor() {
							public boolean visit(IResource resource) throws CoreException {
								if (monitor.isCanceled() || FileUtility.isNotSupervised(resource)) {
									return false;
								}
								ILocalResource local = SVNRemoteStorage.instance().asLocalResourceAccessible(resource);
								if (GetMultiPropertiesOperation.this.filter.accept(local)) {
									GetMultiPropertiesOperation.this.processResource(proxy, resource, monitor);
								}
								return GetMultiPropertiesOperation.this.filter.allowsRecursion(local);
							}
						}, GetMultiPropertiesOperation.this.depth);
					}
				}, monitor, this.resources.length);
			}
			finally {
				location.releaseSVNProxy(proxy);
			}
		}
	}

	protected void processResource(final ISVNConnector proxy, final IResource current, IProgressMonitor monitor) {
		ProgressMonitorUtility.setTaskInfo(monitor, this, current.getFullPath().toString());
		this.protectStep(new IUnprotectedOperation() {
			public void run(IProgressMonitor monitor) throws Exception {
				String wcPath = FileUtility.getWorkingCopyPath(current);
				if (GetMultiPropertiesOperation.this.propertyName != null) {
					SVNProperty data = proxy.getProperty(new SVNEntryRevisionReference(wcPath, null, SVNRevision.WORKING), GetMultiPropertiesOperation.this.propertyName, null, new SVNProgressMonitor(GetMultiPropertiesOperation.this, monitor, null));
					if (data != null) {
						GetMultiPropertiesOperation.this.properties.put(current, new SVNProperty[] {data});
					}
				}
				else {
					SVNProperty []data = SVNUtility.properties(proxy, new SVNEntryRevisionReference(wcPath, null, SVNRevision.WORKING), ISVNConnector.Options.NONE, new SVNProgressMonitor(GetMultiPropertiesOperation.this, monitor, null));
					if (data != null && data.length != 0) {
						GetMultiPropertiesOperation.this.properties.put(current, data);
					}
				}
			}
		}, monitor, 1);
	}
	
}
