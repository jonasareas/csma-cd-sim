package br.ufrj.ad.parametros;

import br.ufrj.ad.controller.variaveis.Geometrica;

public class ParametroP
{
  
  /*
   * Metodo responsavel por gerar o Parametro da distribuicao da quantidade de quadros de uma mensagem.
   * Se o Parametro p for maior ou igual a 1 entao a distribuicao sera o proprio p,
   * caso contrario sera gerada uma amostra Geometrica.
   */
  public static int geraParametro(double p)
  {
    if (p >= 1.0)
      return (int) p;
    else
      return Geometrica.geraAmostraGeometrica(p);
  }

}
