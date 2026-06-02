package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class JefeFinal {

    private double x;
    private double y;
    private int vidas;
    private Image imagen;
    
    
    public JefeFinal(double x, double y) {
        this.x = x;
        this.y = y;
        this.vidas = 20;
        this.imagen = Herramientas.cargarImagen("jefeFinal.gif");
    }

    // Dibuja el jefe en pantalla
    public void dibujarse(Entorno entorno) {
        entorno.dibujarImagen(imagen, 400, 190, 0, 1);
    }

    
    public int getVidas() {
        return vidas;
    }

    public void recibirDanio() {
        vidas--; // reduce vida del jefe
    }
    
    public boolean estaMuerto() {
        return vidas <= 0;
    }
    
    // colisión
    public boolean colisionaCon(double px, double py) {
        return Math.abs(px - x) < 60 &&
               Math.abs(py - y) < 60;
    }
}
    