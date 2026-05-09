# Practica 4 - Conversion AFND a AFD, minimizacion y equivalencia

Este proyecto implementa una aplicacion academica para trabajar con automatas finitos. El sistema permite definir un AFND, convertirlo a AFD mediante construccion de subconjuntos, minimizar el AFD resultante y comprobar que los tres automatas reconocen el mismo lenguaje para una cadena ingresada.

La solucion esta dividida en:

- **Backend:** Java con Spring Boot.
- **Frontend:** React con Vite.
- **Imagenes:** diagramas PNG de los AFD minimizados.

## Objetivo

El proyecto realiza tres procesos principales:

1. **Conversion AFND a AFD:** se transforma un automata finito no determinista, incluyendo transiciones lambda cuando existen, en un automata finito determinista.
2. **Minimizacion del AFD:** se identifican estados equivalentes y se fusionan para obtener un AFD minimo.
3. **Verificacion de equivalencia:** se valida una cadena en el AFND original, el AFD construido y el AFD minimizado para comprobar que los tres aceptan o rechazan igual.

## Estructura Del Proyecto

```text
APE5_AFD/
|
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── automata/
│       │   │       ├── AutomatasApplication.java
│       │   │       ├── bioinformatica/
│       │   │       │   └── BioinformaticaAutomata.java
│       │   │       ├── telemetria/
│       │   │       │   └── TelemetriaAutomata.java
│       │   │       └── common/
│       │   │           ├── controller/
│       │   │           │   └── AutomataController.java
│       │   │           ├── model/
│       │   │           │   ├── Automata.java
│       │   │           │   ├── AutomataResult.java
│       │   │           │   ├── MinimizationResult.java
│       │   │           │   ├── Transition.java
│       │   │           │   ├── TransitionTable.java
│       │   │           │   └── ValidationResult.java
│       │   │           └── service/
│       │   │               ├── AutomataSimulatorService.java
│       │   │               ├── DfaMinimizationService.java
│       │   │               ├── EquivalenceVerificationService.java
│       │   │               └── SubsetConstructionService.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/
│               └── automata/
│                   ├── BioinformaticaAutomataTest.java
│                   └── TelemetriaAutomataTest.java
|
├── frontend/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   ├── public/
│   │   └── images/
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       ├── api/
│       │   └── automataApi.js
│       ├── components/
│       │   ├── AutomataSelector.jsx
│       │   ├── ChainInput.jsx
│       │   ├── DiagramViewer.jsx
│       │   ├── TransitionTable.jsx
│       │   └── ValidationResultCard.jsx
│       └── styles/
│           └── app.css
|
├── images/
│   ├── bioinformatica-afd-minimizado.png
│   └── telemetria-afd-minimizado.png
|
└── README.md
```

## Backend

El backend contiene la logica principal del proyecto. Esta organizado bajo el paquete raiz `automata`.

### Modelos

Los modelos se encuentran en:

```text
backend/src/main/java/automata/common/model/
```

- `Automata.java`: representa un automata con estados, alfabeto, estado inicial, estados finales, transiciones, subconjuntos y tipo.
- `Transition.java`: representa una transicion entre estados.
- `TransitionTable.java`: representa una fila de tabla de transiciones.
- `AutomataResult.java`: respuesta completa para el frontend con AFND, AFD, AFD minimizado y tablas.
- `MinimizationResult.java`: resultado de la minimizacion, incluyendo estados fusionados.
- `ValidationResult.java`: resultado de validar una cadena en los tres automatas.

### Servicios

Los servicios se encuentran en:

```text
backend/src/main/java/automata/common/service/
```

#### (a) Conversion AFND a AFD

La conversion se implementa en:

```text
SubsetConstructionService.java
```

Este servicio aplica construccion de subconjuntos:

- Calcula cerraduras lambda.
- Genera el estado inicial del AFD desde la cerradura del estado inicial del AFND.
- Crea estados del AFD como subconjuntos de estados del AFND.
- Construye las transiciones deterministas.
- Marca como finales los estados del AFD que contienen al menos un estado final del AFND.

#### (b) Minimizacion Del AFD

La minimizacion se implementa en:

```text
DfaMinimizationService.java
```

