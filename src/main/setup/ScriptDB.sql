CREATE DATABASE "CEV";
\c "CEV";

CREATE TABLE INSTITUTION (
	ID_INSTITUTION SERIAL NOT NULL PRIMARY KEY CHECK (ID_INSTITUTION > 0),
	INSTITUTION_NAME VARCHAR(64) NOT NULL,
	INSTITUTION_CODE VARCHAR(32) NOT NULL,
    DOMAIN_URL VARCHAR(32) NOT NULL,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo'))
);
COMMENT ON TABLE INSTITUTION IS 'Tabla para guardar las instituciones involucradas.';

CREATE TABLE COLLECTION (
	ID_COLLECTION SERIAL NOT NULL PRIMARY KEY CHECK (ID_COLLECTION > 0),
	COLLECTION_NAME VARCHAR(64) NOT NULL,
	ID_INSTITUTION INT REFERENCES INSTITUTION (ID_INSTITUTION),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE(ID_INSTITUTION, COLLECTION_NAME)
);
COMMENT ON TABLE COLLECTION IS 'Tabla para guardar las colecciones, tales que pertenezcan a cada institucion.';

CREATE TABLE CATALOG (
	ID_CATALOG SERIAL NOT NULL PRIMARY KEY CHECK (ID_CATALOG > 0),
	CATALOG_NAME VARCHAR(64) NOT NULL,
    ID_COLLECTION INT REFERENCES COLLECTION (ID_COLLECTION),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
	UNIQUE(ID_COLLECTION, CATALOG_NAME)
);
COMMENT ON TABLE CATALOG IS 'Tabla para guardar los catálogos donde yacen los especímenes, para cada coleccion.';

CREATE TABLE SAMPLE_TYPE (
	ID_SATY SERIAL NOT NULL PRIMARY KEY CHECK(ID_SATY > 0),
	SATY_NAME VARCHAR(32) NOT NULL,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE(SATY_NAME)
);
COMMENT ON TABLE SAMPLE_TYPE IS 'Tabla para guardar los tipos de muestra.';

CREATE TABLE REG_TYPE (
	ID_RETY SERIAL NOT NULL PRIMARY KEY CHECK(ID_RETY > 0),
	RETY_NAME VARCHAR(32) NOT NULL,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE(RETY_NAME)
);
COMMENT ON TABLE REG_TYPE IS 'Tabla para guardar los tipos de registro.';

CREATE TABLE USERS (
	ID_USER SERIAL NOT NULL PRIMARY KEY CHECK (ID_USER > 0),
	USER_ID_NUMBER VARCHAR(32) NOT NULL,
	USER_NAMES VARCHAR(64) NOT NULL,
	USER_LASTNAMES VARCHAR(64) NOT NULL,
	USER_EMAIL VARCHAR(64) NOT NULL,
	USER_CREATED TIMESTAMP,
	USER_MODIFIED TIMESTAMP,
	USER_LAST_LOGIN TIMESTAMP,
    USER_PASSWORD VARCHAR(64) NOT NULL,
    ID_INSTITUTION INT REFERENCES INSTITUTION (ID_INSTITUTION),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
	UNIQUE(USER_ID_NUMBER),
    UNIQUE(USER_NAMES, USER_LASTNAMES),
    UNIQUE(USER_EMAIL)
);
COMMENT ON TABLE USERS IS 'Tabla para guardar los usuarios y sus credenciales.';

CREATE TABLE AUTHOR (
	ID_AUTHOR SERIAL NOT NULL PRIMARY KEY CHECK(ID_AUTHOR > 0),
	AUTHOR_NAME VARCHAR(32) NOT NULL,
    AUTHOR_DET INT NOT NULL,
    AUTHOR_AUT INT NOT NULL,
    AUTHOR_COL INT NOT NULL,
    ID_USER INT REFERENCES USERS (ID_USER),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE(AUTHOR_NAME)
);
COMMENT ON TABLE AUTHOR IS 'Tabla para guardar los nombres de los autores, o bien, que exista un usuario del sistema que sea autor y se defininen sus roles.';
COMMENT ON COLUMN AUTHOR.AUTHOR_DET IS 'Define si el autor es determinador.';
COMMENT ON COLUMN AUTHOR.AUTHOR_AUT IS 'Define si el autor es autor de epiteto.';
COMMENT ON COLUMN AUTHOR.AUTHOR_COL IS 'Define si el autor es colector.';

