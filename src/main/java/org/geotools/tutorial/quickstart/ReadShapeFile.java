package org.geotools.tutorial.quickstart;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ReadShapeFile
{

  public static void main(String[] args)
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
      System.out.println(file.getAbsolutePath());
      connect.put("url", file.toURI().toURL());

      DataStore dataStore = DataStoreFinder.getDataStore(connect);
      String[] typeNames = dataStore.getTypeNames();
      String typeName = typeNames[0];
      for (String tn : typeNames)
      {
        System.out.println(tn);
      }

      System.out.println("Reading content " + typeName);

      // FeatureSource featureSource = dataStore.getFeatureSource(typeName);
      FileDataStore store = FileDataStoreFinder.getDataStore(file);
      SimpleFeatureSource featureSource = store.getFeatureSource();
      System.out.println("======================  Feature Info ===================");
      System.out.println(featureSource.getInfo().getCRS());
      CoordinateReferenceSystem crs = featureSource.getInfo().getCRS();
      System.out.println(CRS.lookupEpsgCode(crs, false));
      System.out.println("======================  End Feature Info ===================");

      SimpleFeatureType schema = featureSource.getSchema();
      System.out.println(schema.toString());
      Query query = new Query(schema.getTypeName(), Filter.INCLUDE);
      int count = featureSource.getCount(query);
      if (count == -1)
      {
        // information was not available in the header!
        System.out.println("Information not in header");
        SimpleFeatureCollection collection = featureSource.getFeatures(query);
        count = collection.size();
      }
      System.out.println("There are " + count + " " + schema.getTypeName() + " features");

      BoundingBox bounds = featureSource.getBounds(query);
      if (bounds == null)
      {
        // information was not available in the header
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);
        bounds = collection.getBounds();
      }
      System.out.println("The features are contained within " + bounds);

      System.exit(0);

      SimpleFeatureCollection collection = featureSource.getFeatures();
      SimpleFeatureIterator iterator = collection.features();

      try
      {
        int line = 0;
        while (iterator.hasNext())
        {
          Feature feature = iterator.next();
          line++;
          // Geometry sourceGeometry = feature.getDefaultGeometry();
          // System.out.println(feature.getName());
          System.out.println("Line: " + line);
          for (Property prop : feature.getProperties())
          {
            // System.out.println("  " + prop);
            System.out.println("  " + prop.getName() + ", " + prop.getType().getClass() + ", " + prop.getValue().getClass());
            if (prop.getType() instanceof AttributeTypeImpl)
            {
              AttributeTypeImpl typ = (AttributeTypeImpl) prop.getType();
              // System.out.println(typ.getRestrictions());
              List<Filter> filters = typ.getRestrictions();
              for (Filter f : filters)
              {
                System.out.println(f.toString());
              }
              System.out.println(prop.getValue());
            }
          }
        }
      }
      finally
      {
        iterator.close();
      }

    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

}
