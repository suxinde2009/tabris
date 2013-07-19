/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.internal.ui.action;

import com.eclipsesource.tabris.ui.AbstractAction;

/**
 * @since 1.1
 */
public abstract class SearchAction extends AbstractAction {

  @Override
  public void execute() {
    // might be implemented by subclasses
  }

  public abstract void search( String query );

  public abstract void modified( String query, ProposalHandler proposalHandler );

}