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

package org.eclipse.team.svn.ui.compare;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.internal.BufferedResourceNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.team.svn.ui.SVNUIMessages;

/**
 * Conflicting files merger editor input implementation
 * 
 * @author Alexander Gurov
 */
public class ConflictingFileEditorInput extends CompareEditorInput {
	protected IFile target;
	protected IFile left;
	protected IFile right;
	protected IFile ancestor;
	
	protected MergeElement targetElement;
	
	public ConflictingFileEditorInput(CompareConfiguration configuration, IFile target, IFile left, IFile right, IFile ancestor) {
		super(configuration);
		this.target = target;
		this.left = left;
		this.right = right;
		this.ancestor = ancestor;
	}

	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		CompareConfiguration cc = this.getCompareConfiguration();
		Image img = CompareUI.getImage(this.target);
		cc.setLeftImage(img);
		cc.setRightImage(img);
		cc.setAncestorImage(img);
		cc.setLeftLabel(this.target.getName() + " " + SVNUIMessages.ConflictingFileEditorInput_Working); //$NON-NLS-1$
		cc.setRightLabel(this.target.getName() + " " + SVNUIMessages.ConflictingFileEditorInput_Repository); //$NON-NLS-1$
		cc.setAncestorLabel(this.target.getName() + " " + SVNUIMessages.ConflictingFileEditorInput_Base); //$NON-NLS-1$
		
		this.setTitle(this.target.getName() + " " + SVNUIMessages.ConflictingFileEditorInput_EditConflicts); //$NON-NLS-1$
		
		InputStream stream = null;
		try {
			stream = this.left.getContents();
			byte []buf = new byte[2048];
			int len = 0;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while ((len = stream.read(buf)) > 0) {
				output.write(buf, 0, len);
			}
			this.targetElement = new MergeElement(this.target, output.toByteArray(), true);
		} 
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new InvocationTargetException(e);
		}
		finally {
			if (stream != null) {
				try {stream.close();} catch (Exception ex) {}
			}
		}
		
		try {
			MergeElement rightRef = new MergeElement(this.right);
			rightRef.setCharsetReference(this.targetElement);
			MergeElement ancestorRef = new MergeElement(this.ancestor);
			ancestorRef.setCharsetReference(this.targetElement);
			return new Differencer().findDifferences(true, monitor, null, ancestorRef, this.targetElement, rightRef);
		}
		finally {
			monitor.done();
		}
	}
	
	public Object getAdapter(Class adapter) {
		if (IFile.class.equals(adapter)) {
			// disallow auto-flush of editor content
			return this.target;
		}
		return super.getAdapter(adapter);
	}
	
	public void saveChanges(IProgressMonitor pm) throws CoreException {
		// flush editor content...
		super.saveChanges(pm);
		// ...and save it
		this.targetElement.commit(pm);
		
		this.setDirty(false);
	}
	
	protected class MergeElement extends BufferedResourceNode {
		protected boolean editable;
		protected BufferedResourceNode charsetReference;
		
		public BufferedResourceNode getCharsetReference() {
			return this.charsetReference;
		}

		public void setCharsetReference(BufferedResourceNode charsetReference) {
			this.charsetReference = charsetReference;
		}

		public MergeElement(IResource resource) {
			this(resource, null, false);
		}
		
		public MergeElement(IResource resource, byte []initialContent, boolean editable) {
			super(resource);
			this.editable = editable;
			if (initialContent != null) {
				this.setContent(initialContent);
			}
		}

		public String getCharset() {
			return this.charsetReference != null ? this.charsetReference.getCharset() : super.getCharset();
		}
		
		public String getType() {
			String extension = ConflictingFileEditorInput.this.target.getFileExtension();
			return extension == null ? ITypedElement.UNKNOWN_TYPE : extension;
		}

		public boolean isEditable() {
			return this.editable;
		}
	}
	
}
