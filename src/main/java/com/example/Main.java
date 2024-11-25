package com.example;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws UnknownHostException, IOException {
        // Connessione al server
        Socket server = new Socket("localhost", 3000);
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        System.out.println("Marinaio a bordo");
        Scanner myScan = new Scanner(System.in);
        boolean accesso = false;
    
        // Thread per ascoltare i messaggi dal server
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    if (in.ready()) {
                        String message = in.readLine();
                        if (message != null) {
                            // Controlla se il messaggio è del tipo "msg"
                            if (message.equals("msg")) {
                                String sender = in.readLine(); // Leggi il nome del mittente
                                System.out.println("\n(il tuo pappagallo)Craw! Craw! Messago da: " + sender + " (vola via).");
                            }
                        }
                    }
                    Thread.sleep(100); // Pausa per evitare consumi eccessivi di CPU
                }
            } catch (IOException | InterruptedException e) {
            }
        });// Permette di terminare il thread con il programma principale
        listenerThread.start();
    
        // Login o Signup
        while (!accesso) {
            System.out.println("Benvenuto farabutto, Scegli un'opzione:");
            System.out.println("Seleziona L per fare il LOG IN e riprendere i tuoi averi;");
            System.out.println("Seleziona S per fare il SIGN UP e unirti per la prima volta a questa ciurma;");
            String ins = myScan.nextLine();
            String scelta = ins.equals("L") ? "L_I" : ins.equals("S") ? "S_U" : "!";
            out.writeBytes(scelta + "\n");
    
            switch (scelta) {
                case "L_I":
                    String controllo = in.readLine();
                    if (controllo.equals("noU")) {
                        System.out.println("Spiacente, ma questo galeone è nuovo di zecca, non ci è mai salito nessuno...");
                        accesso = SignUp(myScan, in, out);
                    } else {
                        accesso = Login(myScan, in, out);
                    }
                    break;
                case "S_U":
                    accesso = SignUp(myScan, in, out);
                    break;
                default:
                    System.out.println("Mi stai prendendo in giro? Hai 2 opzioni, non è difficile, riprova.");
                    break;
            }
        }
    
        // Menu principale
        boolean exit = false;
        do{
            System.out.println("\nCi troviamo all'interno del Galeone, cosa vuoi fare?");
            System.out.println("Se vuoi andare a parlare con i marinai a bordo premi pure C.");
            System.out.println("Se vuoi vedere tutti marinai presenti sul Galeone premi pure M.");
            System.out.println("Se vuoi lasciare la ciurma allora premi O:");
            String ins = myScan.nextLine();
            String scelta = ins.equals("C") ? "Chat" : ins.equals("M") ? "Members" : ins.equals("O") ? "Out" : "!";
            out.writeBytes(scelta + "\n");
    
            switch (scelta) {
                case "Chat":
                    int chats = in.read();
                    if(chats == 0){
                        System.out.println("Al momento non hai inviato o ricevuto alcuna lettera.");
                        System.out.println("Inserisci il nome del marinaio");
                        String username = myScan.nextLine();
                        out.writeBytes(username + '\n');
                        sceltaTematica(out, myScan);
                        String response = in.readLine();
                        if (response.equals("u_v")) {
                            startChat(myScan, in, out);
                        } else {
                            System.out.println("Questo nome mi puzza...mi e' nuovo");
                        }
                        exit = false;
                        break;
                    }else{
                        allChats(in, out);
                        boolean controllo = true;
                        do{
                            System.out.println("\nScegli con quale marinaio conversare inserendo il codice della conversazione;");
                            System.out.println("Altrimenti inserisci 'new' per iniziare una nuova conversazione con un altro marinaio;");
                            System.out.println("Altrimenti inserisci 'exit' per svolgere altre attivià");
                            System.out.println("Altrimenti inserisci 'theme' per visualizzare tutti codici delle conversazioni tematiche");
                            String answer = myScan.nextLine();
                            out.writeBytes(answer + "\n");
                            switch (answer) {
                                case "new":
                                    System.out.println("Inserisci il nome del marinaio");
                                    String username = myScan.nextLine();
                                    out.writeBytes(username + '\n');
                                    sceltaTematica(out, myScan);
                                    String response = in.readLine();
                                    if (response.equals("u_v")) {
                                        startChat(myScan, in, out);
                                        controllo = false;
                                    } else {
                                        System.out.println("Questo nome mi puzza...mi e' nuovo");
                                        controllo = true;
                                    }
                                    break;

                                case "exit":
                                    System.out.println("Deciti, scegli per bene cosa vuoi fare...");
                                    controllo = false;
                                    break;

                                case "theme":
                                    System.out.println("Inserisci la tematica che vuoi visualizzare:");
                                    boolean rightChose = false;
                                    String theme;
                                    do {
                                        System.out.println("'SL' per visualizzare la tematica scolastica;");
                                        System.out.println("'GM' per visualizzare la tematica gaming;");
                                        theme = myScan.nextLine();
                                        if(theme.equals("SL") || theme.equals("GM")){
                                            rightChose = true;
                                        }else{
                                            System.out.println("Hai sbagliato a scirvere, riprova...");
                                            rightChose = false;
                                        }
                                    } while (!rightChose);
                                    out.writeBytes(theme + "\n");
                                    showTheme(in);
                                    controllo = true;
                                    break;
                            
                                default:
                                    String verification = in.readLine();
                                    if(verification.equals("u_v")){
                                        showChat(in);
                                        startChat(myScan, in, out);
                                        controllo = false;
                                    }else{
                                        controllo = true;
                                    }
                                    break;
                            }
                        }while(controllo);
                        break;
                    }

                case "Members":
                    listUsers(in, out);
                    exit = false;
                    break;

                case "Out":
                    System.out.println("Vergogati, mi aspettavo di meglio traditore...");
                    exit = true;
                    break;

                default:
                    System.out.println("Mi stai prendendo in giro? Hai 3 opzioni, non è difficile, riprova.");
                    break;
            }
        }while(!exit);
        // Chiusura delle risorse
        server.close();
        myScan.close();
        in.close();
        out.close();
    }

    //Methods
    
    // Method login
    private static boolean Login(Scanner myScan, BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Inserisci il tuo vecchio Nome da pirata:");
        String nomeUtente = myScan.nextLine();
        out.writeBytes(nomeUtente + '\n');
        System.out.println("Inserisci la Chiave del tuo tesoro:");
        String password = myScan.nextLine();
        out.writeBytes(password + '\n');
        String response = in.readLine();
        if (response.equals("v")) {
            System.out.println("Ora mi ricordo di te, che compagno fantanstico, ecco i tuoi averi...");
            return true;
        } else {
            if (response.equals("!u")) {
                System.out.println("Questo Nome mi puzza...");
            } else if (response.equals("!p")) {
                System.out.println("Questa Chiave non gira...");
            } else if (response.equals("!all")) {
                System.out.println("Ti è venuto il mal di mare? Queste caratteristiche mi puzzano...");
            }
            return false;
        }
    }
    // Method sign-up
    private static boolean SignUp(Scanner myScan, BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Inserisci il tuo Nome da pirata:");
        String nomeUtente = myScan.nextLine();
        out.writeBytes(nomeUtente + '\n');
        String response = in.readLine();
        if (response.equals("!u")) {
            System.out.println("Questo Nome mi puzza...magari eri già stato qui a bordo? Scegli di nuovo cosa fare:");
            return false;
        }
        System.out.println("Inserisci la Chiave del tuo tesoro:");
        String password = myScan.nextLine();
        out.writeBytes(password + '\n');
        System.out.println("Ecco un nuovo membro, avremo delle bellissime avventure, attento solo ai mostri marini hahaha!!!");
        return true;
    }
    // Method to start a chat
    private static void startChat(Scanner myScan, BufferedReader in, DataOutputStream out) throws IOException {
        boolean fine = false;
        do {
            System.out.println("Scrivi pure la tua lettera:");
            String message = myScan.nextLine();
            out.writeBytes(message + '\n');
            String controllo = in.readLine();
            if(controllo.equals("!txt")){
                System.out.println("Il codice usato è errato...riprova");
                fine = false;
            }else{
                fine = true;
            }
        } while (!fine);
    }
    // Method to list users
    private static void listUsers(BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Marinai registrati:");
        String userList;
        int i = 1;
        while (!(userList = in.readLine()).equals("end")) {
            System.out.println(i + ") " + userList + ";");
            i++;
        }
    }
    //Methods to show all chats
    private static void allChats(BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Conversazioni tenute:");
        String dstUser;
        int chatCode;
        int size = in.read();
        for(int i = 0; i < size; i++){
            dstUser = in.readLine();
            chatCode = in.read();
            System.out.println("Conversazione con: " + dstUser);
            System.out.println("Codice: " + chatCode);
        }
    }
    // Methods to show chat
    private static void showChat(BufferedReader in) throws IOException{
        String user;
        String text;
        String code;
        int size = in.read();
        for(int i = 0; i < size; i++){
            user = in.readLine();
            text = in.readLine();
            code = in.readLine();
            System.out.println(user + ": " + text);
            System.out.println("[" + code + "]");
        }
    }

    private static void sceltaTematica(DataOutputStream out, Scanner myScan)throws IOException{
        String theme;
        System.out.println("Scegli il tipo di tematica che vuoi dare a questa conversazione:");
        boolean end = true;
        do{
            System.out.println("Inserisci SL per scegliere la tematica scolastica;");
            System.out.println("Inserisci GM per scegliere la tematica gaming;");
            System.out.println("Inserisci NT per non usare alcuna tematica in particolare.");
            theme = myScan.nextLine();
            if(theme.equals("SL") || theme.equals("GM") || theme.equals("NT")){
                end = false;;
            }else{
                System.out.println("Mi stai prendendo in giro? Scegli bene...");
                end = true;
            }
        }while(end);
        out.writeBytes(theme + "\n");
    }

    public static void showTheme(BufferedReader in) throws IOException{
        System.out.println("Tema: " + in.readLine());
        int size = in.read();
        for(int i = 0; i < size; i++){
            System.out.println("[" + in.readLine() + "] = " + in.readLine());
        }
    }

}