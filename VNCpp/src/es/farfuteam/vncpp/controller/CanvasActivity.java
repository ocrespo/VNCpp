/*
 	Copyright 2013 Oscar Crespo Salazar
 	Copyright 2013 Gorka Jimeno Garrachon
 	Copyright 2013 Luis Valero Martin
  
	This file is part of VNC++.

	VNC++ is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	any later version.
	
	VNC++ is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with VNC++.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.farfuteam.vncpp.controller;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import es.farfuteam.vncpp.model.ObserverCanvas;
import es.farfuteam.vncpp.model.VncBridgeJNI;
import es.farfuteam.vncpp.model.VncBridgeJNI.ConnectionError;
import es.farfuteam.vncpp.model.VncBridgeJNI.MouseEvent;
import es.farfuteam.vncpp.view.CanvasView;
import es.farfuteam.vncpp.view.SlideListFragment;


/**
 * @author roni
 *
 */

public class CanvasActivity extends FragmentActivity implements ObserverCanvas{
	
	//etiqueta debug
	private static final String DEBUG_TAG = "CanvasActivity";
	
	private static final int VELOCITY_MOD = 30;
	
	private static final int timerConnection = 30000;

	private VncBridgeJNI vnc;
	private CanvasView canvas;

	private GestureDetector gesture;
	private ScaleGestureDetector scaleGesture;
	
	private int realX = 0;
	private int realY = 0;
	private float scaleFactor = 1;
	
	private Thread scrollThread;
	private Thread endScrollThread;
	
	private Runnable endScrollRun;
	
	private boolean drag = false;
	private boolean zoom = false;
	private boolean oneLoopMore = false;
	
	private Vector<String> specialKeys;
	private Vector<String> functionKeys;
	
	
	private SlidingMenu menu;
	
	private boolean waitDialog = false;
	
	ProgressDialog progressDialog;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//No se muestra la barra de accion superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.canvas);
		canvas = (CanvasView)findViewById(R.id.vnc_canvas);		
		
		//setBehindContentView(R.layout.activity_main);
		menu = new SlidingMenu(this);
		startSlideMenu();
				
		Bundle info = getIntent().getExtras();		
		
		progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando CanvasView...");
        progressDialog.setMessage("Cargando imagen del servidor");
        
        
        progressDialog.show();
        progressDialog.setCancelable(false);
        
        
        runTimerConnection();
        
        iniConnection(info.getString("ip"), info.getString("port"),info.getString("psw"),info.getString("color"),info.getBoolean("wifi"));
		
		//TODO en info tienes la psw,color,wifi(true o false)...las sacas con los get
        
		
		GestureListener gestureListener =  new GestureListener();
		gesture = new GestureDetector(gestureListener);
		
		scaleGesture = new ScaleGestureDetector(this,gestureListener);
		
