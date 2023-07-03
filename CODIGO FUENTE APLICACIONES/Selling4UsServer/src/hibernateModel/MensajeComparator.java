/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernateModel;
import java.util.Comparator;
/**
 *
 * @author Antonio David
 */


public class MensajeComparator implements Comparator<Mensaje> {
    @Override
    public int compare(Mensaje mensaje1, Mensaje mensaje2) {
        return mensaje1.getFechaHora().compareTo(mensaje2.getFechaHora());
    }
}
