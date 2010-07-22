/*
 * Trabalho de Avaliacao e Desempenho - 2010/1
 * Grupo:   Diogo Borges Lima
 *          Jonas Areas Fragozo de Souza
 *          Marcelo Jochem da Silva
 *          Thiago Elias Gomes
 *          
 * Professor Orientador: Paulo Aguiar
 */
package br.ufrj.ad.controller.variaveis;

import static java.lang.Math.log;
import br.ufrj.ad.util.MyRandom;

public class Exponencial
{

  /*
   * Metodo responsavel por gerar a Amostra Exponencial.
   */
  public static double geraAmostraExponencial(double a)
  {
    return (-(log(MyRandom.rand()) / (1 / a)));
  }
  
}
