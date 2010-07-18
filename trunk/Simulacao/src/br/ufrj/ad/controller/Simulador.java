package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import br.ufrj.ad.model.ConfiguracaoPc;
import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.model.Evento;
import br.ufrj.ad.model.Mensagem;
import br.ufrj.ad.model.Quadro;
import br.ufrj.ad.parametros.ParametroA;
import br.ufrj.ad.parametros.ParametroP;
import br.ufrj.ad.util.BinaryBackoff;
import br.ufrj.ad.view.Tela;

public class Simulador
{
  public static enum TipoEvento 
  {
    ChegadaNaFila, 
    TentativaEnvio, 
    FimTransmissao, 
    MeioOcupado,
    MeioOcupadoPorMim,
    MeioLivre,
    MeioLivrePorMim,
    FimEspera, 
    FimReforcoColisao, 
    FimBackOff
  }

  private static final double   PROPAGACAO_ELETRICA      = 0.000005;    // Em milisegundos/metro
  private static final double   TEMPO_ENTRE_TRANSMISSOES = 0.0096;      // Em milisegundos
  private static final double   TEMPO_REFORCO_COLISAO    = 0.0032;      // Em milisegundos
  private static final double   TEMPO_ENVIO_DE_UM_BYTE   = 0.0008;      // Em milisegundos

  private PriorityQueue<Evento> listaEventos;

  private List<Estacao>         listaPCs                 = new ArrayList<Estacao>();
  
  private AcumuladorEstatistico acumulador;
  
  private int codigoEstacaoAnaliseUtilizacao;
  
  private int codigoRodada;

