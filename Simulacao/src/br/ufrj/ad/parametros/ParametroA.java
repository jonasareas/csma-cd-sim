package br.ufrj.ad.util.parametros;

import br.ufrj.ad.util.variaveis.Exponencial;

public class ParametroA
{

  /*
   * Metodo responsavel por gerar o Parametro do tempo medio entre chegadas de mensagem.
   * Se o Parametro for deterministico entao retorna o proprio parametro,
   * caso contrario retorna uma amostra Exponencial
   */
  public static double geraParametro(double a, boolean deterministico)
  {
    if (deterministico)
      return a;
    else
      return Exponencial.geraAmostraExponencial(a);
  }

}
