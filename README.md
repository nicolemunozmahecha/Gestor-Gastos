# README

**Integrantes del grupo:**
* Guillermo Martínez Ruiz
* Nicole Muñoz Mahecha
* Lucía Ludeña López 

**Descripción del proyecto:**
El proyecto es una aplicación que sirve como un gestor de gastos personales y compartidos. En este podemos registrar gastos, organizarlos por categorías, configurar alertas cuando lleguemos a un máximo de gasto y ver estadísticas de los gastos realizados.

**Cómo ejecutar el proyecto:**
1.	Clonar el repositorio de GitHub en el que se encuentra el proyecto. Para ello:
```
git clone https://github.com/Guillermo-MR/TDS.git
```
2. Compilar y ejecutar. Para ello:
```
En la carpeta proyecto ejecutar:

mvn clean install

(Para GUI)
mvn javafx:run

(para CLI)
mvn exec:java -Dexec.mainClass="tds.app.App" -Dexec.args="-cli"
```

**La documentación del proyecto se encuentra en:**
- [Documentación completa del proyecto](docs/documentacion.md)
