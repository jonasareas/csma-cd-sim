package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import static java.lang.Math.random;

public class Exponencial
{

  public static double geraAmostra(double lambda) {
    return -(log(random())/lambda);
  }
  
}
