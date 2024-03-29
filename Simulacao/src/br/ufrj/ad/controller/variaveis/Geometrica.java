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

import br.ufrj.ad.util.MyRandom;

public class Geometrica
{

  /*
   * Metodo responsavel por gerar a Amostra Geometrica utilizando acumulador de probabilidades.
   */
  public static int geraAmostraGeometrica(double p)
  {
    int numeroGerado = 1;
    double d = p;
    double acumuladorPotencias = 1;
    double u = MyRandom.rand();
    
    while(d < u)
    {
      numeroGerado++;
      acumuladorPotencias *= 1-p; 
      d += acumuladorPotencias*p;
    }
    
    return numeroGerado;
  }

}
