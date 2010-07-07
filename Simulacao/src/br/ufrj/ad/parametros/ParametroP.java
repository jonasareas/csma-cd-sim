package br.ufrj.ad.parametros;

import br.ufrj.ad.controller.variaveis.Geometrica;

public class ParametroP
{

  public static int geraParametro(double p)
  {
    if (p >= 1.0)
      return (int) p;
    else
      return Geometrica.geraAmostraGeometrica(p);
  }

}
