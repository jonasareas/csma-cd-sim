package br.ufrj.ad.controller;

import java.util.ArrayList;
import br.ufrj.ad.model.ConfiguracaoPc;
import br.ufrj.ad.util.MyRandom;

public class Start
{
  public static void main(String[] args)
  {
	 MyRandom.setSeed(0);
	  
    Simulador simulador = new Simulador();
    
    ArrayList<ConfiguracaoPc> parametros = new ArrayList<ConfiguracaoPc>();
    ConfiguracaoPc config1 = new ConfiguracaoPc(1, 100, 40, 80, true);
	ConfiguracaoPc config2 = new ConfiguracaoPc(2, 80, 40, 80, true);
	ConfiguracaoPc config3 = new ConfiguracaoPc(3, 60, 0, 0, true);
	ConfiguracaoPc config4 = new ConfiguracaoPc(4, 40, 0, 0, true);
	
	parametros.add(config1);
	parametros.add(config2);
	parametros.add(config3);
	parametros.add(config4);
	
	simulador.iniciaSimulacao(60000000, parametros); // 60 segundo em microsegundos. Este parâmetro também virá da tela!
  }

}
