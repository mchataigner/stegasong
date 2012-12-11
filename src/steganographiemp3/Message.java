package steganographiemp3;

import java.util.BitSet;



public class Message {

    String contenu; // Le contenu du message

    // Le Constructeur
    public Message(String leMessage) {
        contenu = leMessage;
    }

    // Methode de conversion du contenu en tableau de bytes (octets)
    public byte[] toBytes() {
        byte[] leTableauDeByte = null;
        try{
            leTableauDeByte = contenu.getBytes("UTF8"); // La methode magique qui fait tout
        }
        catch(Throwable e){
            leTableauDeByte = contenu.getBytes(); // La methode magique qui fait tout
        }
        return leTableauDeByte;
    }


    // Methode qui transforme le contenu du message en tableau de bits
    public BitSet Parser() {
        byte[] leMessageEnBytes = this.toBytes();
        BitSet leMessageEnBits = new BitSet(leMessageEnBytes.length * 8);

        for (int i = 0; i < leMessageEnBytes.length * 8; i++) {
            if ((leMessageEnBytes[leMessageEnBytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                leMessageEnBits.set(i);
            }
        }
        return leMessageEnBits;
    }
}
