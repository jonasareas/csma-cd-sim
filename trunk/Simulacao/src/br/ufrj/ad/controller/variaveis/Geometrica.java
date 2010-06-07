package br.ufrj.ad.controller.variaveis;

public class Geometrica
{

  public static double geraAmostra(double media) {
    return (Math.log(Math.random())/Math.log(1 - 1/media));
  }
  
}
