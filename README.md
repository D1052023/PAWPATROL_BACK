# PAWPATROL_BACK
**Integrantes:**
- Juan Pablo Caballero Castellanos.
- Oscar Sanchez.
- Robinson Steven Nuñez.
- David Santiago Palacios.
- Diego Chavarro.

**Nombre De la Rama:**
`feature/proyecto_JuanCaballero_OscarSanchez_DiegoChavarro_RobinsonPortela_SantiagoPalacios_2025-2`

---
## Pruebas de ejecución (Proyecto).
**Maven**
![alt text](docs/imagenes/pruebaEjecucion.png)
![alt text](docs/imagenes/pruebaEjecucionJacoco.png)
![alt text](docs/imagenes/pruebaEjecucionSonarqube.png)

---

## Diagrama de contexto.
![alt text](docs/uml/diagramaContexto.drawio.png)

Se realizo el diagrama de Contexto identificando los autores y identificando como interactuan con el sistema de SIRHA

---

## Diagrama de casos de uso.
![alt text](docs/uml/diagramaCasos.drawio.png)

Se realizo el diagrama de casos de uso identificando todos los actores del sistema de horarios, se identifico lo que puede hacer cada uno y se relaciona con las acciones de otros actores

---

## Diagrama de componentes simple.
![alt text](docs/uml/diagramaCS.drawio.png)

Se realizo el diagrama identificando los componentes del sistema, identificando 2.

- Backend: Es la que recibe los datos de la base de datos de SIRHA y proporciona las APIs al otro componente
- Fronted web: Este componente no recibe ningun dato de la base de datos, solo se encarga de la parte visual del la web

---

## Diagrama de componentes especificos.
![alt text](docs/uml/diagramaCE.drawio.png)

Se realizo el diagrama identificando los subcomponentes que interactuan con el componente backend

- Apis: En su interior se observa todas las iteraciones para la creacion de las APIs.
- GestorMaterias: En su interior maneja todos los componentes que se reciben con la gestion de materias
- GestoAcademico: En su interior maneja todos los componentes que se reciben con la gestion de cada usuario
- Seguridad: En su interior maneja todos los componentes que se encargan de la seguridad del sistema
- GestorReportes: En su interior maneja todos los componentes que se encarga de la logica de los reportes del sistema
- GestorSolicitud: En su interior maneja todos los componentes que se reciben con la gestion de cada solucitud
- GestorNotificacion: En su interior maneja todos los componentes que se reciben con la gestion de cada notificacion

---

## Diagrama de clases.

![alt text](docs/uml/diagramaClases.drawio.png)

---

### **Patrones de diseño:**

---

#### **Observer**

Group actúa como el que notifica cambios cuando se llena o alcanza el 90% de capacidad, mientras que GroupObserverService reacciona a esas notificaciones generando alertas o guardando en la base de datos. Asi Group solo gestiona los cupos, y los observadores se encargan de las alertas.


#### **Factory Method**
Se uso ya que nos permitió evitar centralizar la lógica de la validación de los usurios ya que cada uno al entrar en la aplicación SIRHA navega y interactua solo con lo que es de su rol asi evitamos que se mezcle lo que un decano puede hacer y lo que puede hacer un estudiante dentro de la app.


---

### **Principios SOLID:**
---

#### **Single Responsability:**

- Student se encarga de modelar la informacion del estudiante y gestiona su horario de clases a través de la clase Schedule.

- Request maneja las solicitudes que hacen los estudiantes. Contiene tanto los datos del estudiante como de la facultad, con todo lo necesario para procesar cada una y utiliza HistoryEntry para llevar un registro del historial de cambios de cada solicitud.

- La clase Denary junto con su servicio se ocupan de las operaciones básicas CRUD de las solicitudes que reciben de los estudiantes.

- Group representa un grupo de asignatura y controla el número máximo de estudiantes que puede tener.


#### **Open/Closed:**
Podemos extender la clase abstracta de usuarios para incluir a mas tipo de estos ya que cada uno se logea de igual forma pero cambia lo que pueden
hacer dentro de la aplicación SIRHA, asi no modificamos user ya que es la clase en donde establecemos el método de validar el login para los otros usuarios.


#### **Interface Segregation Principle:**

Se diseño una interfaz concreta y con un unico metodo para establecer un contrato de alertas con Group ya que
implementa un observador que notifica si un grupo esta próximo o ya esta lleno.

