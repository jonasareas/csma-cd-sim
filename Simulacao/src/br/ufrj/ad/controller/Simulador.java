package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import br.ufrj.ad.model.ConfiguracaoPc;
import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.model.Evento;
import br.ufrj.ad.model.Mensagem;
import br.ufrj.ad.model.Quadro;
import br.ufrj.ad.model.Evento.TipoEvento;
import br.ufrj.ad.parametros.ParametroA;
import br.ufrj.ad.parametros.ParametroP;
import br.ufrj.ad.util.BinaryBackoff;

public class Simulador
{
  // Constantes
  private static final double   PROPAGACAO_ELETRICA      = 0.000005;    // Em milisegundos/metro
  
  private static final double   TEMPO_ENTRE_TRANSMISSOES = 0.0096;      // Em milessegundos
  
  private static final double   TEMPO_REFORCO_COLISAO    = 0.0032;      // Em milessegundos
  
  private static final double   TEMPO_ENVIO_DE_UM_BYTE   = 0.0008;      // Em milessegundos
  
  private static final int      TAMANHO_QUADRO           = 1000;        // Em bytes

  // Variaveis
  private PriorityQueue<Evento> listaEventos;
  
  private List<Estacao>         listaEstacoes;
  
  private AcumuladorEstatistico acumulador;
  
  private int codigoRodada;

  /*
   * Este metodo recebe da tela os parametros para simulacao, inicializa as estrutras necessarias e programa o primeiro evento
   * da simulacao que eh a chegada na fila das estacies que irao transmitir no cenario em questao.
   */
  public void iniciaSimulacao(double tempoRodada, int numeroRodadas, ArrayList<ConfiguracaoPc> parametros, int codigoEstacaoAnaliseUtilizacao)
  {
    listaEstacoes = new ArrayList<Estacao>();
    listaEventos = new PriorityQueue<Evento>();
    acumulador = AcumuladorEstatistico.getInstancia();    
    
    for (ConfiguracaoPc config : parametros)
    {
      Estacao estacao = new Estacao(config.getCodigo(), config.getDistancia(), config.getP(), config.getA(), config.isDeterministico());
      listaEstacoes.add(estacao);
      if (config.getCodigo() == codigoEstacaoAnaliseUtilizacao)
        estacao.setAnaliseUtilizacaoEthernet(true);
    }

    for (Estacao estacao : listaEstacoes)
    {
      acumulador.addEstacao(estacao.getCodigo());      
      if (estacao.getP() != 0)
        programaChegadaNaFila(ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico()), estacao.getCodigo()); 
    }
    
