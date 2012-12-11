package steganographiemp3;


import java.io.*;
import java.util.BitSet;
import java.util.Vector;
import java.nio.charset.Charset;


public class MP3 {

    // Attributs

    //Nom du MP3
    private String nom;
    //Tableau de Bytes representant le contenu du MP3
    private byte[] leContenu;
    private BitSet leContenu_bits;
    private int tailleContenu;
    private int tailleContenu_bits;
    private boolean id3v2=false;
    private int positionDebut=0;
    private int positionDebut_bits=0;
    private int positionCourante_bits=0;
    // Constructeur. La taille du Buffer n'est a priori pas necessaire
    public MP3(String fichier, int tailleBuffer) throws IOException {
        File lefichier = new File(fichier);

        if (lefichier.exists()) {
        	
	    // Nom du fichier		
            nom = fichier; 
            // Contenu en octets du fichier
            leContenu = FileToByteArray(fichier);
            tailleContenu=leContenu.length;
            tailleContenu_bits=tailleContenu*8;
            leContenu_bits=this.parser();
            
            if(leContenu[0]=='I'&&leContenu[1]=='D'&&leContenu[2]=='3')
                {
                    id3v2=true;
                    byte[] laTaille=new byte[4];
                    laTaille[0]=leContenu[7];
                    laTaille[1]=leContenu[8];
                    laTaille[2]=leContenu[9];
                    laTaille[3]=leContenu[10];
                    BitSet laTaille_bits_tmp=parser(laTaille);
                    BitSet laTaille_bits=new BitSet(32);
                    for(int k=0;k<4;k++)
                        for(int l=0;l<7;l++)
                            laTaille_bits.set((l+k*7),laTaille_bits_tmp.get(l+k*8));
                    for(int k=28;k<32;k++)
                        laTaille_bits.clear(k);
                    System.out.println("ID3TAG version 2 trouvée");
                    byte[] laTaille_byte=toByteArray(laTaille_bits);
                    String laTaille_string="";
                    for(int k=0;k<laTaille_byte.length;k++)
                        laTaille_string+=laTaille_byte[k];
                    int laTaille_int=0;
                    
                    for(int k=0;k<32;k++){
                        int pow=1;
                        for(int l=1;l<=k;l++)
                            pow*=2;
                        if(laTaille_bits.get(k))
                            laTaille_int+=pow;
                    }
                    
                    //int laTaille_int=Integer.parseInt(new String(laTaille_string));
                    laTaille_int+=10*8;
                    positionDebut_bits=laTaille_int-1;
                    positionDebut_bits=Math.round((positionDebut_bits+4)/8)*8;
                    positionDebut=positionDebut_bits/8;
                    positionCourante_bits=positionDebut_bits;
                }
            else {
                
            }
            
        } else {

            // Gestion des erreurs
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

    // Methode permettant d'ajouter un message par insertion
    // C'est a dire tout le contenu en un seul bloc d'octets
    public int ajouterContenu(byte[] aAjouter, int position) {
        int tailleMessage = aAjouter.length;
        String laTaille = Integer.toString(tailleMessage);
        
        position=Math.max(position,positionDebut);
        laTaille +='\n';

        int laTailleDeLaTaille = laTaille.length();
        
        
        // Representera le MP3 après traitement
        byte[] leNouveauContenu = new byte[leContenu.length+aAjouter.length+ laTailleDeLaTaille];
        
        // On colle tel quel le contenu du MP3 avant l'indice d'insertion
        for (int i = 0; i <= position - 1; i++) {
            leNouveauContenu[i] = leContenu[i];
        }

        for (int i = 0; i < laTailleDeLaTaille; i++){
            leNouveauContenu[i + position] = (byte)laTaille.charAt(i);
			
        }

        // On colle le contenu a inserer a partir de l'indice d'insertion
        for (int i = 0; i < aAjouter.length; i++) {
            leNouveauContenu[i + position + laTailleDeLaTaille] = aAjouter[i];
        }

        // On colle la fin du MP3 après le contenu a inserer
        for (int i = position; i < leContenu.length ; i++) {
            leNouveauContenu[i + laTailleDeLaTaille + aAjouter.length] = leContenu[i];
        }

        // On remplace le contenu du MP3
        leContenu = leNouveauContenu;
        return position;
    }

    // Meme methode qu'au dessus mais sans argument de position, il est contraint a la valeur 100.
    public int ajouterContenu(byte[] aAjouter) {
        int position = 100;
        return this.ajouterContenu(aAjouter,position);
    }

	
	
	
	
	
	
	
    // Methode pour ajouter du contenu par substitution
    // C'est a dire en remplacant les bits, en essayant de remplacer les bits de poids faible (LSB)
    public int ajouterContenu(byte[] aAjouter, int position, int lsb)throws Exception {
        int tailleMessage = aAjouter.length*8;
        String laTaille = Integer.toString(tailleMessage);
        
        if(aAjouter.length*8*lsb>tailleContenu_bits)
            throw new Exception("taille du message trop grand : "+aAjouter.length*8*lsb+" bits pour "+tailleContenu_bits+" bits disponibles");
        
        position=Math.max(position,positionDebut_bits/8);
        
        int position_bits=position*8;
        
        
        laTaille +='\n';
        
        int laTailleDeLaTaille = laTaille.length();
        BitSet aAjouter_bits=this.parser(aAjouter);
		
        byte [] entete;
        try{
            entete=(new StringBuffer(laTaille)).reverse().toString().getBytes("UTF8"/*Charset.forName("UTF-8")*/);
        }
        catch(Throwable e){
            entete=(new StringBuffer(laTaille)).reverse().toString().getBytes();
        }
        BitSet entete_bits=this.parser(entete);
		
        byte[] test=toByteArray(entete_bits);
		
        BitSet test2=new BitSet(entete.length*8);
		
        for(int i=0;i<entete.length*8;i++)
            {
                if(entete_bits.get(i)){
                    leContenu_bits.set(i*lsb+lsb-1+position_bits);
                    test2.set(i);
                }
                else{
                    leContenu_bits.clear(i*lsb+lsb-1+position_bits);
                    test2.clear(i);
                }
            }
		
        for(int i=0;i<aAjouter.length*8;i++)
            {
                if(aAjouter_bits.get(i)!=leContenu_bits.get(i*lsb+lsb-1+position_bits+entete.length*8*lsb))
                    leContenu_bits.flip(i*lsb+position_bits+lsb-1+entete.length*8*lsb);
            }
		
		
        leContenu=toByteArray(leContenu_bits);
        return position;
    }







    // Methodes permettant de convertir un fichier MP3 en tableau d'octets
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
    public BitSet parser() throws IOException {
        
        BitSet leMP3EnBits = new BitSet(leContenu.length * 8);

        for (int i = 0; i < leContenu.length * 8; i++) {
            if ((leContenu[leContenu.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                leMP3EnBits.set(i);
            }
        }
        return leMP3EnBits;
    }
    
    
    public BitSet parser(byte[] lesBytes) throws IOException {
        
        BitSet lesBytesEnBits = new BitSet(lesBytes.length * 8);

        for (int i = 0; i < lesBytes.length * 8; i++) {
            if ((lesBytes[lesBytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                lesBytesEnBits.set(i);
            }
        }
        return lesBytesEnBits;
    }
    
    
    
    
    
    
    // Methode permettant de transformer un tableau de bits en tableau d'octets 
    public byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[bits.length()/8+1];
        for (int i=0; i<bits.size(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length-i/8-1] |= 1<<(i%8);
            }
        }
        return bytes;
    }
	
	
	
	
	
    public int stega(Message m, int position)
    {
        byte[] leMessage = m.toBytes();
        return this.ajouterContenu(leMessage,position);
    }
	
    public int stega(Message m, int position,int lsb)throws Exception
    {
        byte[] leMessage = m.toBytes();
        return this.ajouterContenu(leMessage,position,lsb);
    }
	
	
    //Methode permettant de convertir un tableau d'octets en fichier MP3	
    public void toMP3(String name)throws Exception
    {
    	byte[] lesBytes=toByteArray(this.parser());
    	FileOutputStream fos=new FileOutputStream(name);
    	fos.write(lesBytes);
    	fos.flush();
    	fos.close();
    }
    
    
    
    //Methode servant a decoder un MP3 contenant un message cache
    //Pour la methode par insertion
    public String decoder(int position)
    {
    	Vector<Byte> laTaille=new Vector<Byte>();
    	byte courant;
    	int positionCourante;
        if(leContenu[0]=='I'&&leContenu[1]=='D'&&leContenu[2]=='3')
            {
                positionCourante=position;
            }
        else
            {
                positionCourante=position+1;
            }
        
    	do
            {
    		courant=leContenu[positionCourante];
    		if((char)courant!='\n')
                    laTaille.add(new Byte(courant));
    		positionCourante++;
            }while((char)courant!='\n');
    	
    	Byte[] tailleArray=new Byte[laTaille.size()];
    	laTaille.toArray(tailleArray);
    	
    	byte[] tailleSimple=new byte[tailleArray.length];
    	for(int i=0;i<tailleSimple.length;i++)
            {
    		tailleSimple[i]=tailleArray[i].byteValue();
            }
    	String laTailleEnString=new String(tailleSimple);
    	int taille=Integer.parseInt(laTailleEnString/*tailleSimple)*/);
    	
    	byte[] leMessage=new byte[taille];
    	String test="";
        StringBuffer strbuf=new StringBuffer();
    	for(int i=position+laTailleEnString.length()+1;i<position+laTailleEnString.length()+1+taille;i++)
            {
                leMessage[i-position-laTailleEnString.length()-1]=leContenu[i];
            }

        for(byte b : leMessage) {
            strbuf.append((char)b);
        }

    	return strbuf.toString();
    }
    
    
    
    //Methode servant a decoder un MP3 contenant un message cache
    //Pour la methode par substitution
    public String decoder(int position,int lsb)throws Exception
    {
    	BitSet leContenu_bits=this.parser(leContenu);
    	BitSet leChar_bits=new BitSet(8);
    	BitSet leMessage;
    	String taille="";
    	byte[] leChar;
    	int positionCourante=position*8+lsb-1;
    	do
            {
    		leChar_bits.clear();
    		for(int i=0;i<8;i++){
                    if(leContenu_bits.get(positionCourante))
                        leChar_bits.set(i);
                    else leChar_bits.clear(i);
                    positionCourante+=lsb;
    		}
    		leChar=toByteArray(leChar_bits);
    		if((char)leChar[0]!='\n')
                    taille+=(char)leChar[0];
	    	
            }while((char)leChar[0]!='\n');
    	int tailleInt=Integer.parseInt(taille);
    	leMessage=new BitSet(tailleInt);
    	
    	for(int j=0;j<Integer.parseInt(taille);j++)
            {
    		if(leContenu_bits.get(positionCourante))
                    leMessage.set(j);
    		else
                    leMessage.clear(j);
    		positionCourante+=lsb;
            }
    	
    	byte[] leMessage_byte=toByteArray(leMessage);
    	String test=new String(leMessage_byte);
    	
    	return test;	
    }
   
}
