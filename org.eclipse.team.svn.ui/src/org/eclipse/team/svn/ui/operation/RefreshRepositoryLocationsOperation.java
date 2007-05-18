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

package org.eclipse.team.svn.ui.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.operation.AbstractNonLockingOperation;
import org.eclipse.team.svn.core.operation.IUnprotectedOperation;
import org.eclipse.team.svn.core.resource.IRepositoryLocation;
import org.eclipse.team.svn.ui.repository.RepositoriesView;
import org.eclipse.team.svn.ui.repository.RepositoryTreeViewer;
import org.eclipse.team.svn.ui.repository.model.RepositoryLocation;

/**
 * Refresh repository location in the repository tree operation
 * 
 * @author Alexander Gurov
 */
public class RefreshRepositoryLocationsOperation extends AbstractNonLockingOperation {
	protected IRepositoryLocation []resources;
	protected boolean deep;
	
	public RefreshRepositoryLocationsOperation(boolean deep) {
		this(null, deep);
	}

	public RefreshRepositoryLocationsOperation(IRepositoryLocation []resources, boolean deep) {
		super("Operation.RefreshLocations");
		this.resources = resources;
		this.deep = deep;
	}

	protected void runImpl(IProgressMonitor monitor) throws Exception {
		if (this.resources == null) {
			RepositoriesView.refreshRepositories(this.deep);
			return;
		}
		
		for (int i = 0; i < this.resources.length; i++) {
			final IRepositoryLocation current = this.resources[i];
			this.protectStep(new IUnprotectedOperation() {
				public void run(IProgressMonitor monitor) throws Exception {
					RepositoriesView.refresh(current, new RepositoryTreeViewer.IRefreshVisitor() {
						public void visit(Object data) {
							if (data instanceof RepositoryLocation && RefreshRepositoryLocationsOperation.this.deep) {
								((RepositoryLocation)data).refresh();
							}
						}
					});
				}
			}, monitor, this.resources.length);
		}
	}
	
}
