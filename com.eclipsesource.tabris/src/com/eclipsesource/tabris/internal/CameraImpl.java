package com.eclipsesource.tabris.internal;

import static com.eclipsesource.tabris.internal.Constants.EVENT_IMAGE_SELECTION_ERROR;
import static com.eclipsesource.tabris.internal.Constants.EVENT_IMAGE_SELECTION;
import static com.eclipsesource.tabris.internal.Constants.METHOD_OPEN;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_IMAGE;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_RESOLUTION;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_SAVETOALBUM;
import static com.eclipsesource.tabris.internal.Constants.TYPE_CAMERA;
import static com.eclipsesource.tabris.internal.Preconditions.checkArgumentNotNull;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.eclipsesource.tabris.camera.Camera;
import com.eclipsesource.tabris.camera.CameraListener;
import com.eclipsesource.tabris.camera.CameraOptions;


@SuppressWarnings("restriction")
public class CameraImpl extends AbstractOperationHandler implements Camera {

  private final RemoteObject remoteObject;
  private final List<CameraListener> listeners;

  public CameraImpl() {
    listeners = new ArrayList<CameraListener>();
    remoteObject = ( ( ConnectionImpl )RWT.getUISession().getConnection() ).createServiceObject( TYPE_CAMERA );
    remoteObject.setHandler( this );
  }

  private Image decodeImage( String encodedImage ) {
    byte[] bytes = Base64.decode( encodedImage );
    ByteArrayInputStream stream = new ByteArrayInputStream( bytes );
    return new Image( Display.getCurrent(), stream );
  }

  @Override
  public void addCameraListener( CameraListener listener ) {
    checkArgumentNotNull( listener, CameraListener.class.getSimpleName() );
    listeners.add( listener );
  }

  @Override
  public void removeCameraListener( CameraListener listener ) {
    checkArgumentNotNull( listener, CameraListener.class.getSimpleName() );
    listeners.remove( listener );
  }

  @Override
  public void takePicture( CameraOptions options ) {
    checkArgumentNotNull( options, "Options" );
    Map<String, Object> properties = createProperties( options );
    remoteObject.call( METHOD_OPEN, properties );
  }

  private Map<String, Object> createProperties( CameraOptions options ) {
    Map<String, Object> properties = new HashMap<String, Object>();
    addResolution( properties, options );
    addSaveToAlbum( properties, options );
    return properties;
  }

  private void addResolution( Map<String, Object> properties, CameraOptions options ) {
    Point resolution = options.getResolution();
    if( resolution != null ) {
      properties.put( PROPERTY_RESOLUTION, new int[] { resolution.x, resolution.y } );
    }
  }

  private void addSaveToAlbum( Map<String, Object> properties, CameraOptions options ) {
    if( options.savesToAlbum() ) {
      properties.put( PROPERTY_SAVETOALBUM, Boolean.TRUE );
    }
  }

  @Override
  public void handleNotify( String event, Map<String,Object> properties ) {
    if( EVENT_IMAGE_SELECTION.equals( event ) ) {
      Image image = decodeImage( ( String )properties.get( PROPERTY_IMAGE ) );
      notifyListenersWithImage( image );
    } else if( EVENT_IMAGE_SELECTION_ERROR.equals( event ) ) {
      notifyListenersWithError();
    }
  }

  private void notifyListenersWithImage( Image image ) {
    for( CameraListener listener : listeners ) {
      listener.receivedPicture( image );
    }
  }

  private void notifyListenersWithError() {
    for( CameraListener listener : listeners ) {
      listener.receivedPicture( null );
    }
  }

  RemoteObject getRemoteObject() {
    return remoteObject;
  }

}
