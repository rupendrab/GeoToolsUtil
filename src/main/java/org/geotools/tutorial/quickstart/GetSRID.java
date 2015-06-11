package org.geotools.tutorial.quickstart;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.json.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class GetSRID
{
  public static void main(String[] args) throws Exception
  {

    if (args.length == 0)
    {
      System.out.println("Usage: Java org.geotools.tutorial.quickstart.ReadShapeFile <Shape File Name>");
      System.out.println("example Shape File: /Users/bandyr/Documents/postgis/50m_cultural/ne_50m_admin_1_states_provinces_shp.shp");
      System.exit(0);
    }

    File file = new File(args[0]);

    try
    {
      Map<String, URL> connect = new HashMap<String, URL>();
      connect.put("url", file.toURI().toURL());
      FileDataStore store = FileDataStoreFinder.getDataStore(file);
      SimpleFeatureSource featureSource = store.getFeatureSource();
      CoordinateReferenceSystem crs = featureSource.getInfo().getCRS();
      String callURL = "http://prj2epsg.org/search.json?mode=wkt&terms=" + URLEncoder.encode(crs.toString(), "UTF-8");

      URL sridURL = new URL(callURL); 
      
      BufferedReader in = new BufferedReader(new InputStreamReader(sridURL.openStream()));

      StringBuilder sb = new StringBuilder(100);
      String inputLine;
      while ((inputLine = in.readLine()) != null)
      {
        sb.append(inputLine);
      }
      in.close();
      // System.out.println(sb.toString());
      JSONObject obj = new JSONObject(sb.toString());
      JSONObject codes = (JSONObject) obj.getJSONArray("codes").get(0);
      String srid = (String) codes.get("code");
      System.out.println(srid);
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    
  }
}