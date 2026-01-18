# DOCUMENTACIÓN DEL PROYECTO
## DIAGRAMA DE CLASES DEL DOMINO DEL PROYECTO
Diseñamos el siguiente diagrama de clases del dominio del proyecto:

![Diagrama de clases del dominio](imagenes/5enero.png)

En él se muestran las clases principales del dominio junto con sus relaciones dentr de la aplicación.

## ARQUITECTURA DE LA APLICACIÓN
La aplicación la hemos diseñado con una arquitectura en capas, en la que se distinguen:
* **La interfaz de usuario (tds.vista) ->** Tenemos varias clases Controller (por ejemplo, CrearAlertaController o CrearCategoriaController), que se encargan de gestionar las ventanas, actuando como puente entre la interfaz y el dominio. 
* **La lógica de aplicación (tds.controlador) ->** Hemos creado un GestorGastos, el cuál actúa como controlador. Este se encarga de coordinar las operaciones que le solicite la interfaz.
* **El dominio (tds.modelo) ->** Contiene las interfaces de las entidades principales de la aplicación, que serían: Cuenta, CuentaCompartida, CuentaPersonal, Gasto, Categoría, Persona, Alerta, Notificacion, Filtro, EstrategiaAlerta, EstrategiaDistribucion y PeriodoAlerta.
Mediante EstrategiaAlertaFactory y EstrategiaDistribucionFactory tenemos factorías, que se encargan de ls creación de estrategias.
En /tds/modelo/impl tenemos las implementaciones de nuestras entidades.
* **La persistencia (tds.adapters.repository) ->** Mediante repositorios JSON se almacena toda la información en un único fichero, facilitando la gestión de datos.


## DECISIONES DE DISEÑO
Durante la creación de la aplicación, hemos tenido que tomar varias decisiones de diseño. Algunas de ls más destacables son:
* **Separar interfaces e implementaciones en el dominio ->** Como hemos explicado en el apartado anterior, hemos decidido separar las interfaces (/tds/modelo) y las implementaciones (/tds/modelo/impl). De esta forma hemos conseguido facilitar la persistencia y permitir que fuera más sencillo ir añadiendo implementaciones.
* **GestorGastos como único controlador ->** Creamos un único controlador llamado GestorGastos, al que la interfaz llama y él se encarga de llamar al dominio. De esa forma, la interfaz no tiene que acceder directamente al modelo.
* **Persistencia mediante JSON ->** Decidimos almacenar toda la información en un único fichero JSON. Así, podíamos cambiar el mecanismo de persistencia sin afectar al resto del programa.
* **Gestión de gastos en cuentas compartidas ->** Decidimos que la aplicación recaulculará los saldos automáticamente al añadir o eliminar un gasto a la cuenta compartida.
* **Si los porcentajes personalizados no suman 100%, se toma DistribucionEquitativaImpl ->** Hemos decidido que en el caso de que los porcentajes introducidos por el usuario no sumen el 100%, en lugar de dar error y bloquearse, se haga una distribución equitativa de porcentajes entre los miembros de la cuenta.
* **Las cuentas personales ignoran el pagador ->** Como en las cuentas personales no va a insertar gastos más que una persona, el propietario del gasto no afecta al cálculo del total.
* **Las alertas sólo saltan al superar el límite ->** Tuvimos la duda de si la alerta debería saltar cuando gastoActual >= limite o si cuando gastoActual > limite. Al final decidimos que saltaran cuando se supere el límite (es decir, cuando gastoAcctual > limite), no al alcanzarlo.


