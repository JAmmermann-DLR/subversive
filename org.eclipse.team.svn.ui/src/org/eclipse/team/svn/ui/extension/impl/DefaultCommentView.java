/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andrej Zachar - Initial API and implementation
 *    Jens Scheidtmann - butraq:logregex property display disgresses from specification (bug 243678)
 *    Alexei Goncharov (Polarion Software) - URL decoration with bugtraq properties does not work properly (bug 252563)
 *******************************************************************************/

package org.eclipse.team.svn.ui.extension.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.ui.extension.factory.ICommentView;
import org.eclipse.team.svn.ui.panel.local.CommitPanel;
import org.eclipse.team.svn.ui.properties.bugtraq.BugtraqModel;
import org.eclipse.team.svn.ui.properties.bugtraq.IssueList;
import org.eclipse.team.svn.ui.properties.bugtraq.LinkList;
import org.eclipse.team.svn.ui.properties.bugtraq.LinkList.LinkPlacement;

/**
 * Default implementation of history comment
 * 
 * @author Andrej Zachar
 */
public class DefaultCommentView implements ICommentView {
	protected StyledText multilineComment;
	protected Cursor handCursor;
	protected Cursor busyCursor;
	
	protected Color black;
	protected Color blue;
	
	protected boolean mouseDown;
	protected boolean dragEvent;
	
	protected BugtraqModel model;
	protected IssueList linkList = new IssueList();
	protected IssueList hyperList = new IssueList();
	
	protected final static String linkRegExp = "(?:http|https|file|svn|svn\\+[\\w]+)\\:/(?:/)?(?:/[^\\s\\|\\{\\}\"><#\\^\\~\\[\\]`]+)+"; //$NON-NLS-1$

