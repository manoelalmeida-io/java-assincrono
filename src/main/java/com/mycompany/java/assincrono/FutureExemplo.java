package com.mycompany.java.assincrono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FutureExemplo {
    
    private static final ExecutorService THREADPOOL = Executors.newFixedThreadPool(3);
    
    public static void main(String[] args) throws InterruptedException {
        Casa casa = new Casa(new Quarto());
        
        List<? extends Future<String>> futuros = new CopyOnWriteArrayList<Future<String>>(casa.obterAfazeresDaCasa().stream()
                .map(atividade -> THREADPOOL.submit(() -> {
                    try {
                        return atividade.realizar();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FutureExemplo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    return null;
                }))
                .collect(Collectors.toList()));
        
        while (true) {
            int atividadesNaoFinalizadas = 0;
            for (Future futuro : futuros) {
                if (futuro.isDone()) {
                    try {
                        System.out.println("Parabéns você terminou de " + futuro.get());
                        futuros.remove(futuro);
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(FutureExemplo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
                    atividadesNaoFinalizadas++;
                }
            }
            
            if (futuros.stream().allMatch(Future::isDone)) {
                break;
            }
            
            System.out.println("Número de atividades não finalizadas: " + atividadesNaoFinalizadas);
            
            Thread.sleep(500);
        }
        
        THREADPOOL.shutdown();
    }
}

interface Atividade {
    String realizar() throws InterruptedException;
}

class Casa {
    
    private final List<Comodo> comodos;

    public Casa(Comodo... comodos) {
        this.comodos = Arrays.asList(comodos);
    }
    
    public List<Atividade> obterAfazeresDaCasa() {
        return this.comodos.stream().map(Comodo::obterAfazeresDoComodo)
                .reduce(new ArrayList<>(), (pivo, atividades) -> {
                    pivo.addAll(atividades);
                    return pivo;
                });
    }
}

abstract class Comodo {
    abstract List<Atividade> obterAfazeresDoComodo();
}

class Quarto extends Comodo {

    @Override
    List<Atividade> obterAfazeresDoComodo() {
        return Arrays.asList(
                this::arrumarACama,
                this::varrerOQuarto,
                this::arrumarGuardaRoupa
        );
    }
    
    private String arrumarGuardaRoupa() throws InterruptedException {
        Thread.sleep(5000);
        String mensagem = "Arrumar o guarda roupa";
        System.out.println(mensagem);
        return mensagem;
    }
    
    private String varrerOQuarto() throws InterruptedException {
        Thread.sleep(7000);
        String mensagem = "Varrer o quarto";
        System.out.println(mensagem);
        return mensagem;
    }
    
    private String arrumarACama() throws InterruptedException {
        Thread.sleep(10000);
        String mensagem = "Arrumar a carma";
        System.out.println(mensagem);
        return mensagem;
    }
}