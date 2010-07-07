package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import br.ufrj.ad.util.MyRandom;

public class Exponencial
{

  public static double geraAmostraExponencial(double a)
  {
    double numeroGerado = (-(log(MyRandom.rand()) / (1 / a)));
    System.out.println("[LOG] Tempo da próxima chegada: " + numeroGerado);
    return numeroGerado;
  }
  
}
