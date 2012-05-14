package org.llod.wals.csvtordf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class VocabularyMapping {
	
	private File mappingFile;
	private String familyClassUri;
	private String familyClassProperty;
	private String subFamilyClassUri;
	private String featureClassUri;
	private String featureNameProperty;
	private String languageClassUri;
	private String languageNameProperty;
	private String geoLatProperty;
	private String geoLongProperty;
	private String genusProperty;
	private String isoCodeProperty;
	private String valueDescriptionsProperty;
	
	public VocabularyMapping(File mapping){
		try {
			this.setMappingFile(mapping);
		} catch(FileNotFoundException fnf) {
			System.out.println("File not found: " + mapping.getAbsolutePath());
			return;
		}
		try {
			this.initializeMapping();
		} catch (Exception e) {
			System.out.println("Could not read csv file:" + mapping.getAbsolutePath() + "please check file syntax.");
		}
	}
	
	public void setMappingFile(File mappingFile) throws FileNotFoundException {
		if( mappingFile.exists() && !mappingFile.isDirectory()) {
			this.mappingFile=mappingFile;
		} 
		else {
			throw new FileNotFoundException();
		}
	}
	
	private void initializeMapping() throws FileNotFoundException, IOException {
		
		InputStream input=new FileInputStream(this.mappingFile);
		Charset charset=Charset.forName("UTF-8");
		CsvReader reader=new CsvReader(input,",".charAt(0),charset);
		reader.skipLine();
		while(reader.readRecord()) {
			String[] values=reader.getValues();
			//this is fugly
			if(values[0].equals("familyClassUri")){
				this.familyClassUri=values[1];
			} 
			else if(values[0].equals("familyClassProperty")) {
				this.familyClassProperty=values[1];
			}
			else if(values[0].equals("subFamilyClassUri")) {
				this.subFamilyClassUri=values[1];
			}
			else if(values[0].equals("featureClassUri")) {
				this.featureClassUri=values[1];
			}
			else if(values[0].equals("featureNameProperty")) {
				this.featureNameProperty=values[1];
			}
			else if(values[0].equals("languageClassUri")) {
				this.languageClassUri=values[1];
			}
			else if(values[0].equals("languageNameProperty")) {
				this.languageNameProperty=values[1];
			}
			else if(values[0].equals("geoLatProperty")) {
				this.geoLatProperty=values[1];
			}
			else if(values[0].equals("geoLongProperty")) {
				this.geoLongProperty=values[1];
			}
			else if(values[0].equals("genusProperty")) {
				this.genusProperty=values[1];
			}
			else if(values[0].equals("isoCodeProperty")) {
				this.isoCodeProperty=values[1];
			}
			else if(values[0].equals("valueDescriptionsProperty")) {
				this.valueDescriptionsProperty=values[1];
			}
		}
	}

	public String getFamilyClassUri() {
		return familyClassUri;
	}

	public String getFamilyClassProperty() {
		return familyClassProperty;
	}

	public String getSubFamilyClassUri() {
		return subFamilyClassUri;
	}

	public String getFeatureClassUri() {
		return featureClassUri;
	}

	public String getFeatureNameProperty() {
		return featureNameProperty;
	}

	public String getLanguageClassUri() {
		return languageClassUri;
	}

	public String getLanguageNameProperty() {
		return languageNameProperty;
	}

	public String getGeoLatProperty() {
		return geoLatProperty;
	}

	public String getGeoLongProperty() {
		return geoLongProperty;
	}

	public String getGenusProperty() {
		return genusProperty;
	}

	public String getIsoCodeProperty() {
		return isoCodeProperty;
	}

	public String getValueDescriptionsProperty() {
		return valueDescriptionsProperty;
	}
	
	public static void main(String[] args) {
		File mappingFile=new File("/home/martin/projects/llod-cloud/wals/vocabMapping.csv");
		VocabularyMapping mapping = new VocabularyMapping(mappingFile);
		System.out.println(mapping.getFamilyClassProperty());
		System.out.println(mapping.getGenusProperty());
	}
	

}
