package org.llod.wals.csvtordf;

public class Language {
	// wals code,name,latitude,longitude,genus,family,subfamily,iso codes

	private String walsCode;
	private String languageName;
	private String latitude;
	private String longitude;
	private String genus;
	private String family;
	private String subFamily;
	private String isoCode;

	public Language() {

	}

	public Language(String walscode, String langname, String latitude,
			String longitude, String genus, String family, String subfamily,
			String isocode) {
		this.setWalsCode(walscode);
		this.setLanguageName(langname);
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setGenus(genus);
		this.setFamily(family);
		this.setSubFamily(subfamily);
		this.setIsoCode(isocode);
	}

	public String getWalsCode() {
		return walsCode;
	}

	public void setWalsCode(String walsCode) {
		this.walsCode = walsCode;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getFamily() {
		return family;
	}

	public Boolean hasFamily() {
		return !family.trim().isEmpty();
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getSubFamily() {
		return subFamily;
	}

	public Boolean hasSubFamily() {
		return !subFamily.trim().isEmpty();
	}

	public void setSubFamily(String subFamily) {
		this.subFamily = subFamily;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

}
