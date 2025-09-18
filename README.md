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
---
## Diagrama de contexto.
![alt text](docs/uml/diagramaContexto.drawio.png)

Se realizo el diagrama de Contexto identificando los autores y identificando como interactuan con el sistema de SIRHA

---
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