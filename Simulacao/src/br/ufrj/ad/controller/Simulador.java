package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import br.ufrj.ad.controller.variaveis.Exponencial;
import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.model.Evento;

public class Simulador
{
  
  private static final double PROPAGACAO_ELETRICA = 0.005; // Em microsegundos/metro
  private static final double TEMPO_ENTRE_TRANSMISSOES = 9.6; // Em microsegundos
  private static final double TEMPO_REFORCO_COLISAO = 3.2; // Em microsegundos
  
  public static enum TipoEvento {
    ChegadaNaFila,
    InicioTransmissao    
  }

  private PriorityQueue<Evento> listaEventos;
  
  private List<Estacao> listaPCs = new ArrayList<Estacao>();
  
  public void iniciaSimulacao(double tempoSimulacao) 
  {
    // Primeiro cenário! Apenas para testes! Os parâmetros 3, 4 e 5 virão da tela! MAS A TELA EH MONTADA NA MAIN (START)!!!!
    Estacao pc1 = new Estacao(1, 100, 40, 80, true);
    Estacao pc2 = new Estacao(2, 80, 40, 80, true);
    Estacao pc3 = new Estacao(3, 60, 0, 0, true);
    Estacao pc4 = new Estacao(4, 40, 0, 0, true);
    
    listaPCs.add(pc1);
    listaPCs.add(pc2);
    listaPCs.add(pc3);
    listaPCs.add(pc4);
    
    listaEventos = new PriorityQueue<Evento>();
    
    for (Estacao estacao : listaPCs) 
    {
       if (estacao.getP() != 0)
       {
         if (estacao.isDeterministico())
         {         
           programaChegadaNaFila(estacao.getCodigo(), estacao.getA()*1000000);
         } else {
           programaChegadaNaFila(estacao.getCodigo(), Exponencial.geraAmostra(1/estacao.getA())*1000000);
         }
       }
    }
    simula(listaPCs, tempoSimulacao);    
  }
  
  private void programaChegadaNaFila(int codigoEstacao, double tempoExecucao)
  {
    Evento evento = new Evento(TipoEvento.ChegadaNaFila, tempoExecucao, codigoEstacao);
    listaEventos.add(evento);
  }
  
  private void simula(List<Estacao> listaPCs, double tempoSimulacao)
  {
    Evento evento;
    do {
      evento = listaEventos.remove();
      verificaTipoEvento(evento);
    } while(evento.getTempoExecucao() < tempoSimulacao);
  }
  
  private void verificaTipoEvento(Evento evento)
  {
    switch(evento.getTipoEvento()) {
      case ChegadaNaFila :
        processaChegadaNaFila(evento);
        break;
      case InicioTransmissao :
        
    }
  }
  
  private void processaChegadaNaFila(Evento evento)
  {
   // programaChegadaNaFila(evento.getCodigoEstacao(), );
   // verifica se a fila da estacao tah vazia
      // se tiver - programar inicio envio
  }

}
