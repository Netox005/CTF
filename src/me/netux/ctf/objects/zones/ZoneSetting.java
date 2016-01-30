package me.netux.ctf.objects.zones;

public class ZoneSetting {

	public enum EnumZoneSetting { FALLDAMAGE, ENTER, BUILD, SHOTING }
	private EnumZoneSetting type;
	private String[] whom;
	private boolean allowed;
	
	public ZoneSetting(EnumZoneSetting type, boolean allowed, String[] whom) {
		this.type = type;
		this.allowed = allowed;
		this.whom = whom;
	}
	
	public EnumZoneSetting getType() { return type; }
	public boolean isAllowed() { return allowed; }
	public String[] getWhomList() { return whom; }
	
}
