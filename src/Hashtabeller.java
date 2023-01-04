import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Laget av Anna Marie Bøe Tvedt og Marte Solli Vågen
public class Hashtabeller {
    private static final int tabellLengde = 10000000;

    public static String tid(){
        HashTabellTall hashTabell = new HashTabellTall(tabellLengde);
        // HashMap<Integer, Integer> map = new HashMap<>();
        ArrayList<Integer> list = randomList(tabellLengde);

        Date start = new Date();

        for (Integer tall: list) {
            hashTabell.leggTilTall(tall);
            //map.put(tall, tall);
        }

        Date slutt = new Date();
        double tid = slutt.getTime()-start.getTime();

        return ("Antall millisekunder: " + tid);
    }

    public static void main(String[] args) {
        testDelOppgave1();
        System.out.println("\n---------------------------------------------------------------------------------------------------------");
        testDelOppgave2();
        System.out.println(tid());
    }

    public static void testDelOppgave1(){
        Hashtabell hashtabellNavn = new Hashtabell(127);
        hashtabellNavn.leggTilFraFil("src/navn.txt");

        System.out.println();
        System.out.println(hashtabellNavn.getListeKollisjoner());

        System.out.println("Lastfaktor: " + hashtabellNavn.getLastfaktor());
        System.out.println("Antall kollisjoner: " + hashtabellNavn.getAntallKollisjoner());
        System.out.println("Antall kollisjoner per person: " + hashtabellNavn.getKollisjonerPerPers());

        String marte = "Marte Solli Vågen";
        String anna = "Anna Marie Bøe Tvedt";
        String helge = "Helge Hafting";

        System.out.println("\n" + hashtabellNavn.finnPerson(marte));
        System.out.println(hashtabellNavn.finnPerson(anna));
        System.out.println(hashtabellNavn.finnPerson(helge));
    }

    public static void testDelOppgave2(){
        HashTabellTall hashTabell = new HashTabellTall(tabellLengde);
        ArrayList<Integer> list = randomList(tabellLengde);

        for (Integer tall: list) {
            hashTabell.leggTilTall(tall);
        }

        System.out.println("\nTabell med " + hashTabell.getAntall() + " tall:\n");
        System.out.println("Lastfaktor: " + hashTabell.getLastfaktor());
        System.out.println("Antall kollisjoner: " + hashTabell.getKollisjoner());
        System.out.println("Antall kollisjoner per tall: " + hashTabell.getKollisjonerPerTall());
    }

    public static ArrayList<Integer> randomList(int lengde){
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i<lengde; i++){
            list.add(random.nextInt(lengde*1000));
        }
        return list;
    }

    //Deloppgave 1: Hashtabell med tekstnøkler
    public static class Hashtabell{

        private final HashNode[] tabell;
        private double antall;
        private double kollisjoner;

        public Hashtabell(int lengde){
            this.tabell = new HashNode[lengde];
        }

        public double getLastfaktor(){
            return antall/tabell.length;
        }

        public double getKollisjonerPerPers(){
            return kollisjoner/antall;
        }

        public double getAntallKollisjoner(){
            return kollisjoner;
        }

        public int hashfunksjon(String nøkkel){
            return hashverdi(nøkkel) % tabell.length;
        }

        public int hashverdi(String navn) {
            int hashnumber = 0;
            for (int i = 0; i < navn.length(); i++) {
                hashnumber += ((Character) navn.charAt(i)).hashCode() * 7 ^ (i + 1);
            }

            return hashnumber;
        }

        public void add(String tekst){
            if (tabell[hashfunksjon(tekst)] == null){
                tabell[hashfunksjon(tekst)] = new HashNode();
            }else {
                kollisjoner++;
            }
            tabell[hashfunksjon(tekst)].leggTilTekst(tekst);
            antall ++;
        }

        public boolean listeInneholderNavn(String navn){
            int tall = hashfunksjon(navn);
            if (tabell[tall] == null) return false;
            Iterator<String> it = tabell[tall].listIt();

            while (it.hasNext()){
                if (it.next().equals(navn)){
                    return true;
                }
            }
            return false;
        }

        public String finnPerson(String navn){
            if (!listeInneholderNavn(navn)) return navn + " eksisterer ikke i tabellen";
            return navn + " er i tabellen, og har tilhørende hashverdi " + hashverdi(navn) + " og plassering " + hashfunksjon(navn) + " i tabellen";
        }

        public String getListeKollisjoner(){
            StringBuilder sb = new StringBuilder();
            for (HashNode node: tabell) {
                if (!(node == null || node.getAntall() == 1)){
                    sb.append(node).append("\n");
                }
            }
            return sb.toString();
        }

        public void leggTilFraFil(String filepath){
            try (BufferedReader reader = new BufferedReader(new FileReader(filepath))){
                reader.lines().forEach(this::add);
            } catch (IOException | NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public static class HashNode{

        private final LinkedList<String> list;
        private int antall;

        public HashNode(){
            list = new LinkedList<>();
            antall = 0;
        }

        public void leggTilTekst(String tekst){
            list.add(tekst);
            antall++;
        }

        public Iterator<String> listIt(){
            return list.iterator();
        }

        public int getAntall(){
            return antall;
        }

        public String toString(){
            StringBuilder sb = new StringBuilder();
            if (antall == 0) return "";
            Iterator<String> it = listIt();
            sb.append(it.next());

            while (it.hasNext()){
                sb.append(" -> ").append(it.next());
            }
            return sb.toString();
        }
    }

    //Deloppgave 2: Hashtabeller med heltallsnøkler – og ytelse
    public static class HashTabellTall{

        private final int[] tabell;
        private double antall;
        private double kollisjoner;

        public HashTabellTall(int lengde){
            this.tabell = new int[createPrime((int) (lengde*1.2))];
        }

        public boolean isPrime(int tall){
            for(int i = 2; i <= Math.sqrt(tall); i++) {
                int temp = tall % i;
                if(temp == 0) {
                    return false;
                }
            }
            return tall != 0 && tall != 1;
        }

        public int createPrime(int tall){
            while (!isPrime(tall)){
                tall++;
            }
            return tall;
        }

        public double getKollisjoner() {
            return kollisjoner;
        }

        public double getAntall() {
            return antall;
        }

        public double getLastfaktor(){
            return antall/tabell.length;
        }

        public double getKollisjonerPerTall(){
            return kollisjoner/antall;
        }

        //gir index på plassering i listen
        public int hashfunksjon(int nøkkel){
            antall++;
            return nøkkel % tabell.length;
        }

        //gir nytt tall som sier hvor mange plasser som skal "hoppes" videre fra den første index-verdien
        //må lages slik at den aldri kan få verdien 0
        public int dobbelhHashfunksjon(int nøkkel){
            return nøkkel % (tabell.length - 1) + 1;
        }

        public void leggTilTall(int x) {
            int pos = hashfunksjon (x) ;
            if ( tabell[pos] == 0) {
                tabell [pos] = x ; return ;
            }
            int h2 = dobbelhHashfunksjon (x) ;
            for ( ; ; ) {
                pos = ((pos + h2) % tabell.length);
                kollisjoner++;
                if ( tabell[pos] == 0) {
                    tabell[pos] = x ; return ;
                }
            }
        }
    }
}