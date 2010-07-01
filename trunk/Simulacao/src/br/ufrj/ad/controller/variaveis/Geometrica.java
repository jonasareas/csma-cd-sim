package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import static java.lang.Math.random;

public class Geometrica
{

  public static int geraAmostra(double p) {
    return (int) ((log(random())/log(1 - p)) + 0.5);
  }
  
}