Este servicio:

- Divide estados finales y no finales.
- Refina particiones segun el comportamiento de cada estado.
- Detecta estados equivalentes.
- Fusiona estados equivalentes.
- Construye el AFD minimizado.

#### (c) Verificacion De Equivalencia

La verificacion se implementa en:

```text
EquivalenceVerificationService.java
```

Este servicio recibe una cadena y la evalua en:

- AFND original.
- AFD obtenido por subconjuntos.
- AFD minimizado.

Si los tres resultados coinciden, la respuesta marca `equivalentes: true`.

La simulacion individual de cada automata se realiza en:

```text
AutomataSimulatorService.java
```

Este servicio valida AFND con cerraduras lambda y AFD de forma determinista.

### Modulos De Automatas

Los automatas de cada ejercicio se definen en:

```text
backend/src/main/java/automata/telemetria/TelemetriaAutomata.java
backend/src/main/java/automata/bioinformatica/BioinformaticaAutomata.java
```

`TelemetriaAutomata.java` define el AFND del ejercicio de telemetria con alfabeto:

```text
{ r, h, t, c }
```

`BioinformaticaAutomata.java` define el AFND del ejercicio de bioinformatica con alfabeto:

```text
{ X, K, G, F }
```

### Controlador REST

El controlador se encuentra en:

```text
backend/src/main/java/automata/common/controller/AutomataController.java
```

Expone los endpoints:

```http
GET /api/automatas/{tipo}
POST /api/automatas/{tipo}/validar
```

Valores validos para `{tipo}`:

```text
telemetria
bioinformatica
```

### Obtener Informacion Del Automata

```http
GET /api/automatas/telemetria
```

Devuelve:

- AFND original.
- AFD construido.
- AFD minimizado.
- Tabla de transiciones del AFD.
- Tabla de transiciones del AFD minimizado.
- Estados fusionados.

### Validar Una Cadena

```http
POST /api/automatas/telemetria/validar
```

Cuerpo:

```json
{
  "cadena": "rhtc"
}
```

Respuesta:

```json
{
  "cadena": "rhtc",
  "resultadoAfnd": "Acepta",
  "resultadoAfd": "Acepta",
  "resultadoAfdMinimizado": "Acepta",
  "equivalentes": true
}
```

## Frontend

El frontend permite interactuar con el backend desde una interfaz web.

Componentes principales:

- `AutomataSelector.jsx`: selecciona entre telemetria y bioinformatica.
- `ChainInput.jsx`: permite ingresar una cadena.
- `ValidationResultCard.jsx`: muestra si cada automata acepta o rechaza.
- `TransitionTable.jsx`: muestra tablas del AFD y AFD minimizado.
- `DiagramViewer.jsx`: muestra el diagrama PNG del AFD minimizado.

El frontend consume el backend desde:

```text
frontend/src/api/automataApi.js
```

## Imagenes

Las imagenes originales se encuentran en:

```text
images/
```

Las imagenes servidas por Vite se encuentran en:

```text
frontend/public/images/
```

Si se cambian las imagenes en `images/`, deben copiarse tambien al frontend:

```powershell
Copy-Item -Path images\*.png -Destination frontend\public\images -Force
```

## Como Ejecutar

### Backend

```powershell
cd backend
mvn spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8080
```

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

El frontend queda disponible en:

```text
http://127.0.0.1:5173/
```

## Como Probar

### Tests Del Backend

```powershell
cd backend
mvn test
```

### Build Del Frontend

```powershell
cd frontend
npm run build
```

### Prueba Manual De API

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8080/api/automatas/telemetria/validar `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"cadena":"rhtc"}'
```

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8080/api/automatas/bioinformatica/validar `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"cadena":"KGXF"}'
```

## Criterios Cumplidos

- Se define un AFND para telemetria.
- Se define un AFND para bioinformatica.
- Se convierte cada AFND a AFD.
- Se minimiza el AFD obtenido.
- Se valida una cadena en AFND, AFD y AFD minimizado.
- Se verifica equivalencia entre los tres automatas para la cadena ingresada.
- Se muestran tablas de transicion.
- Se muestran diagramas de los AFD minimizados.
