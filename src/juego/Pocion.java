package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Pocion {
	// Para dibujarla se resta el desplazamiento del mapa.
    private double x, y; // coordenadas
    private double velocidadY;
    private static final double GRAVEDAD = 0.4;
    private static final double ANCHO    = 30;
    private static final double ALTO     = 30;
    private Image imagen;

    // xMundo: posición en el mundo donde cayó el enemigo
    // La poción sale con velocidadY = -3 (es un efecto visual para que parezca que sale del enemigo)
    public Pocion(double xMundo, double y) {
        this.x = xMundo;
        this.y = y;
        this.velocidadY = -3;
        this.imagen = Herramientas.cargarImagen("pocion.png");
    }
    
    // Aplica gravedad y detecta si aterriza sobre alguna isla para dejar de caer.
    // islas[] se pasa desde el mapa para poder revisar colisiones.
    public void moverse(Isla[] islas) {
        velocidadY += GRAVEDAD;
        y += velocidadY;

        for (Isla isla : islas) {
            if (isla == null) continue;
            boolean alineadosEnX = Math.abs(x - isla.getX()) < (isla.getAncho() / 2 + ANCHO / 2);
            if (!alineadosEnX) continue;

            // Si la poción cae y sus pies cruzan el tope de la isla, aterriza
            if (velocidadY >= 0
                && y + ALTO / 2 >= isla.tope()
                && y - ALTO / 2 <  isla.tope()) {
                y = isla.tope() - ALTO / 2; //la coloca jsuto encima
                velocidadY = 0;
            }
        }
    }

 // La poción guarda su posición real en el mundo. Para dibujarla en la ventana hay que restarle el desplazamiento de la cámara, igual que con la isla.
 // Ejemplo: poción en x = 2300, cámara corrida 2100 píxeles. En pantalla aparece en: 2300 - 2100 = 200.

    public void dibujarse(Entorno entorno, double offsetX) {
        entorno.dibujarImagen(imagen, x - offsetX, y, 0, 0.25);
    }

    // Devuelve true si la poción cayó fuera del mapa (debajo de y) pone su slot en null (en Juego.java)=
    public boolean fueraDePantalla() {
        return y > 650;
    }

    // Colisión en espacio mundo
    public boolean colisionaCon(double pxMundo, double py, double pancho, double palto) {
        return Math.abs(x - pxMundo) < (ANCHO / 2 + pancho / 2)
            && Math.abs(y - py)      < (ALTO  / 2 + palto  / 2);
    }
}