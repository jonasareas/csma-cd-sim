package br.ufrj.ad.model;

public class Mensagem
{
  private int             quantidadeQuadros;                // Quantidade restante de quadros a serem enviados

  private int             totalQuadros;                     // Total de quadros da mensagem

  private Quadro          quadro = null;                    // Instancia de um Quadro da mensagem

  private double          tempoConsideradaTransmissao;      // Tempo inicial da mensagem para o Tam(i)

  private int             totalColisoes;                    // Total de colisoes da mensagem
  
  private int             codigoRodadaEntrada;              // Numero da rodada a qual a mensagem faz parte

  /*
   * Construtor da Classe: Responsavel por inicializar uma Mensagem com o seu tamanho e o codigo a qual ela pertence.
   */
  public Mensagem(int tamanho,int codigoRodadaEntrada)
  {
    if (tamanho > 0)
    {
      this.totalQuadros = tamanho;
      this.quantidadeQuadros = tamanho - 1;
      quadro = new Quadro();
    }
    this.codigoRodadaEntrada = codigoRodadaEntrada ;
    tempoConsideradaTransmissao = 0.0;
    totalColisoes = 0;
  }

  public Quadro getQuadro()
  {
    return quadro;
  }

  public int getQuantidadeQuadros()
  {
    return quantidadeQuadros;
  }

  public void setTempoConsideradaTransmissao(double tempoConsideradaTransmissao)
  {
    this.tempoConsideradaTransmissao = tempoConsideradaTransmissao;
  }

  public double getTempoConsideradaTransmissao()
  {
    return tempoConsideradaTransmissao;
  }

  public int getTotalQuadros() 
  { 
    return totalQuadros; 
  }
  
  public int getTotalColisoes() 
  { 
    return totalColisoes; 
  }

  public int getCodigoRodadaEntrada()
  {
    return codigoRodadaEntrada;
  }
  
  /*
   * Retorna o numero de colisoes por Quadro na mensagem
   */
  public double colisoesPorQuadro()
  {
    return (double) (totalColisoes) / (double) (totalQuadros);
  }

  /*
   * Finaliza o servico de um quadro e indica se ainda ha quadros a serem transmitidos.
   */
  public boolean fimServicoMensagem()
  {
      this.totalColisoes += quadro.getNumeroColisoes();
    
    if (this.quantidadeQuadros > 0)
    {
      this.quantidadeQuadros--;
      quadro = new Quadro();
      return false;
    }

    quadro = null;
    return true;
  }


}
