package org.llod.wals.csvtordf;

public class Datapoint {
	
	private String featureId;
	private String valueId;
	private String walsCode;
	
	public Datapoint() {
		
	}
	
	public Datapoint(String walscode,String feature,String value){
		this.setWalsCode(walscode);
		this.setFeatureId(feature);
		this.setValueId(value);
	}
	
	public void setWalsCode(String walscode){
		this.walsCode=walscode;
	}
	
	public void setFeatureId(String feature){
		this.featureId=feature;
	}
	
	public void setValueId(String value){
		this.valueId=value;
	}
	
	public String getWalsCode(){
		return this.walsCode;
	}
	
	public String getFeatureId(){
		return this.featureId;
	}
	
	public String getValueId(){
		return this.valueId;
	}

}
