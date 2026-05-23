# Finanz - Especificación Técnica y Arquitectura del Sistema

## 1. Contexto del Proyecto
Finanz es un sistema de gestión financiera empresarial para PYMES, desarrollado en **Java 25**.
**Objetivo actual para el Agente:** Desarrollar EXCLUSIVAMENTE el backend (lógica de negocio OOP) y la persistencia de datos. Ignorar por completo la Interfaz Gráfica (GUI) por ahora.

## 2. Requerimientos de Arquitectura (Orientada a Objetos)

### 2.1. Módulo de Empleados (Herencia)
- **`Empleado` (Clase Abstracta)**:
    - Atributos (`private` o `protected`): `id`, `nombre`, `documento`, `cargo`.
    - Debe contener el método abstracto: `public abstract double calcularSalario();`.
- **`EmpleadoFijo` (Extiende de Empleado)**:
    - Atributo específico: `salarioBase` (double).
    - Implementa `calcularSalario()` retornando el `salarioBase`.
- **`EmpleadoPorHoras` (Extiende de Empleado)**:
    - Atributos específicos: `tarifaHora` (double), `horasTrabajadas` (int o double).
    - Implementa `calcularSalario()` multiplicando tarifa por horas trabajadas.

### 2.2. Módulo de Transacciones (Herencia)
- **`Transaccion` (Clase Abstracta)**:
    - Atributos: `id`, `descripcion`, `monto` (double), `fecha` (LocalDate), `categoria` (String).
- **`Ingreso` (Extiende de Transaccion)**:
    - Conceptualmente, su monto suma al balance total.
- **`Gasto` (Extiende de Transaccion)**:
    - Conceptualmente, su monto resta al balance total.

### 2.3. Clase Gestora Principal (Agregación)
- **`Empresa`**:
    - Colecciones requeridas: `List<Empleado>` y `List<Transaccion>`.
    - Métodos de negocio obligatorios:
        1. `calcularBalance()`: Retorna (Total Ingresos - Total Gastos).
        2. `generarReporteNomina()`: Calcula el costo total de salarios iterando la lista de empleados y aprovechando el polimorfismo del método `calcularSalario()`.
        3. `filtrarTransacciones()`: Lógica para obtener transacciones por rango de fechas o categoría.

### 2.4. Persistencia de Datos
- **`GestorArchivos`**:
    - Utilizar el paquete estándar `java.io` (no usar librerías externas de bases de datos).
    - Debe tener métodos estáticos o de instancia para guardar y leer los datos de Empleados y Transacciones en formato de texto plano (`.csv` o `.txt`).

## 3. Instrucciones Estrictas de Código para el Agente
- **Encapsulamiento:** Todos los atributos deben ser `private`, accediendo a ellos mediante Getters y Setters.
- **Constructores:** Generar constructores completos para todas las clases y usar `super()` en las clases hijas.
- **Sobrescritura:** Sobrescribir el método `toString()` en todas las entidades de dominio para facilitar la depuración y los reportes en consola.
- **Calidad:** Agregar comentarios JavaDoc en la clase `Empresa` y manejar excepciones básicas (por ejemplo, validar que no se ingresen montos negativos).