package com.mycompany.java.assincrono;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadExemplo {
    
    public static void main(String[] args) {
        GeradorDePDF geradorDePDF = new GeradorDePDF();
        BarraDeCarregamento barraDeCarregamento = new BarraDeCarregamento(geradorDePDF);
        
        geradorDePDF.start();
        barraDeCarregamento.start();
    }
}

class GeradorDePDF extends Thread {

    @Override
    public void run() {
        try {
            System.out.println("Gerando PDF...");
            Thread.sleep(5000);
            System.out.println("PDF gerado com sucesso!");
        } catch (InterruptedException ex) {
            Logger.getLogger(GeradorDePDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class BarraDeCarregamento extends Thread {

    private final Thread thread;

    public BarraDeCarregamento(Thread thread) {
        this.thread = thread;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                if (!thread.isAlive()) {
                    break;
                }
                System.out.println("Carregando...");
            } catch (InterruptedException ex) {
                Logger.getLogger(BarraDeCarregamento.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}