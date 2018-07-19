package core;
 
import java.io.*;
import java.math.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
 
public class Main {
	
	       static String country_name;
	       static String currency_symbol;
	       static String pair_code;
	       static Double rate;
	
     public static void CurrencyCode(URL cc) throws IOException, ParseException {
    	
        JSONParser jp = new JSONParser();            

	    BufferedReader in = new BufferedReader(new InputStreamReader(cc.openConnection().getInputStream()));
	             
	    JSONObject cc_jo = (JSONObject) jp.parse(in);
	       
	    country_name = cc_jo.get("geoplugin_countryName").toString();
	    currency_symbol = cc_jo.get("geoplugin_currencySymbol_UTF8").toString();
	    pair_code = cc_jo.get("geoplugin_currencyCode").toString();
	      
     }
	
     public static void CurrencyRate(URL cc) throws IOException, ParseException {
        
        JSONParser jp = new JSONParser();
       
        JSONObject cc_json = (JSONObject) jp.parse(new BufferedReader(
        new InputStreamReader(cc.openConnection().getInputStream())));
       
        String usa_code = "USD";
        String local_code = (String) cc_json.get("geoplugin_currencyCode");  // EUR
        pair_code = usa_code + "_" + local_code;                      // USD_EUR

        URL rate_url = new URL("http://free.currencyconverterapi.com/api/v6/convert?q=" + pair_code + "&compact=y");

        JSONObject rate_json = (JSONObject) jp.parse(new BufferedReader(
        new InputStreamReader(rate_url.openConnection().getInputStream())));

        JSONObject root = (JSONObject) rate_json.get(pair_code);
        rate = ((Double) root.get("val")); 
 
     }
 
       public static void main(String[] args) throws InterruptedException, IOException, ParseException {
              String us_currency_symbol = "$";

              String ip [] = {"80.211.224.40","117.239.54.7","78.39.209.78","91.73.131.254","190.24.145.125"};

              Logger logger = Logger.getLogger(""); logger.setLevel(Level.OFF);
              String url = "https://www.merrell.com/US/en/trail-glove-4/29191M.html?dwvar_29191M_color=J09669#cgid=men-footwear-fitness&start=1";
 
              WebDriver driver;
              
              System.setProperty("webdriver.chrome.driver", "./src/main/resources/webdrivers/mac/chromedriver");
              System.setProperty("webdriver.chrome.silentOutput", "true");
              ChromeOptions option = new ChromeOptions();
              option.addArguments("-start-fullscreen");
              driver = new ChromeDriver(option);
        
                            
              driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
              driver.get(url);
 
              String product_title = driver.findElement(By.xpath("//*[@id=\"product-content\"]/div[3]/div[1]/h1")).getText();
              double original_price = Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"product-content\"]/div[3]/div[2]/span[1]")).getText().replace("$", ""));

              driver.quit();
 
              for (int i=0; i < ip.length;i++){
            	 
            	  URL url_cc = new URL("http://www.geoplugin.net/json.gp?ip=" + ip[i]);
            	  CurrencyCode(url_cc);
                  CurrencyRate(url_cc); 
                  double local_price = new BigDecimal(original_price * rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
                  System.out.println("Item: " + product_title + "; " 
                                   + "US Price: " + us_currency_symbol + original_price + "; "
                                   + "for country: " + country_name + "; "
                                   + "Local Price: " + currency_symbol + local_price);
              }
    }
}
