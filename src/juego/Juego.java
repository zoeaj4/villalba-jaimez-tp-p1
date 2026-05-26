package juego;
 
import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;
 
public class Juego extends InterfaceJuego {
	
    private Entorno   entorno;
    private Elizabeth princesa;
    private Mapa      mapa;
    private Proyectil disparo;
    private ProyectilArea disparoArea;
    private int       vidas;
    
    private Enemigo[] enemigos;
    private Pocion[]  pociones;
 
    // Tamaño máximo de cada arreglo. Si todos los slots están ocupados, no puede aparecer ningún enemigo/pocion más.
    private static final int MAX_ENEMIGOS = 8;
    private static final int MIN_ENEMIGOS = 3; // si hay menos de esto, se fuerza un spawn
    private static final int MAX_POCIONES = 3;
 
    // Cuenta los ticks desde que apareció el último enemigo. Cuando llega a 90 (1,5 segundos a 60 fps) se genera uno nuevo.
    private int ticksDesdeUltimoEnemigo = 0;
 
    // Estados del Juego
    private static final int JUGANDO = 0;
    private static final int PERDIDO = 1;
    private static final int GANADO  = 2;
    private int estado = JUGANDO;
 
    private Image imagenPerdiste;
    private Image imagenGanaste;
    private Image imagenVida;
 
    public Juego() {
    	
        this.entorno  = new Entorno(this, "Super Elizabeth Sis", 800, 600);
        this.mapa     = new Mapa();
        this.princesa = new Elizabeth(400, 300);
        this.vidas    = 5;
        
        // Se crean los arreglos con nulls.
        this.enemigos = new Enemigo[MAX_ENEMIGOS];
        this.pociones = new Pocion[MAX_POCIONES];
        
        this.imagenPerdiste = Herramientas.cargarImagen("perdiste.png");
        this.imagenGanaste  = Herramientas.cargarImagen("ganaste.png");
        this.imagenVida = Herramientas.cargarImagen("vida.png");
        this.entorno.iniciar();
        
    }
 
    public void tick() {
    	
    	entorno.cambiarFont("Tahoma", 25, Color.BLACK, 
    			entorno.NEGRITA);
 
    	// Si el juego ya terminó (ganado o perdido), solo se muestra la pantalla final y el botón de reinicio. El return al final evita que se ejecute el resto del tick.
    	
        if (estado == PERDIDO || estado == GANADO) {
        // Elige una imagen depende si ganamos o perdimos
        	Image img = (estado == GANADO) ? imagenGanaste : imagenPerdiste;
            entorno.dibujarImagen(img, 400, 300, 0, 0.667);
            
         // Botón jugar de nuevo
            entorno.dibujarRectangulo(405, 475, 260, 60, 0, new Color(0, 0, 0, 130));
            entorno.dibujarRectangulo(400, 470, 268, 68, 0, new Color(15, 70, 15));
            entorno.dibujarRectangulo(400, 470, 260, 60, 0, new Color(34, 139, 34));
            entorno.dibujarRectangulo(400, 445, 252, 6, 0, new Color(130, 230, 130, 170));
            entorno.escribirTexto("Jugar de nuevo", 312, 478);
            
            // Reinicio: se recrean todos los objetos y vuelve a Jugando
            if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
                int mx = entorno.mouseX();
                int my = entorno.mouseY();

                // Verifica si el mouse está dentro del botón
                if (mx >= 270 && mx <= 530 &&
                    my >= 440 && my <= 500) {
 
                    this.mapa     = new Mapa();
                    this.princesa = new Elizabeth(400, 300);
                    this.vidas    = 5;
                    this.disparo  = null;
                    this.enemigos = new Enemigo[MAX_ENEMIGOS];
                    this.pociones = new Pocion[MAX_POCIONES];
                    this.ticksDesdeUltimoEnemigo = 0;
                    this.estado   = JUGANDO;
                }
            }
            return;
        }
 

        // JUGANDO (!!!)
        
        // Dibujo el mapa primero porque después se puede poner encima de otros objetos como los enemigos o los proyectiles
        mapa.dibujarse(entorno);
 
