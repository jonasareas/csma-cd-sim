package br.ufrj.ad.model;

public class Evento implements Comparable<Evento>
{
  
  /*
   * Enumerado sobre o tipo de Evento a ser processado
   */
  public static enum TipoEvento 
  {
    CHEGADA_NA_FILA, 
    TENTATIVA_ENVIO, 
    FIM_TRANSMISSAO_QUADRO, 
    MEIO_OCUPADO,
    MEIO_LIVRE,
    FIM_ESPERA_TEMPO_DE_SEGURANCA, 
    FIM_TRANSMISSAO_REFORCO_COLISAO, 
    FIM_ESPERA_BACKOFF
  }  
  
  private double tempoExecucao;     // Tempo de execucao de um evento
  
  private TipoEvento tipoEvento;    // Tipo de evento a ser processado
  
  private int codigoEstacao;        // Codigo da estacao a qual o evento pertence
  
  /*
   * Construtor da Classe: Define o Evento com o seu Tipo, tempo de execucao e de qual estacao ele pertence
   */
  public Evento(TipoEvento tipoEvento, double tempoExecucao, int codigoEstacao) {
    this.tipoEvento = tipoEvento;
    this.tempoExecucao = tempoExecucao;
    this.codigoEstacao = codigoEstacao;
  }
  
  public double getTempoExecucao()
  {
    return tempoExecucao;
  }

  public void setTempoExecucao(double tempoExecucao)
  {
    this.tempoExecucao = tempoExecucao;
  }

  public TipoEvento getTipoEvento()
  {
    return tipoEvento;
  }

  public void setTipoEvento(TipoEvento tipoEvento)
  {
    this.tipoEvento = tipoEvento;
  }
  
  public int getCodigoEstacao()
  {
    return codigoEstacao;
  }

  public void setCodigoEstacao(int codigoEstacao)
  {
    this.codigoEstacao = codigoEstacao;
  }

  /*
   * Metodo utilzado para ordenar a estrutura do Evento atraves de seu tempo de Execucao
   */
  public int compareTo(Evento e)
  {
	if (this.tempoExecucao < e.getTempoExecucao())
      return -1;
    else if (this.tempoExecucao > e.getTempoExecucao())
      return 1;
    return 0;
  }
  
  /*
   * Metodo responsavel por encontrar o evento na lista e remove-lo
   */
  @Override 
  public boolean equals(Object obj)
  {
	  if(obj == null)
		  return false;
	  
	  if (this == obj)
		  return true;
	  
	  if ( !(obj instanceof Evento) ) 
		  return false;
	  
	  Evento e = (Evento)obj;
	  
	  if(this.codigoEstacao == e.getCodigoEstacao() && this.tipoEvento == e.getTipoEvento())
		  return true;
	  
	  return false;
	  
  }  
}
