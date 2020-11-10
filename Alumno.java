package Servidor;

public class Alumno {
    String nombreCompleto;
    String matricula;
    float promedio;

    public Alumno(String matricula, String name, String promedio) {
        this.matricula = matricula;
        nombreCompleto = name;
        float p = Float.parseFloat(promedio);
        this.promedio = p;
    }

    public void imprimir() {
        System.out.println("Alumno " + matricula + " " + nombreCompleto + " " + " Promedio: " + promedio);
    }

}
