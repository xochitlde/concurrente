package Servidor;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.*;

public class Server {

    static int numPersonasOnline = 0;
    static nuevaPeticion personasOnline[] = new nuevaPeticion[50];

    public static void main(String args[]) {

        Socket socket = null;
        boolean ban = true;
        ServerSocket serverSocket = null;
        int port = 2017;

        LocalDateTime initTime = LocalDateTime.now();
        int serverHour = initTime.getHour();
        int serverMinutes = initTime.getMinute();

        System.out.println("\nAutoservicios 2021");
        System.out.println("Servidor iniciado a las " + serverHour + ":" + serverMinutes);

        try { // Establecemos socket servidor
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Escuchando en el puerto " + port + ": " + serverSocket + "\n");

        while (ban) { // Loop infinito para escuchar peticiones de conexion

            try {
                socket = serverSocket.accept();

                // Agregamos al nuevo alumno/profesor a un arreglo global de usuarios conectados
                nuevaPeticion clienteNuevo = new nuevaPeticion(socket, numPersonasOnline, serverHour, serverMinutes);
                personasOnline[numPersonasOnline] = clienteNuevo;
                numPersonasOnline++;

                System.out.println("\nNueva conexion aceptada: " + clienteNuevo.socket);
                clienteNuevo.start(); // Ejecutamos hilo de peticion

                socket = null;

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    // Clase que atiende una nueva peticion de parte de un alumno/profesor
    static class nuevaPeticion extends Thread {

        Socket socket; // Socket del alumno/profesor
        int id;

        String type = ""; // Profesor o alumnmo
        Alumno alumno;
        Profesor profesor;
        String matricula;

        // Tiempo
        LocalDateTime conectionTime;
        int clientHour, clientMinutes, serverHour, serverMinutes, minutosMaestros = 2, minutosAlumnos = 5;

        // Variables de comunicacion
        BufferedReader entrada = null; // Recibe datos del alumno/profesor
        PrintWriter salida = null; // Envia datos al alumno/profesor

        public nuevaPeticion(Socket socket, int id, int hour, int minutes) {
            this.socket = socket;
            this.id = id;
            serverHour = hour;
            serverMinutes = minutes;
        }

        public void run() {

            try {

                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                matricula = entrada.readLine();
                type = searchMatricula(matricula); // Revisamos si la matricula es valida

                if (type != "null") { // Matricula encontrada

                    conectionTime = LocalDateTime.now();
                    clientHour = conectionTime.getHour();
                    clientMinutes = conectionTime.getMinute();

                    // Estudiante o Profesor
                    if (type == "student") {

                        System.out.println("Alumno " + alumno.matricula + " conectado al servidor a las " + clientHour
                                + ":" + clientMinutes);
                        alumno.imprimir(); // Informacion del alumno

                        if (verificarHorarioAlumno()) {
                            System.out.println("El alumno " + alumno.matricula + " ya puede inscribir materias");
                            inscribirMateriasAlumno();
                        } else {
                            System.out.println("El alumno " + alumno.matricula + " aun no puede inscribir materias");
                        }

                    } else if (type == "teacher") {

                        System.out.println("Nuevo maestro conectado al servidor\n");
                    }

                    salir();

                } else {
                    salida.println("null"); // Matricula no encontrada
                    System.out.println("Matricula " + matricula + " no reconocida");
                    salir();
                }

            } catch (SocketException e) {
                System.out.println("Socket Exception");
                salir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // El estudiante/profesor ha abandonao el servidor
        public void salir() {
            try {
                entrada.close();
                salida.close();
                socket.close();

                // Falta quitar el hilo del arreglo

                System.out.println("\nSaliendo: Matricula: " + matricula);

            } catch (Exception e) {
                System.out.println("Excepcion cerrando el socket" + e);
            }
        }

        // Busca una matricula valida
        public String searchMatricula(String matricula) {
            String[] elements;
            String line;

            try { // Recorrer archivo registros.txt
                File f = new File("Servidor/registros.txt");
                Scanner myReader = new Scanner(f);

                while (myReader.hasNextLine()) {
                    line = myReader.nextLine();

                    elements = line.split(",");

                    if (matricula.equals(elements[0])) {
                        if (elements[1].equals("Alumno")) {
                            setAlumno(matricula, elements[2], elements[3]);
                            return "student";
                        } else if (elements[1].equals("Profesor")) {
                            setMaestro(matricula, elements[2], elements[3]);
                            return "teacher";
                        }
                    }
                }
                myReader.close();
                return "null";
            } catch (FileNotFoundException e) {
                System.out.println("Error leyendo registro");
                e.printStackTrace();
                return "null";
            }
            // return 'student','teacher', 'null'
        }

        public void setAlumno(String matricula, String nombre, String promedio) {
            alumno = new Alumno(matricula, nombre, promedio);
        }

        public void setMaestro(String matricula, String nombre, String antiguedad) {
            profesor = new Profesor(matricula, nombre, antiguedad);
        }

        public boolean verificarHorarioAlumno() {

            int prioridad;
            float p = alumno.promedio;

            if (p > 9.5) {
                prioridad = 0;
            } else if (p > 9) {
                prioridad = 1;
            } else if (p > 8.5) {
                prioridad = 2;
            } else if (p > 8) {
                prioridad = 3;
            } else if (p > 7.5) {
                prioridad = 4;
            } else if (p > 7) {
                prioridad = 5;
            } else {
                prioridad = 6;
            }

            int acceso = serverMinutes + minutosMaestros + (minutosAlumnos * prioridad);
            if (acceso > 60) {
                serverHour += acceso / 60;
                acceso = acceso % 60;
            }

            if (clientHour >= serverHour && clientMinutes >= acceso) {
                salida.println("true");
                salida.println("Bienvenido " + alumno.nombreCompleto);
                return true;
            } else {
                String msg = "Favor de entrar a las " + serverHour + ":" + acceso + ", " + alumno.nombreCompleto;
                salida.println("false");
                salida.println(msg);
                return false;
            }

        }

        public void inscribirMateriasAlumno() {
            try {
                String linea = entrada.readLine();

                while (!linea.equals(".")) {
                    System.out.println("\nSolicitud de NRC: " + linea + " de " + alumno.matricula);
                    linea = entrada.readLine();
                }
            } catch (Exception e) {
                System.out.println("\nError leyendo NRC's de " + alumno.matricula);
            }
        }

    }
}