#### **Dependency Inversion Principle:**

Group es un modulo de alto nivel por lo que no estamos dependiendo de los modulos de bajo nivel ya que
define el flujo usando y implementando la interfaz de observer asi trabaja con abstracciones no con clases concretas.


## Diagrama de secuencia.

![DiagramaSecuencia](docs/uml/secuenceDiagram.png)
Diagramas basados en casos de uso principales del sistema SIRHA:
- Login / Autenticación de usuario
- Gestión de usuarios (validar/crear usuario)
- Crear solicitud de reasignación (Application)
- Validar solicitud de reasignación
- Notificación del resultado
---

## Diagrama de Base de datos.

![DiagramaBases](docs/uml/DiagramaBasesDeDatos.png)

En este diagrama encontramos las tablas(relaciones) que vamos a necesitar para tener una base
de datos suficiente para cumplir con los requisitos y poder administrar de manera correcta la información necesaria.

Encontramos 8 tablas, las cuales son:
- Estudiante, guarda la información de un estudiante(id,carrera,semestre).
- Usuario, guarda la información de un usuario general(sin discriminar estudiante o decanatura)(id,nombre,correo_institucional,contraseña y rol).
- Decanatura, guarda la información de un usuario con el rol de decanatura(id, facultad).
- Materia, guarda la información sobre una materia en especifico(id, codigo, nombre y creditos).
- Grupo, encontramos la información sobre un grupo(de una materia), (id, id_materia,profesor,cupoMaximo y horario).
- Solicitud, es la tabla con más valores ya que contiene la información de una solicitud(id, id_estudiante,id_materia,grupo_origen,grupo_destino,estado,prioridad,fecha_creacion,observaciones).
- DecisiónSolicitud, aqui se guardará la información con respecto a las respuestas por parte de la decanatura a las solicitudes(id, id_solicitud, id_decanatura, fechaRespuesta, resultado y comentarios).
- Inscripcion, esta tabla nos permite llevar un control sobre las inscripciones que realiza y tiene un estudiante(id, id_estudiante, id_grupo, fechaInscripcion).

Con esta estructura nos aseguramos:
- Evitar redundancia de datos (no repetimos la información, usamos llaves foraneas).
- Mantener la integridad (las claves foráneas aseguran que una solicitud no se cree para un grupo inexistente)
- Flexibilidad (es un sistema con tendencia a crecer, sin necesidad de modificar lo que hay).
- Escalabilidad (Al estar normalizada, es más eficiente para operaciones CRUD y consultas).

---

## Implementación autenticación usuario - pruebas (Parte de evidencia)

![alt text](docs/imagenes/ImplementacioAutenticacion.png)

![alt text](docs/imagenes/PruebaAu.png)

## Endpoints y Swagger 

- SWAGGER UI:

![alt text](docs/imagenes/endpoints.jpeg)

- Prueba swagger utilizando h2:

![alt text](docs/imagenes/PruebaSwagger.png)

Se realizo la prueba para ver como respondia la base y la autenticación.

- Configuración Swagger:

![alt text](docs/imagenes/Swagger.png)

## Base de datos:

- Se logro implementar la base de datos MySQL en el proyecto, se crea dentro una base de datos sirha_dosw:

![alt text](docs/imagenes/ImplementarBase.png)

![alt text](docs/imagenes/ImplementarBase1.png)

- Hacemos pruebas en la autenticación de usuarios en swager UI y luego se actualiza la base de datos MySQL para ver si se ha enviado información:

![alt text](docs/imagenes/SwaggerBase.png)

- Se refresca la base de datos y miramos si se creo la tabla de usuarios con la prueba que hicimos:

![alt text](docs/imagenes/MySQL.jpeg)

![alt text](docs/imagenes/MySQL1.jpeg)

## Cobertura JACOCO y SonarQube

[alt text](docs/imagenes/EjecucionJacoco.jpeg)

![alt text](docs/imagenes/Jacoco.jpeg)

![alt text](docs/imagenes/Jacoco1.jpeg)

![alt text](docs/imagenes/EjecucionSonar.jpeg)

![alt text](docs/imagenes/Sonar.jpeg)

- Como se evidencia las pruebas cubren gran parte del proyecto verificando asi la funcionalidad y mantenimiento del código

## Tablero de JIRA

![alt text](docs/imagenes/Jira.jpeg)




