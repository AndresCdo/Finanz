package Modelo;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la persistencia de datos del sistema Finanz.
 *
 * Guarda y carga Empleados y Transacciones en archivos .csv usando
 * exclusivamente el paquete estándar java.io. No se utilizan
 * librerías externas de bases de datos.
 *
 * Formato CSV empleados:
 *   FIJO  → tipo,id,nombre,documento,cargo,salarioBase
 *   HORAS → tipo,id,nombre,documento,cargo,tarifaHora,horasTrabajadas
 *
 * Formato CSV transacciones:
 *   tipo,id,descripcion,monto,fecha,categoria
 */
public class GestorArchivos {

    // ── Rutas por defecto ──────────────────────────────────────────────────────
    private static final String RUTA_EMPLEADOS     = "empleados.csv";
    private static final String RUTA_TRANSACCIONES = "transacciones.csv";
    private static final String SEPARADOR          = ",";

    // ── ── ── EMPLEADOS ── ── ──────────────────────────────────────────────────

    /**
     * Guarda la lista de empleados en un archivo CSV.
     *
     * @param empleados lista de empleados a persistir
     * @param ruta      ruta del archivo de destino
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void guardarEmpleados(List<Empleado> empleados, String ruta) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            // Encabezado informativo (línea de comentario)
            bw.write("# tipo,id,nombre,documento,cargo,[salarioBase | tarifaHora,horasTrabajadas]");
            bw.newLine();

            for (Empleado e : empleados) {
                if (e instanceof EmpleadoFijo ef) {
                    bw.write(String.join(SEPARADOR,
                            "FIJO",
                            String.valueOf(ef.getId()),
                            ef.getNombre(),
                            ef.getDocumento(),
                            ef.getCargo(),
                            String.valueOf(ef.getSalarioBase())
                    ));
                } else if (e instanceof EmpleadoPorHoras ep) {
                    bw.write(String.join(SEPARADOR,
                            "HORAS",
                            String.valueOf(ep.getId()),
                            ep.getNombre(),
                            ep.getDocumento(),
                            ep.getCargo(),
                            String.valueOf(ep.getTarifaHora()),
                            String.valueOf(ep.getHorasTrabajadas())
                    ));
                }
                bw.newLine();
            }
        }
        System.out.println("[Modelo.GestorArchivos] Empleados guardados en: " + ruta);
    }

    /** Sobrecarga que usa la ruta por defecto. */
    public static void guardarEmpleados(List<Empleado> empleados) throws IOException {
        guardarEmpleados(empleados, RUTA_EMPLEADOS);
    }

