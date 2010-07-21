package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrj.ad.model.Estacao;

public class AcumuladorEstatistico
{
  private static AcumuladorEstatistico instancia = null; 
 
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tapi;  
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tami;  
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> ncmi;  
  
  private ArrayList<String> mensagens = new ArrayList<String>();
 
  public static AcumuladorEstatistico getInstancia()
  {
    if(instancia == null)
      instancia = new AcumuladorEstatistico();
    return instancia;
  }
   
  public AcumuladorEstatistico()
  {
    tapi = new HashMap<Integer, EstruturaEstatistica>();
    tami = new HashMap<Integer, EstruturaEstatistica>();
    ncmi = new HashMap<Integer, EstruturaEstatistica>();
  }
  
  public void addEstacao(int codigoEstacao)
  {
    tapi.put(codigoEstacao, new EstruturaEstatistica());
    tami.put(codigoEstacao, new EstruturaEstatistica());
    ncmi.put(codigoEstacao, new EstruturaEstatistica());
  }
  
  public void fimRodada()
  {
     for (int codigoEstacao : tapi.keySet())
     {
       tapi.get(codigoEstacao).fimRodada();
       tami.get(codigoEstacao).fimRodada();
       ncmi.get(codigoEstacao).fimRodada();
     }
  }
  
  private double variancia(ArrayList<Double> list)
  {
    if(list.size() <= 1)
      return -1;
    
    double med = media(list);
    double sum = 0.0;
    for(Double d : list)
    {
      sum += (d - med)*(d - med); 
    }
    
    return sum/(list.size()-1);
  }
  
  private double media(ArrayList<Double> list)
  {
    if(list.size() <= 0 )
      return -1;
    
    double sum = 0.0;
    for(Double d: list)
    {
      sum += d;
    }
    return sum/list.size();
  }
  
  public void extraiEstatistica(List<Estacao> listaEstacoes, double tempoSimulacao)
  {
    Double utilizacao = 0.0;
    
    for (Estacao estacao : listaEstacoes)
    {
      mensagens.add("\nESTATISTICAS DA ESTACAO " + estacao.getCodigo() + "\n");
      mensagens.add("TAp da estacao " + estacao.getCodigo() + " = " + media(tapi.get(estacao.getCodigo()).getValorPorRodada()) + "\n");
      mensagens.add("TAm da estacao " + estacao.getCodigo() + " = " + media(tami.get(estacao.getCodigo()).getValorPorRodada()) + "\n");
      mensagens.add("NCm da estacao " + estacao.getCodigo() + " = " + media(ncmi.get(estacao.getCodigo()).getValorPorRodada()) + "\n");
      mensagens.add("Vazao da estacao " + estacao.getCodigo() + " = " + estacao.getQuadrosTransmitidos()/tempoSimulacao + "\n");
      if (estacao.isAnaliseUtilizacaoEthernet())
        utilizacao = estacao.getTempoUtilizacaoEthernet()/tempoSimulacao;
    }
    mensagens.add("\nESTATISTICAS DA ETHERNET\n");
    mensagens.add("Utilizacao = " + utilizacao + " ( " + Math.floor((utilizacao * 100) * 10000)/10000  + " % do tempo total de simulacao)\n");
  }
  
  public void novaAmostraTap(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = tapi.get(codigoEstacao);
    estrutura.novaAmostra(amostra);
  }
  
  public void novaAmostraTam(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = tami.get(codigoEstacao);
    estrutura.novaAmostra(amostra);    
  }
  
  public void novaAmostraNcm(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = ncmi.get(codigoEstacao);
    estrutura.novaAmostra(amostra);    
  }  
  
  public ArrayList<String> getMensagens()
  {
    return mensagens;
  }

  public void setMensagens(ArrayList<String> mensagens)
  {
    this.mensagens = mensagens;
  }

  private class EstruturaEstatistica
  {
    ArrayList<Double> valorPorRodada;
    Double acumuladorRodada;
    int numeroAmostrasRodada;
    
    public EstruturaEstatistica() {
      valorPorRodada = new ArrayList<Double>();
      acumuladorRodada = 0.0;
      numeroAmostrasRodada = 0;
    }
    
    public void novaAmostra(Double amostra)
    {
      numeroAmostrasRodada++;
      acumuladorRodada += amostra;
    }
    
    public void fimRodada()
    {
      if (numeroAmostrasRodada > 0)
      {
        valorPorRodada.add(acumuladorRodada/numeroAmostrasRodada);
        acumuladorRodada = 0.0;
        numeroAmostrasRodada = 0;
      }
    }

    public ArrayList<Double> getValorPorRodada()
    {
      return valorPorRodada;
    }

  }
}
