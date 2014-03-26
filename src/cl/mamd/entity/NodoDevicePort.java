package cl.mamd.entity;

/**
 * 
 * @author mmoscoso
 * @version 0.1
 * @comment Class for store data of device's port
 */
public class NodoDevicePort {
	private Integer id;
	private Integer device;
	private String port;
	private String tag;
	private String action;
	
	
	public NodoDevicePort() {
		super();
	}
	/**
	 * 
	 * @param device
	 * @param port
	 * @param tag
	 * @param action
	 */
	public NodoDevicePort(Integer device, String port, String tag, String action) {
		super();
		this.device = device;
		this.port = port;
		this.tag = tag;
		this.action = action;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDevice() {
		return device;
	}
	public void setDevice(Integer device) {
		this.device = device;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
}
