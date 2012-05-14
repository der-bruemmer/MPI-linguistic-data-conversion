/**
 * This class builds a JENA Model from wals csv files. 3 Files are needed to build the model:
 * languages.csv, datapoints.csv and values.csv
 * TODO: An ontology mapping to name the resources and properties should be added to keep it configurable.
 * TODO: Families should be single resources
 * TODO: Family and subfamily resources should be rdfs:type gold:LanguageFamily
 * TODO: stronger use of gold Ontology
 * TODO: find a good mapping for features and values. The current mapping is semantically wrong:
 * Example: Language->hasFeature:id->with Value:id,shortDescription, longDescription
 * 
 * @author Martin Brümmer 04/26/12
 */


package org.llod.wals.csvtordf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LanguageDataToRDF {
	
	private File languageFile;
	private File datapointFile;
	private File valueFile;
	private ArrayList<WalsValue> valueList;
	private ArrayList<Datapoint> datapointList;
	private ArrayList<Language> languageList;
	private VocabularyMapping mapping;
	
	public LanguageDataToRDF(File languagesCsv, File datapointsCsv, File valuesCsv, File mappingFile) throws FileNotFoundException {
		try {
			setLanguageFile(languagesCsv);
		} catch(FileNotFoundException fnf1) {
			System.out.println("File not found: " + languagesCsv.getAbsolutePath());
		}
		try {
			setDatapointFile(datapointsCsv);
		} catch(FileNotFoundException fnf1) {
			System.out.println("File not found: " + datapointsCsv.getAbsolutePath());
		}
		try {
			setValueFile(valuesCsv);
		} catch(FileNotFoundException fnf1) {
			System.out.println("File not found: " + valuesCsv.getAbsolutePath());
		}
		this.mapping=new VocabularyMapping(mappingFile);
	}
	
	public ArrayList<WalsValue> getValueList(){
		HashMap<String, ArrayList<WalsValue>> valueMap = new HashMap<String, ArrayList<WalsValue>>();
		try {
			valueMap = this.readValueCSV();
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<WalsValue> valueList=new ArrayList<WalsValue>();
		Collection<ArrayList<WalsValue>> values = valueMap.values();
		Iterator<ArrayList<WalsValue>> it=values.iterator();
		while(it.hasNext()) {
			valueList.addAll(it.next());
		}
		return valueList;
	}
	
	public ArrayList<Datapoint> getDatapointList(){
		HashMap<String, ArrayList<Datapoint>> dataMap = new HashMap<String, ArrayList<Datapoint>>();
		try {
			dataMap = this.readDatapointsCSV();
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Datapoint> dataList=new ArrayList<Datapoint>();
		Collection<ArrayList<Datapoint>> data = dataMap.values();
		Iterator<ArrayList<Datapoint>> it=data.iterator();
		while(it.hasNext()) {
			dataList.addAll(it.next());
		}
		return dataList;
	}
	
	public ArrayList<Language> getlanguageList(){
		ArrayList<Language> languageList=new ArrayList<Language>();
		try {
			languageList = this.readLanguagesCSV();
		} catch (UnsupportedCharsetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return languageList;
	}

	
	public void setLanguageFile(File languagesCsv) throws FileNotFoundException {
		if( languagesCsv.exists() && !languagesCsv.isDirectory()) {
			this.languageFile=languagesCsv;
		} 
		else {
			throw new FileNotFoundException();
		}
	}
	
	public void setDatapointFile(File datapointsCsv) throws FileNotFoundException {
		if( datapointsCsv.exists() && !datapointsCsv.isDirectory()) {
			this.datapointFile=datapointsCsv;
		} 
		else {
			throw new FileNotFoundException();
		}
	}
	
	public void setValueFile(File valuesCsv) throws FileNotFoundException {
		if( valuesCsv.exists() && !valuesCsv.isDirectory()) {
			this.valueFile=valuesCsv;
		} 
		else {
			throw new FileNotFoundException();
		}
	}
	
	
	/**
	 * Read the vaues.csv, create WalsValue objects for every entry, put them in a HashMap:
	 * HashMap has the featureId as key and a list of walsValues as value. 
	 * WalsValues contain a featureId, a valueId and 2 descriptions of this value.
	 * @return HashMap<String,ArrayList<WalsValue>>
	 * @throws FileNotFoundException values.csv not found, check file path
	 * @throws UnsupportedCharsetException utf-8 not supported by jre
	 * @throws IOException csv reader could not parse csv, check csv file
	 */
	
	private HashMap<String,ArrayList<WalsValue>> readValueCSV() throws FileNotFoundException,UnsupportedCharsetException,IOException{
		
		HashMap<String,ArrayList<WalsValue>> valueMap = new HashMap<String,ArrayList<WalsValue>>();
		
		//get inputfile, set charset and seperator
		InputStream input=new FileInputStream(this.valueFile);
		Charset charset=Charset.forName("UTF-8");
		//read the csv file
		CsvReader reader=new CsvReader(input,",".charAt(0),charset);
		//skip header row
		reader.skipLine();

		ArrayList<WalsValue> valueList=new ArrayList<WalsValue>();
		String lastId="";
		String featureId="";
		while(reader.readRecord()) {
			String[] values=reader.getValues();
			featureId=values[0];
			WalsValue value=new WalsValue();
			//every key in the hashmap gets a list of WalsValues which contain the same featureId
			//create a new WalsValue as long as the feature is the same
			//check for length()==0 for the first iteration
			if(values[0].equals(lastId) || lastId.isEmpty()) {
				value.setFeatureId(values[0]);
				value.setValueId(values[1]);
				value.setShortDescription(values[2]);
				value.setLongDescription(values[3]);
				valueList.add(value);
			} else {
				//put the list with the featureId into the hashmap
				valueMap.put(lastId, valueList);
				//create a new list, start anew
				valueList=new ArrayList<WalsValue>();
				value.setFeatureId(values[0]);
				value.setValueId(values[1]);
				value.setShortDescription(values[2]);
				value.setLongDescription(values[3]);
				valueList.add(value);
			}
			lastId=values[0];
		}
		//put the valueList of the last iteration into the hashmap
		valueMap.put(featureId, valueList);
		return valueMap;
	}
	
	/**
	 * Read the languages.csv, create Language objects for every row, put em in a list
	 * @return ArrayList<Language>
	 * @throws FileNotFoundException values.csv not found, check file path
	 * @throws UnsupportedCharsetException utf-8 not supported by jre
	 * @throws IOException csv reader could not parse csv, check csv file
	 */
	
	private ArrayList<Language> readLanguagesCSV() throws FileNotFoundException,UnsupportedCharsetException,IOException{
		
		ArrayList<Language> languageList = new ArrayList<Language>();
		//get inputfile, set charset and seperator
		InputStream input=new FileInputStream(this.languageFile);
		Charset charset=Charset.forName("UTF-8");
		//read the csv file
		CsvReader reader=new CsvReader(input,",".charAt(0),charset);
		//skip header row
		reader.skipLine();
		
		//for every row, create a new language object and add it to the list
		while(reader.readRecord()) {
			String[] values=reader.getValues();
			Language language=new Language();
			language.setWalsCode(values[0]);
			language.setLanguageName(values[1]);
			language.setLatitude(values[2]);
			language.setLongitude(values[3]);
			language.setGenus(values[4]);
			language.setFamily(values[5]);
			language.setSubFamily(values[6]);
			language.setIsoCode(values[7]);
			languageList.add(language);
		}		
		return languageList;
	}
	
	/**
	 * Read the datapoints.csv, create a datapoint object for every record+feature combination, put em in a HashMap
	 * HashMap has the wals_code as key and a list of datapoints as value. 
	 * Datapoints contain the wals_code of the language, a specific featureId and the valueId
	 * featureId and valueId are used to extract descriptions from the values.csv
	 * @return HashMap<String,ArrayList<Datapoint>>
	 * @throws FileNotFoundException values.csv not found, check file path
	 * @throws UnsupportedCharsetException utf-8 not supported by jre
	 * @throws IOException csv reader could not parse csv, check csv file
	 */
	
	private HashMap<String,ArrayList<Datapoint>> readDatapointsCSV() throws FileNotFoundException,UnsupportedCharsetException,IOException{
		
		HashMap<String,ArrayList<Datapoint>> datapoints = new HashMap<String,ArrayList<Datapoint>>();
		//get inputfile, set charset and seperator
		InputStream input=new FileInputStream(this.datapointFile);
		Charset charset=Charset.forName("UTF-8");
		//read the csv file
		CsvReader reader=new CsvReader(input,",".charAt(0),charset);
		//read headers. we need the headers because they contain the featureId for the values in the rows
		reader.readHeaders();
		//skip header row
		reader.skipLine();
		
		while(reader.readRecord()) {
			String[] values=reader.getValues();
			String walsCode=values[0];
			ArrayList<Datapoint> languageData=new ArrayList<Datapoint>();
			//for every value, which has a correspoding header-entry with its featureId, creata a new Datapoint
			for(int i=1;i<values.length;i++) {
				Datapoint datapoint=new Datapoint();
				datapoint.setWalsCode(values[0]);
				datapoint.setFeatureId(reader.getHeader(i));
				datapoint.setValueId(values[i]);
				languageData.add(datapoint);
			}
			datapoints.put(walsCode, languageData);
		}		
		return datapoints;
	}
	
	/**
	 * WARNING: mapping is not complete for all properties and resources
	 * TODO: fix warning above, add family model to output family uris
	 * TODO: classes must be urlencoded!
	 * @return
	 * @throws UnsupportedCharsetException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public Model getLanguageModel() throws UnsupportedCharsetException, FileNotFoundException, IOException{
		
		Model languageModel=ModelFactory.createDefaultModel();
		Property languageNameProperty=ResourceFactory.createProperty(this.mapping.getLanguageNameProperty());
		Property geoLat=ResourceFactory.createProperty(this.mapping.getGeoLatProperty());
		Property geoLong=ResourceFactory.createProperty(this.mapping.getGeoLongProperty());
		Property genus=ResourceFactory.createProperty(this.mapping.getGenusProperty());
		Property partOf=ResourceFactory.createProperty(this.mapping.getFamilyClassProperty());
		Property iso=ResourceFactory.createProperty(this.mapping.getIsoCodeProperty());
		//TODO: include in mapping
		Property hasFeat=ResourceFactory.createProperty("http://wals.info/vocabulary/hasFeature"); //this should be a http://wals.info/feature
		//short description may be incorporated later
		//Property shortDesc=ResourceFactory.createProperty("http://wals.info/vocabulary/shortDesc"); 
		Property longDesc=ResourceFactory.createProperty(this.mapping.getValueDescriptionsProperty());
		
		//import from the csv documents
		ArrayList<Language> languages=this.readLanguagesCSV();
		HashMap<String,ArrayList<Datapoint>> datapoints=this.readDatapointsCSV();
		HashMap<String,ArrayList<WalsValue>> values=this.readValueCSV();
		
		//for every language in languages.csv
		for(Language language : languages) {
			//assign uris and properties
			String languageResourceUri=this.mapping.getLanguageClassUri()+language.getWalsCode();
			Resource languageResource=languageModel.createResource(languageResourceUri);
			
			//add data from languages.csv
			languageResource.addLiteral(languageNameProperty, language.getLanguageName());
			languageResource.addLiteral(geoLat, language.getLatitude());
			languageResource.addLiteral(geoLong, language.getLongitude());
			languageResource.addLiteral(genus, language.getGenus());
			//family resources are not in output
			//they need their own model!
			Resource familyResource=null;
			if(language.hasFamily()) {
				//urlencode family
				String family=URLEncoder.encode(language.getFamily(), "UTF-8");
				familyResource=languageModel.createResource(this.mapping.getFamilyClassUri()+family); //add rdfs:type http://purl.org/linguistics/gold/LanguageFamily
				familyResource.addProperty(RDF.type,"http://purl.org/linguistics/gold/LanguageFamily");
				languageResource.addProperty(partOf, familyResource); 
			}
			if(language.hasSubFamily()) {
				String subfamily=URLEncoder.encode(language.getSubFamily(), "UTF-8");
				Resource subFamilyResource=languageModel.createResource(this.mapping.getSubFamilyClassUri()+subfamily); //add rdfs:type http://purl.org/linguistics/gold/LanguageFamily
				subFamilyResource.addProperty(RDF.type,"http://purl.org/linguistics/gold/LanguageFamily");
				
				if(familyResource!=null) subFamilyResource.addProperty(partOf, familyResource);
				languageResource.addProperty(partOf, subFamilyResource);
			}
			languageResource.addLiteral(iso, language.getIsoCode());
			
			//now add datapoints and valuedata
			//if datapoints has an entry for the language with wals_code languages.get(i).get(0) (false for fio and jia)
			if(datapoints.containsKey(language.getWalsCode())){
				
				//get the datapoint entry for the language
				ArrayList<Datapoint> languageData=datapoints.get(language.getWalsCode());
				//for every datapoint of this specific language
				for(Datapoint datapoint : languageData) {
					
					//get the values for the specific featureId
					ArrayList<WalsValue> languageDescriptions=values.get(datapoint.getFeatureId());
					for(WalsValue value : languageDescriptions) {
						//for all data contained in values.csv for the valueId from the datapoint
						if(value.getValueId().equals(datapoint.getValueId())) {
							
							Resource featureResource=languageModel.createResource(this.mapping.getFeatureClassUri()+value.getFeatureId()); 
							languageResource.addProperty(hasFeat, featureResource);
							//languageResource.addLiteral(shortDesc, value.getShortDesc());
							languageResource.addLiteral(longDesc, value.getLongDesc());
						}
					}						
				}		
			}
		}
		return languageModel;
	}
	
	/**
	 * Test main
	 * @param args
	 */
	
	public static void main(String[] args){
		try {
			File langfile=new File(args[0]);
			File datafile=new File(args[1]);
			File valfile=new File(args[2]);
			File mappingfile=new File(args[3]);
			LanguageDataToRDF convert=new LanguageDataToRDF(langfile, datafile, valfile, mappingfile);
			Model rdf=convert.getLanguageModel();
			OutputStream out=new FileOutputStream("./langtest.xml");
			rdf.write(out);
		} catch(Exception fnf) {
			fnf.printStackTrace();
		}
	}
	

}
