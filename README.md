DataStock - Sistema de Gestión de Inventario (UT1 + UT2)

Módulo: Acceso a Datos | Curso: 2.º DAM (2026-2027)

Autor: Alberto Gálvez Molina

Repositorio: https://github.com/algalmo/DataStock-

Etiqueta de entrega: UT2-v1.0

# 1. Descripción del Proyecto

DataStock es una aplicación Java modular orientada a la gestión eficiente de inventarios informáticos. La aplicación combina dos capas de arquitectura de persistencia independientes:

Persistencia en Ficheros y RAM (UT1): Manejo de ficheros planos, JSON (Gson), XML (DOM / XPath), serialización de objetos (.dat) y ficheros de acceso aleatorio de longitud fija (RAF .bin).

Persistencia Relacional JDBC y MySQL (UT2): Acceso a base de datos relacional mediante JDBC, patrón DAO, transacciones complejas (commit/rollback), invocación de procedimientos almacenados (Stored Procedures) y validaciones de integridad referencial.

Persistencia Embebida Demonstrativa (SQLite): Modulo complementario e independiente para la evaluación de motores embebidos sin servidor mediante sqlite-jdbc.

# 2. Requisitos del Sistema y Dependencias

Para la correcta compilación y ejecución de la aplicación se requiere:

Java Development Kit (JDK): Versión 21 o superior.

Apache Maven: Versión 3.9+ para la gestión de dependencias y ciclo de construcción.

Servidor MySQL: Versión 8.0.16 o superior (con soporte para procedimientos almacenados y transacciones InnoDB).

Conectores JDBC y Librerías (pom.xml)

MySQL Connector/J: com.mysql:mysql-connector-j (Versión 9.1.0 o 8.3.0).

SQLite JDBC Driver: org.xerial:sqlite-jdbc (Versión 3.45.1.0).

Google Gson: com.google.code.gson:gson (Versión 2.10.1).

# 3. Configuración y Seguridad (Sin Secretos)

Siguiendo las buenas prácticas de seguridad, nunca se incluyen credenciales de base de datos en el repositorio de Git. La aplicación gestiona la conexión mediante una clase centralizada (ConexionBD.java) que prioriza variables de entorno y utiliza de forma secundaria un archivo local excluido por .gitignore.

Métodos de Configuración

Opción A: Variables de Entorno (Recomendado)

Establece las siguientes variables en tu entorno de sistema o terminal antes de ejecutar:

export JDBC_URL="jdbc:mysql://localhost:3306/datastock?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export JDBC_USER="tu_usuario"
export JDBC_PASSWORD="tu_password_segura"


Opción B: Archivo de Propiedades Local

Copia el archivo de plantilla db.properties.example a db.properties (el cual está ignorado por Git):

cp db.properties.example db.properties


Edita db.properties con tus credenciales locales:

db.url=jdbc:mysql://localhost:3306/datastock?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=root


# 4. Instalación del Esquema de Base de Datos

Antes de iniciar el submenú de MySQL, debes ejecutar el script oficial de creación de tablas y procedimientos almacenados ubicado en el proyecto:

Inicia sesión en tu cliente de MySQL o Workbench:

mysql -u root -p


Ejecuta el script oficial de definición de esquema:

SOURCE datos/sql/datastock_schema.sql;


El script creará la base de datos datastock, las tablas categoria, proveedor, producto, movimiento_stock y el Stored Procedure registrar_movimiento.

# 5. Compilación y Ejecución

Aplicación Principal (App.java)

Para compilar y ejecutar el menú interactivo principal de DataStock:

mvn clean compile
mvn exec:java


Clase Independiente SQLite (DemoSQLite.java)

Tal y como exige el Paso 16 (actividad comparativa obligatoria), la demostración de SQLite se ejecuta de forma aislada sin modificar los menús principales:

mvn exec:java@sqlite


# 6. Arquitectura y Estructura de Paquetes

El proyecto sigue una arquitectura en capas limpia con separación estricta de responsabilidades:

src/main/java/es/iesisidra/datastock/
├── App.java                      # Punto de entrada de la aplicación
├── complementarias/              # Demostraciones aisladas y actividades complementarias
│   ├── DemoSQLite.java           # [Paso 16] Demostración independiente de SQLite embebido
│   ├── DemoXPath.java            # Consultas e inyección XPath
│   └── DemoRAF.java              # Inspección binaria de registros
├── datos/                        # Capa de Acceso a Datos (DAOs y Conexión)
│   ├── ConexionBD.java           # Gestión segura de conexiones JDBC
│   ├── CategoriaDAO.java         # Operaciones CRUD sobre la tabla 'categoria'
│   ├── ProveedorDAO.java         # Operaciones CRUD sobre la tabla 'proveedor'
│   ├── ProductoDAO.java          # Operaciones CRUD sobre la tabla 'producto'
│   ├── MovimientoDAO.java        # Consultas e inserciones en 'movimiento_stock'
│   └── ImportadorLoteDAO.java    # [Paso 14] Importación en lote transaccional
├── modelo/                       # Entidades del dominio DTO/POJO
│   ├── Producto.java             # Objeto de inventario de RAM / Base
│   ├── Categoria.java            # Entidad de categoría relacional
│   ├── Proveedor.java            # Entidad de proveedor relacional
│   └── MovimientoStock.java      # Historial de entradas/salidas
├── ui/                           # Capa de Interfaz de Usuario de Consola
│   ├── MenuPrincipal.java        # Menú raíz (Integración UT1 + UT2)
│   ├── MenuMySQL.java            # Submenú JDBC (Operaciones MySQL)
│   ├── MenuProductos.java        # Submenú CRUD de Productos en MySQL
│   ├── MenuHistorial.java        # Submenú de consultas de movimientos
│   └── MenuImportacion.java      # Menú de importación transaccional
└── util/                         # Utilidades comunes (Colores, Validaciones)


