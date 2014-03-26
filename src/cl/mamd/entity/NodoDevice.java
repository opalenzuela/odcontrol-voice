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