## PATRONES DE DISEÑO USADOS
Hemos utilizado varios patrones de diseño de los vistos en clase:
* **Patrón GRASP Controlador ->** La clase GestorDatos actúa como puente entre la interfaz de usuario y el dominio. Recibe las peticiones de la vista y llama a las operaciones necesarias.
* **Patrón Estrategia ->** Se utiliza para poder repartir los gastos de distintas formas dentro de una cuenta compartida. Por ejemplo, a la hora de calcular el porcentaje, la cuenta no sabe cómo se calcula, sino que delega en la estrategia para que lo haga.
* **Patrón Decorator para los filtros ->** Los filtros de gastos se implementan mediante el patrón Decorator, que permite combinar filtros sin cerar una combinación fija. Es decir, se pueden añadir nuevos filtros sin modificar los existentes.
* **Patrón Factoría ->** Utilizamos las factorías para decidir qué estrategia de distribución crear (Según el id) y para importar ficheros, ya que hay que seleccionar cómo importar según el tipo de archivo.
* **Patrón Singleton ->** Con él garantizamos tener una única instancia global. Se usa en GestorGastos.getInstancia(). Es importante porque no puede ser que en el sistema existan dos controladores de aplicación.


## MANUAL DE USUARIO
A continuación especificamos un manual de usuario para indicar el uso de la aplicación.
Comenzamos en la **ventana principal**. Al abrir la aplicación se crea automáticamente la cuenta Principal y se nos abre la ventana principal de la Cuenta.
![Ventana Principal](imagenes/Ventana_Principal.png)

Además de ver el listado de gastos de la cuenta Principal en formato de listado (o tabla) también podemos verlo como **gráfica de barras o como gráfica circular**. Para ello, pulsamos las pestañas Gráfica de barras o Gráfica circular respectivamente.
![Grafica Barras](imagenes/Grafica_Barras.png)
![Grafica Circular](imagenes/Grafica_Circular.png)

Para **crear un gasto nuevo en la cuenta Principal**, debemos pulsar el botón "Crear Gasto" que queda a la derecha del listado de gastos. A continuación, se debe pulsar "Gasto Nuevo". Se nos abrirá una pestaña en la que deberemos introducir los datos del gasto y pulsar "Aceptar".
![Crear Gasto](imagenes/Crear_Gasto.png)

Para **eliminar un gasto en la cuenta Principal**, debemos pulsar el botón "Eliminar Gasto" que queda a la derecha del listado de gastos. A continuación, se desplegará un listado con todos los gastos de la cuenta Principal, por lo que simplemente debemos pulsar el que deseemos eliminar.

Para **crear una nueva cuenta compartida**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Cuentas" -> "Crear Cuenta". Se nos abrirá una pestaña en la que podremos introducir los datos de esta nueva conta compartirla y pulsar "Crear Cuenta" para crearla.
![Crear Cuenta Compartida](imagenes/Crear_Cuenta_Compartida.png)

Para **eliminar una cuenta**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Cuentas" -> "Eliminar Cuenta". Se nos abrirá un desplegable con todas las cuentas existentes. Pulsamos la cuenta que deseamos eliminar.

Para calcular el **total de una cuenta**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Cuentas" -> "Total Cuenta". Se nos abrirá una pestaña en la que podremos seleccionar la cuenta de la que queramos saber el total.
![Total Cuenta](imagenes/Total_Cuenta.png)

Para **filtrar los gastos de la cuenta**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Gastos" -> "Filtrar Gastos". Se nos abrirá una pestaña en la que podremos seleccionar los filtros que queramos aplicar. Podremos pulsar "Aplicar filtros" o "Borrar selección"
![Filtrar Gasto](imagenes/Filtrar_Gasto.png)

Para **importar gastos desde un fichero externo**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Gastos" -> "Importar Gastos". Se nos abrirá una pestaña en la que podremos navegar por nuestro dispositivo y seleccionar el fichero de gastos que queramos importar.

Para **crear una categoría**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Categorias" -> "Crear Categoria". Se nos abrirá una pestaña en la que podremos introducir el nombre de la nueva categoría y crearla pulsando "Crear Categoria".
![Crear Categoria](imagenes/Crear_Categoria.png)

