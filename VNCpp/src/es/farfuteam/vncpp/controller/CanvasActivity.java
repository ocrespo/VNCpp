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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import es.farfuteam.vncpp.controller.NewConnectionActivity.QualityArray;
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

	public enum EnumDialogs{createServerNotFoundDialog,exitDialog ,serverInterruptConexionDialog,
							comboEventsDialog,functionKeysDialog,openHelpDialog,timeExceededDialog,passwordNeededDialog};

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
	
	private Thread scrollThread;
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
	
	//private boolean showKeyboard = false;
	
	private String pass;
	
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
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		
		
		canvas.measure(MeasureSpec.makeMeasureSpec((displayMetrics.widthPixels), MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, MeasureSpec.AT_MOST));
		
		
		//setBehindContentView(R.layout.activity_main);
		menu = new SlidingMenu(this);
		startSlideMenu();
				
		Bundle info = getIntent().getExtras();		
		
		progressDialog = new ProgressDialog(this);
		
		final String load = getString(R.string.loadingtext);
		final String loadImage = getString(R.string.loadingbodytext);

        progressDialog.setTitle(load);

        progressDialog.setMessage(loadImage);
        
        
        progressDialog.show();
        progressDialog.setCancelable(false);
        
        
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
		/*final View activityRootView = findViewById(R.id.aa);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				int a = activityRootView.getRootView().getWidth();
				int b = activityRootView.getWidth();
				int c =0; t
				c++;			
			}
		} );*/

		
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
		//ConnectionError error = vnc.startConnect(info.getString("ip"),Integer.parseInt(info.getString("port")));
		//new Thread(vnc).start();
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
	public boolean onKeyDown(int keyCode, KeyEvent evt){
		super.onKeyDown(keyCode, evt);
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(vnc != null){
				//Dialog pregunta salir
				showDialog(EnumDialogs.exitDialog.ordinal());
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
		    else{
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
			
				showDialog(EnumDialogs.serverInterruptConexionDialog.ordinal());
				
			}
		});
		while(waitDialog);
		finishConnection();
	}
	@Override
	public String updatePass() {
		waitDialog = true;
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				showDialog(EnumDialogs.passwordNeededDialog.ordinal());
				
			}
		});
			
		while(waitDialog);
		String aux_pass = pass;
		pass = null;
		return aux_pass;
		//TODO
	}
	//publica porque necesito llamarla desde el fragment slide lateral
	public void showKeyboard(){
		//showKeyboard = !showKeyboard;
        //inputMgr.toggleSoftInput(0, 0);
        inputMgr.toggleSoftInputFromWindow(canvas.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        
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
		
		/*if(showKeyboard)
			offSetKeyboard = canvas.getHeight()/2;*/
		
		float width = canvas.getWidth() / scaleFactor ;
		float height = canvas.getHeight() / scaleFactor ;

		if((realX+width >= canvas.getRealWidth() && distanceX > 0)|| (realX == 0 && distanceX < 0) || (canvas.getWidth()/scaleFactor > canvas.getRealWidth() )){
			moveX = 0;
			allOkX = false;
		}
		if( (realY+height >= canvas.getRealHeight()+offSetKeyboard && distanceY>0) || (realY == 0 && distanceY < 0)|| (canvas.getHeight()/scaleFactor > canvas.getRealHeight() ) ){
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
	        	menu.toggle();
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
		for(int s : keys){
			vnc.sendKey(s+adjustKeys,down);
 	   }
	}
	private Dialog comboEventsDialog() {
	   
		specialKeys = new Vector<EnumSpecialKey>();  // Where we track the selected items
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    //final String[] arraySpecialsKeys = getResources().getStringArray(R.array.keys_array);
	
	    // Set the dialog title
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
	            	   Collections.reverse(keys);
	            	   sendKeys(false);
	            	   sendSpecialKeys(false); 
	            	   
	            	   specialKeys = null;
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //se vacia el vector
	            	   specialKeys = null;
	               }
	           });
	    

	    return builder.create();	
		
	}
	
	private Dialog functionKeysDialog() {		   

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   keys = new Vector<Integer>();  // Where we track the selected items
	    
	   // final String[] arrayFunctionKey = getResources().getStringArray(R.array.function_keys_array);
	     
	    	    
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
	                   //TODO evento con la tecla function escogida
	            	   //se vuelve a mostrar el dialog del comboKeys
	            	   showDialog(EnumDialogs.comboEventsDialog.ordinal());
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   //functionKeys se vacía
	            	   keys.clear();
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
		centerImage();
		menu.toggle();
	}

	
		
}
