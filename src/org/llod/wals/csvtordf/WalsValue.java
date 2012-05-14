package org.llod.wals.csvtordf;

public class WalsValue {
	
	private String featureId;
	private String valueId;
	private String shortDescription;
	private String longDescription;
	
	public WalsValue(String feature,String value,String shortDesc,String longDesc){
		this.setFeatureId(feature);
		this.setValueId(value);
		this.setShortDescription(shortDesc);
		this.setLongDescription(longDesc);
	}
	
	public WalsValue(){
		
	}
	
	public void setFeatureId(String featureId){
		this.featureId=featureId;
	}
	
	public void setValueId(String valueId){
		this.valueId=valueId;
	}
	
	public void setShortDescription(String shortdesc){
		this.shortDescription=shortdesc;
	}
	
	public void setLongDescription(String longdesc){
		this.longDescription=longdesc;
	}
	
	public String getFeatureId(){
		return this.featureId;
	}
	
	public String getValueId(){
		return this.valueId;
	}	
	
	public String getShortDesc(){
		return this.shortDescription;
	}	
	
	public String getLongDesc(){
		return this.longDescription;
	}

}
