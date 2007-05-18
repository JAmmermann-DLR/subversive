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

package org.eclipse.team.svn.ui.panel.common;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.ui.composite.RepositoryResourceSelectionComposite;
import org.eclipse.team.svn.ui.panel.AbstractDialogPanel;

/**
 * Abstract complementary URL selection panel
 * 
 * @author Alexander Gurov
 */
public abstract class AbstractRepositoryResourceSelectionPanel extends AbstractDialogPanel {
	protected IRepositoryResource selectedResource;
	
	protected long currentRevision;
	protected boolean stopOnCopy;
	
	protected String historyKey;
	protected RepositoryResourceSelectionComposite selectionComposite;
	
	protected String selectionTitle;
	protected String selectionDescription;
	protected int twoRevisions;
	
    public AbstractRepositoryResourceSelectionPanel(IRepositoryResource baseResource, long currentRevision, String title, String proposal, String historyKey, boolean stopOnCopy, String selectionTitle, String selectionDescription) {
        super();
        this.dialogTitle = title;
        this.dialogDescription = proposal;
        
        this.stopOnCopy = stopOnCopy;
		this.historyKey = historyKey;
		this.selectedResource = baseResource;
		this.currentRevision = currentRevision;
		this.selectionTitle = selectionTitle;
		this.selectionDescription = selectionDescription;
    }
    
    public AbstractRepositoryResourceSelectionPanel(IRepositoryResource baseResource, long currentRevision, String title, String proposal, String historyKey, boolean stopOnCopy, String selectionTitle, String selectionDescription, int twoRevisions) {
    	this(baseResource, currentRevision, title, proposal, historyKey, stopOnCopy, selectionTitle, selectionDescription);
    	this.twoRevisions = twoRevisions;
    }

	public IRepositoryResource []getSelection(IResource []to) {
		IRepositoryResource base = this.getSelectedResource();
		if (to.length == 1) {
			return new IRepositoryResource[] {base};
		}
		IRepositoryResource []retVal = new IRepositoryResource[to.length];
		String baseUrl = base.getUrl();
		for (int i = 0; i < retVal.length; i++) {
			String url = baseUrl + "/" + to[i].getName();
			retVal[i] = to[i].getType() == IResource.FILE ? (IRepositoryResource)base.asRepositoryFile(url, false) : base.asRepositoryContainer(url, false);
		}
		return retVal;
	}

	public IRepositoryResource getSelectedResource() {
		return this.selectedResource;
	}
	
    public void createControls(Composite parent) {
        GridData data = null;

        this.selectionComposite = new RepositoryResourceSelectionComposite(parent, SWT.NONE, this, this.historyKey, this.selectedResource, this.stopOnCopy, this.selectionTitle, this.selectionDescription, this.twoRevisions);
        data = new GridData(GridData.FILL_HORIZONTAL);
        this.selectionComposite.setLayoutData(data);
        this.selectionComposite.setCurrentRevision(this.currentRevision);
    }
    
    protected void saveChanges() {
    	this.selectedResource = this.selectionComposite.getSelectedResource();
    	this.selectionComposite.saveHistory();
    }

    protected void cancelChanges() {

    }

}
