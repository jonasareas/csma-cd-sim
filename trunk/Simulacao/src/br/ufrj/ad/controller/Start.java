package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ad.model.Estacao;

public class Start
{

  public static void main(String[] args)
  {
    Simulador simulador = new Simulador();
    
    simulador.iniciaSimulacao(10000000); // 10 segundos em microsegundos. Este parâmetro também virá da tela!    
  }

}