    codigoRodada = 1;
    simula(tempoRodada, numeroRodadas);
  }

  /*
   * Este metodo eh o que promove a simulacao em si. Para cada uma das rodadas programadas o método vai consumindo a 
   * lista de eventos e delegando o tratamento de cada caso. Ao final da rodada eh acionado o acumulador estatistico que coleta
   * as informacoes necessarias para as grandezas pedidas. Ao final da simulacao eh realizada a extracao das estatisticas coletadas
   * durante a mesma.
   */
  private void simula(double tempoSimulacao, int numeroRodadas)
  {
    Evento evento = null;
    for(int i = 1 ; i <= numeroRodadas; i++)
    {
      do
      {
        evento = listaEventos.remove();
        verificaTipoEvento(evento);
      }
      while (evento.getTempoExecucao() < tempoSimulacao * i);
      System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim da rodada " + i + " --------------------------------------"); //TODO 
      acumulador.fimRodada();      
      codigoRodada++;
    }
    acumulador.extraiEstatistica(listaEstacoes, evento.getTempoExecucao());
  }

  /*
   * Este metodo programa um evento de chegada na fila da estacao cujo codigo eh passado por parametro
   * e no tempo passado, adicionando este evento a lista de eventos.
   */
  private void programaChegadaNaFila(double tempoExecucao, int codigoEstacao)
  {
    Evento evento = new Evento(TipoEvento.CHEGADA_NA_FILA, tempoExecucao, codigoEstacao);
    listaEventos.add(evento);
  }  
  
  /*
   * Metodo auxiliar que retorna a estacao que tem como codigo o valor passado como parametro para o metodo.
   */
  private Estacao buscaEstacao(int codEstacao)
  {
    for (Estacao estacao : listaEstacoes)
    {
      if (estacao.getCodigo() == codEstacao)
        return estacao;
    }
    return null;
  }
  
  /*
   * Metodo auxiliar que promove o envio de informacoes de uma estacao (passada por parametro) e gera o evento correspondente 
   * ao recebimento dessas informacoes (MeioOcupado ou MeioLivre, dependendo do tipo de informacao que a estacao esta enviando).
   */
  private void enviaInformacoes(Evento evento, Estacao estacao, TipoEvento tipoEvento) {
    for (Estacao e : listaEstacoes)
    {
      if (e.getCodigo() != estacao.getCodigo())
      {
        double distanciaTotal = e.getDistancia() + estacao.getDistancia();
        double tempoPercepecao = evento.getTempoExecucao();

        tempoPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;

        Evento eventoPercepcao = new Evento(tipoEvento, tempoPercepecao, e.getCodigo());
        listaEventos.add(eventoPercepcao);
      }
    }  
  }  

  /*
   * Este metodo promove o tratamento de cada tipo de evento, delegando para o metodo correspondente.
   */
  private void verificaTipoEvento(Evento evento)
  {
    switch (evento.getTipoEvento())
    {
      case CHEGADA_NA_FILA:
        // System.out.print(Double.toString(evento.getTempoExecucao()) + ": Chegada na fila de " + evento.getCodigoEstacao()); // TODO 
        processaChegadaNaFila(evento);
        break;
      case TENTATIVA_ENVIO:
     // System.out.println(Double.toString(evento.getTempoExecucao()) + ": Tentativa de envio de " + evento.getCodigoEstacao()); // TODO 
        processaTentativaEnvio(evento);
        break;
      case FIM_TRANSMISSAO_QUADRO:
     // System.out.print(Double.toString(evento.getTempoExecucao()) + ": Fim de transmissao de um quadro de " + evento.getCodigoEstacao()); // TODO
        processaFimTransmissaoQuadro(evento);
        break;
      case FIM_ESPERA_TEMPO_DE_SEGURANCA:
     // if (!buscaEstacao(evento.getCodigoEstacao()).filaServicoVazia()){
     //  System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de espera de " + evento.getCodigoEstacao()); //TODO
     // }
        processaFimEsperaTempoSeguranca(evento);
        break;
      case MEIO_LIVRE:
     //  System.out.println(evento.getTempoExecucao() + ": Percepcao de meio livre em " + evento.getCodigoEstacao()); //TODO
        processaMeioLivre(evento);
        break;
      case MEIO_OCUPADO:
     //   System.out.println(evento.getTempoExecucao() + ": Percepcao de meio ocupado em " + evento.getCodigoEstacao()); //TODO
        processaMeioOcupado(evento);
        break;
      case FIM_TRANSMISSAO_REFORCO_COLISAO:
     //  System.out.println(evento.getTempoExecucao() + ": Fim de reforco de " + evento.getCodigoEstacao()); //TODO
        processaFimTransmissaoReforcoColisao(evento);
        break;
      case FIM_ESPERA_BACKOFF:
     //  System.out.println(evento.getTempoExecucao() + ": Fim de backoff de " + evento.getCodigoEstacao()); //TODO
        processaFimEsperaBackOff(evento);
        break;
    }
  }

  /*
   * Este metodo trata o evento de chegada de uma mensagem na fila da estacao relacionada ao evento.
   * Primeiro eh programada uma nova chegada na fila desta estacao.
   * Depois eh programado um evento de tentativa de envio, caso a estacao nao possua uma mensagem jah em servico
   * (ou seja, sendo enviada)
   */
  private void processaChegadaNaFila(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    // Programa próxima chegada
    double proximoTempo = evento.getTempoExecucao() + ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico());
    programaChegadaNaFila(proximoTempo, estacao.getCodigo());

    // Realiza a chegada na fila
    int quantidadeQuadros = ParametroP.geraParametro(estacao.getP());
    Mensagem mensagem = new Mensagem(quantidadeQuadros, codigoRodada);
    if (estacao.filaServicoVazia() && !(estacao.isEsperandoBackoff() || estacao.isEsperandoTempoSeguranca()))
    {
      Evento novoEvento = new Evento(TipoEvento.TENTATIVA_ENVIO, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novoEvento);
    }
    estacao.addMensagem(mensagem);
 //  System.out.println(" - Chegou uma mensagem com " + quantidadeQuadros + " quadros. " +
 //                 "Existe(m) " + buscaEstacao(evento.getCodigoEstacao()).getQuadrosNaFila() +  
 //                   " quadro(s) na fila de " + evento.getCodigoEstacao()); //TODO
  }

  /*
   * Este metodo trata a tentativa de envio de uma mensagem por parte da estacao relacionada ao evento.
   * Primeiro sao setados os tempos para coleta de TAp e TAm.
   * Depois, caso o meio esteja ocupado, verifica-se se a estacao esta esperando o tempo de seguranca. 
   *    Caso nao esteja, eh programado um evento de fim de transmissao de quadro no tempo devido.
   *    Caso esteja, apenas eh indicado que um envio forcado deve acontecer assim que acabar a espera do tempo de seguranca
   * Se o meio estiver ocupado, verifica-se se a estacao devera forcar um envio.
   *    Se o envio deve ser forcado, ocorrera colisao, e eh programa um evento de fim de transmissao do reforco de colisao no tempo devido.
   *    Se nao, nada eh feito.
   */  
  private void processaTentativaEnvio(Evento evento) 
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
    
    Mensagem mensagem = estacao.getMensagemEmServico();
    Quadro quadro = mensagem.getQuadro();
    
    if (mensagem.getTempoConsideradaTransmissao() == 0.0)
      mensagem.setTempoConsideradaTransmissao(evento.getTempoExecucao());
    if (quadro.getTempoConsideradoTransmissao() == 0.0)
      quadro.setTempoConsideradoTransmissao(evento.getTempoExecucao());

    if (estacao.getMeioOcupado() == 0)
    {
      if (!estacao.isEsperandoTempoSeguranca())
      {
        estacao.setTempoInicioUtilizacao(evento.getTempoExecucao());
        
        quadro.setInicioTransmissao(evento.getTempoExecucao());
     //    System.out.println(Double.toString(evento.getTempoExecucao()) + ": Inicio de envio de " + evento.getCodigoEstacao()); //TODO

        enviaInformacoes(evento, estacao, TipoEvento.MEIO_OCUPADO);
        estacao.setForcarEnvio(false);
        estacao.setTransmitindo(true);
        
        double tempoFimTransmissao = evento.getTempoExecucao() + (TAMANHO_QUADRO * TEMPO_ENVIO_DE_UM_BYTE); 
        Evento fimTransmissao = new Evento(TipoEvento.FIM_TRANSMISSAO_QUADRO, tempoFimTransmissao, estacao.getCodigo());
        listaEventos.add(fimTransmissao);
      }
      else
      {
        estacao.setForcarEnvio(true);
      }
    }
    else
    {
      if (estacao.isForcarEnvio()) 
      {
     //   System.out.println(Double.toString(evento.getTempoExecucao()) + ": Sentiu meio ocupado em " + evento.getCodigoEstacao() + " e vai forcar envio!"); //TODO        
        quadro.setInicioTransmissao(evento.getTempoExecucao());
        enviaInformacoes(evento, estacao, TipoEvento.MEIO_OCUPADO);
        estacao.setForcarEnvio(false);
        estacao.setTransmitindo(true);
        estacao.setMeioEmColisao(true);
        
        double tempoFimReforcoColisao = evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO;
        Evento fimTransmissao = new Evento(TipoEvento.FIM_TRANSMISSAO_REFORCO_COLISAO, tempoFimReforcoColisao, estacao.getCodigo());
        listaEventos.add(fimTransmissao);
      }
      else
      {
     //    System.out.println(Double.toString(evento.getTempoExecucao()) + ": Sentiu meio ocupado em " + evento.getCodigoEstacao() + " mas nao vai forcar envio!"); //TODO
        return;
      }
    }
  }

  /*
   * Este metodo trata o fim da transmissao de um quadro por parte da estacao relacionada ao evento.
   * Primeiro determinamos que o quadro foi enviado com sucesso para para coleta de TAp e TAm.
   * Depois disso, eh programado um evento de fim de espera do tempo de seguranca (ate que se possa enviar
   * um novo quadro), no tempo devido.
   */  
  private void processaFimTransmissaoQuadro(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
    
    estacao.computaUtilizacaoEthernet(evento.getTempoExecucao());

    enviaInformacoes(evento, estacao, TipoEvento.MEIO_LIVRE);
    estacao.setEsperandoTempoSeguranca(true);
    estacao.setTransmitindo(false);
    estacao.setForcarEnvio(false);
    estacao.setMeioEmColisao(false);

    estacao.quadroEnviado(evento.getTempoExecucao(), codigoRodada);

    double tempoFimEspera = evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES;
    Evento fimEsperaTempoSeguranca = new Evento(TipoEvento.FIM_ESPERA_TEMPO_DE_SEGURANCA, tempoFimEspera, estacao.getCodigo());
    listaEventos.add(fimEsperaTempoSeguranca);
 //    System.out.println(" - Existe(m) " + buscaEstacao(evento.getCodigoEstacao()).getQuadrosNaFila() +  " quadro(s) na fila de " + evento.getCodigoEstacao()); //TODO
  }

  /*
   * Este metodo trata o fim da espera do tempo de seguranca necessario ate que a estacao relacionada ao evento possa
   * realizar novo envio de quadro.
   * Neste metodo, simplesemente eh programado um evento de tentativa de envio imediatamente, caso a fila de servico
   * da estacao nao esteja vazia (ou seja, exista mensagem a ser enviada).
   */
  private void processaFimEsperaTempoSeguranca(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoTempoSeguranca(false);

    if (!estacao.filaServicoVazia())
    {
      Evento novaTentativa = new Evento(TipoEvento.TENTATIVA_ENVIO, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novaTentativa);
    }
  }

  /*
   * Este metodo trata o evento que indica a percepcao de meio livre para a estacao relacionada ao evento.
   * TODO NAO ENTENDI! =/
   * Na verdade ela indica que os bits da transmissao de UMA das estacoes que estavam ocupando o meio terminou
   */
  private void processaMeioLivre(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setMeioOcupado(estacao.getMeioOcupado() - 1);
    
    if(estacao.getMeioOcupado() == 0 && !estacao.isTransmitindo())
    {
      estacao.computaUtilizacaoEthernet(evento.getTempoExecucao());
    }

    if (estacao.isTransmitindo())
      return;

    if (estacao.getMeioOcupado() == 0)
    {
      if (estacao.isEsperandoTempoSeguranca() && !estacao.filaServicoVazia())
      {
        estacao.setForcarEnvio(true);
      }
      else
      {
        if (!estacao.isEsperandoBackoff())
        {
          estacao.setEsperandoTempoSeguranca(true);
          Evento fimEspera = new Evento(TipoEvento.FIM_ESPERA_TEMPO_DE_SEGURANCA, evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES, evento.getCodigoEstacao());
          listaEventos.add(fimEspera);
        }
      }
    }
  }
  
  /*
   * Este metodo trata o evento que indica a percepcao de meio ocupado para a estacao relacionada ao evento.
   * TODO NAO ENTENDI! =/
   */  
  private void processaMeioOcupado(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
    
    if(estacao.getMeioOcupado() == 0 && !estacao.isTransmitindo())
      estacao.setTempoInicioUtilizacao(evento.getTempoExecucao());

    estacao.setMeioOcupado(estacao.getMeioOcupado() + 1);

    if (estacao.isTransmitindo() && !estacao.isMeioEmColisao())
    {
      estacao.setMeioEmColisao(true);
      
      listaEventos.remove(new Evento(TipoEvento.FIM_TRANSMISSAO_QUADRO, 0, estacao.getCodigo()));      
  
      Evento fimReforco = new Evento(TipoEvento.FIM_TRANSMISSAO_REFORCO_COLISAO, evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO, estacao.getCodigo());
      listaEventos.add(fimReforco);
      }
  }

  /*
   * Este metodo trata o fim da transmissao de um deforco de colisao por parte da estacao relacionada ao evento.
   * Primeiro incrementamos o numero de colisoes sofridas pelo quadro para calculo do NCm.
   * Depois disso, eh programado um evento de fim de espera do backoff de acordo com o algorimo Binary Backoff.
   */  
  private void processaFimTransmissaoReforcoColisao(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
    
    if(estacao.getMeioOcupado() == 0)
    {
      estacao.computaUtilizacaoEthernet(evento.getTempoExecucao());
    }
    
    enviaInformacoes(evento, estacao, TipoEvento.MEIO_LIVRE);
    estacao.setEsperandoTempoSeguranca(false);
    estacao.setTransmitindo(false);
    estacao.setForcarEnvio(false);
    estacao.setMeioEmColisao(false);

    estacao.getMensagemEmServico().getQuadro().incrementaColisoes(); 
    
    double tempoEsperaBackoff = BinaryBackoff.geraAtraso(estacao.getMensagemEmServico().getQuadro().getNumeroColisoes());

    if (tempoEsperaBackoff < 0)
      estacao.descartaQuadro(evento.getTempoExecucao(), codigoRodada);
    else
    {
      estacao.setEsperandoBackoff(true);
      tempoEsperaBackoff += evento.getTempoExecucao();
      Evento esperaBackOff = new Evento(TipoEvento.FIM_ESPERA_BACKOFF, tempoEsperaBackoff, estacao.getCodigo());
      listaEventos.add(esperaBackOff);
    }
  }

  /*
   * Este metodo trata o fim da espera do backoff apos uma colisao de um quado da estacao relaicionada ao evento.
   * Neste metodo, simplesemente eh programado um evento de tentativa de envio imediatamente. Nao preciso verificar se 
   * a fila de servico nao esta vazia pois o backoff necessariamente eh gerado por uma mensagem que tentou ser enviada mas colidiu.
   */
  private void processaFimEsperaBackOff(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoBackoff(false);

    // Nao preciso verificar se tem mensagem para ser enviada!
    Evento novaTentativa = new Evento(TipoEvento.TENTATIVA_ENVIO, evento.getTempoExecucao(), estacao.getCodigo());
    listaEventos.add(novaTentativa);
  }

}
