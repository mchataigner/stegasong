/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganographiemp3;

/**
 *
 * @author aseite
 */
//import javax.media.*;
import java.io.*;
import java.util.BitSet;

// Classe representant le MP3 a modifier
public class MP3 {

    // Attributs

    //Nom du MP3
    private String nom;
    //Tableau de Bytes representant le contenu du MP3
    private byte[] leContenu;


    // Constructeur. Je ne sais pas si la taille du buffer est necessaire
    // ou si on peut la mettre constante
    public MP3(String fichier, int tailleBuffer) throws IOException {
        File lefichier = new File(fichier);

        if (lefichier.exists()) {

            nom = fichier; // Nom du fichier
            leContenu = FileToByteArray(fichier);// Contenu en octets du fichier


        } else {

            // Gestion des erreurs a deux balles
            System.out.println("Le fichier n'existe pas .\n");
            System.exit(-1);
        }



    }

    // Accesseurs divers
    public String getNom() {
        return nom;
    }

    public byte[] getContenu() {
        return leContenu;
    }

    public int getTaille() {
        return leContenu.length;
    }

    // Ajout d'un tableau de bytes au contenu a partir d'une positition donnee
    // Cette methode etait une premiere approche du probleme mais elle n'est
    // pas satisfaisant parce qu'elle modifie trop le fichier d'origine.
    // Il ne faut pas inserer brutalement des bytes dans le MP3, il faut modifier les
    // bits de poid faible des bytes du MP3 afin qu'ils contiennent les bits des bytes
    // du message a cacher.
        public void ajouterContenu(byte[] aAjouter, int position) {
        
        byte[] leNouveauContenu = null; // Representera le MP3 après traitement
        
        // On colle tel quel le contenu du MP3 avant l'indice d'insertion
        for (int i = 0; i == position - 1; i++) {
            leNouveauContenu[i] = leContenu[i];
        }

        // On colle le contenu a inserer a partir de l'indice d'insertion
        for (int i = 0; i == aAjouter.length; i++) {
            leNouveauContenu[i + position] = aAjouter[i];
        }

        // On colle la fin du MP3 après le contenu a inserer
        for (int i = position; i == leContenu.length + aAjouter.length; i++) {
            leNouveauContenu[i + aAjouter.length] = leContenu[i];
        }

        // On remplace le contenu du MP3
        leContenu = leNouveauContenu;
    }

    // Meme methode qu'au dessus mais sans argument de position, il est contraint a la valeur 100.
    public void ajouterContenu(byte[] aAjouter) {
        int position = 100;
        byte[] leNouveauContenu = null;
        for (int i = 0; i == position - 1; i++) {
            leNouveauContenu[i] = leContenu[i];
        }
        for (int i = 0; i == aAjouter.length; i++) {
            leNouveauContenu[i + position] = aAjouter[i];
        }
        for (int i = position; i == leContenu.length + aAjouter.length; i++) {
            leNouveauContenu[i + aAjouter.length] = leContenu[i];
        }
        leContenu = leNouveauContenu;
    }


    // Methodes trouvees sur le net servant a la creation du tableau de bytes
    // a partir du fichier MP3 de depart
    // NE PAS MODIFIER, CA MARCHE !
    private byte[] StreamtoByteArray(FileInputStream stream) throws IOException {
        int offset = 0;
        int remaining = (int) stream.available();
        byte[] data = new byte[remaining];
        while (remaining > 0) {
            int read = stream.read(data, offset, remaining);
            if (read <= 0) {
                throw new IOException();
            }
            remaining -= read;
            offset += read;
        }
        return data;
    }

    // Idem que methode d'au dessus
    private byte[] FileToByteArray(String path) throws IOException {
        FileInputStream fs = new FileInputStream(path);
        byte[] binary = StreamtoByteArray(fs);
        fs.close();
        return binary;
    }


    // Methode identique a celle transformant une string en tableau de bits mais avec un MP3
    // Elle transforme donc le contenu (tableau de Bytes) en bits.
    public BitSet Parser() throws IOException {
        
        BitSet leMP3EnBits = new BitSet(leContenu.length * 8);

        for (int i = 0; i < leContenu.length * 8; i++) {
            if ((leContenu[leContenu.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                leMP3EnBits.set(i);
            }
        }
        return leMP3EnBits;
    }
}