		endScrollRun = new Runnable() {
			
			@Override
			public void run() {
				while(drag || oneLoopMore){
					oneLoopMore = false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// Auto-generated catch block
	
					}
					
				}
				
				canvas.endDrag();
				
			}
		};	
		
	}
	private void runTimerConnection(){
		final Activity activityThis = this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(timerConnection);
				} catch (InterruptedException e) {
					// Auto-generated catch block

				}
				if(progressDialog.isShowing()){
					waitDialog = true;
					activityThis.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//Ddialogo "Se ha excedido el tiempo de conexion"
							showDialog(8);
							
						}
					});
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							while(waitDialog);
							vnc.finishVnc();
							finishConnection();		
						}
					}).start();
					
				
				}
				
			}
		}).start();
	}
	private void iniConnection(final String host,final String port,final String pass, final String quality,final Boolean compress){
		vnc = new VncBridgeJNI();
		vnc.addObserver(this);
		//ConnectionError error = vnc.startConnect(info.getString("ip"),Integer.parseInt(info.getString("port")));
		//new Thread(vnc).start();
		final Activity activityThis = this;
		new Thread( new Runnable() {
				
			@Override
			public void run() {
				ConnectionError error = vnc.startConnect(host,Integer.parseInt(port),pass,quality,compress);
					
				if(error != ConnectionError.ALLOK){
					waitDialog = true;
						
					
					activityThis.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
						
							showDialog(1);
							
						}
					});
					//showDialog(1);
					new Thread(new Runnable() {
							
						@Override
						public void run() {
							while(waitDialog);
								finishConnection();
								
							}
					}).start();
					/*while(waitDialog);
					vnc = null;
					finish();*/
					return;
				}
			}
		}).start();
	}
	private void startSlideMenu(){
        // configure the SlidingMenu
        //para modificar el margen de dips cambiar MARGIN_THRESHOLD en la 
		//clase CustomViewBehind de la slideLibrary (antes a 48, bajado a 28).
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		
		menu.setMenu(R.layout.menu_frame);		
		
		//lista lateral con el menu
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new SlideListFragment())
		.commit();
	}
	

	
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.canvas_menu, menu);
		return true;
	}
	*/
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.keyboard_down:			
			showKeyboard();
			return true;
			
		case R.id.ctrl_events:
			showDialog(4);
			return true;
			
		case R.id.send_text:
			showDialog(7);
			return true;
			
		case R.id.center_image:
			showDialog(5);
			return true;
			
		case R.id.help_down:
			return true;
			
		}
		return super.onMenuItemSelected(featureId, item);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent evt){
		super.onKeyDown(keyCode, evt);
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(vnc != null){
				//Dialog pregunta salir
				showDialog(2);
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU){
			Log.i("tag","en menu del canvas");
			menu.showMenu();
			//this.openOptionsMenu();
		}
		return true;
		
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.ACTION_MULTIPLE){
			int keyunicode = event.getUnicodeChar(event.getMetaState() );
		    char character = (char) keyunicode;
	
		    
		    
			vnc.sendKey(event.getKeyCode());
		    
		    
		    Log.e(DEBUG_TAG,String.valueOf( event.getKeyCode()));
		}
	    return super.dispatchKeyEvent(event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent evt){
		super.onKeyUp(keyCode, evt);
	
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		super.onTouchEvent(event);
		
		scaleGesture.onTouchEvent(event);
		gesture.onTouchEvent(event);
		
		int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
        
	        //case (MotionEvent.ACTION_DOWN):
	            
	        //case (MotionEvent.ACTION_MOVE):
	 
	        case (MotionEvent.ACTION_UP):
	        	return onUpMouse(event);

	       case (MotionEvent.ACTION_CANCEL):
	            Log.d(DEBUG_TAG, "La accion ha sido CANCEL");
	            return true;
	        case (MotionEvent.ACTION_OUTSIDE):
	            Log.d(DEBUG_TAG,
	                    "La accion ha sido fuera del elemento de la pantalla");
	            return true;
	        default:
	            return true;
	            
        }
	}
	
	public boolean onUpMouse(MotionEvent e){
		if(drag || zoom){
			if(endScrollThread != null){
				if(!endScrollThread.isAlive()){
					endScrollThread = new Thread(endScrollRun);
					endScrollThread.start();
				}
			}
			else{
				endScrollThread = new Thread(endScrollRun);
				endScrollThread.start();
			}
			oneLoopMore = true;
			drag = false;
			zoom = false;
        }
		
        
		return true;
	}
	@Override
	public void updateRedraw(int x,int y,int width,int height) {
		canvas.reDraw();
		canvas.postInvalidate();
		if (canvas.getRealWidth() == x+width && canvas.getRealHeight() == y+height){
			progressDialog.dismiss(); 
		}
	}
	
	@Override
	public void updateIniFrame(int[] data,int offset,int x,int y,int width,int height,int realWidth,int realHeight) {
		canvas.initCanvas(data, offset, realWidth, x,y,  realWidth, realHeight);		
	}

	@Override
	public void updateFinish() {
		//Dialog servidor interrumpida conexion
		waitDialog = true;

		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			
				showDialog(3);
				
			}
		});
		while(waitDialog);
		finishConnection();
	}
	
	//publica porque necesito llamarla desde el fragment slide lateral
	public void showKeyboard(){
		InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.toggleSoftInput(0, 0);
        menu.toggle();
	}
	
	private void finishConnection(){
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		vnc = null;
		finish();
	}
	
	private boolean doScroll(float distanceX,float distanceY){
		if(!drag){
			drag = true;
			canvas.startDrag();
		}
		float moveX = 0;
		float moveY =0;
		boolean allOkX= true;
		boolean allOkY= true;
		
		float width = canvas.getWidth() / scaleFactor ;
		float height = canvas.getHeight() / scaleFactor ;

		if((realX+width >= canvas.getRealWidth() && distanceX > 0)|| (realX == 0 && distanceX < 0) || (canvas.getWidth()/scaleFactor > canvas.getRealWidth() )){
			moveX = 0;
			allOkX = false;
		}
		if( (realY+height >= canvas.getRealHeight() && distanceY>0) || (realY == 0 && distanceY < 0)|| (canvas.getHeight()/scaleFactor > canvas.getRealHeight() ) ){
			moveY = 0;
			allOkY = false;
		}
		if(allOkX){
			if(realX+width+distanceX > canvas.getRealWidth() || realX+distanceX < 0){ 
				if(distanceX < 0){
					moveX = -realX ;
					
				}
				else{
					moveX = canvas.getRealWidth() - (realX + width);
				}
				allOkX = false;
			}
		}
		if(allOkY){
			if(realY+height+distanceY > canvas.getRealHeight() || realY+distanceY < 0){
				if(distanceY < 0){
					moveY = -realY ;
				}
				else{
					moveY = canvas.getRealHeight() - (realY + height);
				}
				allOkY = false;
			}
		}
		if(allOkY){
			moveY =  distanceY;
		}
		if(allOkX){
			moveX =  distanceX;
		}
		if(moveX != 0 || moveY != 0){
			
			realX = realX + (int)moveX;
			realY = realY + (int)moveY;
			
		
			scrollThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					canvas.scrollTo((int)(realX*scaleFactor),(int)(realY*scaleFactor));
				}
			});
			scrollThread.start();
		}
		
		return true;
	}
	

	private class GestureListener implements OnScaleGestureListener,OnGestureListener{
		private float x;
		private float y;
		@Override
		public boolean onDown(MotionEvent e) {
			
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			if(drag){
				drag = false;
		    	canvas.endDrag();
			}
			float posX = realX + (e.getX() / scaleFactor);
			float posY = realY + (e.getY() / scaleFactor);
			if(vnc != null)
				vnc.sendMouseEvent((int)posX,(int)posY,MouseEvent.RightClick);
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			onScroll(e1, e2,  -velocityX/VELOCITY_MOD, -velocityY/VELOCITY_MOD);
			
			return true;
		}
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX,
				float distanceY) {
			if(!zoom){
				doScroll(distanceX, distanceY);
			}
			return true;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if(drag){
				drag = false;
				oneLoopMore = false;
		    	canvas.endDrag();
			}
			
			
			float posX = realX + (e.getX() / scaleFactor);
			float posY = realY + (e.getY() / scaleFactor);
			if(vnc != null)
				vnc.sendMouseEvent((int)posX,(int)posY,MouseEvent.Click);
			return true;
		}
		
		
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float factor = detector.getScaleFactor();
			boolean isDo = false;
			if(factor > 1 && scaleFactor != 3){
				if(scaleFactor + 0.1 < 3){
					scaleFactor += 0.1;
					
				}
				else{
					scaleFactor = 3;
					
					
				}
				isDo = true;
			}
			else if(factor < 1 && scaleFactor != 0.5f){
				if(scaleFactor - 0.1 > 0.5f){
					scaleFactor -= 0.1;
					
				}
				else{
					scaleFactor = 0.5f;
				}
				isDo = true;
			}
			
			canvas.setScale(scaleFactor,0,0);
		
			if(isDo){
				if(factor > 1){		
					int moveX = (int)(x*scaleFactor);
					int moveY = (int)(y*scaleFactor);
					if(x < 0){
						moveX = 0;
						y = 0;
					}
					
					if(y < 0){
						moveY = 0;
						y = 0;
					}
					
					realX = (int)x;
					realY = (int)y;
					canvas.scrollTo(moveX,moveY);
				}
				else if(factor<1 ){
					int auxX = 0;
					int auxY = 0;
					if(x * scaleFactor > canvas.getRealWidth()){
						auxX = -100;
					}
					else{
						auxX = -10;
					}
					if(y * scaleFactor > canvas.getRealHeight()){
						auxY = -100;
					}
					else{
						auxY = -10;
					}
					doScroll(auxX, auxY);
				}
				
			}
			return true;
		}
	
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			
			zoom = true;
			drag = true;
			canvas.startDrag();
			
			x= (int)(((detector.getFocusX() / scaleFactor) + realX) -detector.getFocusX() ) ;
			y= (int)(((detector.getFocusY() / scaleFactor) + realY) -detector.getFocusY() );
		
			if(x <0){
				x = 0;
			}
			if(realY < 0){
				y = 0;
			}		
	
			return true;
		}
	
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			
			doScroll(1, 1);

			//isDrag = false;
			//canvas.endDrag();

		}

	};
	
	
	//control de dialogos
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	 
	    switch(id)
	    {
	        case 1:
	            dialog = createServerNotFoundDialog();
	            
	            break;
	        case 2:
	            dialog = exitDialog();
	            break;
	            
	        case 3:
	        	dialog = serverInterruptConexionDialog();
	        	break;
	        	
	        case 4:
	        	dialog = comboEventsDialog();
	        	menu.toggle();
	        	break;
	        	
	        case 5:
	        	dialog = functionKeysDialog();
	        	menu.toggle();
	        	break;
	        	
	        case 6:	
	        	dialog = openHelpDialog();
	        	menu.toggle();
	        	break;
	        	
	        case 7:
	        	dialog = sendTextDialog();
	        	menu.toggle();
	        	break;
	        	
	        case 8:
	        	dialog = timeExceededDialog();
	        	break;
	    }
	 
	    return dialog;
	}
	
	
	private Dialog sendTextDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.send_text_title);
		String body = getString(R.string.send_text_here);
		 
	    builder.setTitle(info);
	    builder.setMessage(body);
        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        builder.setView(input);
        
	    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
      	  public void onClick(DialogInterface dialog, int whichButton) {
      		  dialog.cancel();
      	  }
	    });
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	        	//You will get as string input data in this variable.
	        	 // here we convert the input to a string and show in a toast.
	        	 String srt = input.getEditableText().toString();
	        	 Log.d("texto enviado", srt);
	        	// TODO Enviar texto Oscar
	        	 //Toast.makeText(this.,srt,Toast.LENGTH_LONG).show();
	        }

	    });
	 
	    return builder.create();
	}
	
	private Dialog createServerNotFoundDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.server_dialog);
		String body = getString(R.string.server_not_found);
		 
	    builder.setTitle(info);
	    builder.setMessage(body);
	    builder.setCancelable(false);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            waitDialog = false;
	        }

	    });
	 
	    return builder.create();
	}
	
	
	private Dialog exitDialog() {
		final String titleExit = getString(R.string.DialogTitleExit);
		final String question = getString(R.string.DialogQuestion);			
	   
	    return new AlertDialog.Builder(this)
	      .setIcon(android.R.drawable.ic_dialog_alert)
	      .setTitle(titleExit)
	      .setMessage(question)
	      .setNegativeButton(android.R.string.cancel, null)//sin listener
	      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which){
				vnc.finishVnc();
				finishConnection();
	        }
	      })
	      .show();
	}
	
	private Dialog serverInterruptConexionDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.server_dialog);
		String body = getString(R.string.server_interrupt);
		 
	    builder.setTitle(info);
	    builder.setMessage(body);
	    builder.setCancelable(false);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            waitDialog = false;
	        }

	    });
	    
	    return builder.create();
	}
	
	private Dialog timeExceededDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.time_info);
		String body = getString(R.string.time_exceeded);
		 
	    builder.setTitle(info);
	    builder.setMessage(body);
	    builder.setCancelable(false);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            waitDialog = false;
	        }

	    });
	    
	    return builder.create();
	}
	
	private Dialog comboEventsDialog() {
	   
		specialKeys = new Vector<String>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    final String[] arraySpecialsKeys = getResources().getStringArray(R.array.keys_array);
	    
	    // Set the dialog title
	    builder.setTitle(R.string.combo_keys_title)
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(R.array.keys_array, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                	   specialKeys.add(arraySpecialsKeys[which]);
	                   } else if (specialKeys.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                	   specialKeys.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	           .setNeutralButton(R.string.function_keys_title, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//muestra function keys
					showDialog(5);					
				}
			})
	    // Set the action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //TODO evento al seleccionar ctrl,alt...
	            	   //las teclas elegidas estan el el vector specialKeys
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //se vacia el vector
	            	   specialKeys.clear();
	               }
	           });
	    

	    return builder.create();	
		
	}
	
	private Dialog functionKeysDialog() {		   

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    functionKeys = new Vector<String>();  // Where we track the selected items
	    
	    final String[] arrayFunctionKey = getResources().getStringArray(R.array.function_keys_array);
	     
	    	    
	    // Set the dialog title
	    builder.setTitle(R.string.function_keys_title)
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(R.array.function_keys_array, null,
	        		   new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                	   functionKeys.add(arrayFunctionKey[which]);
	                   } else if (functionKeys.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                	   functionKeys.remove(Integer.valueOf(which));
	                   }
	               }
	           }) 
	    // Set the action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //TODO evento con la tecla function escogida
	            	   //se vuelve a mostrar el dialog del comboKeys
	            	   showDialog(4);
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //functionKeys se vacía
	            	   functionKeys.clear();
	               }
	           });
	    

	    return builder.create();	
		
	}
	
	private Dialog openHelpDialog()
	 {
	  return new AlertDialog.Builder(this)
	  .setTitle(R.string.help_title).setMessage(R.string.help_message)
	  .setPositiveButton(R.string.help_ok,
	   new DialogInterface.OnClickListener() {
	    
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	     // Auto-generated method stub
	     
	    }
	   }
	    )
	  .show();
	 }
	
	//publica porque se llama desde el fragment lateral
	public void centerImageCanvas(){
		//TODO Oscar centra imagen
		Log.i("tag", "en image center");
		menu.toggle();
	}
	
	//publica porque se llama desde el fragment lateral
	public void hideMouse(){
		//TODO Oscar ocultar raton
		Log.i("tag", "en mouse hide");
		menu.toggle();
	}
	
		
}
