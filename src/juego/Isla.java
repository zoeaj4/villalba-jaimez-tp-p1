package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Isla {
    
	// Coordenadas
    private double x;
    private double y;
    private double ancho;
    private double alto;

    private Image imagen;

    public Isla(double x, double y, double ancho, double alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.imagen = Herramientas.cargarImagen("isla.png");
    }

    // Dibujo sin moverse
    public void dibujarse(Entorno entorno) {
        dibujarse(entorno, 0);
    }

 // Recibe cuántos píxeles se corrió la cámara
 // hacia la derecha (desplazamiento), y los resta a la posición real de la isla para saber dónde dibujarla en la ventana.
 //
 // Ejemplo: la isla está en el mundo en x = 1000.
 // La cámara se corrió 600 píxeles hacia la derecha.
 // Entonces en pantalla la isla aparece en: 1000 - 600 = 400.
 //
 // Si la cámara no se movió nada (desplazamiento = 0):
 // 1000 - 0 = 1000, que está fuera de la ventana de 800px. No se dibuja.
 public void dibujarse(Entorno entorno, double desplazamiento) {
     double xEnPantalla = x - desplazamiento;

     // Antes de dibujar, se comprueba si la isla es visible en la ventana. Si el borde derecho de la isla (xEnPantalla + ancho/2) está a la izquierda del borde izquierdo de la ventana (< 0), está fuera
     // Si el borde izquierdo de la isla (xEnPantalla - ancho/2) está a la  derecha del borde derecho de la ventana (> 800), también está fuera
     // En cualquiera de esos dos casos, no se dibuja nada (return vacío)
     if (xEnPantalla + ancho / 2 < 0 || xEnPantalla - ancho / 2 > 800) {
    	 return;
     }

     // Si cargó bien la imagen, la dibuja
     if (imagen != null) {
         entorno.dibujarImagen(imagen, xEnPantalla, y, 0, 0.30);
     } else {
         // Si la imagen no cargó (por ejemplo, el archivo no existe),  dibuja un rectángulo verde para que la isla siga siendo visible.
         entorno.dibujarRectangulo(xEnPantalla, y, ancho, alto, 0, Color.GREEN);
     }
 }

    // tope() = borde superior de la isla (donde aterrizan los personajes)
    // El objeto está centrado en y, así que el tope es y - alto/2
    public double tope()  { 
    	return y - (alto / 2); 
    	}
    
    // fondo() = borde inferior de la isla (techo para saltos)
    public double fondo() { 
    	return y + (alto / 2); 
    	}
    
    // getters

    public double getX()      { 
    	return x; 
    	}
    public double getY()      { 
    	return y; 
    	}
    public double getAncho()  { 
    	return ancho; 
    	}
    public double getAlto()   { 
    	return alto; 
    	}
}