package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;

public class Exponencial
{

  public static double geraAmostraExponencial(double a)
  {
    //double tempo = (-(log(Math.random()) / (1 / a)));
    //return Math.floor(tempo * 10000)/10000;    
    return (-(log(Math.random()) / (1 / a)));
  }
  
}
