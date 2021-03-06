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

package org.eclipse.team.svn.ui.panel.view;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.team.svn.ui.SVNUIMessages;
import org.eclipse.team.svn.ui.panel.AbstractDialogPanel;
import org.eclipse.team.svn.ui.utility.UserInputHistory;
import org.eclipse.team.svn.ui.verifier.NonEmptyFieldVerifier;

/**
 * History filter modification dialog
 * 
 * @author Alexander Gurov
 */
public class HistoryFilterPanel extends AbstractDialogPanel {
    protected static final String FILTER_AUTHOR_HISTORY_NAME = "filterAuthor"; //$NON-NLS-1$
    protected static final String FILTER_COMMENT_HISTORY_NAME = "filterComment"; //$NON-NLS-1$
    protected static final String FILTER_PATH_HISTORY_NAME = "filterPath"; //$NON-NLS-1$
    
    protected String filter;
    protected Button authorButton;
    protected Button commentButton;
    protected Button pathButton;
    protected Combo authorsCombo;
    protected Combo commentsCombo;
    protected Combo pathCombo;
    protected String authorInput;
    protected String commentInput;
    protected String pathInput;
    protected String[] selectedAuthors;
    protected UserInputHistory authorsHistory;
    protected UserInputHistory commentsHistory;
    protected UserInputHistory pathHistory;
    
    public HistoryFilterPanel(String authorInput, String commentInput, String pathInput, String[] selectedAuthors) {
        super();
        this.dialogTitle = SVNUIMessages.HistoryFilterPanel_Title;
        this.dialogDescription = SVNUIMessages.HistoryFilterPanel_Description;
        this.defaultMessage = SVNUIMessages.HistoryFilterPanel_Message;
        
        this.selectedAuthors = selectedAuthors;
        this.authorInput = authorInput;
        this.commentInput = commentInput;
        this.pathInput = pathInput;
    }

    public String getAuthor() {
        return this.authorInput;
    }
    
    public String getComment() {
        return this.commentInput;
    }
    
    public String getChangedPath() {
    	return this.pathInput;
    }
    
    public Point getPrefferedSizeImpl() {
        return new Point(470, 100);
    }
    
    public void createControlsImpl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(data);

		this.authorButton = new Button(composite, SWT.CHECK);
		this.authorButton.setText(SVNUIMessages.HistoryFilterPanel_Author);
		data = new GridData();
		this.authorButton.setLayoutData(data);
		boolean enabledAuthor = this.authorInput != null;
		this.authorButton.setSelection(enabledAuthor);
		this.authorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    HistoryFilterPanel.this.authorsCombo.setEnabled(((Button)e.widget).getSelection());
			}
		});
		
		this.authorsHistory = new UserInputHistory(HistoryFilterPanel.FILTER_AUTHOR_HISTORY_NAME);
		this.authorsCombo = new Combo(composite, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.authorsCombo.setLayoutData(data);
		this.authorsCombo.setEnabled(enabledAuthor);
		this.authorsCombo.setVisibleItemCount(this.authorsHistory.getDepth());
		this.authorsCombo.setItems(this.mergeAuthorsList());
		this.authorsCombo.setText(this.authorInput == null ? "" : this.authorInput); //$NON-NLS-1$
		
		this.commentButton = new Button(composite, SWT.CHECK);
		this.commentButton.setText(SVNUIMessages.HistoryFilterPanel_Comment);
		data = new GridData();
		this.commentButton.setLayoutData(data);
		boolean enabledComment = this.commentInput != null;
		this.commentButton.setSelection(enabledComment);
		this.commentButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    HistoryFilterPanel.this.commentsCombo.setEnabled(((Button)e.widget).getSelection());
			}
		});
		
		this.commentsHistory = new UserInputHistory(HistoryFilterPanel.FILTER_COMMENT_HISTORY_NAME);
		this.commentsCombo = new Combo(composite, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.commentsCombo.setLayoutData(data);
		this.commentsCombo.setEnabled(enabledComment);
		this.commentsCombo.setVisibleItemCount(this.commentsHistory.getDepth());
		this.commentsCombo.setItems(this.commentsHistory.getHistory());
		this.commentsCombo.setText(this.commentInput == null ? "" : this.commentInput); //$NON-NLS-1$
		
		this.pathButton = new Button(composite, SWT.CHECK);
		this.pathButton.setText(SVNUIMessages.HistoryFilterPanel_Path);
		data = new GridData();
		this.pathButton.setLayoutData(data);
		boolean enabledPath = this.pathInput != null;
		this.pathButton.setSelection(enabledPath);
		this.pathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    HistoryFilterPanel.this.pathCombo.setEnabled(((Button)e.widget).getSelection());
			    HistoryFilterPanel.this.validateContent();
			}
		});
		
		this.pathHistory = new UserInputHistory(HistoryFilterPanel.FILTER_PATH_HISTORY_NAME);
		this.pathCombo = new Combo(composite, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.pathCombo.setLayoutData(data);
		this.pathCombo.setEnabled(enabledPath);
		this.pathCombo.setVisibleItemCount(this.pathHistory.getDepth());
		this.pathCombo.setItems(this.pathHistory.getHistory());
		this.pathCombo.setText(this.pathInput == null ? "" : this.pathInput); //$NON-NLS-1$
		this.attachTo(this.pathCombo, new NonEmptyFieldVerifier(SVNUIMessages.HistoryFilterPanel_Path) {
			protected String getErrorMessageImpl(Control input) {
				if (HistoryFilterPanel.this.pathCombo.isEnabled()) {
					return super.getErrorMessageImpl(input);
				}
				return null;
			}
		});
    }
    
	public String getHelpId() {
    	return "org.eclipse.team.svn.help.historyDialogContext"; //$NON-NLS-1$
	}
    
    protected void saveChangesImpl() {
        if (this.authorButton.getSelection()) {
            this.authorInput = this.authorsCombo.getText();
            this.authorsHistory.addLine(this.authorInput);
        }
        else {
        	this.authorInput = null;
        }
        if (this.commentButton.getSelection()) {
            this.commentInput = this.commentsCombo.getText();
            this.commentsHistory.addLine(this.commentInput);
        }
        else {
        	this.commentInput = null;
        }
        if (this.pathButton.getSelection()) {
            this.pathInput = this.pathCombo.getText();
            this.pathHistory.addLine(this.pathInput);
        }
        else {
        	this.pathInput = null;
        }
    }

    protected void cancelChangesImpl() {
    }
    
    protected String []mergeAuthorsList() {
    	HashSet<String> merged = new HashSet<String>(Arrays.asList(this.selectedAuthors));
    	merged.addAll(Arrays.asList(this.authorsHistory.getHistory()));
    	String []retVal = merged.toArray(new String[merged.size()]);
    	Arrays.sort(retVal);
    	return retVal;
    }

}