CREATE TABLE LOCATION_LEVEL (
	ID_LOCLEVEL SERIAL NOT NULL PRIMARY KEY CHECK(ID_LOCLEVEL > 0),
	LOCLEVEL_NAME VARCHAR(32) NOT NULL,
	LOCLEVEL_RANK INTEGER NOT NULL,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE (ID_LOCLEVEL, LOCLEVEL_NAME, LOCLEVEL_RANK),
    UNIQUE (LOCLEVEL_NAME)
);
COMMENT ON TABLE LOCATION_LEVEL IS 'Tabla para guardar los niveles de las ubicaciones, tales como pais, departamento, municipio, etc.';

CREATE TABLE LOCATION (
	ID_LOCATION SERIAL NOT NULL PRIMARY KEY CHECK(ID_LOCATION > 0),
	LOCATION_NAME VARCHAR(64) NOT NULL,
	LATITUDE FLOAT,
	LONGITUDE FLOAT,
	ALTITUDE FLOAT,
	RADIO FLOAT DEFAULT 0,
	ID_LOCLEVEL INT REFERENCES LOCATION_LEVEL (ID_LOCLEVEL),
	ID_CONTAINER INT REFERENCES LOCATION (ID_LOCATION),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE (ID_CONTAINER, LOCATION_NAME)
);
COMMENT ON TABLE LOCATION IS 'Tabla para guardar las ubicaciones, teniendo en cuenta una estructura jerárjica.';

CREATE TABLE TAXONOMY_LEVEL (
	ID_TAXLEVEL SERIAL NOT NULL PRIMARY KEY CHECK(ID_TAXLEVEL > 0),
	TAXLEVEL_NAME VARCHAR(32) NOT NULL,
	TAXLEVEL_RANK INTEGER NOT NULL,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE (ID_TAXLEVEL, TAXLEVEL_NAME, TAXLEVEL_RANK),
    UNIQUE (TAXLEVEL_NAME)
);
COMMENT ON TABLE TAXONOMY_LEVEL IS 'Tabla para guardar los niveles de las clasificaciones, tales como reino, phylum, clase, etc.';

CREATE TABLE TAXONOMY (
	ID_TAXONOMY SERIAL NOT NULL PRIMARY KEY CHECK(ID_TAXONOMY > 0),
	TAXONOMY_NAME VARCHAR(64) NOT NULL,
	ID_TAXLEVEL INT REFERENCES TAXONOMY_LEVEL (ID_TAXLEVEL),
	ID_CONTAINER INT REFERENCES TAXONOMY (ID_TAXONOMY),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE (ID_CONTAINER, TAXONOMY_NAME)
);
COMMENT ON TABLE TAXONOMY IS 'Tabla para guardar las clasificaciones, teniendo en cuenta una estructura jerárjica.';

