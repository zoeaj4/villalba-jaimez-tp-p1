package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Proyectil {
	
	// coordenadas
    private double x;
    private double y;
    private double angulo;
    private double velocidad = 10;
    private Image imagen;
    
    // destinoX, destinoY = posición del cursor en el momento del disparo. atan2 calcula el ángulo entre el origen del disparo y el destino 
    public Proyectil(double x, double y, double destinoX, double destinoY) {
        this.x = x;
        this.y = y;
        this.angulo = Math.atan2(destinoY - y, destinoX - x);
        this.imagen = Herramientas.cargarImagen("proyectil.png");
    }

    // Avanza en línea recta usando cos/sin del ángulo multiplicados por la velocidad.
    public void moverse() {
        this.x += Math.cos(angulo) * velocidad;
        this.y += Math.sin(angulo) * velocidad;
    }
    
    // Dibuja el proyectil en pantalla
    public void dibujarse(Entorno entorno) {

        // dibuja la imagen en la posición actual
        entorno.dibujarImagen(
            imagen,
            this.x,
            this.y,

            // rota la imagen según la dirección del disparo
            this.angulo,

            // tamaño del proyectil (trate de que quede del mismo tamaño que estaba la bola roja, podes cambiarlo igual)
            0.05
        );
    } 
    
    // Devuelve true si salió de la ventana (w=800, h=600), si pasa en Juego.java disparo se pone en null.
    public boolean fueraDePantalla(int w, int h) {
        return x < 0 || x > w || y < 0 || y > h;
    }
    
    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngulo() {
        return angulo;
    }
}