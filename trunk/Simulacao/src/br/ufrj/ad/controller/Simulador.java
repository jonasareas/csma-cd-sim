package br.ufrj.ad.controller;

import java.util.LinkedList;
import java.util.List;

import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.model.Evento;

public class Simulador
{
  
  private static final int PROPAGACAO_ELETRICA = 5; // Em nanosegundos/metro
  private static final double TEMPO_ENTRE_TRANSMISSOES = 9600; // Em nanosegundos
  private static final double TEMPO_REFORCO_COLISAO = 3200; // Em nanosegundos
  
  private List<Evento> listaEventos;
  
  public void iniciaSimulacao() 
  {
    Estacao pc1 = new Estacao(1, 100);
    Estacao pc2 = new Estacao(2, 80);
    Estacao pc3 = new Estacao(3, 60);
    Estacao pc4 = new Estacao(4, 40);
    
    listaEventos = new LinkedList<Evento>();
    
  }

}
