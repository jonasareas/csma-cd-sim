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

  private boolean              meioEmColisao;

  private int                  meioOcupado;            // indica quantos pcs estao ocupando o meio

  private boolean              esperandoTempoSeguranca;

  private boolean              esperandoBackoff;

  private boolean              transmitindo;

  private boolean              forcarEnvio;

  private LinkedList<Mensagem> listaMensagens;

  private int                  quadrosTransmitidos;

  private double               tempoInicioTentativa;
  
  public double                utilizacaooEthernet;

  public Estacao(int codigo, double distancia, double p, double a, boolean ehDeterministico)
  {
    this.codigo = codigo;
    this.distancia = distancia;
    this.p = p;
    this.a = a;
    this.deterministico = ehDeterministico;
    this.listaMensagens = new LinkedList<Mensagem>();
    this.meioEmColisao = false;
    this.meioOcupado = 0;
    this.esperandoTempoSeguranca = false;
    this.transmitindo = false;
    this.forcarEnvio = false;
    this.esperandoBackoff = false;
    this.quadrosTransmitidos = 0;
    this.utilizacaooEthernet = 0.0;
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

  public void addMensagem(Mensagem mem)
  {
    listaMensagens.addLast(mem);
  }

  public Mensagem getMensagem()
  {
    if (listaMensagens.isEmpty())
      return null;

    return listaMensagens.getFirst();
  }

  public boolean isFilaVazia()
  {
    return listaMensagens.isEmpty();
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
  

  public void pacoteEnviado(double tempoFimServico, int codigoRodada)
  {
    AcumuladorEstatistico ac = AcumuladorEstatistico.getInstancia();
    
    Mensagem msg = listaMensagens.getFirst();
    
    // informacao pedida
    if(msg.getCodigoRodadaEntrada() == codigoRodada)
    {
      this.utilizacaooEthernet += tempoFimServico - this.tempoInicioTentativa;
      quadrosTransmitidos++;
    }
    this.tempoInicioTentativa = 0.0;

    double fim = msg.getQuadro().getTempoFinalAcesso();
    double inicio = msg.getQuadro().getTempoInicialAcesso();
    
    // informacao pedida
    if(msg.getCodigoRodadaEntrada() == codigoRodada)
      ac.addTap(fim - inicio);

    if (!msg.fimServicoQuadro())
    {
      // informacao pedida
      if(msg.getCodigoRodadaEntrada() == codigoRodada)
      {
        ac.addTam(fim - msg.getTempoInicialAcesso());
      
        ac.addNcm(msg.colisoesPorQuadro());
      }
      listaMensagens.removeFirst();
    }
  }

  public int getTamanhoQuadro()
  {
    return listaMensagens.getFirst().getQuadro().getTamanhoQuadro();
  }

  public void incrementaQuantidadeColisoes()
  {
    listaMensagens.getFirst().getQuadro().incrementaColisoes();
  }

  public int getQuantidadeColisoes()
  {
    return listaMensagens.getFirst().getQuadro().getColisoes();
  }

  public void descartaPacote(double tempoFimTentativa, int codigoRodada)
  {
    Mensagem msg = listaMensagens.getFirst();
    
    
    if(msg.getCodigoRodadaEntrada() == codigoRodada)
      this.utilizacaooEthernet += tempoFimTentativa - this.tempoInicioTentativa; //EH SOH PARA O PC 1
    this.tempoInicioTentativa = 0.0;

    

    // Nao contabiliza o TAp nem o TAm

    if (!msg.fimServicoQuadro())
    {
      // informacao pedida
      if(msg.getCodigoRodadaEntrada() == codigoRodada)
        AcumuladorEstatistico.getInstancia().addNcm(msg.colisoesPorQuadro());
      listaMensagens.removeFirst();
    }
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
  
  public int extraiQuadrosTransmitidos()
  {
    int tm = quadrosTransmitidos;
    quadrosTransmitidos = 0;
    return tm;
  }
  
  public double extraiUtilizacao()
  {
    double tm = utilizacaooEthernet;
    utilizacaooEthernet = 0.0;
    return tm;
  }
}
