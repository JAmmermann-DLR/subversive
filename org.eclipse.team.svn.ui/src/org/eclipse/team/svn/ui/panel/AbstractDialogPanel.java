/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Gurov - Initial API and implementation
 *******************************************************************************/

package org.eclipse.team.svn.ui.panel;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.core.utility.FileUtility;
import org.eclipse.team.svn.ui.SVNTeamUIPlugin;
import org.eclipse.team.svn.ui.SVNUIMessages;
import org.eclipse.team.svn.ui.preferences.SVNTeamPreferences;
import org.eclipse.team.svn.ui.verifier.AbstractVerificationKeyListener;
import org.eclipse.team.svn.ui.verifier.AbstractVerifier;
import org.eclipse.team.svn.ui.verifier.IValidationManager;

/**
 * Abstract dialog panel
 * 
 * @author Alexander Gurov
 */
public abstract class AbstractDialogPanel implements IDialogPanel, IValidationManager {
    private AbstractVerificationKeyListener changeListener;
    
    protected IDialogManager manager;
    
    protected String dialogTitle;
    protected String dialogDescription;
    protected String defaultMessage;
    protected String imagePath;
    protected String []buttonNames;
    
    protected Composite parent;

    public AbstractDialogPanel() {
        this(new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL});
    }

    public AbstractDialogPanel(String []buttonNames) {
        this.buttonNames = buttonNames;
        this.changeListener = new VerificationKeyListener();
        this.parent = null;
    }

    public void initPanel(IDialogManager manager) {
        this.manager = manager;
    }

    public void postInit() {
		this.validateContent();
		this.setMessage(IDialogManager.LEVEL_OK, null);
    }
    
    public void addListeners() {
    	this.changeListener.addListeners();
    }

    public void dispose() {
        this.detachAll();
    }

    public String getDialogTitle() {
        return this.dialogTitle;
    }

    public String getDialogDescription() {
        return this.dialogDescription;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
    
    public String getImagePath() {
        return this.imagePath;
    }
    
    protected String getDialogID() {
    	return this.getClass().getName();
    }

    public final Point getPrefferedSize() {
		IPreferenceStore store = SVNTeamUIPlugin.instance().getPreferenceStore();
		int width = SVNTeamPreferences.getDialogInt(store, this.getDialogID() + ".width"); //$NON-NLS-1$
		int height = SVNTeamPreferences.getDialogInt(store, this.getDialogID() + ".height"); //$NON-NLS-1$
		Point prefSize = this.getPrefferedSizeImpl();
		width = Math.max(width, prefSize.x);
		height = Math.max(height, prefSize.y);
    	return new Point(width, height); 
	}
    
    public String []getButtonNames() {
        return this.buttonNames;
    }
    
    public String getHelpId() {
    	return null;
    }
    
    public final void createControls(Composite parent) {
    	this.parent = parent;
    	this.createControlsImpl(parent);
    }
    
    public void buttonPressed(int idx) {
        if (idx == 0) {
            this.saveChanges();
        }
        else {
            this.cancelChanges();
        }
    }

	public boolean isFilledRight() {
		return this.changeListener.isFilledRight();
	}

	public void attachTo(Control cmp, AbstractVerifier verifier) {
		this.changeListener.attachTo(cmp, verifier);
	}
		
	public void detachFrom(Control cmp) {
		this.changeListener.detachFrom(cmp);
	}
		
	public void detachAll() {
		this.changeListener.detachAll();
	}
	
	public void validateContent() {
		this.changeListener.validateContent();
	}
	
	public boolean validateControl(Control cmp) {
		return this.changeListener.validateControl(cmp);
	}

	protected void setMessage(int level, String message) {
		this.manager.setMessage(level, message);		
	}
	
	protected void setButtonsEnabled(boolean enabled) {
	    
	}
	
	protected void retainSize() {
		IPreferenceStore store = SVNTeamUIPlugin.instance().getPreferenceStore();
		Point size = this.parent.getSize();
		SVNTeamPreferences.setDialogInt(store, this.getDialogID() + ".width", size.x); //$NON-NLS-1$
		SVNTeamPreferences.setDialogInt(store, this.getDialogID() + ".height", size.y); //$NON-NLS-1$
	}
	
	protected final void saveChanges() {
		this.retainSize();
		this.saveChangesImpl();
	}
	
	protected final void cancelChanges() {
		this.retainSize();
		this.cancelChangesImpl();
	}
	
    protected Point getPrefferedSizeImpl() {
        return new Point(470, SWT.DEFAULT);
    }
    
    protected abstract void saveChangesImpl();
    protected abstract void cancelChangesImpl();
    protected abstract void createControlsImpl(Composite parent);
    
    /*
     * return false if dialog should not be closed
     * override if needed
     */
    public boolean canClose() {
    	return true;
    };

    public static String makeToBeOperatedMessage(IRepositoryResource []resources) {
		String message;
		if (resources.length == 1) {
			message = SVNUIMessages.RepositoryTreePanel_Message_Single;
		}
		else if (resources.length < 5) {
			message = SVNUIMessages.RepositoryTreePanel_Message_UpTo4;
		}
		else {
			message = SVNUIMessages.RepositoryTreePanel_Message_Multi;
		}
		return SVNUIMessages.format(message, new String[] {FileUtility.getNamesListAsString(resources)});
	}

	protected class VerificationKeyListener extends AbstractVerificationKeyListener {
        public VerificationKeyListener() {
            super();
        }
        
        public void hasError(String errorReason) {
			AbstractDialogPanel.this.setMessage(IDialogManager.LEVEL_ERROR, errorReason);
			this.handleButtons();
        }

        public void hasWarning(String warningReason) {
			AbstractDialogPanel.this.setMessage(IDialogManager.LEVEL_WARNING, warningReason);
			this.handleButtons();
        }

        public void hasNoError() {
			AbstractDialogPanel.this.setMessage(IDialogManager.LEVEL_OK, null);
			this.handleButtons();
        }

        protected void handleButtons() {
            AbstractDialogPanel.this.manager.setButtonEnabled(0, this.isFilledRight());
            AbstractDialogPanel.this.setButtonsEnabled(this.isFilledRight());
        }
        
    }
    
}
