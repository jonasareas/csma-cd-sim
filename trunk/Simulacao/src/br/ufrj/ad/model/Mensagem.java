package br.ufrj.ad.model;

public class Mensagem
{
  private int             quantidadeQuadros;            // Quantidade restante de quadros a serem enviados

  private int             totalQuadros;                 // Total de quadros da mensagem

  private Quadro          quadro         = null;

  private double          tempoConsideradaTransmissao;

  private int             totalColisoes;
  
  private int             codigoRodadaEntrada; 

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

  // Finaliza o serviço de um quadro e indica se ainda há quadros a serem transmitidos.
  public boolean fimServicoMensagem()
  {
    if (this.quantidadeQuadros > 0)
    {
      this.quantidadeQuadros--;
      this.totalColisoes += quadro.getNumeroColisoes();
      quadro = new Quadro();
      return false;
    }

    quadro = null;
    return true;

  }

  public int getQuantidadeQuadros()
  {
    return quantidadeQuadros;
  }

  public void setTempoConsideradaTransmissao(double tempoConsideradaTransmissao)
  {
    this.tempoConsideradaTransmissao = tempoConsideradaTransmissao;
    // quadro.setTempoConsideradoTransmissao(tempoConsideradaTransmissao); TODO: Pode tirar?
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

  public double colisoesPorQuadro()
  {
    return (double) (totalColisoes) / (double) (totalQuadros);
  }

  public int getCodigoRodadaEntrada()
  {
    return codigoRodadaEntrada;
  }

}
