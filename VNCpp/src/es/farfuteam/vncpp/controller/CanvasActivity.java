/*
 	Copyright 2013 Oscar Crespo Salazar
 	Copyright 2013 Gorka Jimeno Garrachon
 	Copyright 2013 Luis Valero Martin
  
	This file is part of VNCpp.

	VNCpp is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	any later version.
	
	VNCpp is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with VNCpp.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.farfuteam.vncpp.controller;

import java.util.Collections;
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
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import es.farfuteam.vncpp.controller.NewConnectionActivity.QualityArray;
import es.farfuteam.vncpp.model.ObserverCanvas;
import es.farfuteam.vncpp.model.VncBridgeJNI;
import es.farfuteam.vncpp.model.VncBridgeJNI.ConnectionError;
import es.farfuteam.vncpp.model.VncBridgeJNI.MouseEvent;
import es.farfuteam.vncpp.view.CanvasView;
import es.farfuteam.vncpp.view.SlideListFragment;


/**
 * @class CanvasActivity
 * @brief This is the activity which controls the Canvas.
 * 
 * This is the detailed description
 *
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @extends FragmentActivity
 * @implements ObserverCanvas
 */

public class CanvasActivity extends FragmentActivity implements ObserverCanvas{

	public enum EnumDialogs{createServerNotFoundDialog,exitDialog ,serverInterruptConexionDialog,
							comboEventsDialog,functionKeysDialog,openHelpDialog,timeExceededDialog,passwordNeededDialog,
							outOfMemoryDialog};

	public enum EnumSpecialKey{ctrl,alt,supr,shift,meta};
	//etiqueta debug
	private static final String DEBUG_TAG = "CanvasActivity";
	
	private static final int VELOCITY_MOD = 30;
	
	private static final int timerConnection = 30000;
	
	private static final int timerScroll = 500;
	
	private static final int adjustKeys = 17;

	private VncBridgeJNI vnc;
	private CanvasView canvas;

	private GestureDetector gesture;
	private ScaleGestureDetector scaleGesture;
	
	private int realX = 0;
	private int realY = 0;
	private float scaleFactor = 1;
	
	private Thread endScrollThread;
	
	private Runnable endScrollRun;
	
	private boolean drag = false;
	private boolean zoom = false;
	private boolean oneLoopMore = false;
	
	private Vector<EnumSpecialKey> specialKeys;
	private Vector<Integer> keys;
	
	
	private SlidingMenu menu;
	
	private boolean waitDialog = false;
	
	private ProgressDialog progressDialog;
	
	private int modKeyCount = 0;
	
	private InputMethodManager inputMgr ;
	
	private String pass;
	
	private boolean connect = false;
	
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
				
		Bundle info = getIntent().getExtras();		
		
		progressDialog = new ProgressDialog(this);
		
		final String load = getString(R.string.loadingtext);
		final String loadImage = getString(R.string.loadingbodytext);

        progressDialog.setTitle(load);

        progressDialog.setMessage(loadImage);
        final String cancel = getString(R.string.cancel);
        