Para **eliminar una categoría**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Categorias" -> "Eliminar Categoria". Se nos abrirá un desplegable en el que podremos seleccionar el nombre de la cuenta que queramos eliminar.

Para **crear una alerta**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Alertas" -> "Crear Alerta". Se nos abrirá una pestaña en la que podremos establecer los datos de la alerta y crearla pulsando "Crear Alerta".
![Crear Alerta](imagenes/Crear_Alerta.png)

Para **eliminar una alerta**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Alertas" -> "Eliminar Alerta". Se nos abrirá un desplegable en el que podremos seleccionar el nombre de la alerta que queramos eliminar.

Para **ver las alertas activas**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Alertas" -> "Ver Alerta". Se nos abrirá una pestaña en la que podremos ver el listado de alertas activas. Aquí además se nos dará la opción de pulsar en una alerta y pulsar "Eliminar Alerta".
![Ver Alertas](imagenes/Ver_Alertas.png)

Para **ver el historial de notificaciones**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Notificaciones" -> "Historial". Se nos abrirá una pestaña en la que podremos marcar notificaciones como leídas o eliminar notificaciones del historial.
![Historial Notificaciones](imagenes/Historial_Notificaciones.png)

Para **ver una la ventana principal de una cuenta compartida** que ya tengamos creada, debemos pulsar la pestaña de dicha cuenta.
![Ventana Principal Cuenta Secundaria](imagenes/Ventana_Principal_Cuenta_Secundaria.png)

Para **crear un gasto en una cuenta compartida**, debemos pulsar el botón "Crear Gasto" que queda a la derecha del listado de gastos. A continuación, se debe pulsar "Gasto Nuevo". Se nos abrirá una pestaña en la que deberemos introducir los datos del gasto (esta vez, introduciendo también qué persona de la cuenta ha realizado el gasto) y pulsar "Aceptar".
![Crear Gasto Cuenta Compartida](imagenes/Crear_Gasto_Cuenta_Compartida.png)

Para **definir el porcentaje de gasto asumido por cada persona**, debemos pulsar el botón "Distribucion" -> "Personalizar" que queda a la derecha del listado de gastos de la cuenta compartida. Se nos abrirá una pestaña en la que podremos introducir el porcentaje de cada miembro y, pulsando "Aplicar", se define el nuevo porcentaje de gasto.
![Personalizar Distribucion Cuenta Compartida](imagenes/Personalizar_Distribucion_Cuenta_Compartida.png)

Para ver el **gasto por persona en una cuenta compartida**, debemos pulsar el botón "Saldo" -> "Por persona" que queda a la derecha del listado de gastos de la cuenta compartida. Se nos abrirá una pestaña en la que podremos ver el gasto por persona.
![Saldo Por Persona Cuenta Compartida](imagenes/Saldo_Por_Persona_Cuenta_Compartida.png)

Los gráficos tanto de barras como circular se pueden consultar de la misma manera que se hacía para la cuenta Principal, pulsando la pestaña "Gráfica de barras" y "Gráfica circular" respectivamente. Sin embargo, ahora podemos **ordenar por categoría o por persona los gastos de la cuenta compartida**.
Para esto, debemos marcar "Por persona" o "Por categoría" en el botón que queda a la derecha del listado de gastos de la cuenta compartida.
![DB_Por_Categoria](imagenes/DB_Por_Categoria.png)
![DB_Por_Persona](imagenes/DB_Por_Persona.png)
![DC_Por_Categoria](imagenes/DC_Por_Categoria.png)
![DC_Por_Persona](imagenes/DC_Por_Persona.png)

Para **salir de la aplicación**, deberemos ir a la barra de la parte superior de la aplicación. Una vez en ella, debemos pulsar "Salir". Se nos abrirá una pestaña en la que nos pregunta si queremos salir. Pulsamos "Aceptar" para salir.
![Salir](imagenes/Salir.png)

