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
package es.farfuteam.vncpp.model.sql;

public class Connection {
	
	private String name;
	private String IP;
	private String PORT;
	private String UserAuth;
	private String psw;
	private String fav;
	private String ColorFormat;
	
	
	public Connection(){
		name="default";
		IP="192.168.1.1";
		PORT="5900";
		UserAuth="";
		psw="";
		fav="false";
		ColorFormat="24-bit color (4 bpp)";
	}
	
	
	public Connection(String name, String IP, String PORT, String UserAuth,String psw, String fav, String ColorFormat){
		this.setName(name);
		this.setIP(IP);
		this.setPORT(PORT);
		this.setUserAuth(UserAuth);
		this.setPsw(psw);
		this.setFav(fav);
		this.setColorFormat(ColorFormat);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getIP() {
		return IP;
	}


	public void setIP(String iP) {
		IP = iP;
	}


	public String getPORT() {
		return PORT;
	}


	public void setPORT(String pORT) {
		PORT = pORT;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}

	public String getFav() {
		return fav;
	}

	public void setFav(String fav) {
		this.fav = fav;
	}


	public String getUserAuth() {
		return UserAuth;
	}


	public void setUserAuth(String userAuth) {
		UserAuth = userAuth;
	}


	public String getColorFormat() {
		return ColorFormat;
	}


	public void setColorFormat(String colorFormat) {
		ColorFormat = colorFormat;
	}
	

}
