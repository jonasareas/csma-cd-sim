package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import br.ufrj.ad.util.MyRandom;

public class Exponencial
{
		
  public static double geraAmostra(double a, boolean deterministico) {
	  if(deterministico)
		  return a*1000000;
	  else
    return (-(log(MyRandom.next())/(1/a)))*1000000;
  }
  
}
