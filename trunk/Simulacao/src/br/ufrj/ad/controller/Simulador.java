package br.ufrj.ad.controller;

import java.util.LinkedList;
import java.util.List;

import br.ufrj.ad.model.Computador;
import br.ufrj.ad.model.Evento;

public class Simulador
{
  
  private static final int PROPAGACAO_ELETRICA = 5; // Em nanosegundos/metro
  private static final double TEMPO_ENTRE_TRANSMISSOES = 9600; // Em nanosegundos
  private static final double TEMPO_REFORCO_COLISAO = 3200; // Em nanosegundos
  
  private List<Evento> listaEventos;
  
  public void iniciaSimulacao() 
  {
    Computador pc1 = new Computador(1, 100);
    Computador pc2 = new Computador(2, 80);
    Computador pc3 = new Computador(3, 60);
    Computador pc4 = new Computador(4, 40);
    
    listaEventos = new LinkedList<Evento>();
    
  }

}
