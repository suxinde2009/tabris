/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.internal;

import static com.eclipsesource.tabris.internal.Constants.EVENT_PLAYBACK;
import static com.eclipsesource.tabris.internal.Constants.EVENT_PRESENTATION;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_MODE;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_PARENT;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_VIDEO_LISTENER;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_VIDEO_URL;
import static com.eclipsesource.tabris.internal.Constants.TYPE_VIDEO;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValueAsString;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

import com.eclipsesource.tabris.widgets.Video;
import com.eclipsesource.tabris.widgets.Video.PlaybackAdapter;
import com.eclipsesource.tabris.widgets.Video.PlaybackMode;
import com.eclipsesource.tabris.widgets.Video.PresentationMode;


@SuppressWarnings("restriction")
public class VideoLifeCycleAdapter extends AbstractWidgetLCA {

  public enum PlaybackOptions {
    SPEED, REPEAT, CONTROLS_VISIBLE, PLAYBACK_MODE, PRESENTATION_MODE, HEAD_POSITION
  }

  @Override
  public void readData( Widget widget ) {
    readPlaybackMode( widget );
    readPresentationMode( widget );
  }

  private void readPlaybackMode( Widget widget ) {
    if( wasEventSent( getId( widget ), EVENT_PLAYBACK ) ) {
      String playbackMode = readEventPropertyValueAsString( getId( widget ), EVENT_PLAYBACK, PROPERTY_MODE );
      PlaybackMode newMode = PlaybackMode.valueOf( playbackMode.toUpperCase() );
      Video video = ( Video )widget;
      video.getAdapter( PlaybackAdapter.class ).setPlaybackMode( newMode );
      notifyListenersAboutPlaybackModeChange( newMode, video );
    }
  }

  private void notifyListenersAboutPlaybackModeChange( final PlaybackMode newMode, final Video video ) {
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        video.getAdapter( PlaybackAdapter.class ).firePlaybackChange( newMode );
      }
    } );
  }

  private void readPresentationMode( Widget widget ) {
    if( wasEventSent( getId( widget ), EVENT_PRESENTATION ) ) {
      String presentationMode = readEventPropertyValueAsString( getId( widget ), EVENT_PRESENTATION, PROPERTY_MODE );
      PresentationMode newMode = PresentationMode.valueOf( presentationMode.toUpperCase() );
      Video video = ( Video )widget;
      video.getAdapter( PlaybackAdapter.class ).getOptions().put( PlaybackOptions.PRESENTATION_MODE, newMode );
      notifyListenersAboutPresentationModeChange( newMode, video );
    }
  }

  private void notifyListenersAboutPresentationModeChange( final PresentationMode newMode, final Video video ) {
    ProcessActionRunner.add( new Runnable() {
      @Override
      public void run() {
        video.getAdapter( PlaybackAdapter.class ).firePresentationChange( newMode );
      }
    } );
  }

  @Override
  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    Video video = ( Video ) widget;
    PlaybackAdapter adapter = video.getAdapter( PlaybackAdapter.class );
    Map<PlaybackOptions, Object> options = adapter.getOptions();
    for( Entry<PlaybackOptions, Object> entry : options.entrySet() ) {
      preserveProperty( video, keyForEnum( entry.getKey() ), jsonizeValue( entry ) );
    }
    preserveListener( video, PROPERTY_VIDEO_LISTENER, adapter.hasVideoListener() );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ControlLCAUtil.renderChanges( ( Control )widget );
    Video video = ( Video ) widget;
    PlaybackAdapter adapter = video.getAdapter( PlaybackAdapter.class );
    Map<PlaybackOptions, Object> options = adapter.getOptions();
    for( Entry<PlaybackOptions, Object> entry : options.entrySet() ) {
      renderProperty( widget, keyForEnum( entry.getKey() ), jsonizeValue( entry ), null );
    }
    renderListener( video, PROPERTY_VIDEO_LISTENER, adapter.hasVideoListener(), false );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Video video = ( Video ) widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( video );
    clientObject.create( TYPE_VIDEO );
    clientObject.set( PROPERTY_PARENT, WidgetUtil.getId( video.getParent() ) );
    clientObject.set( PROPERTY_VIDEO_URL, video.getURL().toString() );
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  private static Object jsonizeValue( Entry<PlaybackOptions, Object> entry ) {
    Object value = entry.getValue();
    if( value instanceof PlaybackMode || value instanceof PresentationMode ) {
      value = keyForEnum( ( ( Enum )value ) );
    }
    return value;
  }

  static String keyForEnum( Enum<?> type ) {
    return type.name().toLowerCase();
  }
}