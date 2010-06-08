package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import static java.lang.Math.random;

public class Geometrica
{

  public static double geraAmostra(double media) {
    return (log(random())/log(1 - 1/media));
  }
  
}