	public void createCommentView(Composite parent) {
		this.createCommentView(parent, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
	}
	
	public void createCommentView(Composite parent, int style) {
		this.multilineComment = new StyledText(parent, style);
		this.multilineComment.setEditable(false);
		// set system color
		this.multilineComment.setBackground(this.multilineComment.getBackground());
		
		this.handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		this.busyCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_WAIT);
		
		this.blue = new Color(parent.getDisplay(), 0, 0, 192);
		this.black = new Color(parent.getDisplay(), 2, 200, 30);
		
		this.multilineComment.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					DefaultCommentView.this.mouseDown = true;
				}
			}
			public void mouseUp(MouseEvent e) {
				DefaultCommentView.this.mouseDown = false;
				StyledText text = (StyledText)e.widget;
				int offset = text.getCaretOffset();
				LinkPlacement issue = DefaultCommentView.this.linkList.getLinkAt(offset);
				LinkPlacement hIssue = DefaultCommentView.this.hyperList.getLinkAt(offset);
				if (DefaultCommentView.this.dragEvent) {
					DefaultCommentView.this.dragEvent = false;
					if (issue != null) {
						text.setCursor(DefaultCommentView.this.handCursor);
						text.getStyleRangeAtOffset(offset).background = DefaultCommentView.this.blue;
					}
					else if (hIssue != null) {
						text.setCursor(DefaultCommentView.this.handCursor);
						text.getStyleRangeAtOffset(offset).background = DefaultCommentView.this.blue;
					} 
				}
				else if (issue != null) {
					text.setCursor(DefaultCommentView.this.busyCursor);
					String url = DefaultCommentView.this.getModel().getResultingURL(issue);
					if (url != null) {
						Program.launch(url);
					}
					text.setCursor(null);
					text.getStyleRangeAtOffset(offset).background = DefaultCommentView.this.black;
				}
				else if (hIssue != null) {
					text.setCursor(DefaultCommentView.this.busyCursor);
					String url = hIssue.getURL();
					if (url != null) {
						Program.launch(url);
					}
					text.setCursor(null);
					text.getStyleRangeAtOffset(offset).background = DefaultCommentView.this.black;
				}
			}
		});
		this.multilineComment.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				// Do not change cursor on drag events
				if (DefaultCommentView.this.mouseDown) {
					if (!DefaultCommentView.this.dragEvent) {
						StyledText text = (StyledText) e.widget;
						text.setCursor(null);
					}
					DefaultCommentView.this.dragEvent = true;
					return;
				}
				StyledText text = (StyledText) e.widget;
				int offset = -1;
				try {
					offset = text.getOffsetAtLocation(new Point(e.x, e.y));
				}
				catch (IllegalArgumentException ex) {
					// ok
				}
				if (offset != -1 && DefaultCommentView.this.linkList.hasLinkAt(offset)) {
					text.setCursor(DefaultCommentView.this.handCursor);
					text.getStyleRangeAtOffset(offset).background = DefaultCommentView.this.blue;
					DefaultCommentView.this.multilineComment.redraw();
				}
				else if (offset != -1 && DefaultCommentView.this.hyperList.hasLinkAt(offset)) {
					text.setCursor(DefaultCommentView.this.handCursor);
					text.getStyleRangeAtOffset(offset).background = DefaultCommentView.this.blue;
					DefaultCommentView.this.multilineComment.redraw();
				}
				else {
					text.setCursor(null);
				}
			}
		});

		this.multilineComment.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				DefaultCommentView.this.linkList.getLinks().clear();
				DefaultCommentView.this.hyperList.getLinks().clear();
				StyledText textView = (StyledText)e.getSource();
				String text = textView.getText();
				Pattern linkPattern = Pattern.compile(DefaultCommentView.linkRegExp);
				Matcher linkMatcher = linkPattern.matcher(text);
				int start = 0;
				while (linkMatcher.find(start)) {
					start = linkMatcher.end();
					DefaultCommentView.this.hyperList.getLinks().add(new LinkPlacement(linkMatcher.start(), start, text));
				}
				if (DefaultCommentView.this.getModel().getMessage() != null ||
					DefaultCommentView.this.getModel().getLogregex() != null) {
					DefaultCommentView.this.linkList.parseMessage(text, DefaultCommentView.this.getModel());
				}
				List<StyleRange> styledRanges = new ArrayList<StyleRange>();
				for (LinkPlacement issue : DefaultCommentView.this.linkList.getLinks()) {
					StyleRange range = new StyleRange();
					range.start  = issue.getStart();
					range.length = issue.getEnd() - issue.getStart();
					range.foreground = DefaultCommentView.this.blue;
					range.underline = true;
					styledRanges.add(range);
				}
				for (LinkList.LinkPlacement issue : DefaultCommentView.this.hyperList.getLinks()) {
					StyleRange range = new StyleRange();
					range.start  = issue.getStart();
					range.length = issue.getEnd() - issue.getStart();
					range.foreground = DefaultCommentView.this.blue;
					range.underline = true;
					styledRanges.add(range);
				}
				StyleRange[] sorted = styledRanges.toArray(new StyleRange[styledRanges.size()]);
				for (int i = 0; i < sorted.length-1; i++){
					for (int j = sorted.length-1; j > i; j--) {
						if (sorted[j].start < sorted[j-1].start) {
							StyleRange tmp = sorted[j];
							sorted[j] = sorted[j-1];
							sorted[j-1] = tmp;
						}
					}
				}
				textView.setStyleRanges(sorted);
			}
		});
		
		this.multilineComment.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				DefaultCommentView.this.busyCursor.dispose();
				DefaultCommentView.this.handCursor.dispose();
				DefaultCommentView.this.blue.dispose();
				DefaultCommentView.this.black.dispose();			
			}
		});
	}

	public void usedFor(IResource resource) {
		CommitPanel.CollectPropertiesOperation bugtraqOp = new CommitPanel.CollectPropertiesOperation(new IResource[] {resource});
		bugtraqOp.run(new NullProgressMonitor());
		this.model = bugtraqOp.getBugtraqModel();
	}

	public void usedFor(IRepositoryResource resource) {
		//FIXME implement support of Bugtraq properties
	}

	public void setComment(String comment) {
		this.multilineComment.setText(comment);
	}
	
	protected BugtraqModel getModel() {
		return this.model != null ? this.model : new BugtraqModel();
	}
	
}