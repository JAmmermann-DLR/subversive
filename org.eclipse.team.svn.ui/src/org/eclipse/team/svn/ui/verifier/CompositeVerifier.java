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

package org.eclipse.team.svn.ui.verifier;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;

/**
 * Composite verifier allow us to compose simple verifiers into one more complex
 * 
 * @author Alexander Gurov
 */
public class CompositeVerifier extends AbstractVerifier implements IVerifierListener {
    protected List<AbstractVerifier> verifiers;

    public CompositeVerifier() {
        super();
        this.verifiers = new ArrayList<AbstractVerifier>();
    }

	public List<AbstractVerifier> getVerifiers() {
		return this.verifiers;
	}
	
	public void add(AbstractVerifier verifier) {
		if (!this.verifiers.contains(verifier)) {
			verifier.addVerifierListener(this);
			this.verifiers.add(verifier);
		}
	}
	
	public void remove(AbstractVerifier verifier) {
		if (this.verifiers.remove(verifier)) {
			verifier.removeVerifierListener(this);
		}
	}
	
	public void removeAll() {
		for (AbstractVerifier verifier : this.verifiers) {
			verifier.removeVerifierListener(this);
		}
		this.verifiers.clear();
	}

	public boolean verify(Control input) {
		this.hasWarning = false;
		for (AbstractVerifier verifier : this.verifiers) {
			if (!verifier.verify(input)) {
				return false;
			}
		}
		if (!this.hasWarning) {
			this.fireOk();
		}
		return true;
	}

    public void hasError(String errorReason) {
        this.fireError(errorReason);
    }

    public void hasWarning(String warningReason) {
        this.fireWarning(warningReason);
    }

    public void hasNoError() {

    }

    protected String getErrorMessage(Control input) {
        return null;
    }

    protected String getWarningMessage(Control input) {
        return null;
    }

}
