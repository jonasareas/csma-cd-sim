package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.view.Tela;

public class AcumuladorEstatistico
{
  private static AcumuladorEstatistico instancia = null; 
 
  HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tapi;  
  HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tami;  
  HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> ncmi;  
 
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
    // Math.floor(tempo * 10000)/10000
    // Imprimir variancia?
    
    for (Estacao estacao : listaEstacoes)
    {
      Tela.jTextLog.append("TAp da estacao " + estacao.getCodigo() + " = " + media(tapi.get(estacao.getCodigo()).getValorPorRodada()) + "\n");
      Tela.jTextLog.append("TAm da estacao " + estacao.getCodigo() + " = " + media(tami.get(estacao.getCodigo()).getValorPorRodada()) + "\n");
      Tela.jTextLog.append("NCm da estacao " + estacao.getCodigo() + " = " + media(ncmi.get(estacao.getCodigo()).getValorPorRodada()) + "\n");
      Tela.jTextLog.append("Vazao da estacao " + estacao.getCodigo() + " = " + estacao.getQuadrosTransmitidos()/tempoSimulacao + "\n");
      if (estacao.isAnaliseUtilizacaoEthernet())
        Tela.jTextLog.append("Utilizacao = " + estacao.getTempoUtilizacaoEthernet()/tempoSimulacao + "\n\n");
    }
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
