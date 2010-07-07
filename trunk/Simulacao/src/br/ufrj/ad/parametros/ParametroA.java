package br.ufrj.ad.parametros;

import br.ufrj.ad.controller.variaveis.Exponencial;

public class ParametroA
{

  public static double geraParametro(double a, boolean deterministico)
  {
    if (deterministico)
      return a;
    else
      return Exponencial.geraAmostraExponencial(a);
  }

}
