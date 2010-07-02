package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import br.ufrj.ad.util.MyRandom;

public class Geometrica
{

  public static int geraAmostra(double p) {
	  if(p >= 1.0 )
		  return (int)p;
	  else
		  return (int) ((log(MyRandom.next())/log(1 - p)) + 0.5);
  }
  
}
