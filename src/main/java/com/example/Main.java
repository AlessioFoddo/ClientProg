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
        // Establish connection to the server
        // collegamento tra pc diversi in classe 10.22.9.17
        Socket server = new Socket("localhost", 3000);
        BufferedReader in = new BufferedReader(new
        InputStreamReader(server.getInputStream()));
        DataOutputStream out = new
        DataOutputStream(server.getOutputStream());
        System.out.println("Marinaio a bordo");
        Scanner myScan = new Scanner(System.in);
        String ins;
        boolean accesso = false;
        // Login/Sign Up loop
        while (!accesso) {
            System.out.println("Benvenuto farabutto, Scegli un’opzione:");
            System.out.println("Seleziona L per fare il LOG IN e riprendere i tuoi averi;");
            System.out.println("Seleziona S per fare il SIGN UP e unirti per la prima volta a questa ciurma;");
            ins = myScan.nextLine();
            String scelta;
            if(ins.equals("L")){
                scelta = "L_I";
            }else if(ins.equals("S")){
                scelta = "S_U";
            }else{
                scelta = "!";
            }
            out.writeBytes(scelta + "\n");
            switch (scelta) {
                case "L_I":
                    String controllo = in.readLine();
                    if(controllo.equals("noU")){
                        System.out.println("Spiacente, ma questo galeone è nuovo di zecca, non ci è mai salito nessuno apparte me...");
                        System.out.println("Che ne dici di essere il primo membro?");
                        accesso = SignUp(myScan, in, out);
                        break;
                    }
                    accesso = Login(myScan, in, out);
                    break;
                case "S_U":
                    accesso = SignUp(myScan, in, out);
                    break;
                default:
                    accesso = false;
                    System.out.println("Mi stai prendendo in giro, hai 2 opzioni, non è difficile, riprova.");
                    break;
            }
        }
        // Main menu after login/signup
        boolean exit = false;
        while (!exit) {
            System.out.println("Ci troviamo all’interno del Galeone, cosa vuoi fare?");
            System.out.println("Se vuoi andare a parlare con i marinai a bordo premi pure C”");
            System.out.println("Se vuoi vedere tutti marinai presenti sul Galeone premi pure M");
            String actionChoice = myScan.nextLine();
            switch (actionChoice) {
                case "1":
                    startChat(myScan, in, out);
                    break;
                case "2":
                    listUsers(in, out);
                    break;
                case "3":
                    blockContacts(myScan, out);
                    break;
                case "4":
                    System.out.println("Disconnessione...");
                    exit = true;
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
                    break;
            }
        }
    // Closing resources
        server.close();
        myScan.close();
    }
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
            System.out.println("Questo Nome mi puzza...");
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
        String userCode = myScan.nextLine();
        out.writeBytes("CHAT " + userCode + '\n');
        String response = in.readLine();
        if (response.equals("presente")) {
            System.out.println("Inizio chat con l'utente " + userCode);
            System.out.println("Scrivi il tuo messaggio (digita 'exit' per terminare):");
            while (true) {
                String message = myScan.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                out.writeBytes("END_CHAT\n");
                System.out.println("Chat terminata.");
                break;
                }
                out.writeBytes("MSG " + message + '\n');
            }
        } else {
            System.out.println("Utente non trovato o non disponibile.");
        }
    }
    // Method to list users
    private static void listUsers(BufferedReader in, DataOutputStream out) throws IOException {
        out.writeBytes("LIST_USERS\n");
        String userList = in.readLine();
        System.out.println("Utenti attivi:\n" + userList);
    }
    // Method to block contacts
    private static void blockContacts(Scanner myScan, DataOutputStream out) throws IOException {
        System.out.println("Inserisci il nome utente del contatto che vuoi bloccare:");
        String contactToBlock = myScan.nextLine();
        out.writeBytes("BLOCK " + contactToBlock + '\n');
        System.out.println("Contatto " + contactToBlock + " bloccato.");
    }
}