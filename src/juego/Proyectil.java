package juego;

import java.awt.Color;
import entorno.Entorno;

public class Proyectil {
	
	// coordenadas
    private double x;
    private double y;
    private double angulo;
    private double velocidad = 10;

    // destinoX, destinoY = posición del cursor en el momento del disparo. atan2 calcula el ángulo entre el origen del disparo y el destino 
    public Proyectil(double x, double y, double destinoX, double destinoY) {
        this.x = x;
        this.y = y;
        this.angulo = Math.atan2(destinoY - y, destinoX - x);
    }

    // Avanza en línea recta usando cos/sin del ángulo multiplicados por la velocidad.
    public void moverse() {
        this.x += Math.cos(angulo) * velocidad;
        this.y += Math.sin(angulo) * velocidad;
    }

    // Dibuja un círculo rojo de radio 15 (esto si queres podemos cambiarlo, pero me parece que este tamaño esta bueno) en la posición actual
    public void dibujarse(Entorno entorno) {
        entorno.dibujarCirculo(this.x, this.y, 15, Color.RED); 
    }
    
    // Devuelve true si salió de la ventana (w=800, h=600), si pasa en Juego.java disparo se pone en null.
    public boolean fueraDePantalla(int w, int h) {
        return x < 0 || x > w || y < 0 || y > h;
    }
    
    // getters
    public double getX() { return x; }
    public double getY() { return y; }
}