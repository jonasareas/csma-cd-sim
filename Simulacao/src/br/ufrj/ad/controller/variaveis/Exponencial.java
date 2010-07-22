package br.ufrj.ad.util.variaveis;

import static java.lang.Math.log;
import br.ufrj.ad.util.MyRandom;

public class Exponencial
{

  /*
   * Metodo responsavel por gerar a Amostra Exponencial
   */
  public static double geraAmostraExponencial(double a)
  {
    return (-(log(MyRandom.rand()) / (1 / a)));
  }
  
}
