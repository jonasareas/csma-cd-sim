package br.ufrj.ad.controller;

import java.util.ArrayList;

import br.ufrj.ad.view.Tela;

public class AcumuladorEstatistico
{
  // TODO: Que valores são esses?????
  private static final double VAR_MAX_TAP = 1000.000000001;
  private static final double VAR_MAX_TAM = 1000.000001;
  private static final double VAR_MAX_NCM = 1000.00000001;
  
  private static AcumuladorEstatistico instancia = null; 

  private ArrayList<Double> tapi;
  private ArrayList<Double> tap;
    
  private ArrayList<Double> tami;
  private ArrayList<Double> tam;
  
  private ArrayList<Double> ncmi;
  private ArrayList<Double> ncm;
  
  private ArrayList<Double> utilizacaoEthernet;
  
  private ArrayList< ArrayList<Double> > vazao; 
  
  private boolean transiente;
  
  public static AcumuladorEstatistico getInstancia()
  {
    if(instancia == null)
      instancia = new AcumuladorEstatistico();
    return instancia;
  }
   
  private AcumuladorEstatistico() {}
  
  private AcumuladorEstatistico(int numeroEstacoes)
  {
    clear(numeroEstacoes);
  }
  
  private void reinicia()
  {
    tap.clear();
    tam.clear();
    ncm.clear();
    
    utilizacaoEthernet.clear();
    
    for(ArrayList<Double> a : vazao)
    {
      a.clear();
    }
  }
  
  public void fimRodada(double utilizacao, ArrayList<Double> vazao)
  {
    if(transiente)
    {
      if(!verificaTransiencia())
      {
        transiente = false;
        Tela.jTextLog.append("Fim da Fase Transiente\n");
      }
      reinicia();
      return;
    }
    
    if(tap.size()>0)
    {
      tapi.add(media(tap));
      tap.clear();
    }
    
    if(tam.size()>0)
    {
      tami.add(media(tam));
      tam.clear();
    }
    
    if(ncm.size()>0)
    {
      ncmi.add(media(ncm));
      ncm.clear();
    }
         
    utilizacaoEthernet.add(utilizacao);
    
    for(int i = 0; i < vazao.size(); i++)
    {
      this.vazao.get(i).add(vazao.get(i));
    }
  }
  
  public void addTap(double val)
  {
    tap.add(val);
  }
  
  public void addTam(double val)
  {
    tam.add(val);
  }
  
  public void addNcm(double val)
  {
    ncm.add(val);
  }
  
  private boolean verificaTransiencia()
  {
    double varTap = variancia(tap);
    double varTam = variancia(tam);
    double varNcm = variancia(ncm);
    
    /*
    System.out.println("Analize Transiencia:");
    System.out.println("    Var[Tap] = " + varTap + " - Valor Maximo = " + VAR_MAX_TAP);
    System.out.println("    Var[Tam] = " + varTam + " - Valor Maximo = " + VAR_MAX_TAM);
    System.out.println("    Var[Ncm] = " + varNcm + " - Valor Maximo = " + VAR_MAX_NCM);
 //   */
    
    if(varTap > VAR_MAX_TAP || varTam > VAR_MAX_TAM || varNcm > VAR_MAX_NCM)
      return true;
    
    return false;
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
  
  public boolean isTransiente()
  {
    return transiente;
  }
  //TODO: Não deveria ser para cada estação?
  public void extraiEstatistica()
  {
    Tela.jTextLog.append("Dados de Tapi:\n");
    Tela.jTextLog.append("   N        = " + tapi.size() + "\n");
    Tela.jTextLog.append("   E[Tap]   = " + media(tapi) + "\n");
    Tela.jTextLog.append("   VAR[Tap] = " + variancia(tapi) + "\n\n");
    
    Tela.jTextLog.append("Dados de Tami:\n");
    Tela.jTextLog.append("   N        = " + tami.size() + "\n");
    Tela.jTextLog.append("   E[Tam]   = " + media(tami) + "\n");
    Tela.jTextLog.append("   VAR[Tam] = " + variancia(tami) + "\n\n");
    
    Tela.jTextLog.append("Dados de Ncmi:\n");
    Tela.jTextLog.append("   N        = " + ncmi.size() + "\n");
    Tela.jTextLog.append("   E[Tam]   = " + media(ncmi) + "\n");
    Tela.jTextLog.append("   VAR[Tam] = " + variancia(ncmi) + "\n\n");
  }
  
  public void clear(int numeroEstacoes)
  {
    vazao = new ArrayList< ArrayList<Double> > ();
    for(int i = 0; i < numeroEstacoes; i++)
    {
       vazao.add(new ArrayList<Double>());
    }
    
    this.transiente = true;
    
    tapi = new ArrayList<Double>();
    tami = new ArrayList<Double>();
    ncmi = new ArrayList<Double>();
    
    tap = new ArrayList<Double>();
    tam = new ArrayList<Double>();
    ncm = new ArrayList<Double>();
    
    utilizacaoEthernet = new ArrayList<Double>(); 
  }
}
