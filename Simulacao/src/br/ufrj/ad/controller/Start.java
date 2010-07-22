package br.ufrj.ad.controller;

import br.ufrj.ad.util.MyRandom;
import br.ufrj.ad.view.Tela;

public class Start
{
  /*
   * Classe Principal.  Inicializa a geracao de numeros aleatorios a partir da semente zero
   * e inicializa a Interface
   */
  public static void main(String[] args)
  {
    MyRandom.setSeed(0);
    new Tela().setVisible(true);
  }
}
