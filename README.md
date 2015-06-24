# GeoToolsUtil

### Create Greenplum Table Definition from Shape File

```
./run.sh org.geotools.tutorial.quickstart.TableDefFromShapeFile /Users/bandyr/Documents/postgis/50m_cultural/ne_50m_admin_1_states_provinces_shp.shp

Create Table ne_50m_admin_1_states_provinces_shp
(
  gid                                                    serial,
  featurecla                                        varchar(32),
  scalerank                                    double precision,
  adm1_code                                         varchar(10),
  diss_me                                               integer,
  adm1_cod_1                                        varchar(10),
  iso_3166_2                                        varchar(10),
  wikipedia                                        varchar(254),
  sr_sov_a3                                          varchar(3),
  sr_adm0_a3                                         varchar(3),
  iso_a2                                             varchar(2),
  adm0_sr                                               integer,
  admin0_lab                                            integer,
  name                                             varchar(100),
  name_alt                                         varchar(200),
  name_local                                       varchar(200),
  type                                             varchar(100),
  type_en                                          varchar(100),
  code_local                                        varchar(50),
  code_hasc                                         varchar(10),
  note                                             varchar(254),
  hasc_maybe                                        varchar(50),
  region                                           varchar(100),
  region_cod                                        varchar(50),
  region_big                                       varchar(200),
  big_code                                          varchar(50),
  provnum_ne                                            integer,
  gadm_level                                            integer,
  check_me                                              integer,
  scaleran_1                                            integer,
  datarank                                              integer,
  abbrev                                            varchar(10),
  postal                                            varchar(10),
  area_sqkm                                    double precision,
  sameascity                                            integer,
  labelrank                                             integer,
  featurec_1                                        varchar(50),
  admin                                            varchar(200),
  name_len                                              integer,
  mapcolor9                                             integer,
  mapcolor13                                            integer
)
distributed by (gid)
;
select AddGeometryColumn('public', 'ne_50m_admin_1_states_provinces_shp', 'geom_4326', '4326', 'MultiPolygon', 2);
;
```
