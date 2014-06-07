/**
Copyright 2014 Manuel Moscoso Dominguez
This file is part of ODControl-Voice.

ODCOntrol-Voice is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation, either version 3 of the License, or 
(at your option) any later version.

ODControl-Voice is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License for more details.

You should have received a copy of the GNU General Public License 
along with ODControl-Voice.  If not, see <http://www.gnu.org/licenses/>.

**/
package cl.mamd.entity;

import java.util.List;



/**
 * 
 * @author mmoscoso	
 * @version 0.1
 * @comment Class for store data of device
 */
public class NodoDevice {
	private Integer id;
	private String name;
	private String location;
	private String ipaddress;
	private String username;
	private String passwd;
	private List<NodoDevicePort> ports;
	
	
	public List<NodoDevicePort> getPorts() {
		return ports;
	}
	public void setPorts(List<NodoDevicePort> ports) {
		this.ports = ports;
	}
	public NodoDevice() {
		super();
		// TODO Auto-generated constructor stub
	}
	public NodoDevice(String name, String location, String ipaddress) {
		super();
		this.name = name;
		this.location = location;
		this.ipaddress = ipaddress;
	}
	public NodoDevice(String name, String location, String ipaddress,
			String username, String passwd) {
		super();
		this.name = name;
		this.location = location;
		this.ipaddress = ipaddress;
		this.username = username;
		this.passwd = passwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		return "Device [name=" + name + "]\n [ipaddress=" + ipaddress + "]";
	}
	public String toFullString(){
		
		return "NodoDevice [id=" + id + ", name=" + name + ", location="
		+ location + ", ipaddress=" + ipaddress + ", username="
		+ username + ", passwd=" + passwd + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	

}
