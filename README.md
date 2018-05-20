# CEV - Colección Entomológica Virtual

Actualmente los curadores de la colección entomológica virtual, realizan un proceso de consignación de sus ejemplares basándose en la información de clasificación, ubicación, autoría, y otros elementos que conforman la ficha técnica de un ejemplar, el cual acompaña al espécimen preservado. Tal ficha constituye un recurso importante para los investigadores y estudiantes que quieran hacer uso de este recurso, ya que provee una forma consistente y descriptiva de la definición de un ejemplar. No obstante, el acceso a estas fichas está limitado debido a la ubicación del centro de Biosistemas donde físicamente están consignadas tales fichas. 

El Centro Entomológico Virtual es un sistema destinado a la gestión y visualización sobre plataformas Web de la información de los ejemplares el cual yace almacenada en fichas técnicas, y además permite la apreciación de los ejemplares a través de contenido multimedia.

## Acerca de
El código que reposa en este repositorio hace parte del proyecto de grado para Ingeniero de Sistemas de la Universidad Central.

Puede ser clonado desde cualquier cliente Git, no obstante recomiendo usar el IDE JBoss Developer Studio 11.1.0.GA, versión con la que este proyecto fue diseñado y puesto en marcha. Actualmente, el sistema en ejecución se encuentra en la siguiente ruta: http://hpclab.ucentral.edu.co:8080/CEV/ 

## Construido con:

Específicamente, las tecnologías que se usaron en el proyecto incluyen:

* [Apache Maven](https://maven.apache.org/) Administración de dependencias y estructura del proyecto.
* [JSF 2.2](https://javaee.github.io/javaserverfaces-spec/) Simplifica la construcción de interfaces Web.
* [Bootstrap](https://getbootstrap.com/) Permite la alta adaptación a distintos dispositivos.
* [Hibernate](http://hibernate.org/) Modelo de persistencia para bases de datos.

## Como desplegar

Principalmente, el proyecto debe ser construido con los siguientes comandos:

```
mvn install
mvn compile war:war
```

Los cuales permiten actualizar todas las dependencias, y adicionalmente, empacarlas en un artefacto tipo WAR. El servidor de despliegue recomendado es JBoss WildFly 11.1.0 Final.

## Autores

* **Sebastián Motavita Medellín** - *Graduando, desarrollador principal (y único)* - [Sebasir](https://github.com/sebasir)
* **Hugo Franco Triana** - *PhD, Director de Proyecto*

