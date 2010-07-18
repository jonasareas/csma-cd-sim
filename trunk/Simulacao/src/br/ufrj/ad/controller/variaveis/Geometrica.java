package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import br.ufrj.ad.util.MyRandom;

public class Geometrica
{

  public static int geraAmostraGeometrica(double p)
  {
    int numeroGerado = (int) ((log(MyRandom.rand()) / log(1 - p)) + 0.5);
    while (numeroGerado == 0) {
      numeroGerado = (int) ((log(MyRandom.rand()) / log(1 - p)) + 0.5);
    }
    return numeroGerado;
  }

}
