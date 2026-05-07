import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;

import java.time.LocalDateTime;

// document builder to parse xml
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// document object model to help with turning xml into table
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// for displaying the time above the table
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class realTime
{
	public static void main(String args[]) throws Exception
	{
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet("https://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML_WithNumMins?StationCode=DCDRA&NumMins=90&format=xml");
		CloseableHttpResponse response = client.execute(request);
		
		// if http request successful
		if(response.getStatusLine().getStatusCode() == 200)
		{
			// initialise java document builders
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(response.getEntity().getContent());
			createTable(doc); // call function that creates and prints the html
		}
		
		response.close();
		client.close();
	}
	
	// function to create and print html
	public static void createTable(Document doc)
	{
		// get the the time of the http request
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formated = DateTimeFormatter.ofPattern("HH:mm:ss");
		String currentTime = now.format(formated);
		
		// print html 
		System.out.println("<!DOCTYPE html>");
		System.out.println("<html>");
		System.out.println("<head>");
		System.out.println("<style>");
		System.out.println("table { border-collapse: collapse; }");
		System.out.println("th, td { border: 1px solid black; padding: 8px; }");
		System.out.println("</style>");
		System.out.println("</head>");
		System.out.println("<body>");
		System.out.println("<h1>Drumcondra Train Station</h1>"); // name of the train station
		System.out.println("<p>Current time: " + currentTime + "</p>"); // the time of the http request
		System.out.println("<p>Departures in the next 90 minutes</p>");
		System.out.println("<table>");
		System.out.println("<tr><th>Expected Arrival Time</th><th>Origin</th><th>Destination</th><th>Expected Departure Time</th><th>Arrival Time at Destination</th></tr>");
		
		// get all the data from the xml file
		NodeList trains = doc.getElementsByTagName("objStationData");
		
		// loop through each train entry
		for (int i = 0; i < trains.getLength(); i++)
		{
			// node to element casting
		    Element train = (Element) trains.item(i);
		    
		 // get the expected arrival time
		    String expectedArrival = train.getElementsByTagName("Exparrival").item(0).getChildNodes().item(0).getNodeValue();
		    
		    // get the origin
		    String origin = train.getElementsByTagName("Origin").item(0).getChildNodes().item(0).getNodeValue();
		    
		    // get the destination
		    String destination = train.getElementsByTagName("Destination").item(0).getChildNodes().item(0).getNodeValue();
		    
		    // get the expected departure time
		    String expectedDeparture = train.getElementsByTagName("Expdepart").item(0).getChildNodes().item(0).getNodeValue();
		    
		    // get the arrival time at destination
		    String arrivalTime = train.getElementsByTagName("Destinationtime").item(0).getChildNodes().item(0).getNodeValue();
		    
		    // print table row with the train data
		    System.out.println("<tr><td>" + expectedArrival + "</td><td>" + origin + "</td><td>" + destination + "</td><td>" + expectedDeparture + "</td><td>" + arrivalTime + "</td></tr>");
		}
		
		System.out.println("</table>");
		System.out.println("</body>");
		System.out.println("</html>");
		
	}
}
