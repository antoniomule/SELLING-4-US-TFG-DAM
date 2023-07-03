/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author Antonio David
 */

        
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProvinciasCercanas {
    
    
    public String listaProvinciasCercanas(double latitudReferencia, double longitudReferencia){
        String lista="";
        List<Provincia> provincias = new ArrayList<>();
        String csvFile = "C:\\Users\\anton\\Desktop\\PROYECTO SELLING4US\\listado-SoloAndalucia.txt";
        String line = "";
        String csvSplitBy = "\t";

         double latitud = 0;
         double longitud = 0;
         String nombre = "";
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvFile), "Windows-1252"))) {
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(csvSplitBy);
                if (fields.length >= 3) {
                    latitud = Double.parseDouble(fields[1].replace(",", "."));
                    longitud = Double.parseDouble(fields[2].replace(",", "."));
                    nombre = fields[0];
                    ;
                }
                
            double distancia = calcularDistancia(latitud, longitud, latitudReferencia, longitudReferencia);
            if(distancia<=100){
                 provincias.add(new Provincia(nombre,latitud, longitud, distancia));
            }
           
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
            Collections.sort(provincias);
            List<Provincia> provinciasCercanas = provincias.subList(1, Math.min(30, provincias.size()));
            for (Provincia provincia : provinciasCercanas) {
                lista+=provincia.getNombre()+"="+provincia.getLatitud()+"="+provincia.getLongitud()+"@";
            }
            return lista;
    }
    
    
    
    public static void main(String[] args) {
        ProvinciasCercanas PC = new ProvinciasCercanas();
        System.out.println(PC.listaProvinciasCercanas(36.52672, -6.2891));
        ;

       
    }

    private static double calcularDistancia(double latitud1, double longitud1, double latitud2, double longitud2) {
        double radioTierraKm = 6371.0;
        double dLat = Math.toRadians(latitud2 - latitud1);
        double dLon = Math.toRadians(longitud2 - longitud1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitud1)) * Math.cos(Math.toRadians(latitud2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanciaKm = radioTierraKm * c;

        return distanciaKm;
    }

   
    static class Provincia implements Comparable<Provincia> {
        private String nombre;
        private double latitud;
        private double longitud;
        private double distancia;

        

        public Provincia(String nombre, double latitud, double longitud, double distancia) {
            this.nombre=nombre;
            this.latitud = latitud;
            this.longitud = longitud;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(Provincia otra) {
            return Double.compare(distancia, otra.distancia);
        }

        public String getNombre() {
            return nombre;
        }

        public double getLatitud() {
            return latitud;
        }

        public double getLongitud() {
            return longitud;
        }
        
        @Override
        public String toString() {
            return "Nombre:"+nombre+" Latitud: " + latitud + ", Longitud: " + longitud + ", Distancia: " + distancia;
        }
    }
}


