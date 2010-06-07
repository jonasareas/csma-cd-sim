package br.ufrj.ad.controller.variaveis;

public class Exponencial
{

  public static double geraAmostra(double lambda) {
    return -(Math.log(Math.random())/lambda);
  }
  
}
