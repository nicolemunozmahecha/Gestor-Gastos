sGUIA VENTANAS PROYECTO:



menuVentanaPrincipal:

--------------------------------------------------------------------------------------------------------

| EN LA VENTANA PRINCIPAL, TANTO DE LA CUENTA INDIVICUAL COMO DE LAS CUENTAS COMPARTIDAS, A PARTE DE

| REPRESENTAR LOS DATOS EN TABLAS PONE QUE OPCIONALMENTE PODRÍAMOS REPRESENTARLOS EN CALENDARIO. SI

| AL FINAL DECIDIMOS HACERLO, ES CUESTIÓN DE AÑADIR UNA LABEL CON EL CALENDARIO

--------------------------------------------------------------------------------------------------------



Cuentas -> Crear Cuenta -> crearCuenta.fxml										HECHO
 	-> Eliminar Cuenta -> muestra listado de cuentas, elimina una
 	-> Total Cuenta -> elige entre las cuentas existentes y sale una ventana con el mensaje con el valor -> totalCuenta.fxml		HECHO

Gastos
**-> Obtener Gastos -> VER COMO HACER PUES LOS GASTOS APARECEN EN LA VENTANA DE LA CUENTA**
**-> Obtener Gastos/cuenta -> VER COMO HACER PUES LOS GASTOS APARECEN EN CADA VENTANA DE LA CUENTA**
-> Filtrar Gastos -> filtrarGastos.fxml -> se ponen los filtros y los gastos que los cumplen aparecen en la ventana	HECHO

---------------------------------------------------------------------------------------------------------

| CUANDO HAGAMOS EL CÓDIGO DE filtrarGastos.fxml, HAY QUE TENER EN CUENTA QUE LAS CATEGORÍAS SON DINÁMICAS,

| YA QUE DEPENDEN DE LAS QUE EL USUARIO VAYA CREANDO. YO HE PUESTO EN CATEGORÍAS UN ListView (ahora mismo no

| se ve, porque no tiene ninguna categoría dentro), EL CUÁL DEBEMOS IR LLENANDO CON LAS CATEGORÍAS QUE CREE

| EL USUARIO DESDE EL CÓDIGO. ¡Recordar que hay también categorías de base!

|

| CUANDO HAGAMOS EL CÓDIGO DE filtrarGastos.fxml, DEBEMOS HACER QUE EN LA SECCIÓN Filtros activos VAYAN

| APARECIENDO LOS FILTROS QUE SE VAN SELECCIONANDO.

---------------------------------------------------------------------------------------------------------



Categorías -> Crear Categoria -> crearCategoria.fxml 									HECHO

*---------------------------------------------------------------------------------------------------------*

*| CUANDO SE CREA UNA CATEGORIA ESTA DEBE APARECER:*
*|	- EN EL DESPLEGABLE DE LAS ALERTAS PARA ELEGIR CATEGORIA*
*|	- EN EL DESPLEGABLE DE ELIMINAR CATEGORIAS*
*|CUANDO SE CREA UNA CUENTA ESTA DEBE APARECER:*
*|	- EN EL DESPLEGABLE DE ELIMINAR CUENTAS*
*|CUANDO SE CREA UNA ALERTA ESTA DEBER APARECER:*
*|	- EN EL DESPLEGABLE DE ELIMINAR ALERTA*
*|	- EN EL HISTORIAL DE NOTIFICACIONES*
*---------------------------------------------------------------------------------------------------------*

 	   -> Eliminar categoría -> muestra listado de categorías, elimina una

alertas -> Crear alerta -> crearAlerta.fxml										HECHO
 	-> Eliminar alerta -> muestra listado de alertas, elimina una

Notificaciones -> Historial -> historial.fxml(muestra listado de notificaciones)					HECHO

salir -> 														HECHO YA



*---------------------------------------------------------------------------------------------------------*

*| IMPORTANTE: AL CREAR CUENTAS, LAS VENTANAS DE CUENTA NUEVAS SON CUENTAS COMPARTIDAS, MENOS LA PRIMERA |*

*---------------------------------------------------------------------------------------------------------*



cuentaPrincipal:
crearGasto -> Crear gasto -> crearGasto.fxml										HECHO
**-> Importar gasto -> VER COMO IMPORTAR FICHEROS, PONER NOMBRE DE FICHERO EXISTENTE?**
**-> Eliminar gasto -> eliminarGasto.fxml(muestra listado de gastos, elimina uno)**
-> Gráfica -> La he metido en la propia interfaz de las cuentas, tanto la principal como las compartidas.		HECHO



cuentaSecundaria:

* creaGasto														HECHO
* visualización													HECHO

saldo -> Por persona -> gastoPorPersona.fxml										HECHO
**distribución -> personalizar -> distribucionPersonalizada.fxml**



*----------------------------------------------------------------------------------------------------------------------------------------*

*| IMPORTANTE: AL CREAR GASTOS, ESTOS DEBEN APARECER EN LA VENTANA PRINCIPAL, en la Tabla por defecto, ASÍ COMO LA VISUALIZACIÓN ELEGIDA |*

*----------------------------------------------------------------------------------------------------------------------------------------*

