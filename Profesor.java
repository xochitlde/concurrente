package Servidor;

public class Profesor {
    String nombreCompleto;
    String matricula;
    int antiguedad;

    public Profesor(String matricula, String name, String antiguedad) {
        this.matricula = matricula;
        nombreCompleto = name;
        int a = Integer.parseInt(antiguedad);
        this.antiguedad = a;
    }

    public void imprimir() {
        System.out.println(
                "Proefesor " + matricula + " " + nombreCompleto + " " + " Antiguedad: " + antiguedad + " años");
    }
}
