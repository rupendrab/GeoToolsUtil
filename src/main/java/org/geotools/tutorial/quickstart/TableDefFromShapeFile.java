package org.geotools.tutorial.quickstart;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TableDefFromShapeFile
{
  static Pattern lengthPattern;

  FileDataStore store;
  SimpleFeatureSource featureSource;
  SimpleFeatureType schema;
  String fileName;
  File file;
  int srid;
  String tableName;
  
  TreeMap<Integer, String[]>  columnDetails;
  
  public TableDefFromShapeFile(String fileName) throws Exception
  {
    this.fileName = fileName;
    this.file = new File(fileName);
    this.srid = GetSRID.getSRID(fileName);
    store = FileDataStoreFinder.getDataStore(file);
    featureSource = store.getFeatureSource();
    
    // showFeatureInfo(featureSource);

    schema = featureSource.getSchema();
    this.tableName = schema.getTypeName();
  }
  
  public static String getDataType(String columnClass, String dataType, int dataLen)
  {
    if (dataType.equalsIgnoreCase("integer"))
    {
      return "integer";
    }
    else if (dataType.equalsIgnoreCase("Double"))
    {
      return "double precision";
    }
    else if (dataType.equalsIgnoreCase("String"))
    {
      if (dataLen <= 0)
      {
        return "text";
      }
      else
      {
        return "varchar(" + dataLen + ")";
      }
    }
    else if (columnClass.equalsIgnoreCase("GeometryTypeImpl"))
    {
      return "geometry";
    }
    return "text";
  }
  
  public TreeMap<Integer, String[]> getColumnDetails()
  {
    TreeMap<Integer, String[]> columnDetails = new TreeMap<Integer, String[]>();
    int i = 0;
    columnDetails.put(i++, new String[]{"gid", "serial", ""});

    List<AttributeType> columnList = schema.getTypes();
    for (AttributeType column : columnList)
    {
      String columnName = column.getName().toString();
      String columnClass = column.getClass().getSimpleName();
      String columnType = column.getBinding().getSimpleName();
      int columnLength = -1;
      if (columnType.equalsIgnoreCase("String"))
      {
        List<Filter> columnRestrictions = column.getRestrictions();
        for (Filter columnRestriction : columnRestrictions)
        {
          // System.out.println(columnRestriction);
          String cons = columnRestriction.toString();
          Matcher m = lengthPattern.matcher(cons);
          if (m.find())
          {
            columnLength = Integer.parseInt(m.group(1));
          }
        }
      }
      // System.out.println(column.getBinding());
      // System.out.println(columnName + ", " + columnType + ", " + columnClass + ", " + columnLength);
      String dataType = getDataType(columnClass, columnType, columnLength);
      if (dataType.equalsIgnoreCase("geometry"))
      {
        columnDetails.put(10000, new String[]{"geom_" + srid, dataType, columnType});
      }
      else
      {
        columnDetails.put(i++, new String[]{columnName, dataType, columnType});
      }
    }
    
    this.columnDetails = columnDetails;
    return this.columnDetails;
  }
  
  public String getCreateTableScript()
  {
    StringBuilder sb = new StringBuilder(1000);
    sb.append("Create Table " + tableName);
    sb.append("\n");
    sb.append("(");
    sb.append("\n");
    int i = 0;
    for (Entry<Integer, String[]> entry: columnDetails.entrySet())
    {
      String[] colData = entry.getValue();
      if (colData[1].equalsIgnoreCase("geometry"))
      {
        continue;
      }
      if ( i > 0)
      {
        sb.append(",\n");
      }
      sb.append(String.format("  %-30s %30s", colData[0], colData[1]));
      i++;
    }
    sb.append("\n");
    sb.append(")");
    sb.append("\n");
    sb.append("distributed by (gid)");
    return sb.toString();
  }
  
  public String getAlterTableScript()
  {
    StringBuilder sb = new StringBuilder(200);
    String[] geomColumn = columnDetails.get(10000);
    sb.append(String.format("select AddGeometryColumn('%s', '%s', '%s', '%s', '%s', %d);",  
        "public", tableName, geomColumn[0], srid, geomColumn[2], 2));
    return sb.toString();
  }
  public void printColumnDetails()
  {
    System.out.println(getCreateTableScript());
    System.out.println(";");
    System.out.println(getAlterTableScript());
    System.out.println(";");
  }
  
  public void showFeatureInfo() throws Exception
  {
    System.out.println("======================  Feature Info ===================");
    System.out.println(featureSource.getInfo().getCRS());
    CoordinateReferenceSystem crs = featureSource.getInfo().getCRS();
    System.out.println(CRS.lookupEpsgCode(crs, false));
    System.out.println("======================  End Feature Info ===================");
  }
  
  public static void main(String[] args) throws Exception
  {
    if (args.length == 0)
    {
      System.out.println("Usage: Java org.geotools.tutorial.quickstart.TableDefFromShapeFile <Shape File Name>");
      System.out.println("example Shape File: /Users/bandyr/Documents/postgis/50m_cultural/ne_50m_admin_1_states_provinces_shp.shp");
      System.exit(0);
    }
    
    TableDefFromShapeFile tfds = new TableDefFromShapeFile(args[0]);
    tfds.getColumnDetails();
    tfds.printColumnDetails();

  }
  
  static
  {
    String lengthPatternStr = "^\\[\\s*length\\(\\[\\.\\]\\)\\s*<=\\s*([0-9]+)\\s*\\]\\s*$";
    lengthPattern = Pattern.compile(lengthPatternStr, Pattern.CASE_INSENSITIVE);
  }

  public SimpleFeatureSource getFeatureSource()
  {
    return featureSource;
  }

  public SimpleFeatureType getSchema()
  {
    return schema;
  }

}
