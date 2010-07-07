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

  public void iniciaSimulacao(double tempoSimulacao, ArrayList<ConfiguracaoPc> parametros)
  {
    Estacao est;
    for (ConfiguracaoPc config : parametros)
    {
      est = new Estacao(config.getCodigo(), config.getDistancia(), config.getP(), config.getA(), config.isDeterministico());
      listaPCs.add(est);
    }

    listaEventos = new PriorityQueue<Evento>();

    for (Estacao estacao : listaPCs)
    {
      if (estacao.getP() != 0)
      {
        programaChegadaNaFila(ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico()), estacao.getCodigo()); 
      }
    }
    simula(tempoSimulacao);
  }

  private void programaChegadaNaFila(double tempoExecucao, int codigoEstacao)
  {
    Evento evento = new Evento(TipoEvento.ChegadaNaFila, tempoExecucao, codigoEstacao);
    listaEventos.add(evento);
  }

  private void simula(double tempoSimulacao)
  {
    Evento evento;
    do
    {
      evento = listaEventos.remove();
      verificaTipoEvento(evento);
    }
    while (evento.getTempoExecucao() < tempoSimulacao); 
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
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Chegada na Fila de " + evento.getCodigoEstacao());
        processaChegadaNaFila(evento);
        break;
      case TentativaEnvio:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Tentativa de Envio de " + evento.getCodigoEstacao());
        processaTentativaEnvio(evento);
        break;
      case FimTransmissao:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de transmissao de " + evento.getCodigoEstacao());
        processaFimTransmissao(evento);
        break;
      case FimEspera:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de espera de " + evento.getCodigoEstacao());
        processaFimEspera(evento);
        break;
      case MeioLivre:
        System.out.println(evento.getTempoExecucao() + ": Meio Livre em " + evento.getCodigoEstacao());
        processaMeioLivre(evento);
        break;
      case MeioOcupado:
        System.out.println(evento.getTempoExecucao() + ": Meio ocupado em " + evento.getCodigoEstacao());
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
    Mensagem mensagem = new Mensagem(ParametroP.geraParametro(estacao.getP()));
    if (estacao.isFilaVazia())
    {
      Evento novoEvento = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novoEvento);
    }
    estacao.addMensagem(mensagem);

  }

  private void processaTentativaEnvio(Evento evento) 
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    if (estacao.isFilaVazia()) 
    {
      estacao.setForcarEnvio(false); 
      System.out.println("ACONTECE SIM ARMANDO!!!!! =P Fila vazia na estacao " +  estacao.getCodigo());
      return;
    }

    if (estacao.getTempoInicioTentativa() == 0.0) // Estatísticas
      estacao.setTempoInicioTentativa(evento.getTempoExecucao());

    Mensagem msg = estacao.getMensagem();
    Quadro qd = msg.getQuadro();

    if (msg.getTempoPrimeiroAcesso() == 0.0)
    {
      msg.setTempoPrimeiroAcesso(evento.getTempoExecucao());
    }
    if (qd.getTempoEntradaServidor() == 0.0)
    {
      qd.setTempoEntradaServidor(evento.getTempoExecucao());
    }

    if (estacao.getMeioOcupado() == 0)
    {
      if (!estacao.isEsperandoTempoSeguranca())
      {
        if (qd.getTempoInicioEnvio() == 0.0) // Estatísticas
        {
          qd.setTempoInicioServico(evento.getTempoExecucao());
        }
        estacao.setForcarEnvio(false);
        for (Estacao e : listaPCs)
        {
          if (e.getCodigo() != estacao.getCodigo())
          {
            double distanciaTotal = e.getDistancia() + estacao.getDistancia();
            double tempoInicioPercepecao = evento.getTempoExecucao();

            tempoInicioPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;

            Evento inicioPercepcao = new Evento(TipoEvento.MeioOcupado, tempoInicioPercepecao, e.getCodigo());
            listaEventos.add(inicioPercepcao);
          }
        }
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
      if (estacao.isForcarEnvio()) 
      {
        if (qd.getTempoInicioEnvio() == 0.0)
          qd.setTempoInicioServico(evento.getTempoExecucao());

        estacao.setForcarEnvio(false);
        for (Estacao e : listaPCs)
        {
          if (e.getCodigo() != estacao.getCodigo()) //TODO Vamos colocar isso num método separado. Código repetido (varias e varias vezes =S)!
          {
            double distanciaTotal = e.getDistancia() + estacao.getDistancia();
            double tempoInicioPercepecao = evento.getTempoExecucao();

            tempoInicioPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;

            Evento inicioPercepcao = new Evento(TipoEvento.MeioOcupado, tempoInicioPercepecao, e.getCodigo());
            listaEventos.add(inicioPercepcao);
          }
        }

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

    for (Estacao e : listaPCs)
    {
      if (e.getCodigo() != estacao.getCodigo())
      {
        double distanciaTotal = e.getDistancia() + estacao.getDistancia();
        double tempoFimPercepecao = evento.getTempoExecucao();

        tempoFimPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;

        Evento inicioPercepcao = new Evento(TipoEvento.MeioLivre, tempoFimPercepecao, e.getCodigo());
        listaEventos.add(inicioPercepcao);
      }
    }

    estacao.setEsperandoTempoSeguranca(true);
    estacao.setTransmitindo(false);
    estacao.setForcarEnvio(false);
    estacao.setMeioEmColisao(false);

    estacao.pacoteEnviado(evento.getTempoExecucao());

    Evento transmissaoCompleta = new Evento(TipoEvento.FimEspera, evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES, estacao.getCodigo());
    listaEventos.add(transmissaoCompleta);

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

    for (Estacao e : listaPCs)
    {
      if (e.getCodigo() != estacao.getCodigo())
      {
        double distanciaTotal = e.getDistancia() + estacao.getDistancia();
        double tempoPercepecao = evento.getTempoExecucao();

        tempoPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;

        Evento eventoPercepcao = new Evento(TipoEvento.MeioLivre, tempoPercepecao, e.getCodigo());
        listaEventos.add(eventoPercepcao);
      }
    }

    estacao.setEsperandoTempoSeguranca(false);
    estacao.setTransmitindo(false);
    estacao.setForcarEnvio(false);
    estacao.setMeioEmColisao(false);

    estacao.incrementaQuantidadeColisoes(); 
    
    double tempoEspera = BinaryBackoff.geraAtraso(estacao.getQuantidadeColisoes());

    if (tempoEspera < 0)
      estacao.descartaPacote(evento.getTempoExecucao());
    else
    {
      tempoEspera += evento.getTempoExecucao();

      Evento esperaBackOff = new Evento(TipoEvento.FimBackOff, tempoEspera, estacao.getCodigo());
      listaEventos.add(esperaBackOff);

      estacao.setEsperandoBackoff(true);
    }

  }

  private void processaFimBackOff(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoBackoff(false);

    Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
    listaEventos.add(novaTentativa);
  }
   
}