CREATE TABLE SPECIMEN (
	ID_SPECIMEN SERIAL NOT NULL PRIMARY KEY CHECK (ID_SPECIMEN > 0),
	SPECIFIC_EPITHET VARCHAR(64),
	COMMON_NAME VARCHAR(64) NOT NULL,
	ID_TAXONOMY INT REFERENCES TAXONOMY (ID_TAXONOMY) NOT NULL,
	ID_COLLECTOR INT REFERENCES AUTHOR (ID_AUTHOR),
	IDEN_COMMENT VARCHAR(2048),
	IDEN_DATE TIMESTAMP NOT NULL,
	ID_RETY INT REFERENCES REG_TYPE (ID_RETY) NOT NULL,
	ID_SATY INT REFERENCES SAMPLE_TYPE (ID_SATY) NOT NULL,
	ID_LOCATION INT REFERENCES LOCATION (ID_LOCATION) NOT NULL,
	ID_DETERMINER INT REFERENCES AUTHOR (ID_AUTHOR),
	ID_EPITHET_AUTHOR INT REFERENCES AUTHOR (ID_AUTHOR),
	ID_BIOREG VARCHAR(32) NOT NULL,
	ID_CATALOG INT REFERENCES CATALOG (ID_CATALOG),
	COLLECT_DATE TIMESTAMP,
	COLLECT_COMMENT VARCHAR(2048),
    ID_USER INT REFERENCES USERS (ID_USER),        
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE (ID_BIOREG),
    UNIQUE (ID_TAXONOMY),
    UNIQUE (COMMON_NAME),
    UNIQUE (SPECIFIC_EPITHET)
);
COMMENT ON TABLE SPECIMEN IS 'Tabla para guardar los especímenes.';

CREATE TABLE SPECIMEN_CONTENT (
	ID_SPECCONT SERIAL NOT NULL PRIMARY KEY CHECK(ID_SPECCONT > 0),
	ID_SPECIMEN INT REFERENCES SPECIMEN (ID_SPECIMEN),
	FILE_NAME VARCHAR(128) NOT NULL,
	FILE_CONTENT BYTEA NOT NULL,
	SHORT_DESCRIPTION VARCHAR(2048) NOT NULL,
	PUBLISH CHAR(1) NOT NULL DEFAULT 'N',
	FILE_UPLOAD_DATE TIMESTAMP NOT NULL DEFAULT NOW(),
	PUBLISH_DATE TIMESTAMP,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
	UNIQUE (ID_SPECIMEN)
);
COMMENT ON TABLE SPECIMEN_CONTENT IS 'Tabla para guardar el contenido de imágen de cada especímenes.';

CREATE TABLE MODULES (
	ID_MODULE SERIAL NOT NULL PRIMARY KEY CHECK (ID_MODULE > 0),
	MODULE_NAME VARCHAR(64) NOT NULL,
	MODULE_DESCR VARCHAR(256) NOT NULL,
    MODULE_ORDER INT NOT NULL,
    MODULE_PAGE VARCHAR(32),
    MODULE_ICON VARCHAR(16),
    MODULE_CODE VARCHAR(16),
    ID_CONTAINER INT REFERENCES MODULES (ID_MODULE),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE(MODULE_NAME)
);
COMMENT ON TABLE MODULES IS 'Tabla para guardar los modulos del sistema.';

CREATE TABLE ROLES (
	ID_ROLE SERIAL NOT NULL PRIMARY KEY CHECK (ID_ROLE > 0),
	ROLE_NAME VARCHAR(32) NOT NULL,
	ROLE_DESCR VARCHAR(256) NOT NULL,
	ROLE_CREATED TIMESTAMP,
	ROLE_MODIFIED TIMESTAMP,
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
	UNIQUE(ROLE_NAME)
);
COMMENT ON TABLE ROLES IS 'Tabla para guardar los roles de usuario del sistema.';

CREATE TABLE ROLES_MODULES (
	ID_ROMO SERIAL NOT NULL PRIMARY KEY CHECK(ID_ROMO > 0),
	ID_ROLE INT REFERENCES ROLES (ID_ROLE),
	ID_MODULE INT REFERENCES MODULES (ID_MODULE),
	ACCESS_LEVEL INT NOT NULL CHECK(ACCESS_LEVEL BETWEEN 0 AND 15),
	STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
    UNIQUE (ID_ROLE, ID_MODULE)
);
COMMENT ON TABLE ROLES_MODULES IS 'Tabla para guardar las relaciones roles-modulos.';
COMMENT ON COLUMN ROLES_MODULES.ACCESS_LEVEL IS 'Define el nivel de acceso del usuario:
DUCR
0000 ->  0 NONE
0001 ->  1 ONLY READ
0010 ->  2 ONLY CREATE
0011 ->  3 READ, CREATE
0100 ->  4 ONLY UPDATE
0101 ->  5 READ, UPDATE
0110 ->  6 CREATE, UPDATE
0111 ->  7 READ, CREATE, UPDATE
1000 ->  8 ONLY DELETE
1001 ->  9 READ, DELETE
1010 -> 10 CREATE, DELETE
1011 -> 11 READ, CREATE, DELETE
1100 -> 12 UPDATE, DELETE
1101 -> 13 READ, UPDATE, DELETE
1110 -> 14 CREATE, UPDATE, DELETE
1111 -> 15 READ, CREATE, UPDATE, DELETE';

