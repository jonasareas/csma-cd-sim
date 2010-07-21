package br.ufrj.ad.model;

import java.util.LinkedList;

import br.ufrj.ad.controller.AcumuladorEstatistico;

public class Estacao
{
  // Parametros da estacao

  private int                  codigo;

  private double               distancia;

  private double               p;

  private double               a;

  private boolean              deterministico;

  // Variaveis de controle

  private boolean              analiseUtilizacaoEthernet;
  
  private boolean              meioEmColisao;

  private int                  meioOcupado;            // Indica quantas estacoes estao ocupando o meio

  private boolean              esperandoTempoSeguranca;

  private boolean              esperandoBackoff;

  private boolean              transmitindo;

  private boolean              forcarEnvio;

  private LinkedList<Mensagem> filaMensagens;

  private int                  quadrosTransmitidos;

  private double               tempoInicioTentativa;
  
  private double               tempoUtilizacaoEthernet;

  public Estacao(int codigo, double distancia, double p, double a, boolean ehDeterministico)
  {
    this.codigo = codigo;
    this.distancia = distancia;
    this.p = p;
    this.a = a;
    this.deterministico = ehDeterministico;
    this.filaMensagens = new LinkedList<Mensagem>();
    this.meioEmColisao = false;
    this.meioOcupado = 0;
    this.esperandoTempoSeguranca = false;
    this.transmitindo = false;
    this.forcarEnvio = false;
    this.esperandoBackoff = false;
    this.quadrosTransmitidos = 0;
    this.tempoUtilizacaoEthernet = 0.0;
    this.analiseUtilizacaoEthernet = false;
  }

  public int getCodigo()
  {
    return codigo;
  }

  public double getDistancia()
  {
    return distancia;
  }

  public boolean isMeioEmColisao()
  {
    return meioEmColisao;
  }

  public void setMeioEmColisao(boolean meioEmColisao)
  {
    this.meioEmColisao = meioEmColisao;
  }

  public int getMeioOcupado()
  {
    return meioOcupado;
  }

  public void setMeioOcupado(int meioOcupado)
  {
    this.meioOcupado = meioOcupado;
  }

  public double getP()
  {
    return p;
  }

  public double getA()
  {
    return a;
  }

  public boolean isDeterministico()
  {
    return deterministico;
  }

  public void addMensagem(Mensagem mensagem)
  {
    filaMensagens.addLast(mensagem);
  }

  public Mensagem getMensagemEmServico()
  {
    if (filaMensagens.isEmpty())
      return null;

    return filaMensagens.getFirst();
  }

  public boolean filaServicoVazia()
  {
    return filaMensagens.isEmpty();
  }

  public void setEsperandoTempoSeguranca(boolean esperandoTempoSeguranca)
  {
    this.esperandoTempoSeguranca = esperandoTempoSeguranca;
  }

  public boolean isEsperandoTempoSeguranca()
  {
    return esperandoTempoSeguranca;
  }

  public void setTransmitindo(boolean transmitindo)
  {
    this.transmitindo = transmitindo;
  }

  public boolean isTransmitindo()
  {
    return transmitindo;
  }

  public void setForcarEnvio(boolean forcarEnvio)
  {
    this.forcarEnvio = forcarEnvio;
  }

  public boolean isForcarEnvio()
  {
    return forcarEnvio;
  }
  
  public void setEsperandoBackoff(boolean esperandoBackoff)
  {
    this.esperandoBackoff = esperandoBackoff;
  }

  public boolean isEsperandoBackoff()
  {
    return esperandoBackoff;
  }

  public int getQuadrosTransmitidos()
  {
    return quadrosTransmitidos;
  }

  public void setTempoInicioTentativa(double tempoInicioTentativa)
  {
    this.tempoInicioTentativa = tempoInicioTentativa;
  }

  public double getTempoInicioTentativa()
  {
    return tempoInicioTentativa;
  }
  
  public double getTempoUtilizacaoEthernet()
  {
    return tempoUtilizacaoEthernet;
  }  
  
  public boolean isAnaliseUtilizacaoEthernet()
  {
    return analiseUtilizacaoEthernet;
  }

  public void setAnaliseUtilizacaoEthernet(boolean analiseUtilizacaoEthernet)
  {
    this.analiseUtilizacaoEthernet = analiseUtilizacaoEthernet;
  }

  public void quadroEnviado(double tempoFimServico, int codigoRodada)
  {
    AcumuladorEstatistico acumulador = AcumuladorEstatistico.getInstancia();
    
    Mensagem msg = filaMensagens.getFirst();
    
    // Não avalia estatísticas das mensagens que chegaram em rodadas anteriores!
    if(msg.getCodigoRodadaEntrada() == codigoRodada)
    {
      this.tempoUtilizacaoEthernet += tempoFimServico - this.tempoInicioTentativa;
      quadrosTransmitidos++;
    }
    this.tempoInicioTentativa = 0.0;

    double inicioTransmissao = msg.getQuadro().getTempoInicioTransmissao();
    double chegadaQuadroNoServidor = msg.getQuadro().getTempoConsideradoTransmissao();
    double chegadaMensagemNoServidor = msg.getTempoConsideradaTransmissao();
    
    if(msg.getCodigoRodadaEntrada() == codigoRodada)
      acumulador.novaAmostraTap(inicioTransmissao - chegadaQuadroNoServidor, codigo);

    if (msg.fimServicoMensagem())
    {
      if(msg.getCodigoRodadaEntrada() == codigoRodada)
      {
        acumulador.novaAmostraTam(inicioTransmissao - chegadaMensagemNoServidor, codigo);
        acumulador.novaAmostraNcm(msg.colisoesPorQuadro(), codigo);
      }
      filaMensagens.removeFirst();
    }
  }  

  public void descartaPacote(double tempoFimTentativa, int codigoRodada)
  {
    Mensagem msg = filaMensagens.getFirst();
    
    if(msg.getCodigoRodadaEntrada() == codigoRodada && analiseUtilizacaoEthernet)
      this.tempoUtilizacaoEthernet += tempoFimTentativa - this.tempoInicioTentativa; //TODO: Rever se tá certo!
    this.tempoInicioTentativa = 0.0;

    // Nao contabiliza o TAp nem o TAm
    if (msg.fimServicoMensagem())
    {
      if(msg.getCodigoRodadaEntrada() == codigoRodada)
        AcumuladorEstatistico.getInstancia().novaAmostraNcm(msg.colisoesPorQuadro(), codigo);
      filaMensagens.removeFirst();
    }
  }
  
  public int getQuadrosNaFila()
  {
    int total = 0;
    for (Mensagem msg : filaMensagens)
    {
      total += msg.getQuantidadeQuadros() + 1; // Total de quadros eh a quantidade de quadros que a 
                                             // que a mensagem recebeu para enviar. O "+1" eh pq o 
                                             // valor quantidade de quadros, eh a quantidade de
                                             // quantidade de quadros restantes menos 1 que eh o 
                                             // quadro que esta na classe Quadro (proximo quadro
                                             // da mensagem)
    }
    return total;
  }
}
