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

package org.eclipse.team.svn.ui.action.remote;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.team.svn.core.resource.IRepositoryFile;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.ui.SVNTeamUIPlugin;
import org.eclipse.team.svn.ui.action.AbstractRepositoryTeamAction;
import org.eclipse.team.svn.ui.operation.OpenRemoteFileOperation;
import org.eclipse.team.svn.ui.repository.model.RepositoryFile;

/**
 * Open remote file action implementation
 * 
 * @author Alexander Gurov
 */
public class OpenFileAction extends AbstractRepositoryTeamAction {
	public OpenFileAction() {
		super();
	}

	public void run(IAction action) {
		RepositoryFile []resources = (RepositoryFile [])this.getSelectedResources(RepositoryFile.class);
	    IRepositoryFile []files = new IRepositoryFile[resources.length];
	    for (int i = 0; i < resources.length; i++) {
	    	files[i] = (IRepositoryFile)resources[i].getRepositoryResource();
	    }
		this.runScheduled(new OpenRemoteFileOperation(files, OpenRemoteFileOperation.OPEN_DEFAULT));
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		if (this.isEnabled()) {
			IRepositoryResource []resources = this.getSelectedRepositoryResources();
			IEditorDescriptor descriptor = SVNTeamUIPlugin.instance().getWorkbench().getEditorRegistry().getDefaultEditor(resources[0].getName());
			action.setImageDescriptor(descriptor == null ? null : descriptor.getImageDescriptor());
		}
		else {
			action.setImageDescriptor(null);
		}
	}
	
	protected boolean isEnabled() {
		return this.getSelectedResources(RepositoryFile.class).length > 0;
	}

}
