package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Enemigo {
    // Los enemigos viven en coordenadas de PANTALLA (0-800). no en coordenadas de mundo como la princesa o las pociones
    private double x, y;
    private double velocidad; // positivo = va hacia la derecha, negativo = hacia la izquierda
    private static final double ANCHO = 40;
    private static final double ALTO  = 40;
    private Image imagen; 

    // desdeDerecha: si es true el enemigo aparece por el borde derecho y va hacia la izquierda. si es false aparece por la izquierda y va hacia la derecha y: altura aleatoria donde aparece.
    public Enemigo(boolean desdeDerecha, double y) {
        this.y = y;
        if (desdeDerecha) {
            this.x = 810; // justo fuera del borde derecho de la ventana
            this.velocidad = -5; // se mueve hacia la izquierda
            this.imagen = Herramientas.cargarImagen("enemigo_derecha.png"); 
        } else {
            this.x = -10;  // justo fuera del borde izquierdo
            this.velocidad = 5; // se mueve hacia la derecha
            this.imagen = Herramientas.cargarImagen("enemigo_izquierda.png");   
        }
    }
    
    // Mueve al enemigo horizontalmente según su velocidad. Se llama una vez por tick

    public void moverse() {
        this.x += velocidad;
    }

    // Dibujarse (el 0.1 es la escala de la imagen)
    public void dibujarse(Entorno entorno) {
        entorno.dibujarImagen(imagen, x, y, 0, 0.1);
    }

    // Devuelve true si el enemigo salió completamente de la ventana (por cualquier lado).
    // Cuando esto ocurre, en Juego.java su slot se pone en null para reutilizarlo.
    public boolean fueraDePantalla() {
        return x < -50 || x > 850;
    }

    // Cuando el proyectil mata al enemigo, este tiene un 15 % de probabilidad de soltar una poción. offsetX (= desplazamientoMapaX) se suma para convertir la posición de pantalla del enemigo a coordenada MUNDO, que es donde vive la poción
    public Pocion soltarPocion(double offsetX) {
        if (Math.random() < 0.15) {
            return new Pocion(x + offsetX, y);// crea la poción en el mundo
        }
        return null;
    }

    // Colisión con la princesa usando "cajas" centradas en cada objeto.
    // px, py = posición de pantalla de la princesa; pancho, palto = su tamaño.
    // La colisión ocurre si la distancia en X es menor que la suma de sus medios anchos, y la distancia en Y es menor que la suma de sus medios altos.
    public boolean colisionaCon(double px, double py, double pancho, double palto) {
        return Math.abs(x - px) < (ANCHO / 2 + pancho / 2)
            && Math.abs(y - py) < (ALTO  / 2 + palto  / 2);
    }
    // Colisión con el proyectil: se le agrega un margen de 15 px para que sea más fácil acertar (hitbox un poco más grande que el sprite)
    public boolean colisionaConProyectil(double px, double py) {
        return Math.abs(x - px) < ANCHO / 2 + 15
            && Math.abs(y - py) < ALTO  / 2 + 15;
    }

    public double getX() { 
    	return x; 
    	}
    public double getY() { 
    	return y; 
    	}
}