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
package com.eclipsesource.tabris.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.junit.Test;

import com.eclipsesource.tabris.widgets.swipe.Swipe;


public class SwipeOperationHandlerTest {

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullSwipe() {
    new SwipeOperationHandler( null );
  }

  @Test
  public void testNotifiesAboutSwipeEvent() {
    Swipe swipe = mock( Swipe.class );
    SwipeOperationHandler handler = new SwipeOperationHandler( swipe );
    JsonObject properties = new JsonObject();
    properties.add( "item", 0 );

    handler.handleNotify( "Swipe", properties );

    verify( swipe ).show( 0 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSwipeEventFailsWithoutItemProperty() {
    SwipeOperationHandler handler = new SwipeOperationHandler( mock( Swipe.class ) );
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Swipe", properties );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSwipeEventFailsWithNoIntegerItemProperty() {
    SwipeOperationHandler handler = new SwipeOperationHandler( mock( Swipe.class ) );
    JsonObject properties = new JsonObject();
    properties.add( "item", "0" );

    handler.handleNotify( "Swipe", properties );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSwipeEventFailsWithNullProperties() {
    SwipeOperationHandler handler = new SwipeOperationHandler( mock( Swipe.class ) );

    handler.handleNotify( "Swipe", null );
  }
}
