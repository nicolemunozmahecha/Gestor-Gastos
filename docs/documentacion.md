# DOCUMENTACIÓN DEL PROYECTO
### DIAGRAMA DE CLASES DEL DOMINO DEL PROYECTO
Diseñamos el siguiente diagrama de clases del dominio del proyecto:

![Diagrama de clases del dominio](imagenes/diagrama_clases.png)

En él se muestran las clases principales del dominio junto con sus relaciones dentr de la aplicación.


### ESPECIFICACIÓN DE LAS HISTORIAS DE USUARIO DEL PROYECTO
A continuación se muestran las historias de usuario del sistema:

**Historia de Usuario 1: Crear una cuenta personal**
- Como usuario,
- quiero crear una cuenta personal,
- para gestionar mis gastos.

**Historia de Usuario 2: Eliminar una cuenta**
- Como usuario,
- quiero eliminar una cuenta,
- para eliminar las cuentas que ya no necesito.

**Historia de Usuario 3: Crear una cuenta compartida**
- Como usuario,
- quiero crear una cuenta compartida con varias personas,
- para gestionar gastos comunes.

**Historia de Usuario 4: Añadir personas a una cuenta compartida**
- Como usuario,
- quiero añadir participantes a una cuenta compartida,
- para que puedan aparecer como que hacen un gasto.

**Historia de Usuario 5: Registrar un gasto**
- Como usuario,
- quiero registrar un gasto, utilizando su nombre, cantidad, fecha y categoría,
- para llevar un control de mis movimientos.

**Historia de Usuario 6: Registrar un gasto en una cuenta compartida**
- Como usuario,
- quiero registrar un gasto en una cuenta compartida, indicando quién lo ha pagado,
- para llevar un control de los movimientos de la cuenta.

**Historia de Usuario 7: Eliminar un gasto**
- Como usuario,
- quiero eliminar un gasto,
- para eliminar gastos incorrectos o que ya no quiero tener en cuenta.

**Historia de Usuario 8: Ver los gastos de una cuenta**
- Como usuario,
- quiero ver todos los gastos asociados a una cuenta,
- para poder ver el total de gastos que he realizado.

**Historia de Usuario 9: Ver categorías**
- Como usuario,
- quiero ver las categorías existentes,
- para poder ver las categorías predefinidas.

**Historia de Usuario 10: Crear una categoría personalizada**
- Como usuario,
- quiero crear nuevas categorías de gasto,
- para poder añadir nuevas categorías en las que clasificar mis gastos.

**Historia de Usuario 11: Eliminar una categoría**
- Como usuario,
- quiero eliminar una categoría,
- para poder eliminar categorías que ya no necesito.

**Historia de Usuario 12: Crear una alerta de gasto**
- Como usuario,
- quiero crear una alerta,
- para marcarme un límite de gastos semanal o mensual.

**Historia de Usuario 13: Crear una alerta para una categoría**
- Como usuario,
- quiero crear una alerta,
- para marcarme un límite de gastos en una categoría concreta.

**Historia de Usuario 14: Recibir una notificación al superar una alerta**
- Como usuario,
- quiero recibir una notificación cuando supere el límite de gastos de una alerta,
- para ser consciente de que he superado el límite que me puse.

**Historia de Usuario 15: Marcar notificaciones como leídas**
- Como usuario,
- quiero poder marcar mis notificaciones como leídas,
- para poder saber cuáles he leído y cuáles son nuevas.

**Historia de Usuario 16: Importar gastos desde un fichero**
- Como usuario,
- quiero importar gastos desde un fichero,
- para poder importar mis ficheros de gastos de golpe.

**Historia de Usuario 17: Filtrar gastos por fecha**
- Como usuario,
- quiero filtrar los gastos que hice entre dos fechas o por mes,
- para poder ver mis gastos en un periodo de tiempo.

**Historia de Usuario 18: Filtrar gastos por categoría**
- Como usuario,
- quiero filtrar mis gastos por categoría,
- para poder ver cuánto he gastado en una categoría en concreto.

**Historia de Usuario 19: Consultar el saldo de cada persona en una cuenta compartida**
- Como usuario,
- quiero ver el saldo de cada persona en una cuenta compartida,
- para poder ver cuánto debe o le deben a una persona de la cuenta compartida.


### DIAGRAMA DE INTERACCIÓN PARA UNA DE LAS HISTORIAS DE USUARIO
Hemos decidido hacer el diagrama de interacción de la Historia de Usuario 5: Registrar un gasto.

**Historia de Usuario 5: Registrar un gasto**
- Como usuario,
- quiero registrar un gasto, utilizando su nombre, cantidad, fecha y categoría,
- para llevar un control de mis movimientos.

A continuación se muestra su diagrama de interacción:
![Diagrama de interacción de la Historia de Usuario 5](imagenes/diagrama_interaccion.png)


### ARQUITECTURA DE LA APLICACIÓN
La aplicación la hemos diseñado con una arquitectura en capas, en la que se distinguen:
* **La interfaz de usuario (tds.vista)** -> Tenemos varias clases Controller (por ejemplo, CrearAlertaController o CrearCategoriaController), que se encargan de gestionar las ventanas, actuando como puente entre la interfaz y el dominio. 
* **La lógica de aplicación (tds.controlador)** -> Hemos creado un GestorGastos, el cuál actúa como controlador. Este se encarga de coordinar las operaciones que le solicite la interfaz.
* **El dominio (tds.modelo)** -> Contiene las interfaces de las entidades principales de la aplicación, que serían: Cuenta, CuentaCompartida, CuentaPersonal, Gasto, Categoría, Persona, Alerta, Notificacion, Filtro, EstrategiaAlerta, EstrategiaDistribucion y PeriodoAlerta.
Mediante EstrategiaAlertaFactory y EstrategiaDistribucionFactory tenemos factorías, que se encargan de ls creación de estrategias.
En /tds/modelo/impl tenemos las implementaciones de nuestras entidades.
* **La persistencia (tds.adapters.repository)** -> Mediante repositorios JSON se almacena toda la información en un único fichero, facilitando la gestión de datos.


### DECISIONES DE DISEÑO
