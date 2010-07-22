/*
 * Trabalho de Avaliacao e Desempenho - 2010/1
 * Grupo:   Diogo Borges Lima
 *          Jonas Areas Fragozo de Souza
 *          Marcelo Jochem da Silva
 *          Thiago Elias Gomes
 *          
 * Professor Orientador: Paulo Aguiar
 */
package br.ufrj.ad.parametros;

import br.ufrj.ad.controller.variaveis.Exponencial;

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
