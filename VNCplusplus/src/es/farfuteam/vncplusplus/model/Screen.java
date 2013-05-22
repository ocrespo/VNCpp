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
package es.farfuteam.vncplusplus.model;

/**
 * @author roni
 *
 */
public class Screen {
	private int[] data;
	private int width;
	private int height;
	private int bpp;
	
	public Screen(int width, int height,int bpp){
		this.setWidth(width);
		this.setHeight(height);
		this.setBpp(bpp);
		
	}
	public void createData(){
		data = new int[height*width];
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the bpp
	 */
	public int getBpp() {
		return bpp;
	}

	/**
	 * @param bpp the bpp to set
	 */
	public void setBpp(int bpp) {
		this.bpp = bpp;
	}

	public int[] getData() {
		return data;
	}
	public void add(int pos, int value){
		data[pos] = value;
	}
}
