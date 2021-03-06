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

package org.eclipse.team.svn.ui.panel;

import org.eclipse.jface.dialogs.IDialogConstants;

/**
 * Abstract advanced dialog panel
 * 
 * @author Sergiy Logvin
 */
public abstract class AbstractAdvancedDialogPanel extends AbstractDialogPanel {
	protected String []buttonNamesEx;
	
	public AbstractAdvancedDialogPanel() {
        this(new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, new String[] {IDialogConstants.SHOW_DETAILS_LABEL});
    }

    public AbstractAdvancedDialogPanel(String []buttonNames, String []buttonNamesEx) {
        super(buttonNames);
        this.buttonNamesEx = buttonNamesEx;
    }
        
    public String []getButtonNamesEx() {
        return this.buttonNamesEx;
    }

    public void extendedButtonPressed(int idx) {
    	if (idx == 0) {
    		this.showDetails();
    	}
    }
    
    protected abstract void showDetails();
}
