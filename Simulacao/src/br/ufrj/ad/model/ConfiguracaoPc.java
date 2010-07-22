/*
 * Trabalho de Avaliacao e Desempenho - 2010/1
 * Grupo:   Diogo Borges Lima
 *          Jonas Areas Fragozo de Souza
 *          Marcelo Jochem da Silva
 *          Thiago Elias Gomes
 *          
 * Professor Orientador: Paulo Aguiar
 */
package br.ufrj.ad.model;

public class ConfiguracaoPc
{
  private int     codigo;           // Codigo da Estacao
  
  private double  distancia;        // Distancia ao Hub
  
  private double  a;                // Parametro A (Tempo medio entre chegadas de mensagens)
  
  private double  p;                // Parametro P (Distribuicao da quantidade de quadros de uma mensagem)
  
  private boolean deterministico;   // Determina se o tempo entre chegadas eh deterministico ou exponencialmente distribuido

  /*
   * Construtor da Classe: Responsavel por inicializar uma configuracao de uma estacao
   */
  public ConfiguracaoPc(int codigo, double distancia, double p, double a, boolean deterministico)
  {
    this.codigo = codigo;
    this.a = a;
    this.p = p;
    this.distancia = distancia;
    this.deterministico = deterministico;
  }

  public int getCodigo()
  {
    return codigo;
  }

  public double getDistancia()
  {
    return distancia;
  }

  public double getA()
  {
    return a;
  }

  public double getP()
  {
    return p;
  }

  public boolean isDeterministico()
  {
    return deterministico;
  }
}
