/**
 * This class builds a JENA Model from wals features.csv file.
 * TODO: An ontology mapping to name the resources and properties should be added to keep it configurable.
 * TODO: use of gold Ontology and/or Olia
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
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import org.llod.wals.csvtordf.CsvReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class FeaturesToRDF {
	
	/**"asv toolbox"
	 * The features.csv file
	 */
	private File featurefile;
	/**
	 * The URI of the feature class i.e. http://wals.info/feature
	 */
	private String featureClassUri;
	/**
	 * The property URI of the feature name i.e http://wals.info/vocabulary/featureName
	 */
	private String featureNameProperty;
	
	public FeaturesToRDF(File featurecsv,String URIprefix,String featureNameProperty) throws FileNotFoundException {
		if( featurecsv.exists() && !featurecsv.isDirectory()) {
			this.featurefile=featurecsv;
		} 
		else {
			throw new FileNotFoundException();
		}
		setFeatureClassUri(URIprefix);
		setFeatureNameProperty(featureNameProperty);
	}
	
	public void setFeatureClassUri(String uri) {
		//no validating of URIs atm, just checking if it ends with "/"
		if(uri.endsWith("/")){
			this.featureClassUri=uri;
		} 
		else {
			this.featureClassUri=uri+"/";
		}
	}
	
	public void setFeatureNameProperty(String uri) {
		//no validating of URIs atm
		this.featureNameProperty=uri;
	}
	
	/**
	 * Read the features.csv file and put the values into an ArrayList for further processing
	 * @return ArrayList<String[]> String[0] is the featureID, String[1] the name of the feature 
	 * @throws FileNotFoundException
	 * @throws UnsupportedCharsetException
	 * @throws IOException
	 */
	
	private ArrayList<String[]> readCSV() throws FileNotFoundException,UnsupportedCharsetException,IOException{
		
		ArrayList<String[]> csvmap=new ArrayList<String[]>();
		InputStream input=new FileInputStream(this.featurefile);
		Charset charset=Charset.forName("UTF-8");
		CsvReader reader=new CsvReader(input,",".charAt(0),charset);
		reader.skipLine();
		while(reader.readRecord()) {
			String featureId=reader.getValues()[0];
			String featureName=reader.getValues()[1];
			String[] value=new String[2];
			value[0]=featureId;
			value[1]=featureName;
			csvmap.add(value);
		}
		return csvmap;
	}
	
	/**
	 * get a JENA Model containing featureId resources with featureName properties. Values of the properties are the featureName literals.
	 * TODO: typed literal
	 * @return com.hp.hpl.jena.rdf.model.Model
	 * @throws UnsupportedCharsetException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public Model getFeatureModel() throws UnsupportedCharsetException, FileNotFoundException, IOException{
		Model featureModel=ModelFactory.createDefaultModel();
		Property featureNameProperty=ResourceFactory.createProperty(this.featureNameProperty);
		ArrayList<String[]> featureValues=this.readCSV();
		for(int i=0;i<featureValues.size();i++) {
			String featureResourceUri=this.featureClassUri+featureValues.get(i)[0];
			Resource featureResource=featureModel.createResource(featureResourceUri);
			featureResource.addLiteral(featureNameProperty, featureValues.get(i)[1]);
		}
		return featureModel;
	}
	
	/**
	 * Test main
	 * @param args
	 */
	
	public static void main(String[] args){
		String filename="./features.csv";
		try {
			File featurefile=new File(filename);
			FeaturesToRDF convert=new FeaturesToRDF(featurefile,"http://wals.info/feature/","http://wals.info/vocabulary/featureName");
			Model rdf=convert.getFeatureModel();
			OutputStream out=new FileOutputStream("./features.xml");
			rdf.write(out);
		} catch(Exception fnf) {
			fnf.printStackTrace();
		}
	}
	
}
