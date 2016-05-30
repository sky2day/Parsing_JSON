package core;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class WebserviceJSON {
	
	//method returns customer's country name. Parameters: customer's IP address.
			public static String countryName (String ip) throws Exception{
				
				URL webservice_geo = new URL("http://www.geoplugin.net/json.gp?ip="+ip);
				//String element_name_01 = "Country: ";
				String country = null;
			
				InputStream stream_geo = webservice_geo.openStream();
				JsonParser parser_geo = Json.createParser(stream_geo);
				
				while (parser_geo.hasNext()) {
				Event e = parser_geo.next();
				if (e == Event.KEY_NAME) {
					switch (parser_geo.getString()) {
					case "geoplugin_countryName":
						parser_geo.next();
						//System.out.println(element_name_01 + parser.getString());
					country = parser_geo.getString();
					}
				}
			}
		return country; 
	}
						
	//method returns local price. Parameters: customer's IP address, price in USD:
	public static float calculateLocalPrice (String ip, float price_USD) throws Exception{
		
			URL webservice = new URL("http://www.geoplugin.net/json.gp?ip="+ip);
			//String element_name_02 = null;
			String localCurrencyCode = null;
			String baseCurrencyCode = "USD";
			float exchangeRate = 0;
			
			InputStream stream = webservice.openStream();
			JsonParser parser = Json.createParser(stream);

			while (parser.hasNext()) {
			Event e = parser.next();
			if (e == Event.KEY_NAME) {
				switch (parser.getString()) {
			
				case "geoplugin_currencyCode":
					parser.next();
					//System.out.println(element_name_02 + parser.getString());
					localCurrencyCode = parser.getString();
					break;
				}
			}
		}
	webservice = new URL ("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20%28%22"+baseCurrencyCode+localCurrencyCode+"%22%29&format=json&env=store://datatables.org/alltableswithkeys");
		//String element_name_03 = "RateID: ";
		//String element_name_04 = "Exchange Rate: ";
		stream = webservice.openStream();
	    parser = Json.createParser(stream);

		while (parser.hasNext()) {
		Event e = parser.next();
		if (e == Event.KEY_NAME) {

			switch (parser.getString()){
			case "Name":
				parser.next();
				//System.out.println(element_name_03 + parser.getString());
			break;

				case "Rate":
				parser.next();
				//System.out.println(element_name_04 + parser.getString());
				exchangeRate = Float.parseFloat(parser.getString());
			break;
			}
		  }
		}
		 	float priceLocal = Math.round(price_USD*exchangeRate * 100.0)/100.0f;
			return priceLocal;	
	}

	//method removes $-sign and converts String to float.
	public static float priceStringToFloat(String a){
	return Float.parseFloat(a.replaceAll("[$]", ""));
	}
		
	public static void main (String[] args) throws Exception {
				
		//Item names:
		String[] item_name = new String[5];
		item_name[0] = "Adidas Soccer Ball";
		item_name[1] = "Withings Activity Tracker";
		item_name[2] = "Samsung Galaxy Phone";
		item_name[3] = "GoPro HERO3 Camera";
		item_name[4] = "Apple iPhone 6s";
		
		//customer's IP addresses:	
		String[] ip = new String[5];
		ip[0] = "88.191.179.56"; // France
		ip[1] = "61.135.248.220"; //China
		ip[2] = "92.40.254.196"; // UK
		ip[3] = "93.183.203.67"; // Ukraine
		ip[4] = "213.87.141.36"; // Russia
	
		//URLs for five selling items:
		String[] url = new String[5];
		url[0] = "http://www.amazon.com/Adidas-Football-Brazuca-Brazil-Glider/dp/B00QLH688O/ref=sr_1_2?ie=UTF8&qid=1463865348&sr=8-2&keywords=Football+Brazuca+Fifa+World+Cup+Brazil+2014";
		url[1] = "http://www.amazon.com/Withings-Wireless-Activity-Tracker-Monitoring/dp/B00CW7KK9K/ref=sr_1_1?ie=UTF8&qid=1463874077&sr=8-1&keywords=Withings+Pulse+Wireless+Activity+Tracker+%2B+Sleep";
		url[2] = "http://www.amazon.com/Samsung-Galaxy-Grand-Factory-Unlocked/dp/B00OKTY3A0/ref=sr_1_1?ie=UTF8&qid=1463874245&sr=8-1&keywords=Samsung+Galaxy+Grand+Prime+Dual+Sim+Factory+Unlocked";
		url[3] = "http://www.amazon.com/GoPro-Adventure-Camera-Discontinued-Manufacturer/dp/B00F3F0GLU/ref=sr_1_1?ie=UTF8&qid=1463874321&sr=8-1&keywords=GoPro+HERO3%2B+Black+Edition+Adventure+Camera";
		url[4] = "http://www.amazon.com/Apple-iPhone-Warranty-Unlocked-Cellphone/dp/B015E8UTIU/ref=sr_1_3?ie=UTF8&qid=1463874382&sr=8-3&keywords=iPhone+6s+64+GB+US+Warranty+Unlocked";
		
	//Turning HtmlUnit Warnings off 
		//BEGIN		
				LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
				java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
				java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
				java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);
		//END
		
		WebDriver driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_11);
		float[] priceUSD = new float[5];
		
		// loop adds values to priceUSD array.
		for (int i=0; i<=4; i++){
		driver.get(url[i]);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		String item_price = driver.findElement(By.xpath(".//*[@class='a-size-medium a-color-price']")).getText();
		priceUSD[i] = priceStringToFloat (item_price);
		}			
	    		
		//loop prints out the output
	for (int i=0; i<=4; i++){
		for (int j=0; j<=4; j++){
		System.out.println("Item " + (i+1)+ ": "+ item_name[i] + "; US Price: " + priceUSD[i] + "; Country: " + countryName(ip[j])+ "; Local price: " + calculateLocalPrice(ip[j], priceUSD[i]));
		}
		System.out.println();
	}
  }
}