        progressDialog.setButton(cancel, new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
            	vnc.finishVnc();
				finishConnection();                
            }
           });

        
        progressDialog.show();
        progressDialog.setCancelable(false);
        
        menu = new SlidingMenu(this);
		startSlideMenu();
        
        runTimerConnection();
        
        String aux_quality = info.getString("color");
        QualityArray quality = QualityArray.valueOf(aux_quality);
        
        iniConnection(info.getString("ip"), info.getString("port"),info.getString("psw"),quality,info.getBoolean("wifi"));
        
        inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
     
		
		GestureListener gestureListener =  new GestureListener();
		gesture = new GestureDetector(gestureListener);
		
		scaleGesture = new ScaleGestureDetector(this,gestureListener);
		
		endScrollRun = new Runnable() {
			
			@Override
			public void run() {
				while(drag || oneLoopMore){
					oneLoopMore = false;
					try {
						Thread.sleep(timerScroll);
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
				if(!connect && progressDialog.isShowing()){
					waitDialog = true;
					
					activityThis.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//Dialogo "Se ha excedido el tiempo de conexion"
							progressDialog.dismiss();
							showDialog(EnumDialogs.timeExceededDialog.ordinal());
							
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
	private void iniConnection(final String host,final String port,final String pass, final QualityArray quality,final Boolean compress){
		vnc = new VncBridgeJNI();
		vnc.addObserver(this);

		final Activity activityThis = this;
		new Thread( new Runnable() {
				
			@Override
			public void run() {
				ConnectionError error = vnc.startConnect(host,Integer.parseInt(port),pass,quality,compress,(es.farfuteam.vncpp.controller.Configuration.getInstance()).isHideMouse());
					
				if(error != ConnectionError.ALLOK){
					waitDialog = true;
						
					activityThis.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
						
							showDialog(EnumDialogs.createServerNotFoundDialog.ordinal());
							
						}
					});

					new Thread(new Runnable() {
							
						@Override
						public void run() {
							while(waitDialog);
							finishConnection();
								
						}
					}).start();

					return;
				}
				else{
					connect = true;
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
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent evt){
		//super.onKeyDown(keyCode, evt);
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(vnc != null){
				//Dialog pregunta salir
				showDialog(EnumDialogs.exitDialog.ordinal());
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU){

			//Log.i("tag","en menu del canvas");
			menu.toggle();

			
		}
		return true;
		
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.ACTION_UP|| event.getAction() == KeyEvent.ACTION_MULTIPLE){
			int keyunicode = event.getUnicodeChar(event.getMetaState() );
		    char character = (char) keyunicode;
		    int key = event.getKeyCode();
		    if(key == 59){
		    	if(modKeyCount == 0){
		    		modKeyCount = 100;
		    	}
		    	else{
		    		modKeyCount = 0;
		    	}
		    }
		    else if(key >= 7){
		    	boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
		    	vnc.sendKey(modKeyCount+key,down);
		    	
		    }
		   
		    
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
	public void updateRedraw() {
		canvas.reDraw();
		if(progressDialog.isShowing())
			progressDialog.dismiss();
			
		canvas.postInvalidate();
		/*if(progressDialog.isShowing() && canvas.getRealWidth() == x+width && canvas.getRealHeight() == y+height){
			progressDialog.dismiss(); 
			canvas.postInvalidate();
		}*/
	}
	
	@Override
	public void updateIniFrame(int[] data,int offset,int x,int y,int width,int height) {
		canvas.initCanvas(data, offset, width, x,y,  width, height);		
	}

	@Override
	public void updateFinish() {
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		showDialogWait(EnumDialogs.serverInterruptConexionDialog);
		finishConnection();
	}
	@Override
	public String updatePass() {
		showDialogWait(EnumDialogs.passwordNeededDialog);
		String aux_pass = pass;
		pass = null;
		return aux_pass;
	}
	@Override
	public void updateOutOfMemory() {
		progressDialog.dismiss();
		showDialogWait(EnumDialogs.outOfMemoryDialog);
		if(vnc != null)
			vnc.finishVnc();
		finishConnection();
		
	}
	public void showDialogWait(final EnumDialogs dialog){
		waitDialog = true;
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			
				showDialog(dialog.ordinal());
			}
		});
		while(waitDialog);
		
	}

	//publica porque necesito llamarla desde el fragment slide lateral
	public void showKeyboard(){
		//showKeyboard = !showKeyboard;
		
        //inputMgr.toggleSoftInputFromWindow(canvas.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
		//IMMResult result = new IMMResult();
		//inputMgr.showSoftInput(canvas, InputMethodManager.SHOW_FORCED, result);
		//inputMgr.showSoftInput(canvas, InputMethodManager.SHOW_IMPLICIT, result);
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
		int offSetKeyboard =0;
		
		//if(showKeyboard)
			offSetKeyboard = canvas.getHeight()/2;
		
		float width = canvas.getWidth() / scaleFactor ;
		float height = canvas.getHeight() / scaleFactor ;

		if((realX+width >= canvas.getRealWidth() && distanceX > 0)|| (realX == 0 && distanceX < 0) || (canvas.getWidth()/scaleFactor > canvas.getRealWidth() )){
			moveX = 0;
			allOkX = false;
		}
		if( (realY+height >= canvas.getRealHeight()+offSetKeyboard && distanceY>0) || (realY == 0 && distanceY < 0)|| (canvas.getHeight()/scaleFactor > canvas.getRealHeight()+offSetKeyboard ) ){
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
			
			if(inputMgr.isAcceptingText()){
				//offSetKeyboard = canvas.getRealHeight()/2;
			}
			if(realY+height+distanceY > canvas.getRealHeight()+offSetKeyboard || realY+distanceY < 0){
				if(distanceY < 0){
					moveY = -realY ;
				}
				else{
					moveY =( canvas.getRealHeight()+offSetKeyboard) - (realY + height);
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
			
		
			canvas.scrollTo((int)(realX*scaleFactor),(int)(realY*scaleFactor));
		}
		
		return true;
	}
	
	private void centerImage(){
		realX = 0;
		realY = 0;
		canvas.scrollTo(0,0);
		scaleFactor = 1;
		canvas.setScale(scaleFactor,0,0);
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
	        case 0:
	            dialog = createServerNotFoundDialog();
	            
	            break;
	        case 1:
	            dialog = exitDialog();
	            break;
	            
	        case 2:
	        	dialog = serverInterruptConexionDialog();
	        	break;
	        	
	        case 3:
	        	menu.toggle();
	        	dialog = comboEventsDialog();
	        	
	        	break;
	        	
	        case 4:
	        	dialog = functionKeysDialog();
	        	
	        	break;
	        	
	        case 5:	
	        	dialog = openHelpDialog();
	        	menu.toggle();
	        	break;

	        	
	        case 6:
	        	dialog = timeExceededDialog();
	        	break;
	        	
	        case 7:
	        	dialog = passwordNeededDialog();
	        	break;
	        case 8:
	        	dialog = outOfMemoryDialog();
	        	break;
	    }
	 
	    return dialog;
	}
	
	
	
	
	private Dialog passwordNeededDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.password_title);
		String body = getString(R.string.password_dialog);
		 
	    builder.setTitle(info);
	    builder.setMessage(body);
        // Set an EditText view to get user password 
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        
	    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
      	  public void onClick(DialogInterface dialog, int whichButton) {
      		  dialog.cancel();
      		  pass = "";
      		  waitDialog = false;
      	  }
	    });
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	        	
	        	String str = input.getEditableText().toString();
	        	pass = str;
	        	waitDialog = false;
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
	            dialog.dismiss();
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
	            dialog.dismiss();
	            waitDialog = false;
	        }

	    });
	    
	    return builder.create();
	}
	private Dialog outOfMemoryDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    String info = getString(R.string.memory_info);
		String body = getString(R.string.memory_exceeded);
		 
	    builder.setTitle(info);
	    builder.setMessage(body);
	    builder.setCancelable(false);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	            waitDialog = false;
	        }

	    });
	    
	    return builder.create();
	}
	private void sendSpecialKeys(boolean down){
		for(EnumSpecialKey s : specialKeys){
 		   switch (s) {
				case ctrl:
					vnc.sendKey(1, down);
					break;
				case alt:
					vnc.sendKey(3, down);
					break;
				case supr:
					vnc.sendKey(4, down);
					break;
				case shift:
					vnc.sendKey(2, down);
					break;
				case meta:
					vnc.sendKey(6, down);
					break;
				
				}
 	   }
	}
	private void sendKeys(boolean down){
		if(keys != null){
			for(int s : keys){
				vnc.sendKey(s+adjustKeys,down);
	 	   }
		}
	}
	private Dialog comboEventsDialog() {
	   
		specialKeys = new Vector<EnumSpecialKey>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	
	    // Set the dialog title
	    final Activity actThis = this;
	    builder.setTitle(R.string.combo_keys_title)
	    // Specify the list array, the items to be selected by default (null for none),
	    // and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(R.array.keys_array, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	            	   EnumSpecialKey aux = EnumSpecialKey.values()[which];;
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                	   if(aux == EnumSpecialKey.ctrl || aux == EnumSpecialKey.alt){
	                		   specialKeys.add(0, aux);
	                	   }
	                	   else{
	                		   specialKeys.add(aux);
	                	   }
	                   } else if (specialKeys.contains(which)) {
	                       // Else, if the item is already in the array, remove it
	                	   specialKeys.remove(aux);
	                   }
	               }
	           })
	           .setNeutralButton(R.string.function_keys_title, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//muestra function keys
					showDialog(EnumDialogs.functionKeysDialog.ordinal());
				}
			})
	    // Set the action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   sendSpecialKeys(true);
	            	   sendKeys(true);
	            	   Collections.reverse(specialKeys);
	            	   if(keys != null)
	            		   Collections.reverse(keys);
	            	   sendKeys(false);
	            	   sendSpecialKeys(false); 
	            	   
	            	   actThis.removeDialog(EnumDialogs.comboEventsDialog.ordinal());
	            	   actThis.removeDialog(EnumDialogs.functionKeysDialog.ordinal());
	            	   specialKeys = null;
	            	   keys = null;
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //se vacia el vector
	            	   actThis.removeDialog(EnumDialogs.comboEventsDialog.ordinal());
	            	   actThis.removeDialog(EnumDialogs.functionKeysDialog.ordinal());
	            	   specialKeys = null;
	            	   keys = null;
	               }
	           });
	    

	    return builder.create();	
		
	}
	
	private Dialog functionKeysDialog() {		   

	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   if(keys == null)
		   keys = new Vector<Integer>();  // Where we track the selected items
	    
	   final Activity actThis = this;
	    // Set the dialog title
	    builder.setTitle(R.string.function_keys_title)
	           .setMultiChoiceItems(R.array.function_keys_array, null,
	        		   new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                	   keys.add(which);
	                   } else if (keys.contains(Integer.valueOf(which))) {
	                       // Else, if the item is already in the array, remove it 
	                	   keys.remove(Integer.valueOf(which));
	                   }
	               }
	           }) 
	    // Set the action buttons
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   showDialog(EnumDialogs.comboEventsDialog.ordinal());
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   showDialog(EnumDialogs.comboEventsDialog.ordinal());
	            	   actThis.removeDialog(EnumDialogs.functionKeysDialog.ordinal());
	            	  
	            	   
	               }
	           });
	    

	    return builder.create();	
		
	}
	
	private Dialog openHelpDialog()
	 {
		
               
        final TextView text = new TextView(this);
        text.setText(R.string.help_message); 

	  return new AlertDialog.Builder(this)
	  .setTitle(R.string.help_title)
	  .setView(text)
	  .setPositiveButton(R.string.help_ok,
	   new DialogInterface.OnClickListener() {
	    
	    @Override
	    public void onClick(DialogInterface dialog, int which) {	     
	    }
	   }
	    )
	  .show();
	 }
	
	//publica porque se llama desde el fragment lateral
	public void centerImageCanvas(){
		centerImage();
		menu.toggle();
	}
	
	
		
}
