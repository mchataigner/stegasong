/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package steganographiemp3;

/**
 *
 * @author aseite
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try
        {
            MP3 leMp3 = new MP3("/home/aseite/Bureau/Cours/TI/Projet TI/unMP3.mp3",1000000000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