CREATE TABLE ROLES_USERS (
	ID_ROUS SERIAL NOT NULL PRIMARY KEY CHECK(ID_ROUS > 0),
	ID_ROLE INT REFERENCES ROLES (ID_ROLE),
	ID_USER INT REFERENCES USERS (ID_USER),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
	UNIQUE (ID_ROLE, ID_USER)
);
COMMENT ON TABLE ROLES_USERS IS 'Tabla para guardar las relaciones roles-usuarios, así como el nivel de acceso.';

CREATE TABLE MODULES_USERS (
	ID_MOUS SERIAL NOT NULL PRIMARY KEY CHECK(ID_MOUS > 0),
	ID_MODULE INT REFERENCES MODULES (ID_MODULE),
	ID_USER INT REFERENCES USERS (ID_USER),
    ACCESS_LEVEL INT NOT NULL CHECK(ACCESS_LEVEL BETWEEN 0 AND 15),
    STATUS VARCHAR(32) DEFAULT 'Activo' CHECK (STATUS IN ('Activo', 'Deshabilitado', 'Bloqueado', 'Incompleto', 'Completo')),
	UNIQUE (ID_MODULE, ID_USER)
);
COMMENT ON TABLE MODULES_USERS IS 'Tabla para guardar las relaciones modulos-usuarios, así como el nivel de acceso.';
COMMENT ON COLUMN MODULES_USERS.ACCESS_LEVEL IS 'Define el nivel de acceso del usuario:ACCESS_LEVEL IS Define el nivel de acceso del usuario:
DUCR
0000 ->  0 NONE
0001 ->  1 ONLY READ
0010 ->  2 ONLY CREATE
0011 ->  3 READ, CREATE
0100 ->  4 ONLY UPDATE
0101 ->  5 READ, UPDATE
0110 ->  6 CREATE, UPDATE
0111 ->  7 READ, CREATE, UPDATE
1000 ->  8 ONLY DELETE
1001 ->  9 READ, DELETE
1010 -> 10 CREATE, DELETE
1011 -> 11 READ, CREATE, DELETE
1100 -> 12 UPDATE, DELETE
1101 -> 13 READ, UPDATE, DELETE
1110 -> 14 CREATE, UPDATE, DELETE
1111 -> 15 READ, CREATE, UPDATE, DELETE';

CREATE TABLE AUDIT_LOG (
	ID_AULOG SERIAL NOT NULL PRIMARY KEY CHECK (ID_AULOG > 0),
	ID_USER INT REFERENCES USERS (ID_USER) NOT NULL,
    ID_MODULE INT REFERENCES MODULES (ID_MODULE),
	AULOG_TIME TIMESTAMP DEFAULT NOW(),
	AULOG_IP_ADDRESS VARCHAR(16),
	AULOG_ACTION VARCHAR(32) CHECK (AULOG_ACTION IN ('LOGIN', 'LOGOUT', 'INSERT', 'UPDATE', 'DELETE', 'STATUS_CHANGE')),
	AULOG_TARGET VARCHAR(2048) NOT NULL
);
COMMENT ON TABLE MODULES_USERS IS 'Tabla para guardar las operaciones de usuarios en los modulos, con fecha, direccion IP, accion realizada y cambio.';