package com.example;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws UnknownHostException, IOException {
        System.out.println("Client partito");
    
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
                                String text = in.readLine();  // Leggi il contenuto del messaggio
                                System.out.println("\n[NOTIFICA]: Hai un nuovo messaggio da " + sender + ": " + text);
                            }
                        }
                    }
                    Thread.sleep(1000); // Pausa per evitare consumi eccessivi di CPU
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Errore nel thread di ascolto: " + e.getMessage());
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
            System.out.println(scelta);
            out.writeBytes(scelta + "\n");
    
            switch (scelta) {
                case "Chat":
                    startChat(myScan, in, out);
                    exit = false;
                    break;

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
    
    // Method login
    private static boolean Login(Scanner myScan, BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Inserisci il tuo vecchio Nome da pirata:");
        String nomeUtente = myScan.nextLine();
        out.writeBytes(nomeUtente + '\n');
        System.out.println(in.readLine());
        System.out.println("Inserisci la Chiave del tuo tesoro:");
        String password = myScan.nextLine();
        out.writeBytes(password + '\n');
        String response = in.readLine();
        System.out.println("input: " + response);
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
        System.out.println("Inserisci il nome del marinaio con cui vuoi conversare:");
        String username = myScan.nextLine();
        out.writeBytes(username + '\n');
        String response = in.readLine();
        if (response.equals("u_v")) {
            System.out.println(" " + username);
            System.out.println("Scrivi il tuo messaggio (digita 'end' per terminare):");
            while (true) {
                String message = myScan.nextLine();
                if (message.equalsIgnoreCase("end")) {
                    out.writeBytes("end\n");
                    System.out.println("Chat terminata.");
                    break;
                }
                out.writeBytes(message + '\n');
            }
        } else {
            System.out.println("Questo nome mi puzza...questo nome mi e' nuovo");
        }
    }
    // Method to list users
    private static void listUsers(BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Utenti esistenti:");
        String userList;
        int i = 1;
        while (!(userList = in.readLine()).equals("end")) {
            System.out.println(i + ") " + userList + ";");
            i++;
        }
    }
    // Method to block contacts
    private static void blockContacts(Scanner myScan, DataOutputStream out) throws IOException {
        System.out.println("Inserisci il nome utente del contatto che vuoi bloccare:");
        String contactToBlock = myScan.nextLine();
        out.writeBytes("BLOCK " + contactToBlock + '\n');
        System.out.println("Contatto " + contactToBlock + " bloccato.");
    }
}