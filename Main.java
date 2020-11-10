package Cliente;

import java.net.*;
import java.io.*;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static BufferedReader entrada = null;
    static PrintWriter salida = null;
    static String respuesta, servidor;
    static Socket socket;
    static int puerto;

    public static void main(String[] args) {

        // Variables para socket
        // String host ="192.168.1.74";
        servidor = "127.0.0.1"; // Host por defecto
        puerto = 2017;
        socket = null;

        if (args.length == 1) {
            servidor = args[0];
        }
        System.out.println("\nDireccion del servidor: " + servidor);

        // Establecer conexion con el servidor
        try {

            socket = new Socket(servidor, puerto);
            System.out.println("Conexion establecida con server: " + socket + "\n");

            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            if (login()) {
                inscripcionMaterias();
            }
            System.out.println("\nSaliendo del Servidor\n");
            sc.close();

        } catch (UnknownHostException e) {
            System.out.println("Error: UnknownHostException");
            System.out.println("Verifique su conexion a internet y la direccion IP del Servidor");
        } catch (IOException e) {
            System.out.println("Error: IOException");
            System.out.println("Verifique su conexion a internet y la direccion IP del Servidor");
        }

    }

    static public boolean login() {
        try {
            System.out.print("Ingresar su matricula: ");
            String matricula = sc.nextLine();

            salida.println(matricula); // Se envia la matricula
            respuesta = entrada.readLine();
            // null: matricula no encontrada
            // false: encontrada pero horario incorrecto
            // true: logeo exitoso

            if (respuesta.equals("null")) {
                System.out.println("Matricula no encontrada");
                return false;
            } else if (respuesta.equals("false")) {
                System.out.println("Usted aun no tiene acceso al servidor");
                respuesta = entrada.readLine();
                System.out.println(respuesta);
                return false;
            } else if (respuesta.equals("true")) {
                System.out.println("Acceso concedido al servidor");
                respuesta = entrada.readLine();
                System.out.println(respuesta);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static public void inscripcionMaterias() {
        System.out.println("\nA continuacion ingrese los nrc de las materias que desea inscribir [ . para salir]\n");
        System.out.print("NRC: ");

        try {
            String linea = sc.nextLine();
            salida.println(linea);
            while (!linea.equals(".")) {
                System.out.print("NRC: ");
                linea = sc.nextLine();

                // Recibir respuesta del server

                salida.println(linea);
            }
        } catch (Exception e) {
            salida.println(".");
        }
    }

}