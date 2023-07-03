/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selling4usserver;

/**
 *
 * @author Antonio David
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Antonio David
 */
import DAO.Selling4UsDAO;
import Utils.AnunciosScheduler;
import Utils.ProvinciasCercanas;
import hibernateModel.Rol;
import hibernateModel.Usuario;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    static Map<Integer, PrintWriter> CollecionUsuarioHilo = new TreeMap<>();
    static TreeMap<String, String> ColeccionAnunciosXCategoria = new TreeMap<>();
    static TreeMap<String, TreeMap<Integer, Boolean>> ColeccionUsuariosXCategoria = new TreeMap<>();
    static TreeMap<String, AnunciosScheduler> ColeccionCategoriaXThreadScheduler = new TreeMap<>();
    static TreeMap<Integer, PrintWriter> ColeccionMovilHilo = new TreeMap<>();
    public static class HiloCliente extends Thread {
        private final Socket clientSocket;
        private static final int WAITING = 0;//Poner comentario de cada estado del protocolo
        private static final int LOGINREGISTRAR = 1;
        private static final int RUNNING = 2;
        private static final int SALIR = 3;
        private static String ServerCon = "Server:";
        private int estado = WAITING;
        Selling4UsDAO selling4UsDAO;
        PrintWriter out = null;
        BufferedReader in = null;
        int portUDP;
        InetAddress IPAddressUDP;
        int puertoNew;
        String ip = null;
        int idUsuario=-1;
        String categoriaUsuario="";
        
        //-------------//
        AnunciosScheduler anunciosScheduler = new AnunciosScheduler(this);
        //-------------//
        
        ProvinciasCercanas provinciasCercanas = new ProvinciasCercanas();
       

        public HiloCliente(Socket clientsocket, int puerto, String ip, int portUDP) throws SocketException {
            this.clientSocket = clientsocket;
            this.portUDP = portUDP;
            this.ip = ip;
        }
        
        public HiloCliente(Socket clientsocket) throws SocketException {
            this.clientSocket = clientsocket;
        }
        

        public void run() {
            selling4UsDAO = new Selling4UsDAO();
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String inputLine;
            try {
                while ((inputLine = in.readLine()) != null) {
                   
                    ProcesarEntrada(inputLine.split(":")[1], inputLine.split(":")[2], inputLine.split(":")[3]);
                }
            }catch (SocketException e) {
                try {
                    out.close();
                    in.close();
                    clientSocket.close();
                    System.out.println("Perdida la conexion con el cliente, cliente desconectado");
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }

        void ProcesarEntrada(String TipoAccion, String mensajeEntradaCodigo, String mensajeEntradaArgumento) throws ParseException, IOException {
            switch (estado) {
                case WAITING:
                    switch (TipoAccion) {
                        case "CONEXION":
                            switch (mensajeEntradaCodigo) {
                                case "peticion":
                                    peticion();
                                    break;
                                case "salir":
                                    estado = SALIR;
                                    break;
                                case "CerrarConexion":
                                    eliminarUsaurioHilo(Integer.parseInt(mensajeEntradaArgumento));
                                   
                                    break;
                            }
                    }
                    break;
                case LOGINREGISTRAR:
                    switch (TipoAccion) {
                        case "USUARIO":
                            switch (mensajeEntradaCodigo) {
                                case "Login":
                                    login(mensajeEntradaArgumento);
                                    break;
                                case "CerrarConexion":
                                    eliminarUsaurioHilo(Integer.parseInt(mensajeEntradaArgumento.split("\\|")[0]));
                                    eliminarUsaurioXCategoria(Integer.parseInt(mensajeEntradaArgumento.split("\\|")[0]));
                                    break;
                                case "anadirUsuarioEstandar":
                                    añadirUsuarioEstandar(mensajeEntradaArgumento);
                                    break;
                            }
                            break;
                    }
                case RUNNING:
                    switch (TipoAccion) {
                        case "USUARIO":
                            switch (mensajeEntradaCodigo) {
                                case "atras":
                                    estado = LOGINREGISTRAR;
                                     eliminarUsaurioHilo(Integer.parseInt(mensajeEntradaArgumento.split("\\|")[0]));
                                    if(existeThreadSchedulerXCategoria(categoriaUsuario)){
                                        int cont=0;
                                            for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                                               
                                                if (entry.getKey().equals(categoriaUsuario)) {
                                                    for (Map.Entry<Integer, Boolean> entrySet : entry.getValue().entrySet()) {
                                                        cont++;
                                                    }
                                                }
                                            }
                                        if(cont==1){//significa que es el ultimo moderador de su categoria en salir
                                            eliminiarThreadXCategoriaColeccionCategoriaXThread(categoriaUsuario);
                                        }
                                        }
                                    try {
                                        selling4UsDAO.volverEstadoAnunciosNoRevisadoAnuncios(mensajeEntradaArgumento.split("\\|")[1].split("="));
                                    } catch (Exception e) {
                                    }eliminarUsaurioXCategoria(Integer.parseInt(mensajeEntradaArgumento.split("\\|")[0]));
                                    break;
                                case "CerrarConexion":
                                    eliminarUsaurioHilo(Integer.parseInt(mensajeEntradaArgumento.split("\\|")[0]));
                                    if(existeThreadSchedulerXCategoria(categoriaUsuario)){
                                        int cont=0;
                                            for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                                                if (entry.getKey().equals(categoriaUsuario)) {
                                                    
                                                    for (Map.Entry<Integer, Boolean> entrySet : entry.getValue().entrySet()) {
                                                        cont++;
                                                    }
                                                }
                                            }
                                        if(cont==1){//significa que es el ultimo moderador de su categoria en salir
                                            eliminiarThreadXCategoriaColeccionCategoriaXThread(categoriaUsuario);
                                            selling4UsDAO.volverEstadoAnunciosNoRevisadoAnuncios(vaciarColeccionAnunciosXCategoria(categoriaUsuario));
                                        }
                                    }
                                    try {
                                        selling4UsDAO.volverEstadoAnunciosNoRevisadoAnuncios(mensajeEntradaArgumento.split("\\|")[1].split("="));
                                    } catch (Exception e) {
                                    }
                                    
                                    
                                    eliminarUsaurioXCategoria(Integer.parseInt(mensajeEntradaArgumento.split("\\|")[0]));
                                    break;
                                case "getMisNotificacionesOffline":
                                    getMisNotificacionesOffline(mensajeEntradaArgumento);
                                    break;
                                case "valorarUsuario":
                                    valorarUsuario(mensajeEntradaArgumento);
                                    break;
                                case "CerrarConexionMovil":
                                    eliminarUsaurioHiloMovil(Integer.parseInt(mensajeEntradaArgumento));
                                    break;
                                case "actualizarContrasena":
                                    actualizarContraseña(mensajeEntradaArgumento);
                                    break;
                            }
                            break;
                        case "CATEGORIAS":
                            switch (mensajeEntradaCodigo) {
                                case "GetCategorias":
                                    getCategorias();
                                    break;
                            }
                            break;
                        case "MODERADOR":
                            switch (mensajeEntradaCodigo) {
                                case "AnadirModerador":
                                    añadirModerador(mensajeEntradaArgumento);
                                    break;
                                case "GetModeradores":
                                    getmoderadores();
                                    break;
                                case "deleteModerador":
                                    deleteModerador(mensajeEntradaArgumento);
                                    break;
                            }
                            break;
                            
                        case "CHAT":
                             switch (mensajeEntradaCodigo) {
                                case "getMensajesChat":
                                getMensajesChat(mensajeEntradaArgumento);
                                    break;
                                case "addMensajeChat":
                                    addMensajeChat(mensajeEntradaArgumento, true);
                                    break;
                                case "getMisChats":
                                    getMisChats(mensajeEntradaArgumento);
                                    break;
                                case "crearChat":
                                    crearChat(mensajeEntradaArgumento);
                                    break;
                                case "mensajeNoleido":
                                    mensajeNoleido(mensajeEntradaArgumento);
                                    break;
                             }
                         break;

                        case "RESTRICCION":
                            switch (mensajeEntradaCodigo) {
                                case "getTiposRestriccion":
                                    getTiposRestriccion();
                                    break;
                                case "getRestriccionXTipo":
                                    getRestriccionXTipo(mensajeEntradaArgumento);
                                    break;
                                case "anadirRestriccion":
                                    añadirRestriccion(mensajeEntradaArgumento);
                                    break;
                            }
                            break;

                            case "MAPA":
                                switch(mensajeEntradaCodigo){
                                    case "getProvinciasCercanas":
                                        getProvinciasCercanas(mensajeEntradaArgumento);
                                        break;
                                }
                            break;
                        case "ANUNCIO":
                            switch (mensajeEntradaCodigo) {
                                case "getAnuncios":
                                    getAnuncios(mensajeEntradaArgumento);
                                    break;
                                case "subirAnuncio":
                                    subirAnuncio(mensajeEntradaArgumento);
                                    break;
                                case "getMisAnunciosAppAndroid":
                                    getMisAnunciosAppAndroid(Integer.parseInt(mensajeEntradaArgumento));
                                    break;
                                case "revisarAnunciosCheck":
                                    revisarAnunciosCheck(mensajeEntradaArgumento);
                                    break;
                                case "bloquearAnuncio":
                                    bloquearAnuncio(mensajeEntradaArgumento);
                                    break;
                                case "getAnunciosAppAndroid":
                                    getAnunciosAppAndroid(mensajeEntradaArgumento);
                                    break;
                                case "getMisComprasAppAndroid":
                                    getMisComprasAppAndroid(mensajeEntradaArgumento);
                                    break;
                                case "comprarAnuncio":
                                    comprarAnuncio(mensajeEntradaArgumento);
                                    break;
                                case "ofertaAnuncio":
                                    ofertaAnuncio(mensajeEntradaArgumento);
                                    break;
                                case "getMisOfertasRecibidas":
                                    getMisOfertasRecibidas(mensajeEntradaArgumento);
                                    break;
                                case "getMisVentas":
                                    getMisVentas(Integer.valueOf(mensajeEntradaArgumento));
                                    break;
                                case "denegarOferta":
                                    denegarOferta(mensajeEntradaArgumento);
                                    break;
                                case "aceptarOferta":
                                    aceptarOferta(mensajeEntradaArgumento);
                                    break;
                                case "getAnunciosFiltro":
                                    getAnunciosFiltro(mensajeEntradaArgumento);
                                    break;
                                case "getMisComprasPendientesAppAndroid":
                                    getMisComprasPendientesAppAndroid(mensajeEntradaArgumento);
                                    break;
                                case "pagoAnuncio":
                                    pagoAnuncio(mensajeEntradaArgumento);
                                    break;
                                case "eliminarAnuncio":
                                    eliminarAnuncio(mensajeEntradaArgumento);
                                    break;
                                case "getMisFavoritos":
                                    getMisFavoritos(mensajeEntradaArgumento);
                                    break;
                                case "anadirFavorito":
                                    añadirFavorito(mensajeEntradaArgumento);
                                    break;
                                case "eliminarFavorito":
                                    eliminarFavorito(mensajeEntradaArgumento);
                                    break;
                                case "getMisAnunciosBloqueados":
                                    getMisAnunciosBloqueados(mensajeEntradaArgumento);
                                    break;
                                case "modificarAnuncio":
                                    modificarAnuncio(mensajeEntradaArgumento);
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }

       

        void peticion() throws IOException {
            out.println(ServerCon + "CONEXION:ComenzamosConexion:");
            estado = LOGINREGISTRAR;
        }

        void login(String mensajeEntradaArgumento) {
            String[] nInputArgumento = mensajeEntradaArgumento.split("=");
            Logger.getLogger("org.hibernate").setLevel(Level.OFF);
            synchronized (this) {
                if (selling4UsDAO.getUsuarioXNombre(nInputArgumento[0], nInputArgumento[1])) {
                    estado = RUNNING;
                    out.println(ServerCon + "Correcto");
                    Usuario user = (Usuario) selling4UsDAO.getUsuarioXNombreUser(nInputArgumento[0], nInputArgumento[1]);

                    String tipo = "";
                    String categoria = null;
                    for (Object roles : user.getRoles()) {
                        Rol rol = (Rol) roles;

                        if (rol.getTipoRol().equals("Administrador")) {
                            tipo = "Administrador";
                        }
                        if (rol.getTipoRol().equals("Moderador")) {
                            tipo = "Moderador";
                            categoria = selling4UsDAO.getCategoriaModerador(user.getIdUsuario());
                        }
                        if (rol.getTipoRol().equals("Estándar")) {
                            añadirUsaurioHiloMovil(user.getIdUsuario(), out);
                            tipo = "Estándar";
                        }
                    }
                    idUsuario=user.getIdUsuario();
                    categoriaUsuario=categoria;
                    out.println(ServerCon+"USUARIO:"+user.getIdUsuario() + "=" + user.getNombre() + "=" + tipo + "=" + categoria);
                    if(tipo.equals("Moderador")){
                    añadirUsaurioHilo(user.getIdUsuario(), out);
                    añadirUsaurioXCategoria(categoria, user.getIdUsuario());
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
            }
        }
        
        
        private void añadirUsuarioEstandar(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
                int idRegistro=selling4UsDAO.añadirUsuarioEstandar(mensajeEntradaArgumento.split("\\|")[0], mensajeEntradaArgumento.split("\\|")[1], mensajeEntradaArgumento.split("\\|")[2], mensajeEntradaArgumento.split("\\|")[3]);
               if(idRegistro!=0){
                  out.println(ServerCon + "Correcto:"+idRegistro);
                  estado = RUNNING;
                  añadirUsaurioHiloMovil(idRegistro, out);
                  idUsuario=idRegistro;
               }else out.println(ServerCon + "Incorrecto");
             }
        }

        void getCategorias() {
            List<Object> lista = new ArrayList<>();
            synchronized (this) {
                lista = selling4UsDAO.getCategorias();
            }
            if (lista != null) {
                out.println(ServerCon + "Correcto");
                String envio = "";
                for (int i = 0; i < lista.size(); i++) {
                    envio += lista.get(i) + "=";
                }
                out.println(ServerCon+"CATEGORIAS:"+envio);
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }

        void añadirModerador(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this){
            String[] nInputArgumento = mensajeEntradaArgumento.split("=");
            if (selling4UsDAO.añadirModerador(nInputArgumento[0], nInputArgumento[1], nInputArgumento[2], nInputArgumento[3], nInputArgumento[4])) {
                out.println(ServerCon + "Correcto");
            } else {
                out.println(ServerCon + "Incorrecto");
            }
            }
        }
        
        void getmoderadores() {
           
            String getModeradores = "";
            synchronized (this) {
                getModeradores = selling4UsDAO.getModeradores();
            }
            if (!getModeradores.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon+"MODERADOR:"+getModeradores);
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }

        void deleteModerador(String mensajeEntradaArgumento) {
            synchronized (this) {
                
                if (selling4UsDAO.DeleteModerador(mensajeEntradaArgumento)) {
                    out.println(ServerCon + "Correcto");
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
            }
        }

        void getTiposRestriccion() {
            String getTiposRestriccion = "";
            synchronized (this) {
                getTiposRestriccion = selling4UsDAO.getTiposRestriccion();
            }
            if (!getTiposRestriccion.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon+"RESTRICCION:"+getTiposRestriccion);
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }

        void getRestriccionXTipo(String mensajeEntradaArgumento) {
            String getRestriccionXTipo = "";
            synchronized (this) {
                getRestriccionXTipo = selling4UsDAO.getRestriccionXTipo(mensajeEntradaArgumento);
            }
            if (!getRestriccionXTipo.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon+"RESTRICCION:"+getRestriccionXTipo);
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }
        
        

        void añadirRestriccion(String mensajeEntradaArgumento) throws ParseException {
            String[] nInputArgumento = mensajeEntradaArgumento.split("=");
            if (selling4UsDAO.añadirRestriccionYTipo(nInputArgumento[0], nInputArgumento[1])) {
                out.println(ServerCon + "Correcto");
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }

        void getAnuncios(String Categoria) {
            String getAnuncios = null;

            if (getTamañoColeccionAnunciosXCategoria(Categoria) > 0) {
                cambiarEstadoEsperar(idUsuario);
                out.println(ServerCon + "Correcto");
                out.println(ServerCon + "ANUNCIOS:" + cogerAnuncioCola(Categoria));
            } else {
                synchronized (this) {
                    getAnuncios = selling4UsDAO.getAnuncios(Categoria);
                }
                if (getAnuncios != null || getAnuncios.equals("")) {
                    out.println(ServerCon + "Correcto");
                    out.println(ServerCon + "ANUNCIOS:" + getAnuncios);
                    if (getAnuncios.equals("")) {
                        
                            
                        if(!existeThreadSchedulerXCategoria(Categoria)){
                            añadirThreadXCategoriaColeccionCategoriaXThread(Categoria, anunciosScheduler);
                        }
                        cambiarEstadoEsperar(idUsuario);
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
            }
        }
        
        private void getMisNotificacionesOffline(String mensajeEntradaArgumento) {
            String getMisNotificacionesOffline = null;
                synchronized (this) {
                    getMisNotificacionesOffline = selling4UsDAO.getMisNotificacionesOffline(Integer.valueOf(mensajeEntradaArgumento));
                }
                if (getMisNotificacionesOffline != null || getMisNotificacionesOffline.equals("")) {
                    out.println(ServerCon + "NOTIFICACION:"+"BIENVENIDO"+":"+getMisNotificacionesOffline);
                    if (getMisNotificacionesOffline.equals("")) {
                    }
                }
        }
        
        void getMisAnunciosAppAndroid(int id) {
            String getMisAnunciosAppAndroid = null;

            synchronized (this) {
                getMisAnunciosAppAndroid = selling4UsDAO.getMisAnunciosAppAndroid(id);
            }
            if (getMisAnunciosAppAndroid != null || getMisAnunciosAppAndroid.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon + "ANUNCIOS:" + getMisAnunciosAppAndroid);
                if (getMisAnunciosAppAndroid.equals("")) {
                }
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }
        
        
        void getMisVentas(int id) {
            String getMisVentas = null;
                synchronized (this) {
                    getMisVentas = selling4UsDAO.getMisVentas(id);
                }
                if (getMisVentas != null || getMisVentas.equals("")) {
                    out.println(ServerCon + "Correcto");
                   
                    out.println(ServerCon + "ANUNCIOS:" + getMisVentas);
                    if (getMisVentas.equals("")) {
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
        }
        
        
        void subirAnuncio (String anuncio){
            boolean subido = false;
            synchronized (this){
                subido = selling4UsDAO.subirAnuncio(anuncio);
            }
            if(subido){
                out.println(ServerCon + "Correcto");
            }else{
                 out.println(ServerCon + "Incorrecto");
            }
        }
        
        
        
        void getAnunciosAppAndroid(String cadena) {
            String getAnunciosAppAndroid = null;
            String categoria=cadena.split("\\|")[0];
            int id = Integer.valueOf(cadena.split("\\|")[1]);
                synchronized (this) {
                    getAnunciosAppAndroid = selling4UsDAO.getAnunciosAppAndroid(categoria, id);
                }
                if (getAnunciosAppAndroid != null || getAnunciosAppAndroid.equals("")) {
                    out.println(ServerCon + "Correcto");
                    out.println(ServerCon + "ANUNCIOS:" + getAnunciosAppAndroid);
                    if (getAnunciosAppAndroid.equals("")) {
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
            }
        
        
         void revisarAnunciosCheck(String mensajeEntradaArgumento) throws ParseException{
            if (selling4UsDAO.revisarAnunciosCheck(mensajeEntradaArgumento)) {
                out.println(ServerCon + "Correcto");
            } else {
                out.println(ServerCon + "Incorrecto");
            }
         }
         
         void bloquearAnuncio(String mensajeEntradaArgumento) throws ParseException{
            if (selling4UsDAO.bloquearAnuncio(mensajeEntradaArgumento)) {
                out.println(ServerCon + "Correcto");
                enviarNotificacionClienteMovil(Integer.valueOf(mensajeEntradaArgumento.split("=")[1]), "BLOQUEO DE ANUNCIO", mensajeEntradaArgumento.split("=")[2]+"="+mensajeEntradaArgumento.split("=")[3]+"="+mensajeEntradaArgumento.split("=")[4]);
            } else {
                out.println(ServerCon + "Incorrecto");
            }
         }
         
         private void comprarAnuncio(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
               if(selling4UsDAO.comprarAnuncio(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]), mensajeEntradaArgumento.split("\\|")[2], mensajeEntradaArgumento.split("\\|")[3])){
                  out.println(ServerCon + "Correcto");
                  enviarNotificacionClienteMovil(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[4]), "COMPRA", mensajeEntradaArgumento.split("\\|")[5]);
               }else out.println(ServerCon + "Incorrecto");
             }
         }
         private void pagoAnuncio(String mensajeEntradaArgumento) throws ParseException {
             synchronized (this) {
               if(selling4UsDAO.pagoAnuncio(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]))){
                  out.println(ServerCon + "Correcto");
                }else out.println(ServerCon + "Incorrecto");
             }
         }
         
         private void ofertaAnuncio(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
               if(selling4UsDAO.ofertaAnuncio(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]), mensajeEntradaArgumento.split("\\|")[2])){
                  out.println(ServerCon + "Correcto");
                   enviarNotificacionClienteMovil(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[3]), "OFERTA", mensajeEntradaArgumento.split("\\|")[2]+"="+mensajeEntradaArgumento.split("\\|")[4]);
               }else out.println(ServerCon + "Incorrecto");
             }
         }
         
         
         private void getMisComprasAppAndroid(String mensajeEntradaArgumento) {
            String getMisComprasAppAndroid = null;
                synchronized (this) {
                    getMisComprasAppAndroid = selling4UsDAO.getMisComprasAppAndroid(Integer.valueOf(mensajeEntradaArgumento));
                }
                if (getMisComprasAppAndroid != null || getMisComprasAppAndroid.equals("")) {
                    out.println(ServerCon + "Correcto");
                    out.println(ServerCon + "ANUNCIOS:" + getMisComprasAppAndroid);
                    if (getMisComprasAppAndroid.equals("")) {
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
        }
         
         private void getMisComprasPendientesAppAndroid(String mensajeEntradaArgumento) {
            String getMisComprasPendientesAppAndroid = null;
                synchronized (this) {
                    getMisComprasPendientesAppAndroid = selling4UsDAO.getMisComprasPendientesAppAndroid(Integer.valueOf(mensajeEntradaArgumento));
                }
                if (getMisComprasPendientesAppAndroid != null || getMisComprasPendientesAppAndroid.equals("")) {
                    out.println(ServerCon + "Correcto");
                    out.println(ServerCon + "ANUNCIOS:" + getMisComprasPendientesAppAndroid);
                    if (getMisComprasPendientesAppAndroid.equals("")) {
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
         
         }
         
         
         private void getProvinciasCercanas(String mensajeEntradaArgumento) {
            synchronized (this) {
            out.println(ServerCon + "Correcto");
            out.println(ServerCon+"MAPA:"+provinciasCercanas.listaProvinciasCercanas(Double.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Double.valueOf(mensajeEntradaArgumento.split("\\|")[1])));
              }
         }
         
         
         private void getMisOfertasRecibidas(String mensajeEntradaArgumento) {
         String getMisOfertasRecibidas = null;
                synchronized (this) {
                    getMisOfertasRecibidas = selling4UsDAO.getMisOfertasRecibidas(Integer.valueOf(mensajeEntradaArgumento));
                }
                if (getMisOfertasRecibidas != null || getMisOfertasRecibidas.equals("")) {
                    out.println(ServerCon + "Correcto");
                    out.println(ServerCon + "ANUNCIOS:" + getMisOfertasRecibidas);
                    if (getMisOfertasRecibidas.equals("")) {
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
         }
         
          private void denegarOferta(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
               if(selling4UsDAO.denegarOferta(Integer.valueOf(mensajeEntradaArgumento))){
                  out.println(ServerCon + "Correcto");
                  }else out.println(ServerCon + "Incorrecto");
             }
          }
         
         private void aceptarOferta(String mensajeEntradaArgumento) throws ParseException {
         synchronized (this) {
             String[] resuelto=selling4UsDAO.aceptarOferta(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), mensajeEntradaArgumento.split("\\|")[1]).split("=");
               if(!resuelto[0].equals("")){
                  out.println(ServerCon + "Correcto");
                   enviarNotificacionClienteMovil(Integer.valueOf(resuelto[0]), "OFERTA ACEPTADA", "Se ha aceptado tu oferta por el artículo "+resuelto[1]);
               }else out.println(ServerCon + "Incorrecto");
             } 
         
         }
         
         private void getAnunciosFiltro(String mensajeEntradaArgumento) {
            String getAnunciosFiltro = null;
            synchronized (this) {
                getAnunciosFiltro = selling4UsDAO.getAnunciosFiltro(mensajeEntradaArgumento);
            }
            if (getAnunciosFiltro != null || getAnunciosFiltro.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon + "ANUNCIOS:" + getAnunciosFiltro);
                if (getAnunciosFiltro.equals("")) {
                }
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }
         
         private void eliminarAnuncio(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
               if(selling4UsDAO.eliminarAnuncio(Integer.valueOf(mensajeEntradaArgumento))){
                  out.println(ServerCon + "Correcto");
                  }else out.println(ServerCon + "Incorrecto");
             }
         }
         
         
         private void getMisFavoritos(String mensajeEntradaArgumento) {
            
         String getMisFavoritos = null;
                synchronized (this) {
                    getMisFavoritos = selling4UsDAO.getMisFavoritos(Integer.valueOf(mensajeEntradaArgumento));
                }
                if (getMisFavoritos != null || getMisFavoritos.equals("")) {
                    out.println(ServerCon + "Correcto");
                    out.println(ServerCon + "ANUNCIOS:" + getMisFavoritos);
                    if (getMisFavoritos.equals("")) {
                    }
                } else {
                    out.println(ServerCon + "Incorrecto");
                }
         }
         
         private void añadirFavorito(String mensajeEntradaArgumento) throws ParseException {
             synchronized (this) {
               if(selling4UsDAO.añadirFavorito(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]))){
                  out.println(ServerCon + "Correcto");
                }else out.println(ServerCon + "Incorrecto");
             }
         }
    
        private void eliminarFavorito(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
                if (selling4UsDAO.eliminarFavorito(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]))) {
                    out.println(ServerCon + "Correcto");
                } else out.println(ServerCon + "Incorrecto");
            }
        }
         
        
        private void getMisAnunciosBloqueados(String mensajeEntradaArgumento) {
            String getMisAnunciosBloqueados = null;

            synchronized (this) {
                getMisAnunciosBloqueados = selling4UsDAO.getMisAnunciosBloqueados(Integer.valueOf(mensajeEntradaArgumento));
            }
            if (getMisAnunciosBloqueados != null || getMisAnunciosBloqueados.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon + "ANUNCIOS:" + getMisAnunciosBloqueados);
                if (getMisAnunciosBloqueados.equals("")) {
                }
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }

        private void modificarAnuncio(String mensajeEntradaArgumento) throws ParseException {
            boolean modificado = false;
            synchronized (this){
                modificado = selling4UsDAO.modificarAnuncio(mensajeEntradaArgumento);
            }
            if(modificado){
                out.println(ServerCon + "Correcto");
            }else{
                 out.println(ServerCon + "Incorrecto");
            }
        }
        
        private void valorarUsuario(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this){
                selling4UsDAO.valorarUsuario(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Math.round(Float.parseFloat(mensajeEntradaArgumento.split("\\|")[1])));
            }
        }
        
        
        private void getMensajesChat(String mensajeEntradaArgumento) {
            String getMensajesChat = null;

            synchronized (this) {
                getMensajesChat = selling4UsDAO.getMensajesChat(Integer.valueOf(mensajeEntradaArgumento));
                
            }
            if (getMensajesChat != null || getMensajesChat.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon + "CHAT:" + getMensajesChat);
                if (getMensajesChat.equals("")) {
                }
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }
        
        private void addMensajeChat(String mensajeEntradaArgumento, boolean primerMensaje) throws ParseException {
            synchronized (this) {
               if(selling4UsDAO.addMensajeChat(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[2]), mensajeEntradaArgumento.split("\\|")[3])){
                  if(primerMensaje){
                  out.println(ServerCon + "Correcto");
                  enviarNotificacionClienteMovil(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[2]), "CHAT", mensajeEntradaArgumento.split("\\|")[0]+"|"+ mensajeEntradaArgumento.split("\\|")[1]+"|"+ mensajeEntradaArgumento.split("\\|")[2]+"|"+ mensajeEntradaArgumento.split("\\|")[3]);
                }
               }else out.println(ServerCon + "Incorrecto");
             }
        }
        
        private void crearChat(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
                int idChat=selling4UsDAO.crearChat(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]));
               if(idChat!=0){
                   String primerMensaje = idChat +"|"+Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1])+"|"+ Integer.valueOf(mensajeEntradaArgumento.split("\\|")[2])+"|"+ "Hola, Estoy interesado en tu Anuncio!";
                   addMensajeChat(primerMensaje, false);
                  out.println(ServerCon + "Correcto:"+idChat);
                   enviarNotificacionClienteMovil(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[2]), "CHAT", "Tienes un nuevo chat que atender para tu anuncio: "+mensajeEntradaArgumento.split("\\|")[3]);
                
               }else out.println(ServerCon + "Incorrecto");
             }
        }
        
        private void getMisChats(String mensajeEntradaArgumento) {
            String getMisChat = null;

            synchronized (this) {
                getMisChat = selling4UsDAO.getMisChat(Integer.valueOf(mensajeEntradaArgumento));
             }
            if (getMisChat != null || getMisChat.equals("")) {
                out.println(ServerCon + "Correcto");
                out.println(ServerCon + "CHAT:" + getMisChat);
                if (getMisChat.equals("")) {
                }
            } else {
                out.println(ServerCon + "Incorrecto");
            }
        }
        
        
        private void mensajeNoleido(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
                selling4UsDAO.mensajeNoleido(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), Integer.valueOf(mensajeEntradaArgumento.split("\\|")[1]));
            }
        }
        private void actualizarContraseña(String mensajeEntradaArgumento) throws ParseException {
            synchronized (this) {
               if(selling4UsDAO.actualizarContraseña(Integer.valueOf(mensajeEntradaArgumento.split("\\|")[0]), mensajeEntradaArgumento.split("\\|")[1], mensajeEntradaArgumento.split("\\|")[2])){
                 out.println(ServerCon + "Correcto");
               }else out.println(ServerCon + "Incorrecto");
             }
        }
    
        
         //-----------------------------------------------------------------------//
         
        synchronized void añadirUsaurioHiloMovil(int usuario, PrintWriter pw) {
            ColeccionMovilHilo.put(usuario, pw);
        }
        
        synchronized void eliminarUsaurioHiloMovil(int usuario) {
            ColeccionMovilHilo.remove(usuario);
            
        }
        
        synchronized void añadirUsaurioHilo(int usuario, PrintWriter pw) {
            CollecionUsuarioHilo.put(usuario, pw);
        }

        synchronized void eliminarUsaurioHilo(int usuario) {
            CollecionUsuarioHilo.remove(usuario);
        }
        
        synchronized void enviarNotificacionClienteMovil(int idUsuarioPropietarioMovil, String accion, String contenido) throws ParseException {
            if (ColeccionMovilHilo.containsKey(idUsuarioPropietarioMovil)) {
                ColeccionMovilHilo.get(idUsuarioPropietarioMovil).println(ServerCon + "NOTIFICACION:"+accion+":"+contenido);
            }else{
                if(accion.equals("CHAT")){
                    if(contenido.split("|").length>2){
                        String mensajeNoLeido=contenido.split("\\|")[0]+"|"+contenido.split("\\|")[1];
                      
                        mensajeNoleido(mensajeNoLeido);
                    }
                
                }
            }
        }
        
        synchronized void añadirUsaurioXCategoria(String categoria, int usuario) {
            if (ColeccionUsuariosXCategoria.containsKey(categoria)) {
                ColeccionUsuariosXCategoria.get(categoria).put(usuario, false);
                 for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                     if(!entry.equals(categoria)){
                         entry.getValue().remove(usuario);
                     }
                }
            } else {
                
                TreeMap<Integer, Boolean> ColeccionUsuarioXEsperandoLista = new TreeMap<>();
                ColeccionUsuarioXEsperandoLista.put(usuario, false);
                ColeccionUsuariosXCategoria.put(categoria, ColeccionUsuarioXEsperandoLista);
            }
        }
        
        synchronized void eliminarUsaurioXCategoria(int usuario) {
            for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                entry.getValue().remove(usuario);
            }
            
        }
        
        synchronized int getTamañoColeccionAnunciosXCategoria(String categoria){
            int tamaño=-1;
            try {
                 tamaño = ColeccionAnunciosXCategoria.get(categoria).length();
            } catch (NullPointerException e) {
            }
            if(tamaño != -1){
                return tamaño;
            }else return -1;
        }
        
        synchronized void cambiarEstadoEsperar(int usuario) {
            for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                if(entry.getKey().equals(categoriaUsuario)){
                 entry.getValue().put(usuario, true);
                }
            }
        }

        
        
        synchronized int getTamañoColeccionUsuarioHilo(){
            return CollecionUsuarioHilo.size();
            
        }
        
        public synchronized void añadirThreadXCategoriaColeccionCategoriaXThread(String categoria, AnunciosScheduler thread){
            thread.setCategoria(categoria);
            boolean nuevo=false;
            try {
                thread.start();
                nuevo=true;
            } catch (IllegalThreadStateException e) {
                //estado terminated no se puede reanudar
                AnunciosScheduler AS = new AnunciosScheduler(this);
                AS.setCategoria(categoria);
                AS.start();
                ColeccionCategoriaXThreadScheduler.put(categoria, AS);
            }
            if(nuevo){
            ColeccionCategoriaXThreadScheduler.put(categoria, thread);
            }
            
            
        }
        
        public synchronized void eliminiarThreadXCategoriaColeccionCategoriaXThread(String categoria){
            for (Map.Entry<String, AnunciosScheduler> entry : ColeccionCategoriaXThreadScheduler.entrySet()) {
                 if(entry.getKey().equals(categoria)){
                    entry.getValue().parar();
                 }
             }
            ColeccionCategoriaXThreadScheduler.remove(categoria);
            
        }
        
        public synchronized boolean existeThreadSchedulerXCategoria(String categoria){
        boolean existe=false;
        try{
            if(ColeccionCategoriaXThreadScheduler.containsKey(categoria)){
                existe=true;
            }
        }catch(NullPointerException e){
        
        }
            return existe;
        }
        
        public synchronized void añadirAnuncioCola(String categoria, String lista) {
            if (ColeccionAnunciosXCategoria.containsKey(categoria)) {
                String listaActual = ColeccionAnunciosXCategoria.get(categoria);
                if (listaActual == null) {
                    listaActual = "";
                }
                ColeccionAnunciosXCategoria.put(categoria, listaActual.concat(lista));
            } else {
                ColeccionAnunciosXCategoria.put(categoria, lista);
            }
        }

       
        synchronized String[] vaciarColeccionAnunciosXCategoria(String categoria) {
            String[] lista = null;
            for (Map.Entry<String, String> entry : ColeccionAnunciosXCategoria.entrySet()) {
                if (entry.getKey().equals(categoria)) {
                    int tamaño = entry.getValue().split(":").length;
                    lista = new String[tamaño];
                    for (int i = 0; i < tamaño; i++) {
                        lista[i] = entry.getValue().split(":")[i].split("|")[0];
                    }
                }
            }
            
            return lista;
        }
        
        
        
        synchronized String cogerAnuncioCola(String categoria) {
            String lista = "";

            for (Map.Entry<String, String> entry : ColeccionAnunciosXCategoria.entrySet()) {
                if (entry.getKey().equals(categoria)) {
                    if (entry.getValue().split(":").length >= 2) {
                        for (int i = 0; i < 2; i++) {
                            String cadena = entry.getValue().split(":")[i];
                            if(lista.equals("")){
                                lista=cadena;
                            }else  lista += ":"+cadena;
                        }
                        ColeccionAnunciosXCategoria.put(categoria,  entry.getValue().replace(lista, ""));
                    }
                    else {
                        lista+=entry.getValue();
                        ColeccionAnunciosXCategoria.put(categoria, "");
                    }
                }
            }
            return lista;
        }
        
        
        
        public synchronized void avisoNuevoAnuncio(String categoria){
            for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                if (entry.getKey().equals(categoria)) {
                    for (Map.Entry<Integer, Boolean> entrySet : entry.getValue().entrySet()) {
                        if (entrySet.getValue()) {
                            enviarAnuncios(categoria);
                            break;
                        }
                    }
                }
            }
        }
        
        public synchronized void enviarAnuncios(String categoria) {
            //true esperando anuncios
            //false no espera anuncios
            int user = -1;
            //Busco en la coleccion de categoria-Lista de moderadores
            for (Map.Entry<String, TreeMap<Integer, Boolean>> entry : ColeccionUsuariosXCategoria.entrySet()) {
                if (entry.getKey().equals(categoria)) {
                    //Busco el idUsuario que tenga el valor false(Que no haya recibido anuncios)
                    for (Map.Entry<Integer, Boolean> entrySet : entry.getValue().entrySet()) {
                        if (entrySet.getValue()) {
                            user = entrySet.getKey();
                            
                            entry.getValue().put(user, false);
                            String anuncios = cogerAnuncioCola(categoria);
                            CollecionUsuarioHilo.get(user).println(ServerCon + "NOTIFICACION:" + anuncios);
                            break;
                        }
                    }
                }
            }
        }

        

        

        

        

        

        

        

        
    }

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("config.init"));

        int portService = Integer.parseInt(prop.getProperty("puerto"));
        ServerSocket serverSocket = null;
        System.out.println("Servidor Iniciado");
        try {
            serverSocket = new ServerSocket(portService);
        } catch (IOException e) {
            System.err.println("No es posible escuchar por el puerto: " + portService);
            System.exit(1);
        }
        Socket clientSocket = null;

        try {
            while (true) {
                clientSocket = serverSocket.accept();
                new Servidor.HiloCliente(clientSocket).start();
                System.out.println("Cliente conectado.");
            }
        } catch (IOException e) {
            System.err.println("Fallo al aceptar conexión.");
        }
    }
}
