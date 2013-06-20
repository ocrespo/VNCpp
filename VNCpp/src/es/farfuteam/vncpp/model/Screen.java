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
package es.farfuteam.vncpp.model;

/**
 * @class Screen
 * @brief Stores the bitmap data
 * @authors Oscar Crespo, Gorka Jimeno, Luis Valero
 * @details This Class stores the bitmap data
 */
public class Screen {
	/**
	 * The bitmap data
	 */
	private int[] data;
	
	/**
	 * The image width
	 */
	private int width;
	
	/**
	 * The image height
	 */
	private int height;
	
	/**
	 * Bytes per pixel
	 */
	private int bpp;
	
	/**
	 * @brief The default constructor
	 * @param width The image width
	 * @param height The image height
	 * @param bpp The bytes per pixel
	 * @details The default constructor. Sets the width, height and bytes per pixel attributes
	 */
	public Screen(int width, int height,int bpp){
		this.setWidth(width);
		this.setHeight(height);
		this.setBpp(bpp);
		
	}
	
	/**
	 * @brief Creates the bitmap data
	 * @details Creates the bitmap data with the width and the height attributes
	 */
	public void createData(){
		data = new int[height*width];
	}

	/**
	 * @brief Returns the width
	 * @return The width
	 * @details Returns the image width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @brief Sets the width
	 * @param width the width to set
	 * @details Sets the image width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @brief Return the height
	 * @return the height
	 * @details Returns the image height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @brief Sets the height
	 * @param height the height to set
	 * @details Sets the image height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @brief Returns the bytes per pixel
	 * @return the bpp
	 * @details Returns the bytes per pixel
	 */
	public int getBpp() {
		return bpp;
	}

	/**
	 * @brief Sets the bytes per pixel
	 * @param bpp the bpp to set
	 * @details Sets the bytes per pixel
	 */
	public void setBpp(int bpp) {
		this.bpp = bpp;
	}
	
	/**
	 * @brief Returns the bitmap
	 * @return The bitmap data
	 * @detaisl Returns the bitmap data
	 */
	public int[] getData() {
		return data;
	}
	
}
