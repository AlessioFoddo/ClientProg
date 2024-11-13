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
        System.out.println("Client collegato");
        Scanner myScan = new Scanner(System.in);
        String ins;
        boolean accesso = false;
        // Login/Sign Up loop
        while (!accesso) {
            System.out.println("Inserisci la tua scelta:");
            System.out.println("L- Effettua il log in");
            System.out.println("S- Effettua il sign up");
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
                    accesso = Login(myScan, in, out);
                    break;
                case "S_U":
                    accesso = SignUp(myScan, in, out);
                    break;
                default:
                    System.out.println("Scelta non valida, riprova.");
                    break;
            }
        }
        // Main menu after login/signup
        boolean exit = false;
        while (!exit) {
            System.out.println("\nScegli un'azione:");
            System.out.println("\n1 - Inizia chat");
            System.out.println("\n2 - Lista utenti");
            System.out.println("\n3 - Blocca contatti");
            System.out.println("\n4 - Logout\n");
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
        System.out.println("Inserisci nome utente: ");
        String nomeUtente = myScan.nextLine();
        out.writeBytes(nomeUtente + '\n');
        System.out.println("Inserisci la password: ");
        String password = myScan.nextLine();
        out.writeBytes(password + '\n');
        String response = in.readLine();
        if (response.equals("v")) {
            System.out.println("Log in effettuato.");
            return true;
        } else {
            if (response.equals("!u")) {
                System.out.println("Username errato.");
            } else if (response.equals("!p")) {
                System.out.println("Password errata.");
            } else if (response.equals("!all")) {
                System.out.println("Username e password errati.");
            }
            return false;
        }
    }
    // Method sign-up
    private static boolean SignUp(Scanner myScan, BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Inserisci nome utente: ");
        String nomeUtente = myScan.nextLine();
        out.writeBytes(nomeUtente + '\n');
        String response = in.readLine();
        if (response.equals("!u")) {
            System.out.println("Username già esistente.");
            return false;
        }
        System.out.println("Crea una password: ");
        String password = myScan.nextLine();
        out.writeBytes(password + '\n');
        response = in.readLine();
        if (response.equals("v")) {
            System.out.println("Sign up effettuato con successo!");
            return true;
        } else {
            System.out.println("Errore nella registrazione. Riprova.");
            return false;
        }
    }
    // Method to start a chat
    private static void startChat(Scanner myScan, BufferedReader in, DataOutputStream out) throws IOException {
        System.out.println("Inserisci il codice utente con cui vuoi chattare:");
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