        // Movimiento Princesa
        if (entorno.estaPresionada(entorno.TECLA_DERECHA) || entorno.estaPresionada('d')) {
            princesa.moverDerecha();
        }
        if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA) || entorno.estaPresionada('a')) {
            princesa.moverIzquierda();
        }
        if (entorno.sePresiono(entorno.TECLA_ARRIBA) || entorno.sePresiono(entorno.TECLA_ESPACIO) || entorno.sePresiono('w')) {
            princesa.saltar();
        }
 
        // MOVIMIENTO DE CÁMARA (desplazamiento)
        // La princesa vive en un mundo de 5000 px (TAMAÑO DEL MAPA, lo puedo cambiar en Mapa.java), pero la ventana mide 800. actualizarDesplazamiento() calcula cuánto correr la cámara para mantener a la princesa centrada en pantalla.
        // Fórmula para dibujar cualquier objeto del mundo en pantalla:
        //   xPantalla = xMundo - desplazamientoMapaX
        mapa.actualizarDesplazamiento(princesa.getX());
        double desplazamientoMapaX = mapa.getDesplazamientoMapaX();
 
        
        // GRAVEDAD Y COLISIONES CON ISLAS
        // aplicarGravedad() suma GRAVEDAD a velocidadY cada tick, haciendo que la caída se acelere. Luego se revisa isla por isla si la princesa aterrizó (pies tocan el tope) o rebotó contra un techo (cabeza toca el fondo de una isla)
        
        princesa.aplicarGravedad();
        boolean apoyada = false; // se vuelve true si alguna isla tiene a la princesa encima
        
 
        for (Isla isla : mapa.getIslas()) {
            if (isla == null) continue;
            
         // Primero comprueba alineación horizontal: si la princesa no está sobre la isla en X, no tiene sentido revisar la colisión vertical
            boolean alineadosEnX = Math.abs(princesa.getX() - isla.getX())
                                   < (isla.getAncho() / 2 + princesa.getAncho() / 2);
            if (!alineadosEnX) continue;
 
            double mitadAlto = princesa.getAlto() / 2;
         // aterrizaje: la princesa cae y sus pies cruzan el tope de la isla.
            if (!princesa.estaSubiendo()
                && princesa.getY() + mitadAlto >= isla.tope()
                && princesa.getY() - mitadAlto <  isla.tope()) {
                princesa.aterrizar(isla.tope());
                apoyada = true;
                
          // Choca con el techo: la princesa sube y su cabeza toca el fondo de la isla.
            } else if (princesa.estaSubiendo()
                       && princesa.getY() - mitadAlto <= isla.fondo()
                       && princesa.getY() + mitadAlto >  isla.fondo()) {
                princesa.pegarTecho(isla.fondo());
            }
        }
        
        // Si ninguna isla la soporta, se marca que está en el aire (sigue cayendo)
        if (!apoyada) princesa.setEnElAire(true);
 
        // Solo puede existir un proyectil a la vez (disparo == null significa que no hay ninguno) Al hacer clic izquierdo se crea uno apuntando al cursor. moverse() lo avanza cada tick, al salir de pantalla se  destruye poniéndolo en null
        if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO) && disparo == null && disparoArea == null) {
            // La posición de origen se convierte a coordenadas de pantalla restando el desplazamiento, porque el proyectil viaja en pantalla.
            disparo = new Proyectil(princesa.getX() - desplazamientoMapaX, princesa.getY(),
                                    entorno.mouseX(), entorno.mouseY());
        }
        // disparo secundario con click derecho
        if (entorno.sePresionoBoton(entorno.BOTON_DERECHO) && disparo == null && disparoArea == null) {
                disparoArea = new ProyectilArea(princesa.getX() - desplazamientoMapaX,princesa.getY(),
                    entorno.mouseX(),entorno.mouseY()
                );
            }    
            
        
        if (disparo != null) {
            disparo.moverse();
            if (disparo.fueraDePantalla(800, 600)) disparo = null;
        }
        
        if (disparoArea != null) {
            disparoArea.moverse();
            if (disparoArea.fueraDePantalla(800, 600)) disparoArea = null;
        }
        
 
        // (SPAWN DE ENEMIGOS) (!!!)
        // El arreglo tiene tamaño fijo (MAX_ENEMIGOS). Un slot null significa  que está libre. Se genera un nuevo enemigo si hay menos de MIN_ENEMIGOS vivos (mantiene mínimo en pantalla), o SI pasaron más de 90 (lo podemnos cambiar si querés) ticks desde el último spawn.
        // Solo se crea uno por tick; el break sale del loop al encontrar el primer slot libre.
        
        int vivos = 0;
        for (Enemigo e : enemigos) if (e != null) vivos++;
 
        ticksDesdeUltimoEnemigo++;
        if (vivos < MIN_ENEMIGOS || ticksDesdeUltimoEnemigo > 90) {
            for (int i = 0; i < enemigos.length; i++) {
                if (enemigos[i] == null) { // SLOT LIBRE ENCONTRADO
                    boolean desdeDerecha = Math.random() < 0.5; // lado aleatorio
                    double yAleatorio = 100 + Math.random() * 350; // altura aleatoria
                    enemigos[i] = new Enemigo(desdeDerecha, yAleatorio);
                    ticksDesdeUltimoEnemigo = 0; // reinicia el temporizador
                    break; // solo un enemigo por tick
                }
            }
        }
 
        // MOVER Y COLISIONAR ENEMIGOS (!!!)
        // Se recorre el arreglo. Los slots null se saltan. Cuando un enemigo muere o sale de pantalla, su slot pasa a null para que pueda reutilizarse en el siguiente spawn.
       
        // Posición de la princesa convertida a coordenadas de pantalla para comparar con la posición del enemigo 
        double xPrincessPantalla = princesa.getX() - desplazamientoMapaX;
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] == null) continue;// slot vacío, se salta
 
            enemigos[i].moverse();
            
            // Si el enemigo salió de la ventana se elimina.
            if (enemigos[i].fueraDePantalla()) {
                enemigos[i] = null;
                continue;
            }
 
            // Colisión con el proyectil

            if (disparo != null && enemigos[i].colisionaConProyectil(disparo.getX(), disparo.getY())) {
            
            	// soltarPocion para convertir a coordenada mundo.
                agregarPocion(enemigos[i].soltarPocion(desplazamientoMapaX));
                enemigos[i] = null; // cuando muere libera el espacio para que se cree otro enemigo
                disparo = null; // el disparo desaparece
                continue;
            }
            
            // Colisión con el proyectil en area
            
            if (disparoArea != null &&
            	    enemigos[i].colisionaConProyectil(disparoArea.getX(),disparoArea.getY())) {

            	    explotar(disparoArea);

            	    disparoArea = null;
            	    break;
            	}
 
            // Colisión con la princesa: se compara con su posición en pantalla.

            if (enemigos[i] != null &&
             enemigos[i].colisionaCon(xPrincessPantalla, princesa.getY(),
                                          princesa.getAncho(), princesa.getAlto())) {
                vidas--;
                enemigos[i] = null;
                continue;
            }
 
            enemigos[i].dibujarse(entorno);
        }
 
        // --- Pociones ---
        for (int i = 0; i < pociones.length; i++) {
            if (pociones[i] == null) continue; // slot vacío, se salta
            // moverse() aplica gravedad y colisiona con islas para que la poción quede posada encima de ellas en vez de atravesarlas.
            pociones[i].moverse(mapa.getIslas());
 
            // Si cayó fuera del mapa (en Y), se elimina.
            if (pociones[i].fueraDePantalla()) {
                pociones[i] = null; // slot liberado
                continue;
            }
 
            // Colisión con la princesa en coordenadas MUNDO (princesa.getX() = xMundo).
            if (pociones[i].colisionaCon(princesa.getX(), princesa.getY(),
                                          princesa.getAncho(), princesa.getAlto())) {
                vidas++;            // la princesa recupera una vida
                pociones[i] = null; // poción consumida, espacio en el array libre
                continue;
            }
 
            // Dibuja la poción pasando el desplazamiento para convertir a pantalla.
            pociones[i].dibujarse(entorno, desplazamientoMapaX);
        }
 
        // DIBUJOS
        princesa.dibujarse(entorno, desplazamientoMapaX);
        if (disparo != null) disparo.dibujarse(entorno);
        if (disparoArea != null) disparoArea.dibujarse(entorno);
        
        //entorno.escribirTexto("Vidas: " + vidas, 20, 30);
 
        dibujarVidas();
        
        // Condicion de derrota: sin vidas
        if (vidas <= 0) {
            estado = PERDIDO;
            return;
        }
 
        // Condicion de derrota: sin vidas pero despues de caer al vacio
        
        if (princesa.getY() > 650) {
            vidas--;
            if (vidas <= 0) {
                estado = PERDIDO;
                return;
            }
            princesa = new Elizabeth(400, 300);
            mapa     = new Mapa();
        }
 
        // Condicion de victoria: llegar al castillo
        if (princesa.getX() >= mapa.getXCastillo() - 80) {
            estado = GANADO;
        }
    }
 
    // Pociones, esto lo podes cambiar de lugar para que sea más prolijo si querés, yo lo puse acá porque fue lo último que hice xD
    private void agregarPocion(Pocion p) {
        if (p == null) return;
        for (int i = 0; i < pociones.length; i++) {
            if (pociones[i] == null) {
                pociones[i] = p;
                return;
            }
        }
    }
    
    // Disparo secundario; proyectil en area
    private void explotar(ProyectilArea p) {

        for (int i = 0; i < enemigos.length; i++) {

            if (enemigos[i] == null)
                continue;

            // distancia entre enemigo y centro de explosión
            double dx =
                enemigos[i].getX() - p.getX();

            double dy =
                enemigos[i].getY() - p.getY();

            double distancia =
                Math.sqrt(dx*dx + dy*dy);

            // si está dentro del radio muere
            if (distancia <= p.getRadioExplosion()) {
                enemigos[i] = null;
            }
        }
    }
    
    // Vidas
        private void dibujarVidas() {
        	for (int i = 0; i < vidas; i++) {
        		entorno.dibujarImagen(
        				imagenVida,
        				35 + i * 40,
        				30,
        				0,
        				0.02
        		);
        	}       	
        }       
    
    
    public static void main(String[] args) {
        new Juego();
    }
}