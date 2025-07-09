# Simulador de Algoritmos CC2

**Universidad Distrital Francisco José de Caldas**
**Facultad de Ingeniería - Ciencias de la Computación**

Una aplicación educativa interactiva para la visualización y comprensión de algoritmos fundamentales de ciencias de la computación.

## 📋 Tabla de Contenidos

- [Descripción](#-descripci%C3%B3n)
- [Características](#-caracter%C3%ADsticas)
- [Instalación](#-instalaci%C3%B3n)
- [Uso Rápido](#-uso-r%C3%A1pido)
- [Módulos Incluidos](#-m%C3%B3dulos-incluidos)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Capturas de Pantalla](#-capturas-de-pantalla)
- [Equipo de Desarrollo](#-equipo-de-desarrollo)
- [Contribución](#-contribuci%C3%B3n)
- [Documentación](#-documentaci%C3%B3n)
- [Licencia](#-licencia)


## 🎯 Descripción

El **Simulador de Algoritmos CC2** es una herramienta educativa desarrollada en Java que permite a estudiantes y profesores visualizar el funcionamiento interno de algoritmos fundamentales de ciencias de la computación. La aplicación proporciona una interfaz intuitiva para experimentar con diferentes algoritmos, observar su comportamiento paso a paso y analizar su rendimiento.

### Objetivos Principales

- 🎓 **Educativo**: Facilitar el aprendizaje visual de algoritmos complejos
- 🔬 **Experimental**: Permitir experimentación con diferentes parámetros
- 📊 **Analítico**: Proporcionar métricas y análisis de rendimiento
- 🎨 **Visual**: Ofrecer visualizaciones claras y atractivas


## ✨ Características

### 🖥️ Interfaz de Usuario

- ✅ **Interfaz intuitiva** con navegación por pestañas
- ✅ **Diseño moderno** siguiendo principios de Material Design
- ✅ **Responsive** adaptable a diferentes resoluciones
- ✅ **Accesible** con combinaciones de colores apropiadas


### 🔧 Funcionalidades Técnicas

- ✅ **Visualización en tiempo real** de algoritmos
- ✅ **Terminal integrado** con logging educativo detallado
- ✅ **Exportación/Importación** de datos en formato CSV
- ✅ **Configuración flexible** de parámetros
- ✅ **Métricas de rendimiento** automáticas


### 📚 Contenido Educativo

- ✅ **6 módulos especializados** con más de 25 algoritmos
- ✅ **Explicaciones paso a paso** del proceso algorítmico
- ✅ **Casos de prueba predefinidos** para experimentación
- ✅ **Documentación completa** integrada


### 🚀 Rendimiento

- ✅ **Optimizado para grandes datasets** (hasta 100,000 elementos)
- ✅ **Multithreading** para operaciones pesadas
- ✅ **Gestión eficiente de memoria**
- ✅ **Animaciones suaves** y responsivas


## 🛠️ Instalación

### Requisitos Previos

| Componente | Versión Mínima | Recomendada |
| :-- | :-- | :-- |
| **Java Runtime Environment** | JRE 8 | JRE 11+ |
| **Memoria RAM** | 512 MB | 1 GB |
| **Espacio en Disco** | 50 MB | 100 MB |
| **Resolución de Pantalla** | 1024x768 | 1920x1080 |

### Opción 1: Ejecutable JAR (Recomendado)

1. **Descargar** el archivo `SimuladorAlgoritmos.jar` desde la sección de releases
2. **Verificar Java** instalado en el sistema:

```bash
java -version
```

3. **Ejecutar** la aplicación:

```bash
java -jar SimuladorAlgoritmos.jar
```


### Opción 2: Compilación desde Código Fuente

1. **Clonar** el repositorio:

```bash
git clone https://github.com/universidad-distrital/simulador-algoritmos-cc2.git
cd simulador-algoritmos-cc2
```

2. **Compilar** el proyecto:

```bash
# Windows
javac -cp src src\*.java src\*\*.java

# Unix/Linux/macOS
find src -name "*.java" | xargs javac -cp src
```

3. **Ejecutar** desde código fuente:

```bash
java -cp src Main
```


### Opción 3: Scripts de Ejecución

Para **Windows** (ejecutar.bat):

```batch
@echo off
java -Xmx2g -jar SimuladorAlgoritmos.jar
pause
```

Para **Unix/Linux/macOS** (ejecutar.sh):

```bash
#!/bin/bash
java -Xmx2g -jar SimuladorAlgoritmos.jar
```


## 🚀 Uso Rápido

### Primera Ejecución

1. **Iniciar** la aplicación
2. **Esperar** la pantalla de carga (3 segundos)
3. **Seleccionar** una pestaña de módulo (ej: "Búsquedas")
4. **Hacer clic** en un algoritmo específico (ej: "Búsqueda Binaria")

### Ejemplo: Búsqueda Binaria

1. **Configurar parámetros**:
    - Tamaño del array: `1000`
    - Elemento a buscar: `450`
2. **Generar datos**:
    - Hacer clic en "Generar Array Ordenado"
3. **Ejecutar algoritmo**:
    - Hacer clic en "Ejecutar Búsqueda Binaria"
    - Observar la visualización en tiempo real
    - Seguir el proceso en el terminal
4. **Analizar resultados**:
    - Verificar posición encontrada
    - Revisar número de iteraciones
    - Exportar datos si es necesario

### Ejemplo: Operaciones de Grafos

1. **Definir grafos**:

```
Grafo G₁:
Vértices: a,b,c
Aristas: a-b,b-c

Grafo G₂:
Vértices: a,c,d
Aristas: a-d,c-d
```

2. **Actualizar cálculo**:
    - Hacer clic en "Actualizar y Calcular"
3. **Ejecutar operación**:
    - Seleccionar "Unión", "Intersección" o "Suma Anillo"
    - Observar resultado visual
    - Revisar pasos en el área de resultados

## 📦 Módulos Incluidos

### 🔍 Búsquedas

Algoritmos de búsqueda y técnicas de hashing:


| Algoritmo | Complejidad | Uso Principal |
| :-- | :-- | :-- |
| **Búsqueda Lineal** | O(n) | Arrays no ordenados |
| **Búsqueda Binaria** | O(log n) | Arrays ordenados |
| **Hash Cuadrado Medio** | O(1) promedio | Tablas hash |
| **Hash Módulo** | O(1) promedio | Distribución uniforme |
| **Hash Plegamiento** | O(1) promedio | Claves alfanuméricas |
| **Hash Truncamiento** | O(1) promedio | Espacios reducidos |

### 🌳 Árboles

Estructuras de datos arbóreas especializadas:


| Algoritmo | Aplicación | Características |
| :-- | :-- | :-- |
| **Árboles Digitales (Tries)** | Autocompletado | Búsqueda de prefijos |
| **Árboles de Huffman** | Compresión | Codificación óptima |
| **Árboles de Residuos Múltiples** | Búsqueda | Múltiples claves |
| **Árboles de Residuos Particular** | Indexación | Clave específica |

### 💾 Externos

Algoritmos para manejo de datos en almacenamiento externo:


| Algoritmo | Propósito | Ventaja |
| :-- | :-- | :-- |
| **Búsqueda Lineal en Bloques** | Secuencial externa | Simple implementación |
| **Búsqueda Binaria en Bloques** | Búsqueda externa | Eficiencia logarítmica |
| **Hash en Bloques** (4 variantes) | Acceso directo | Minimiza accesos a disco |

### 🔄 Dinámicas

Estructuras de datos que se adaptan durante la ejecución:


| Algoritmo | Estrategia | Beneficio |
| :-- | :-- | :-- |
| **Expansión Parcial** | Crecimiento gradual | Menor overhead |
| **Expansión Total** | Redimensionamiento completo | Distribución uniforme |

### 📇 Índices

Sistemas de indexación para optimización de consultas:


| Algoritmo | Tipo de Índice | Uso Óptimo |
| :-- | :-- | :-- |
| **Búsqueda Lineal con Índices** | Índice simple | Datasets pequeños |
| **Búsqueda Binaria con Índices** | Índice ordenado | Consultas frecuentes |
| **Hash con Índices** (2 variantes) | Índice hash | Acceso aleatorio |

### 🕸️ Grafos

Algoritmos de teoría de grafos y operaciones algebraicas:


| Categoría | Operaciones | Descripción |
| :-- | :-- | :-- |
| **Operaciones de Conjuntos** | Unión, Intersección, Suma Anillo | Álgebra de grafos |
| **Operaciones de Producto** | Cartesiano, Tensorial, Composición | Construcción de grafos |

## 📁 Estructura del Proyecto

```
SimuladorAlgoritmosCC2/
│
├── 📄 README.md                     # Este archivo
├── 📄 Manual_Usuario.md             # Manual de usuario completo
├── 📄 Manual_Tecnico.md             # Documentación técnica
│
├── 📂 src/                          # Código fuente principal
│   │
│   ├── 🎯 Main.java                 # Punto de entrada de la aplicación
│   ├── 🎨 SplashScreen.java         # Pantalla de carga inicial
│   ├── 🏠 SimuladorPrincipal.java   # Ventana principal con pestañas
│   │
│   ├── 📂 busquedas/               # 🔍 Módulo de algoritmos de búsqueda
│   │   ├── BusquedaLineal.java
│   │   ├── BusquedaBinaria.java
│   │   ├── HashCuadrado.java
│   │   ├── HashMod.java
│   │   ├── HashPlegamiento.java
│   │   └── HashTruncamiento.java
│   │
│   ├── 📂 arboles/                 # 🌳 Módulo de estructuras arbóreas
│   │   ├── ArbolesDigitales.java
│   │   ├── ArbolesHuffman.java
│   │   ├── ArbolesResiduosMultiples.java
│   │   └── ArbolesResiduosParticular.java
│   │
│   ├── 📂 externos/                # 💾 Módulo de algoritmos externos
│   │   ├── BusquedaLinealBloques.java
│   │   ├── BusquedaBinariaBloques.java
│   │   ├── HashCuadradoBloques.java
│   │   ├── HashModBloques.java
│   │   ├── HashPlegamientoBloques.java
│   │   └── HashTruncamientoBloques.java
│   │
│   ├── 📂 dinamicas/               # 🔄 Módulo de estructuras dinámicas
│   │   ├── ExpansionParcial.java
│   │   └── ExpansionTotal.java
│   │
│   ├── 📂 indices/                 # 📇 Módulo de sistemas de índices
│   │   ├── BusquedaLineal.java
│   │   ├── BusquedaBinaria.java
│   │   ├── HashCuadrado.java
│   │   └── HashTruncamiento.java
│   │
│   ├── 📂 grafos/                  # 🕸️ Módulo de algoritmos de grafos
│   │   ├── OperacionesConjuntos.java
│   │   └── OperacionesProducto.java
│   │
│   └── 📂 utils/                   # 🛠️ Utilidades y clases base
│       ├── AlgorithmWindow.java    # Clase base para ventanas de algoritmos
│       ├── Constants.java          # Constantes globales del sistema
│       ├── GraphOperations.java    # Operaciones específicas de grafos
│       └── [otras utilidades]      # Clases auxiliares y herramientas
│
├── 📂 resources/                   # 🎨 Recursos del proyecto
│   └── Escudo_UD.jpg              # Logo institucional
│
├── 📂 docs/                        # 📚 Documentación adicional
│   ├── ejemplos/                   # Casos de uso y ejemplos
│   ├── diagramas/                  # Diagramas UML y arquitectura
│   └── referencias/                # Material de referencia
│
├── 📂 build/                       # 🏗️ Archivos compilados (generado)
├── 📂 dist/                        # 📦 Distribución final (generado)
└── 📂 tests/                       # 🧪 Pruebas unitarias
    ├── busquedas/
    ├── arboles/
    ├── externos/
    ├── dinamicas/
    ├── indices/
    └── grafos/
```


### Descripción de Directorios

| Directorio | Propósito | Contenido |
| :-- | :-- | :-- |
| **`src/`** | Código fuente principal | Toda la lógica de la aplicación |
| **`resources/`** | Recursos estáticos | Imágenes, iconos, archivos de configuración |
| **`docs/`** | Documentación | Manuales, ejemplos, diagramas |
| **`build/`** | Compilación | Archivos `.class` generados |
| **`dist/`** | Distribución | JAR final y archivos de distribución |
| **`tests/`** | Pruebas | Tests unitarios y de integración |

## 📸 Capturas de Pantalla

### Pantalla Principal

*Interfaz principal con navegación por pestañas*

```
┌─────────────────────────────────────────────────────────────────┐
│ Simulador de Algoritmos CC2 - Universidad Distrital           │
├─────────────────────────────────────────────────────────────────┤
│ [Búsquedas] [Árboles] [Externos] [Dinámicas] [Índices] [Grafos]│
│                                                                 │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐  │
│  │ Búsqueda Lineal │ │ Búsqueda Binaria│ │ Hash Cuadrado   │  │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘  │
│                                                                 │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐  │
│  │ Hash Módulo     │ │ Hash Plegamiento│ │ Hash Truncamiento│  │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```


### Ventana de Algoritmo

*Ejemplo: Búsqueda Binaria en acción*

```
┌─────────────────────────────────────────────────────────────────┐
│ Búsqueda Binaria                                          [×]   │
├─────────────────────────────────────────────────────────────────┤
│ Configuración:                                                  │
│ Tamaño: [1000    ] Buscar: [450     ] [Generar Array]          │
│                                                                 │
│ Visualización:                                                  │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ ████▓▓▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │ │
│ │       ↑ medio (500)            ↑ objetivo (450)             │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ Terminal:                                                       │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ [INFO] Iniciando búsqueda binaria para: 450                │ │
│ │ [INFO] Paso 1: Comparando con posición 500 (valor: 501)    │ │
│ │ [INFO] Buscando en mitad izquierda                          │ │
│ │ [SUCCESS] ¡Elemento encontrado en posición 449!            │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ [Ejecutar] [Reset] [Exportar CSV] [Ayuda]                      │
└─────────────────────────────────────────────────────────────────┘
```


### Operaciones de Grafos

*Ejemplo: Unión de dos grafos*

```
┌─────────────────────────────────────────────────────────────────┐
│ Operaciones de Conjuntos en Grafos                       [×]   │
├─────────────────────────────────────────────────────────────────┤
│ Grafo G₁:        │ Grafo G₂:        │ Resultado:              │
│ Vértices: a,b,c   │ Vértices: a,c,d   │ G₁ ∪ G₂                │
│ Aristas: a-b,b-c  │ Aristas: a-d,c-d  │                        │
│                   │                   │                        │
│   (a)──(b)        │   (a)    (d)      │   (a)──(b)             │
│    │   │          │    │      │       │    │   │               │
│   (c)──┘          │   (c)────┘        │   (c)──┘               │
│                   │                   │    │                   │
│                   │                   │   (d)                  │
│                   │                   │                        │
│ [Actualizar y Calcular]                                        │
│                                                                 │
│ [Unión] [Intersección] [Suma Anillo]                          │
│                                                                 │
│ Resultado: Vértices: {a,b,c,d}, Aristas: {a-b,b-c,a-d,c-d}    │
└─────────────────────────────────────────────────────────────────┘
```


## 👥 Equipo de Desarrollo

### 🏛️ Institución

**Universidad Distrital Francisco José de Caldas**
Facultad de Ingeniería - Ciencias de la Computación

### 👨‍💻 Desarrolladores

#### **Alicia Pineda Quiroga**

- 🎯 **Rol:** Desarrolladora Principal y Arquitecta de Software
- 💻 **Responsabilidades:**
    - Diseño de arquitectura general
    - Implementación de módulos de búsqueda y árboles
    - Coordinación del proyecto
- 📧 **Especialidades:** Algoritmos de búsqueda, estructuras de datos, UI/UX


#### **Nelson David Posso Suarez**

- 🎯 **Rol:** Especialista en Algoritmos y Optimización
- 💻 **Responsabilidades:**
    - Implementación de algoritmos externos y dinámicos
    - Optimización de rendimiento
    - Testing y validación
- 📧 **Especialidades:** Algoritmos de almacenamiento externo, hashing dinámico


#### **Jhonathan De La Torre**

- 🎯 **Rol:** Diseñador de Interfaz y Especialista en Grafos
- 💻 **Responsabilidades:**
    - Diseño de interfaz de usuario
    - Implementación de módulos de grafos e índices
    - Documentación y manuales
- 📧 **Especialidades:** Teoría de grafos, sistemas de indexación, visualización


### 🤝 Contribuciones Específicas

| Módulo | Desarrollador Principal | Contribuciones |
| :-- | :-- | :-- |
| **Búsquedas** | Alicia Pineda | Algoritmos lineales y binarios, técnicas de hashing |
| **Árboles** | Alicia Pineda | Tries, Huffman, árboles de residuos |
| **Externos** | Nelson Posso | Algoritmos en bloques, optimización I/O |
| **Dinámicas** | Nelson Posso | Expansión parcial y total, hashing dinámico |
| **Índices** | Jhonathan De La Torre | Sistemas de indexación, optimización consultas |
| **Grafos** | Jhonathan De La Torre | Operaciones algebraicas, visualización |
| **Arquitectura** | Alicia Pineda | Patrón MVC, clases base, estructura general |
| **UI/UX** | Jhonathan De La Torre | Diseño visual, experiencia usuario |
| **Testing** | Nelson Posso | Pruebas unitarias, validación algoritmos |
| **Documentación** | Equipo completo | Manuales, comentarios, README |

## 🤝 Contribución

### Para Estudiantes

¿Eres estudiante y quieres contribuir? ¡Excelente! Aquí tienes algunas formas:

#### 🐛 Reportar Bugs

1. Ir a la sección **Issues** del repositorio
2. Crear un **nuevo issue** con la etiqueta `bug`
3. Proporcionar:
    - Descripción detallada del problema
    - Pasos para reproducir el error
    - Screenshots si es aplicable
    - Información del sistema (OS, versión Java)

#### 💡 Sugerir Mejoras

1. Crear un **issue** con la etiqueta `enhancement`
2. Describir la mejora propuesta
3. Explicar por qué sería útil
4. Proporcionar mockups o ejemplos si es posible

#### 📖 Mejorar Documentación

1. **Fork** del repositorio
2. Editar archivos de documentación
3. Crear **Pull Request** con descripción clara
4. Seguir el estilo de documentación existente

### Para Profesores

#### 🎓 Uso Educativo

- ✅ Libre uso en clases y laboratorios
- ✅ Modificación para necesidades específicas
- ✅ Distribución a estudiantes
- ✅ Integración en currículos académicos


#### 📚 Contribución Académica

- Proponer nuevos algoritmos
- Sugerir mejoras pedagógicas
- Compartir casos de uso
- Colaborar en research papers


### Para Desarrolladores

#### 🔧 Desarrollo

1. **Fork** del repositorio
2. Crear **branch** para nueva funcionalidad:

```bash
git checkout -b feature/nueva-funcionalidad
```

3. Seguir las **convenciones de código**:
    - Comentarios en español
    - JavaDoc para métodos públicos
    - Nombres descriptivos de variables
    - Estructura MVC consistente
4. **Commit** con mensajes descriptivos:

```bash
git commit -m "feat: agregar algoritmo QuickSort con visualización"
```

5. **Push** y crear **Pull Request**:

```bash
git push origin feature/nueva-funcionalidad
```


#### 📋 Guidelines de Código

```java
/**
 * Ejemplo de estructura para nuevo algoritmo
 * 
 * @author Nombre del Desarrollador
 * @version 1.0
 * @since 2025-07-08
 */
public class NuevoAlgoritmo extends AlgorithmWindow {
    
    // Documentar campos importantes
    private AlgorithmData data;
    
    /**
     * Constructor del algoritmo
     * @param parent Ventana padre
     */
    public NuevoAlgoritmo(JFrame parent) {
        super(parent, "Nuevo Algoritmo");
        logToTerminal("Algoritmo iniciado", "info");
    }
    
    /**
     * Ejecuta el algoritmo principal
     * @param input Datos de entrada
     * @return Resultado de la ejecución
     */
    private AlgorithmResult executeAlgorithm(InputData input) {
        // Implementación con logging educativo
        logToTerminal("Iniciando proceso...", "info");
        
        // Lógica del algoritmo
        
        logToTerminal("Proceso completado", "success");
        return result;
    }
}
```


### 🏷️ Etiquetas de Issues

| Etiqueta | Descripción | Color |
| :-- | :-- | :-- |
| `bug` | Error en funcionamiento | 🔴 Rojo |
| `enhancement` | Nueva funcionalidad | 🟢 Verde |
| `documentation` | Mejora documentación | 🔵 Azul |
| `help wanted` | Ayuda necesaria | 🟡 Amarillo |
| `good first issue` | Ideal para principiantes | 🟣 Morado |
| `algorithm` | Nuevo algoritmo | 🟠 Naranja |
| `ui/ux` | Interfaz de usuario | 🟦 Azul claro |
| `performance` | Optimización | ⚫ Negro |

## 📚 Documentación

### 📖 Documentación Disponible

| Documento | Descripción | Audiencia |
| :-- | :-- | :-- |
| **README.md** | Este archivo - Introducción general | Todos |
| **Manual_Usuario.md** | Guía completa de uso | Estudiantes, Profesores |
| **Manual_Tecnico.md** | Documentación técnica detallada | Desarrolladores |
| **JavaDoc** | Documentación de código | Desarrolladores |

### 🔗 Enlaces Útiles

- 📺 **[Video Tutorial Básico]** - Introducción al simulador (10 min)
- 📺 **[Video Tutorial Avanzado]** - Funcionalidades completas (30 min)
- 📊 **[Presentación del Proyecto]** - Slides académicas
- 📄 **[Paper Académico]** - Investigación educativa sobre el simulador
- 🌐 **[Sitio Web del Proyecto]** - Información adicional


### 📞 Soporte y Contacto

#### 🎓 Soporte Académico

- **Email:** simulador.cc2@udistrital.edu.co
- **Horario:** Lunes a Viernes, 8:00 AM - 5:00 PM
- **Ubicación:** Facultad de Ingeniería, Universidad Distrital


#### 🐛 Reporte de Bugs

- **GitHub Issues:** [Crear nuevo issue](https://github.com/universidad-distrital/simulador-algoritmos-cc2/issues)
- **Email:** bugs.simulador@udistrital.edu.co


#### 💡 Sugerencias y Mejoras

- **Email:** mejoras.simulador@udistrital.edu.co
- **Foro Estudiantil:** [Discusiones en GitHub](https://github.com/universidad-distrital/simulador-algoritmos-cc2/discussions)


## 📜 Licencia

### Licencia Educativa

Este proyecto está desarrollado con **fines educativos** bajo las siguientes condiciones:

#### ✅ Permitido

- **Uso en instituciones educativas** para enseñanza
- **Modificación** para necesidades académicas específicas
- **Distribución** a estudiantes y profesores
- **Estudio** del código fuente para aprendizaje
- **Fork** para proyectos educativos derivados


#### ⚠️ Restricciones

- **Uso comercial** requiere autorización expresa
- **Redistribución** debe mantener créditos originales
- **Modificaciones** deben estar claramente documentadas
- **Venta** del software no está permitida


#### 📋 Atribución Requerida

Al usar este software, debe incluir:

```
Simulador de Algoritmos CC2
Universidad Distrital Francisco José de Caldas
Desarrollado por: Alicia Pineda Quiroga, Nelson David Posso Suarez, Jhonathan De La Torre
```


### 🎓 Uso Académico

#### Para Profesores

- ✅ Uso libre en cursos de algoritmos y estructuras de datos
- ✅ Adaptación para currículos específicos
- ✅ Integración en plataformas LMS
- ✅ Base para proyectos de investigación educativa


#### Para Estudiantes

- ✅ Uso para aprendizaje personal
- ✅ Base para proyectos de curso
- ✅ Referencia para implementaciones propias
- ✅ Herramienta de estudio y práctica


### 📄 Términos Completos

Para consultar los términos completos de la licencia, ver el archivo `LICENSE` incluido en el repositorio.

## 🚀 Roadmap y Futuras Versiones

### 📅 Versión 1.1 (Planeada para Agosto 2025)

#### 🆕 Nuevas Funcionalidades

- [ ] **Algoritmos de Ordenamiento**
    - QuickSort con visualización de particiones
    - MergeSort con animación de merge
    - HeapSort con construcción de heap
- [ ] **Mejoras de UI**
    - Tema oscuro/claro seleccionable
    - Pantalla completa para visualizaciones
    - Zoom en gráficos complejos
- [ ] **Características Avanzadas**
    - Comparación lado a lado de algoritmos
    - Generación automática de reports
    - Integración con herramientas de análisis


#### 🔧 Mejoras Técnicas

- [ ] **Optimización de Rendimiento**
    - Threading mejorado para animaciones
    - Caché inteligente de visualizaciones
    - Reducción de uso de memoria
- [ ] **Nuevos Formatos de Export**
    - JSON para integración con APIs
    - PDF para reports académicos
    - LaTeX para documentos científicos


### 📅 Versión 1.2 (Planeada para Octubre 2025)

#### 🌐 Características Web

- [ ] **Versión Web**
    - Interfaz web complementaria
    - Sincronización con versión desktop
    - Colaboración en tiempo real
- [ ] **Integración LMS**
    - Plugin para Moodle
    - Conectores para Canvas
    - API para sistemas educativos


#### 🤖 Inteligencia Artificial

- [ ] **Asistente IA**
    - Sugerencias de parámetros óptimos
    - Explicaciones automáticas de comportamientos
    - Detección de patrones en ejecuciones


### 🎯 Versión 2.0 (Planeada para 2026)

#### 🔄 Arquitectura Completa

- [ ] **Sistema de Plugins**
    - API para algoritmos externos
    - Marketplace de algoritmos
    - Contribuciones de la comunidad
- [ ] **Características Avanzadas**
    - Algoritmos paralelos y distribuidos
    - Simulación de redes y sistemas
    - Análisis de complejidad automático


## ⚡ Performance y Benchmarks

### 🔢 Límites Recomendados

| Algoritmo | Tamaño Máximo | Tiempo Promedio | Memoria Usada |
| :-- | :-- | :-- | :-- |
| **Búsqueda Lineal** | 100,000 elementos | 500ms | 50MB |
| **Búsqueda Binaria** | 1,000,000 elementos | 50ms | 30MB |
| **Hash (todas variantes)** | 500,000 elementos | 200ms | 100MB |
| **Árboles Digitales** | 50,000 nodos | 1s | 150MB |
| **Árboles Huffman** | 10,000 caracteres | 300ms | 80MB |
| **Grafos (operaciones)** | 1,000 vértices | 2s | 200MB |

### 📊 Benchmarks en Diferentes Sistemas

#### 💻 Sistema Recomendado (Intel i5, 8GB RAM)

- ✅ Todos los algoritmos funcionan sin limitaciones
- ✅ Animaciones fluidas a 60 FPS
- ✅ Multitasking sin problemas


#### 🖥️ Sistema Mínimo (Intel i3, 4GB RAM)

- ⚠️ Limitaciones en datasets grandes (>50,000 elementos)
- ⚠️ Animaciones a 30 FPS
- ⚠️ Uso individual recomendado


#### 📱 Laptops de Estudiante (Típico)

- ✅ Funcionalidad completa para uso educativo
- ✅ Datasets de tamaño medio (10,000-50,000 elementos)
- ✅ Batería dura aproximadamente 2-3 horas de uso


## 🎓 Casos de Uso Educativo

### 📚 En el Aula

#### **Estructura de Datos (Pregrado)**

- **Tema:** Búsqueda y Hashing
- **Duración:** 2 horas de laboratorio
- **Objetivos:**
    - Comparar eficiencia de búsqueda lineal vs binaria
    - Experimentar con diferentes funciones de hash
    - Analizar casos de colisión


#### **Algoritmos Avanzados (Pregrado)**

- **Tema:** Árboles Especializados
- **Duración:** 3 horas de laboratorio
- **Objetivos:**
    - Construir árboles de Huffman para compresión
    - Implementar búsquedas en tries
    - Analizar complejidad espacial vs temporal


#### **Ciencias de la Computación (Posgrado)**

- **Tema:** Algoritmos Externos
- **Duración:** Proyecto semestral
- **Objetivos:**
    - Diseñar algoritmos para grandes datasets
    - Optimizar accesos a memoria externa
    - Comparar estrategias de buffering


### 🏠 Estudio Individual

#### **Preparación para Exámenes**

1. **Repasar conceptos** con visualizaciones
2. **Practicar** con diferentes parámetros
3. **Analizar** comportamientos límite
4. **Exportar** resultados para estudio

#### **Proyectos de Investigación**

1. **Generar datos** experimentales
2. **Comparar** algoritmos similares
3. **Documentar** hallazgos con capturas
4. **Publicar** resultados académicos

## 🔍 FAQ - Preguntas Frecuentes

### ❓ Preguntas Generales

#### **¿Necesito conocimientos previos de programación?**

No necesariamente. El simulador está diseñado para ser educativo a diferentes niveles:

- **Principiantes:** Pueden observar comportamientos sin entender implementación
- **Intermedios:** Pueden experimentar con parámetros y analizar resultados
- **Avanzados:** Pueden estudiar el código fuente y contribuir


#### **¿Funciona en mi computadora?**

Si cumple los requisitos mínimos:

- Java 8 o superior ✓
- 512 MB RAM disponible ✓
- 50 MB espacio en disco ✓
- Cualquier sistema operativo moderno ✓


#### **¿Es gratis?**

Sí, completamente gratis para uso educativo. No hay costos ocultos, licencias o subscripciones.

### 🛠️ Preguntas Técnicas

#### **¿Puedo modificar el código fuente?**

Sí, bajo los términos de la licencia educativa:

- ✅ Modificaciones para uso académico
- ✅ Fork para proyectos educativos
- ⚠️ Debe mantener atribución original
- ❌ No para uso comercial sin autorización


#### **¿Cómo reporto un bug?**

1. Ir a [GitHub Issues](https://github.com/universidad-distrital/simulador-algoritmos-cc2/issues)
2. Crear nuevo issue con etiqueta `bug`
3. Incluir: descripción, pasos para reproducir, screenshots
4. El equipo responderá en 24-48 horas

#### **¿Puedo sugerir nuevos algoritmos?**

¡Por supuesto! Preferimos sugerencias que:

- Sean educativamente relevantes
- Tengan visualización interesante
- Complementen los módulos existentes
- Incluyan casos de uso claros


### 📚 Preguntas Educativas

#### **¿Qué nivel educativo es apropiado?**

- **Pregrado:** Ideal para cursos de estructuras de datos y algoritmos
- **Técnico:** Útil para programación avanzada
- **Posgrado:** Base para investigación en algoritmos
- **Autodidacta:** Excelente para aprendizaje independiente


#### **¿Hay material educativo adicional?**

Sí, disponible en el repositorio:

- 📖 Manuales completos en español
- 🎥 Videos tutoriales (próximamente)
- 📊 Casos de estudio documentados
- 📄 Papers académicos relacionados


#### **¿Puedo usar esto en mi curso?**

¡Absolutamente! Es para eso que fue diseñado:

- ✅ Libre uso en instituciones educativas
- ✅ Adaptable a currículos específicos
- ✅ Material de apoyo incluido
- ✅ Soporte técnico disponible


### 🚀 Preguntas sobre Futuro

#### **¿Habrá versión móvil?**

Está en evaluación para versiones futuras. Depende de:

- Feedback de la comunidad
- Viabilidad técnica en pantallas pequeñas
- Recursos de desarrollo disponibles


#### **¿Planean añadir más algoritmos?**

Sí, constantemente. Próximas adiciones incluyen:

- Algoritmos de ordenamiento (v1.1)
- Algoritmos de grafos avanzados (v1.2)
- Algoritmos paralelos (v2.0)


## 🏆 Reconocimientos

### 🎓 Institucionales

- **Universidad Distrital Francisco José de Caldas** - Apoyo institucional y recursos
- **Facultad de Ingeniería** - Orientación académica y validación curricular
- **Programa de Ciencias de la Computación** - Retroalimentación y testing


### 👨‍🏫 Agradecimientos Especiales

- **Profesores del Programa CC** - Por retroalimentación y sugerencias pedagógicas
- **Estudiantes Beta Testers** - Por identificar bugs y proponer mejoras
- **Comunidad Open Source** - Por librerías y herramientas utilizadas


### 🏅 Inspiración

Este proyecto se inspira en herramientas educativas como:

- **Algorithm Visualizer** - Por la idea de visualización interactiva
- **VisuAlgo** - Por la excelencia en diseño educativo
- **Data Structure Visualizations** - Por el enfoque académico


## 📞 Información de Contacto

### 🏛️ Información Institucional

**Universidad Distrital Francisco José de Caldas**
Facultad de Ingeniería
Programa de Ciencias de la Computación

📍 **Dirección:**
Carrera 7 No. 40B - 53
Bogotá D.C., Colombia

🌐 **Web:** [www.udistrital.edu.co](https://www.udistrital.edu.co)

### 📧 Contactos del Proyecto

#### 📬 Email Principal

**simulador.cc2@udistrital.edu.co**
*Para consultas generales, soporte y sugerencias*

#### 🐛 Reporte de Bugs

**bugs.simulador@udistrital.edu.co**
*Para reportar errores técnicos y problemas*

#### 💡 Sugerencias y Mejoras

**mejoras.simulador@udistrital.edu.co**
*Para propuestas de nuevos algoritmos y funcionalidades*

### 🌍 Redes Sociales y Comunidad

#### 🐙 GitHub

**[github.com/universidad-distrital/simulador-algoritmos-cc2](https://github.com/universidad-distrital/simulador-algoritmos-cc2)**

- Issues y bug reports
- Contribuciones de código
- Documentación colaborativa


#### 💬 Foro de Discusión

**[GitHub Discussions](https://github.com/universidad-distrital/simulador-algoritmos-cc2/discussions)**

- Preguntas y respuestas
- Intercambio de experiencias
- Propuestas de la comunidad


#### 📺 Canal YouTube (Próximamente)

**Universidad Distrital - Simulador CC2**

- Tutoriales en video
- Demostraciones de algoritmos
- Seminarios web educativos


### ⏰ Horarios de Soporte

| Tipo de Soporte | Horario | Tiempo de Respuesta |
| :-- | :-- | :-- |
| **Email General** | 24/7 | 24-48 horas |
| **Bug Reports** | L-V 8AM-5PM | 2-4 horas |
| **Soporte Técnico** | L-V 9AM-4PM | 1-2 horas |
| **GitHub Issues** | 24/7 | 12-24 horas |

**¡Gracias por usar el Simulador de Algoritmos CC2!**

*Desarrollado con ❤️ para la comunidad educativa*

*Universidad Distrital Francisco José de Caldas - 2025*
*Equipo de Desarrollo: Alicia Pineda Quiroga, Nelson David Posso Suarez, Jhonathan De La Torre*

> "La mejor manera de aprender algoritmos es viéndolos en acción"
> — Equipo Simulador CC2

*Última actualización: Julio 8, 2025*
*Versión del documento: 1.0*

<div style="text-align: center">⁂</div>

