package br.ufrj.ad.model;

import java.util.LinkedList;

import br.ufrj.ad.controller.AcumuladorEstatistico;

public class Estacao
{
  // Parametros da estacao

  private int                  codigo;                          // Codigo da Estacao

  private double               distancia;                       // Distancia ao Hub

  private double               p;                               // Parametro P (Distribuicao da quantidade de quadros de uma mensagem) 

  private double               a;                               // Parametro A (Tempo medio entre chegadas de mensagens)

  private boolean              deterministico;                  // Determina se o tempo entre chegadas eh deterministico ou exponencialmente distribuido

  // Variaveis de controle

  private boolean              analiseUtilizacaoEthernet;       // Indica se a estacao esta sendo utilizada para avaliar a utilizacao da ethernet
  
  private boolean              meioEmColisao;                   // Indica se uma colisao foi detectada

  private int                  meioOcupado;                     // Indica quantas estacoes estao ocupando o meio

  private boolean              esperandoTempoSeguranca;         // Indica se a Estacao esta esperando o tempo de 9,6 microssegundos

  private boolean              esperandoBackoff;                // Indica se a Estacao esta esperando o tempo de espera gerado pelo binaryBackoff

  private boolean              transmitindo;                    // Indica se a Estacao esta transmitindo um quadro ou um reforco de colisao

  private boolean              forcarEnvio;                     // Indica se a Estacao ira forcar o envio mesmo que o meio esteja ocupado

  private LinkedList<Mensagem> filaMensagens;                   // Fila de espera da estacao

  private int                  quadrosTransmitidos;             // Quantidade de quadros transmitidos

  private double               tempoInicioUtilizacao;           // Tempo de inicio de meio ocupado na estacao
  
  private double               tempoUtilizacaoEthernet;         // Computa o tempo total de utilizacao da ethernet

  
  
  /*
   * Construtor da Classe: Responsavel por inicializar uma Estacao
   */
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

  public void setTempoInicioUtilizacao(double tempoInicioUtilizacao)
  {
    this.tempoInicioUtilizacao = tempoInicioUtilizacao;
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
  
  /*
   * Adiciona uma mensagem na fila da estacao
   */
  public void addMensagem(Mensagem mensagem)
  {
    filaMensagens.addLast(mensagem);
  }

  /*
   * Retorna a mensagem atual na estacao que esta em servico
   */
  public Mensagem getMensagemEmServico()
  {
    if (filaMensagens.isEmpty())
      return null;

    return filaMensagens.getFirst();
  }

  /*
   * Verifica se nao ha mensagens na estacao
   */
  public boolean filaServicoVazia()
  {
    return filaMensagens.isEmpty();
  }
  
  /*
   * Computa o tempo de utilizacao do Ethernet
   */
  public void computaUtilizacaoEthernet(double tempoFim)
  {
    tempoUtilizacaoEthernet += (tempoFim - tempoInicioUtilizacao);
    tempoInicioUtilizacao = 0.0; 
  }

  /*
   * Processa a finalizacao de um quadro como Enviado.
   */
  public void quadroEnviado(double tempoFimServico, int codigoRodada)
  {
    AcumuladorEstatistico acumulador = AcumuladorEstatistico.getInstancia();
    
    Mensagem msg = filaMensagens.getFirst();
    
    double inicioTransmissao = msg.getQuadro().getTempoInicioTransmissao();             //Tempo inicial da transmissao de um quadro
    
    double chegadaQuadroNoServidor = msg.getQuadro().getTempoConsideradoTransmissao();  //Tempo inicial do TAp(i)
    
    double chegadaMensagemNoServidor = msg.getTempoConsideradaTransmissao();            // Tempo inicial do TAm(i)
    
    // Nao avalia estatisticas das mensagens que chegaram em rodadas anteriores!
    if(msg.getCodigoRodadaEntrada() == codigoRodada)
    {
      quadrosTransmitidos++;
    }
    
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

  /*
   * Processamento para descartar um quadro
   */
  public void descartaQuadro(double tempoFimTentativa, int codigoRodada)
  {
    Mensagem msg = filaMensagens.getFirst();
    
    // Nao contabiliza o TAp nem o TAm
    if (msg.fimServicoMensagem())
    {
      // Verifica se a mensagem eh da rodada atual
      if(msg.getCodigoRodadaEntrada() == codigoRodada)
        AcumuladorEstatistico.getInstancia().novaAmostraNcm(msg.colisoesPorQuadro(), codigo);
      filaMensagens.removeFirst();
    }
  }
  
  /*
   * Retorna a quantidade de quadros conmtidos fila
   */
  public int getQuadrosNaFila()
  {
    int total = 0;
    
    for (Mensagem msg : filaMensagens)
    {
      total += msg.getQuantidadeQuadros() + 1; /* Total de quadros eh a quantidade de quadros que a 
                                                  que a mensagem recebeu para enviar. O "+1" eh pq o 
                                                  valor quantidade de quadros, eh a quantidade de
                                                  quantidade de quadros restantes menos 1 que eh o 
                                                  quadro que esta na classe Quadro (proximo quadro
                                                  da mensagem) */
    }
    
    return total;
  }
  
  
}