    /**
     * Carga la lista de empleados desde un archivo CSV.
     *
     * @param ruta ruta del archivo a leer
     * @return lista de {@link Empleado} reconstruidos desde el archivo
     * @throws IOException si el archivo no existe o no puede leerse
     */
    public static List<Empleado> cargarEmpleados(String ruta) throws IOException {
        List<Empleado> empleados = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar comentarios y líneas vacías
                if (linea.isBlank() || linea.startsWith("#")) continue;

                String[] campos = linea.split(SEPARADOR);
                String tipo = campos[0].trim();

                switch (tipo) {
                    case "FIJO" -> {
                        // tipo,id,nombre,documento,cargo,salarioBase
                        int    id          = Integer.parseInt(campos[1].trim());
                        String nombre      = campos[2].trim();
                        String documento   = campos[3].trim();
                        String cargo       = campos[4].trim();
                        double salarioBase = Double.parseDouble(campos[5].trim());
                        empleados.add(new EmpleadoFijo(id, nombre, documento, cargo, salarioBase));
                    }
                    case "HORAS" -> {
                        // tipo,id,nombre,documento,cargo,tarifaHora,horasTrabajadas
                        int    id              = Integer.parseInt(campos[1].trim());
                        String nombre          = campos[2].trim();
                        String documento       = campos[3].trim();
                        String cargo           = campos[4].trim();
                        double tarifaHora      = Double.parseDouble(campos[5].trim());
                        double horasTrabajadas = Double.parseDouble(campos[6].trim());
                        empleados.add(new EmpleadoPorHoras(id, nombre, documento, cargo,
                                tarifaHora, horasTrabajadas));
                    }
                    default -> System.err.println("[Modelo.GestorArchivos] Tipo de empleado desconocido: " + tipo);
                }
            }
        }

        System.out.println("[Modelo.GestorArchivos] " + empleados.size() + " empleados cargados desde: " + ruta);
        return empleados;
    }

    /** Sobrecarga que usa la ruta por defecto. */
    public static List<Empleado> cargarEmpleados() throws IOException {
        return cargarEmpleados(RUTA_EMPLEADOS);
    }

    // ── ── ── TRANSACCIONES ── ── ─────────────────────────────────────────────

    /**
     * Guarda la lista de transacciones en un archivo CSV.
     *
     * @param transacciones lista de transacciones a persistir
     * @param ruta          ruta del archivo de destino
     * @throws IOException si ocurre un error al escribir
     */
    public static void guardarTransacciones(List<Transaccion> transacciones, String ruta) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write("# tipo,id,descripcion,monto,fecha(yyyy-MM-dd),categoria");
            bw.newLine();

            for (Transaccion t : transacciones) {
                String tipo = (t instanceof Ingreso) ? "INGRESO" : "GASTO";
                bw.write(String.join(SEPARADOR,
                        tipo,
                        String.valueOf(t.getId()),
                        t.getDescripcion(),
                        String.valueOf(t.getMonto()),
                        t.getFecha().toString(),
                        t.getCategoria()
                ));
                bw.newLine();
            }
        }
        System.out.println("[Modelo.GestorArchivos] Transacciones guardadas en: " + ruta);
    }

    /** Sobrecarga que usa la ruta por defecto. */
    public static void guardarTransacciones(List<Transaccion> transacciones) throws IOException {
        guardarTransacciones(transacciones, RUTA_TRANSACCIONES);
    }

    /**
     * Carga la lista de transacciones desde un archivo CSV.
     *
     * @param ruta ruta del archivo a leer
     * @return lista de {@link Transaccion} reconstruidas desde el archivo
     * @throws IOException si el archivo no existe o no puede leerse
     */
    public static List<Transaccion> cargarTransacciones(String ruta) throws IOException {
        List<Transaccion> transacciones = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.startsWith("#")) continue;

                String[] campos = linea.split(SEPARADOR);
                String tipo = campos[0].trim();

                int       id          = Integer.parseInt(campos[1].trim());
                String    descripcion = campos[2].trim();
                double    monto       = Double.parseDouble(campos[3].trim());
                LocalDate fecha       = LocalDate.parse(campos[4].trim());
                String    categoria   = campos[5].trim();

                switch (tipo) {
                    case "INGRESO" -> transacciones.add(new Ingreso(id, descripcion, monto, fecha, categoria));
                    case "GASTO"   -> transacciones.add(new Gasto(id, descripcion, monto, fecha, categoria));
                    default        -> System.err.println("[Modelo.GestorArchivos] Tipo de transacción desconocido: " + tipo);
                }
            }
        }

        System.out.println("[Modelo.GestorArchivos] " + transacciones.size() + " transacciones cargadas desde: " + ruta);
        return transacciones;
    }

    /** Sobrecarga que usa la ruta por defecto. */
    public static List<Transaccion> cargarTransacciones() throws IOException {
        return cargarTransacciones(RUTA_TRANSACCIONES);
    }

    // ── ── ── EMPRESA COMPLETA ── ── ──────────────────────────────────────────

    /**
     * Guarda todos los datos de una empresa (empleados + transacciones)
     * usando las rutas por defecto.
     *
     * @param empresa instancia de {@link Empresa} a persistir
     * @throws IOException si ocurre un error al escribir
     */
    public static void guardarEmpresa(Empresa empresa) throws IOException {
        guardarEmpleados(empresa.getEmpleados());
        guardarTransacciones(empresa.getTransacciones());
        System.out.println("[Modelo.GestorArchivos] Modelo.Empresa '" + empresa.getNombre() + "' guardada correctamente.");
    }

    /**
     * Carga todos los datos de una empresa desde los archivos CSV por defecto
     * y los inyecta en la instancia recibida.
     *
     * @param empresa instancia de {@link Empresa} donde se cargarán los datos
     * @throws IOException si alguno de los archivos no puede leerse
     */
    public static void cargarEmpresa(Empresa empresa) throws IOException {
        List<Empleado>    empleados     = cargarEmpleados();
        List<Transaccion> transacciones = cargarTransacciones();

        for (Empleado e : empleados)         empresa.agregarEmpleado(e);
        for (Transaccion t : transacciones)  empresa.agregarTransaccion(t);

        System.out.println("[Modelo.GestorArchivos] Datos cargados en empresa: " + empresa.getNombre());
    }
}
