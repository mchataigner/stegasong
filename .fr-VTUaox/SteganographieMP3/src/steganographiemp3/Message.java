/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographiemp3;

/**
 *
 * @author aseite
 */
import java.util.BitSet;

// Un classe representant les messages a integrer aux MP3

public class Message {

    String contenu; // Le contenu du message

    // Le Constructeur
    public Message(String leMessage) {
        contenu = leMessage;
    }

    // Methode de conversion du contenu en tableau de bytes (octets)
    public byte[] toBytes() {
        byte[] leTableauDeByte = null;
        leTableauDeByte = contenu.getBytes(); // La methode magique qui fait tout
        return leTableauDeByte;
    }

    public MP3 Inserer(MP3 leFichierAudio) {
        // Methode pas encore codee sensee inseree le message dans un MP3 passe en parametre
        // Redondante par rapport a la methode "ajouterContenu" de la classe MP3
        // Pas forcement necessaire mais c'est mieux si on la fait
        return leFichierAudio;
    }

    // Methode qui transforme le contenu du message en tableau de bits (booleens)
    public BitSet Parser() {
        byte[] leMessageEnBytes = this.toBytes(); // Reutilisation de la methode au dessus

        // Classe BitSet tres pratique
        BitSet leMessageEnBits = new BitSet(leMessageEnBytes.length * 8);

        // Truc que j'ai trouve sur le net sans vraiment trop comprendre comment ca marche
        // mais ca fait bien son travail, c'est à dire convertir un tableau de bytes en tableau de bits
        for (int i = 0; i < leMessageEnBytes.length * 8; i++) {
            if ((leMessageEnBytes[leMessageEnBytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                leMessageEnBits.set(i);
            }
        }
        return leMessageEnBits;
    }
}