# 7. Diferencias Fundamentales: Memoria (UT1) vs. MySQL (UT2)

Aspecto

Inventario en Memoria RAM (UT1)

Base de Datos MySQL (UT2)

Persistencia

Volátil. Los datos se pierden al cerrar el programa salvo exportación manual a JSON/XML/DAT.

Permanente. Los datos persisten tras reiniciar el sistema en el servidor MySQL.

Identificadores

Basados exclusivamente en la clave de negocio (codigo tipo String, ej. TEC-001).

Utiliza IDs autoincrementales (LONG) como PK técnica y el codigo como clave alternativa UNIQUE.

Relaciones

Tipos incrustados como cadenas de texto simples en los objetos Java.

Tablas relacionales con restricciones de clave foránea (FOREIGN KEY) hacia categoria y proveedor.

Modificación de Stock

Edición directa del atributo stock mediante setters en el POJO.

Prohibido editar stock directamente. Se modifica exclusivamente mediante el Stored Procedure registrar_movimiento.

Transacciones

Inexistentes. Las modificaciones en listas Java son inmediatas.

Soportadas con setAutoCommit(false), commit() y rollback() para garantizar la atomicidad en lote.

# 8. Demostración de Transacciones JDBC (Opción 9)

La opción 9 del submenú de MySQL demuestra el control manual de transacciones JDBC mediante dos escenarios:

Escenario A (Commit Exitoso): Inserta dos productos de prueba con IDs temporales (P-DEMO1 y P-DEMO2). Todas las validaciones son correctas, se ejecuta conexion.commit() y la transacción se confirma.

Escenario B (Rollback Forzado): Intenta insertar un producto válido (P-DEMO1) seguido de un producto con clave primaria duplicada (TEC-001). Al saltar la SQLException, el bloque catch ejecuta conexion.rollback(), dejando la base de datos intacta sin insertar ningún registro parcial.

Mecanismo de Limpieza: Tras cada demostración, la opción ofrece limpiar automáticamente los registros de prueba (P-DEMO1, P-DEMO2) para permitir ejecuciones limpias continuadas.

# 9. Comparativa Técnica: MySQL vs. SQLite (§7.4)

En cumplimiento del requisito RA2-b (Paso 16), se detalla la comparativa entre motores de base de datos relacionales:

Característica

MySQL

SQLite

¿Necesita servidor?

Sí. Requiere un servicio demonio activo escuchando en puerto TCP (por defecto 3306).

No. Es un motor embebido que se ejecuta dentro del propio proceso de la JVM.

Almacenamiento

Archivos internos gestionados exclusivamente por el motor de base de datos.

Un único archivo local en el sistema de archivos (datos/sqlite/productos_demo.db).

Conexión JDBC

DriverManager.getConnection("jdbc:mysql://localhost:3306/datastock", ...)

DriverManager.getConnection("jdbc:sqlite:datos/sqlite/productos_demo.db")

Dependencia Maven

com.mysql:mysql-connector-j

org.xerial:sqlite-jdbc

Ventajas

Excelente rendimiento en accesos concurrentes masivos, permisos granulares, Stored Procedures y triggers.

Cero configuración, extremadamente ligero, ideal para despliegues portátiles y pruebas aisladas.

Desventajas

Requiere instalación previa, mantenimiento del servidor y gestión de credenciales de red.

Bloqueo de escritura a nivel de archivo completo durante operaciones pesadas de concurrencia.

Caso de Uso Ideal

Sistemas cliente-servidor, aplicaciones web multiusuario y entornos empresariales.

Aplicaciones de escritorio mono-usuario, prototipos rápidos, aplicaciones móviles e inspección local.

Justificación de Arquitectura: Pool de Conexiones

Un pool de conexiones (como HikariCP o Apache DBCP) mantiene un conjunto de conexiones TCP previamente abiertas y listas para ser reutilizadas, eliminando el coste de latencia que supone abrir y cerrar conexiones de red en cada petición. Resulta indispensable en servicios web de alto tráfico.

En DataStock, al tratarse de una aplicación de consola mono-usuario interactiva, el enfoque de conexiones cortas y bien cerradas mediante try-with-resources (Connection, PreparedStatement, ResultSet) es el más adecuado, garantizando la liberación inmediata de recursos del sistema sin añadir complejidad innecesaria a la arquitectura.

# 10. Checklist de Comprobación y Entrega

[x] Inexistencia de secretos: Código y commits verificados sin contraseñas expuestas.

[x] Cierre estricto de recursos: Todos los DAOs aplican try-with-resources en llamadas JDBC.

[x] Validaciones previas: Precios controlados con BigDecimal, emails con regex y stock validado por Stored Procedure.

[x] Etiqueta Git: Etiqueta UT2-v1.0 creada y subida al repositorio remoto (git push origin UT2-v1.0).

[x] Documento de Pruebas: Archivo PDF generado y listo para subir al buzón Moodle.