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
package es.farfuteam.vncplusplus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author roni
 *
 */
public class CanvasView extends  ImageView{
	private int[] data;
	private int height;
	private int width;
	private int stride;
	private int x;
	private int y;
	private int offset;
	
	private Bitmap bitmap;
	private boolean drag = false;
	private boolean update = false;
	
	private Rect bitmapRect;

	
	private float scale = 1;
	private float scaleX = 0;
	private float scaleY = 0;
	
	public CanvasView(Context context,AttributeSet attrs) {
		super(context,attrs);
		data = null;
		

	}

	@Override
	protected void onDraw (Canvas canvas){		
		if(data != null){
			canvas.save();
			canvas.scale(scale,scale,scaleX,scaleY);
			if(!drag){	
				canvas.drawBitmap(data,offset, stride, x,y, width,height, false, null);
			}
			else{
				
				canvas.drawBitmap(bitmap, bitmapRect, bitmapRect,null);
				
			}
		
		}
		
	}
	
	public int getRealHeight() {
		return height;
	}
	public int getRealWidth() {
		return width;
	}
	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale,float scaleX,float scaleY) {
		this.scale = scale;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	public void startDrag() {
		if(bitmap ==null){
			bitmap = Bitmap.createBitmap(data, offset, stride, width, height,Bitmap.Config.RGB_565);
		}
		update = false;
		drag = true;	
	}
	public void endDrag(){
		drag = false;
		if(update){
			bitmap = null;
		}
		postInvalidate();
	}
	public void reDraw(){
		if(!drag){
			bitmap = null;
			
		}
		else{
			update = true;
		}

	}
	public void initCanvas(int[] data,int offset,int stride,int x,int y,int width,int height) {
		this.x = x;
		this.y = y;
		this.data = data;
		this.width = width;
		this.height = height;
		this.stride = stride;
		this.offset = offset;
		
		bitmapRect = new Rect(0,0, width,height);


	}
}
