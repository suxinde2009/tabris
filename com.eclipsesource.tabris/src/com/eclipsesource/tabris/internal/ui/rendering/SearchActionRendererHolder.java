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
package com.eclipsesource.tabris.internal.ui.rendering;

import java.io.Serializable;


public class SearchActionRendererHolder implements Serializable {

  private SearchActionRenderer searchActionRenderer;

  public SearchActionRenderer getSearchActionRenderer() {
    return searchActionRenderer;
  }

  public void setSearchActionRenderer( SearchActionRenderer searchActionRenderer ) {
    this.searchActionRenderer = searchActionRenderer;
  }

}