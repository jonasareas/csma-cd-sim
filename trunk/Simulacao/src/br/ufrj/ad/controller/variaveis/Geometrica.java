package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;

public class Geometrica
{

  public static int geraAmostraGeometrica(double p)
  {
    // return (int) ((log(Math.random()) / log(1 - p)));
    return (int) ((log(Math.random()) / log(1 - p)) + 0.5);
  }

}