  public void iniciaSimulacao(double tempoRodada, int numeroRodadas, ArrayList<ConfiguracaoPc> parametros, int codigoEstacaoAnaliseUtilizacao)
  {
    Estacao est;
    for (ConfiguracaoPc config : parametros)
    {
      est = new Estacao(config.getCodigo(), config.getDistancia(), config.getP(), config.getA(), config.isDeterministico());
      listaPCs.add(est);
    }
    this.codigoEstacaoAnaliseUtilizacao = codigoEstacaoAnaliseUtilizacao;

    listaEventos = new PriorityQueue<Evento>();

    int i = 0;
    for (Estacao estacao : listaPCs)
    {
      if (estacao.getP() != 0)
      {
        i++;
        programaChegadaNaFila(ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico()), estacao.getCodigo()); 
      }
    }
    acumulador = AcumuladorEstatistico.getInstancia();
    acumulador.clear(i);
    codigoRodada = 1;
    simula(tempoRodada, numeroRodadas);
  }

  private void programaChegadaNaFila(double tempoExecucao, int codigoEstacao)
  {
    Evento evento = new Evento(TipoEvento.ChegadaNaFila, tempoExecucao, codigoEstacao);
    listaEventos.add(evento);
  }

  private void simula(double tempoSimulacao, int numeroRodadas)
  {
    Evento evento;
    for(int i = 1 ; i <= numeroRodadas; i++)
    {
      do
      {
        evento = listaEventos.remove();
        verificaTipoEvento(evento);
      }
      while (evento.getTempoExecucao() < tempoSimulacao*i);
      
      ArrayList<Double> vz = new ArrayList<Double>();
      for(Estacao e: listaPCs)
      {
        if(e.getP() != 0)
        {
          vz.add(e.extraiQuadrosTransmitidos()/tempoSimulacao);
        }
      }
      
      Estacao pcAnaliseUtilizacao = buscaEstacao(codigoEstacaoAnaliseUtilizacao);
      if(acumulador.isTransiente())
      {
        // numeroRodadas++; //TODO: Isso não pode ser assim, senão caga o tempo de simulação! =S
      }
      else
      {
        Tela.jTextLog.append("Final da rodada " + codigoRodada + "\n");
        codigoRodada++;
      }
      acumulador.fimRodada(pcAnaliseUtilizacao.extraiUtilizacao(), vz);
    }
    acumulador.extraiEstatistica();
  }

  private Estacao buscaEstacao(int codEstacao)
  {
    for (Estacao estacao : listaPCs)
    {
      if (estacao.getCodigo() == codEstacao)
        return estacao;
    }
    return null;
  }

  private void verificaTipoEvento(Evento evento)
  {
    switch (evento.getTipoEvento())
    {
      case ChegadaNaFila:
        System.out.print(Double.toString(evento.getTempoExecucao()) + ": Chegada na fila de " + evento.getCodigoEstacao());
        processaChegadaNaFila(evento);
        break;
      case TentativaEnvio:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Tentativa de envio de " + evento.getCodigoEstacao());
        processaTentativaEnvio(evento);
        break;
      case FimTransmissao:
        System.out.print(Double.toString(evento.getTempoExecucao()) + ": Fim de transmissao de " + evento.getCodigoEstacao());
        processaFimTransmissao(evento);
        break;
      case FimEspera:
        if (!buscaEstacao(evento.getCodigoEstacao()).isFilaVazia()){
          System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de espera de " + evento.getCodigoEstacao());
        }
        processaFimEspera(evento);
        break;
      case MeioLivre:
        System.out.println(evento.getTempoExecucao() + ": Percepcao de meio livre em " + evento.getCodigoEstacao());
        processaMeioLivre(evento);
        break;
      case MeioOcupado:
        System.out.println(evento.getTempoExecucao() + ": Percepcao de meio ocupado em " + evento.getCodigoEstacao());
        processaMeioOcupado(evento);
        break;
      case FimReforcoColisao:
        System.out.println(evento.getTempoExecucao() + ": Fim de reforco de " + evento.getCodigoEstacao());
        processaFimReforcoColisao(evento);
        break;
      case FimBackOff:
        System.out.println(evento.getTempoExecucao() + ": Fim de backoff de " + evento.getCodigoEstacao());
        processaFimBackOff(evento);
        break;
    }
  }

  private void processaChegadaNaFila(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    // Programa próxima chegada
    double proximoTempo = evento.getTempoExecucao() + ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico());
    programaChegadaNaFila(proximoTempo, estacao.getCodigo());

    // Realiza a chegada na fila
    Mensagem mensagem = new Mensagem(ParametroP.geraParametro(estacao.getP()), codigoRodada);
    if (estacao.isFilaVazia() && !(estacao.isEsperandoBackoff() || estacao.isEsperandoTempoSeguranca()))
    {
      Evento novoEvento = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novoEvento);
    }
    estacao.addMensagem(mensagem);
    System.out.println(" - Existe(m) " + buscaEstacao(evento.getCodigoEstacao()).getQuadrosNaFila() +  " quadro(s) na fila de " + evento.getCodigoEstacao());
  }

  private void processaTentativaEnvio(Evento evento) 
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    if (estacao.isFilaVazia()) 
    {
      estacao.setForcarEnvio(false); 
      return;
    }

    if (estacao.getTempoInicioTentativa() == 0.0) // Estatísticas
      estacao.setTempoInicioTentativa(evento.getTempoExecucao());

    Mensagem msg = estacao.getMensagem();
    
    // ESTAH ERRADO!!!!!!!!! Temos que preocessar o envio para CADA quadro da mensagem!!!!!!
    // Não quero consertar agora, primeiro pq to com sono e vou acabar fazendo merda, segundo por que preciso que o Armando veja e confirme
    // que isso de fato está errado, e também qual é o melhor jeito para resolver.
    // Para comprovar que está errado, rode o simulador com A = 100, Deterministico, e primeiro P = 1, depois P = 100 (em 100.8 mostra que
    // todos os quadros jah forma enviados, o que soh verdade se a mensagem soh tiver 1 quadro!)
    
    // RESOLVIDO: o erro foi inserido na ultima revisao! Ele estava na troca (getFirst -> removeFirst) da linha 159 da Classe Estacao. 
    // e na remocao da linha 184 da mesma Classe que eh o removeFirst que esta CONDICIONADO ao if
    Quadro qd = msg.getQuadro();

    if (msg.getTempoInicialAcesso() == 0.0)
    {
      msg.setTempoInicialAcesso(evento.getTempoExecucao());
    }
    if (qd.getTempoInicialAcesso() == 0.0)
    {
      qd.setTempoInicialAcesso(evento.getTempoExecucao());
    }

    if (estacao.getMeioOcupado() == 0)
    {
      if (!estacao.isEsperandoTempoSeguranca())
      {
        qd.setFinalAcesso(evento.getTempoExecucao()); // Estatisticas
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Inicio de envio de " + evento.getCodigoEstacao());

        enviaInformacoes(evento, estacao, TipoEvento.MeioOcupado);
        
        estacao.setForcarEnvio(false);
        estacao.setTransmitindo(true);
        double tempoDeFim = evento.getTempoExecucao();
        tempoDeFim += estacao.getTamanhoQuadro() * TEMPO_ENVIO_DE_UM_BYTE; 

        Evento fimTransmissao = new Evento(TipoEvento.FimTransmissao, tempoDeFim, estacao.getCodigo());
        listaEventos.add(fimTransmissao);
      }
      else
      {
        estacao.setForcarEnvio(true);
      }
    }
    else
    {
      System.out.println(Double.toString(evento.getTempoExecucao()) + ": Sentiu meio ocupado em " + evento.getCodigoEstacao());
      if (estacao.isForcarEnvio()) 
      {
        enviaInformacoes(evento, estacao, TipoEvento.MeioOcupado);

        estacao.setForcarEnvio(false);
        estacao.setTransmitindo(true);
        estacao.setMeioEmColisao(true);
        double tempoDeFim = evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO;

        Evento fimTransmicao = new Evento(TipoEvento.FimReforcoColisao, tempoDeFim, estacao.getCodigo());
        listaEventos.add(fimTransmicao);
      }
      else
      {
        return;
      }
    }
  }

  private void processaFimTransmissao(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    enviaInformacoes(evento, estacao, TipoEvento.MeioLivre);

    estacao.setEsperandoTempoSeguranca(true);
    estacao.setTransmitindo(false);
    estacao.setForcarEnvio(false);
    estacao.setMeioEmColisao(false);

    estacao.pacoteEnviado(evento.getTempoExecucao(), codigoRodada);

    Evento transmissaoCompleta = new Evento(TipoEvento.FimEspera, evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES, estacao.getCodigo());
    listaEventos.add(transmissaoCompleta);
    System.out.println(" - Existe(m) " + buscaEstacao(evento.getCodigoEstacao()).getQuadrosNaFila() +  " quadro(s) na fila de " + evento.getCodigoEstacao());
  }

  private void processaFimEspera(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoTempoSeguranca(false);

    if (!estacao.isFilaVazia())
    {
      Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novaTentativa);
      // processaTentativaEnvio(evento);
    }
  }

  private void processaMeioLivre(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setMeioOcupado(estacao.getMeioOcupado() - 1);

    if (estacao.isTransmitindo())
      return;

    if (estacao.getMeioOcupado() == 0)
    {
      if (estacao.isEsperandoTempoSeguranca() && !estacao.isFilaVazia())
      {
        estacao.setForcarEnvio(true);
      }
      else
      {
        if (!estacao.isEsperandoBackoff())
        {
          estacao.setEsperandoTempoSeguranca(true);
          Evento fimEspera = new Evento(TipoEvento.FimEspera, evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES, evento.getCodigoEstacao());
          listaEventos.add(fimEspera);
        }
      }
    }
  }

  private void processaMeioOcupado(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setMeioOcupado(estacao.getMeioOcupado() + 1);

    if (!estacao.isTransmitindo() || estacao.isMeioEmColisao())
      return;

    listaEventos.remove(new Evento(TipoEvento.FimTransmissao, 0, estacao.getCodigo()));

    estacao.setMeioEmColisao(true);

    Evento fimReforco = new Evento(TipoEvento.FimReforcoColisao, evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO, estacao.getCodigo());
    listaEventos.add(fimReforco);

  }

  private void processaFimReforcoColisao(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
    
    enviaInformacoes(evento, estacao, TipoEvento.MeioLivre);

    estacao.setEsperandoTempoSeguranca(false);
    estacao.setTransmitindo(false);
    estacao.setForcarEnvio(false);
    estacao.setMeioEmColisao(false);

    estacao.incrementaQuantidadeColisoes(); 
    
    double tempoEspera = BinaryBackoff.geraAtraso(estacao.getQuantidadeColisoes());

    if (tempoEspera < 0)
      estacao.descartaPacote(evento.getTempoExecucao(), codigoRodada);
    else
    {
      estacao.setEsperandoBackoff(true);
      tempoEspera += evento.getTempoExecucao();
      Evento esperaBackOff = new Evento(TipoEvento.FimBackOff, tempoEspera, estacao.getCodigo());
      listaEventos.add(esperaBackOff);
    }
  }

  private void processaFimBackOff(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoBackoff(false);

    Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
    listaEventos.add(novaTentativa);
  }
  
  private void enviaInformacoes(Evento evento, Estacao estacao, TipoEvento tipoEvento) {
    for (Estacao e : listaPCs)
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
   
}
