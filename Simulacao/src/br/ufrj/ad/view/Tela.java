package br.ufrj.ad.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import br.ufrj.ad.controller.AcumuladorEstatistico;
import br.ufrj.ad.controller.Simulador;
import br.ufrj.ad.model.ConfiguracaoPc;

public class Tela extends JFrame implements WindowListener,ActionListener,ItemListener
{
  private static final int CODIGO_ESTACAO_ANALISE_UTILIZACAO = 1;
  private static final int NUMERO_RODADAS = 30;
  private static final double TEMPO_RODADA_EM_SEGUNDOS = 1;
  
  private JPanel jContentPane;
  private JLabel jLabelTitulo;
  private JPanel jPanelTitulo;
  
  private JLabel jLabelTempoSimulacao;
  private JTextField jTextTempoSimulacao;
  
  private JPanel jPanelCenarios;
  private JPanel jPanelCenariosFixos;
  private JLabel jLabelCenariosFixos;
  private JButton jButtonCenario1;
  private JButton jButtonCenario2;
  private JButton jButtonCenario3;
  private JButton jButtonCenario4;  

  private JPanel jPanelCenarioGenerico;  
  private JLabel jLabelCenarioGenerico;  
  private JLabel jLabelParametroP;
  private JLabel jLabelParametroA;  
  private JLabel jLabelParametroDeterministico;
  private JLabel jLabelPC1;
  private JLabel jLabelPC2;
  private JLabel jLabelPC3;
  private JLabel jLabelPC4;
  private JTextField jText1P;
  private JTextField jText2P;
  private JTextField jText3P;
  private JTextField jText4P;
  private JTextField jText1A;
  private JTextField jText2A;
  private JTextField jText3A;
  private JTextField jText4A;
  private JCheckBox jCheck1;
  private JCheckBox jCheck2;
  private JCheckBox jCheck3;
  private JCheckBox jCheck4;
  private JButton jButtonCenarioGenerico;  
  
  private JPanel jPanelGrafico;
  private JLabel jLabelLog;
  private JScrollPane scrollTextArea;
  
  public JTextArea jTextAreaEstatisticas;  
  
  private boolean check1 = false;
  private boolean check2 = false;
  private boolean check3 = false;
  private boolean check4 = false;
  
  public Tela()
  {
    super();
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    initialize();
  }

  private void initialize()
  {
    jContentPane = new JPanel();
    FlowLayout jContentPaneLayout = new FlowLayout();
    getContentPane().add(jContentPane, BorderLayout.CENTER);
    setResizable(false);
    setTitle("Trabalho de Avaliacao e Desempenho - Simulador CSMA/CD");
    
    jContentPane.setLayout(jContentPaneLayout);

    jPanelTitulo = new JPanel();
    jPanelTitulo.setPreferredSize(new java.awt.Dimension(778, 46));
    jPanelTitulo.setBorder(BorderFactory.createTitledBorder(""));
    jPanelTitulo.setBackground(new java.awt.Color(207,213,215));
    jContentPane.add(jPanelTitulo);
    
    jLabelTitulo = new JLabel();
    jLabelTitulo.setText("Trabalho de Avaliação e Desempenho - Simulador CSMA/CD");
    jLabelTitulo.setFont(new java.awt.Font("Dialog",3,24));
    jPanelTitulo.add(jLabelTitulo);

    jPanelCenarios = new JPanel();
    jPanelCenarios.setPreferredSize(new java.awt.Dimension(790, 515));
    jPanelCenarios.setLayout(null);    
    jContentPane.add(jPanelCenarios);

    jLabelTempoSimulacao = new JLabel();
    jLabelTempoSimulacao.setText("Selecione um dos cenarios especificos ou simule um cenario generico");
    jLabelTempoSimulacao.setFont(new java.awt.Font("Dialog",1,15));
    jLabelTempoSimulacao.setBounds(130, 12, 550, 30);
    jTextTempoSimulacao = new JTextField();
    jTextTempoSimulacao.setText("10");
    jTextTempoSimulacao.setBounds(500, 15, 200, 25);

    jPanelCenarios.add(jLabelTempoSimulacao);
    // jPanelCenarios.add(jTextTempoSimulacao);
    
    jPanelCenarios.add(getJPanelCenariosFixos());
    jPanelCenarios.add(getJPanelCenarioGenerico());  
    jPanelCenarios.add(getJPanelGrafico());
    
    this.setSize(800, 600);
    this.setName("Simulacao");
  }
  
  private JPanel getJPanelGrafico() 
  {
	jPanelGrafico = new JPanel();
	jPanelGrafico.setLayout(null);
	jPanelGrafico.setBounds(50, 320, 700, 180);
	jPanelGrafico.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
	
	jLabelLog = new JLabel();
	jLabelLog.setText("Estatisticas da simulacao:");
	jLabelLog.setFont(new java.awt.Font("Dialog",1,18));
	jLabelLog.setBounds(250, 10, 250, 25);
	jPanelGrafico.add(jLabelLog);
	
	jTextAreaEstatisticas = new JTextArea();
	scrollTextArea = new JScrollPane(jTextAreaEstatisticas);
	scrollTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	scrollTextArea.setBounds(20,40,660,120);
	
	jPanelGrafico.add(scrollTextArea);
	
    return jPanelGrafico;
  }
  
  private JPanel getJPanelCenariosFixos() 
  {
    jPanelCenariosFixos = new JPanel();
    jPanelCenariosFixos.setLayout(null);
    jPanelCenariosFixos.setBounds(50, 50, 325, 250);
    jPanelCenariosFixos.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
    
    jLabelCenariosFixos = new JLabel();
    jLabelCenariosFixos.setText("Simular cenário pré-definido:");
    jLabelCenariosFixos.setFont(new java.awt.Font("Arial",1,14));
    jLabelCenariosFixos.setBounds(60, 10, 220, 30);
    jPanelCenariosFixos.add(jLabelCenariosFixos);    

    jButtonCenario1 = new JButton();
    jButtonCenario1.setBounds(100, 50, 125, 25);
    jButtonCenario1.setEnabled(true);
    jButtonCenario1.setText("Cenário 1");
    jButtonCenario1.addActionListener(this);
    jPanelCenariosFixos.add(jButtonCenario1);
    
    jButtonCenario2 = new JButton();
    jButtonCenario2.setBounds(100, 95, 125, 25);
    jButtonCenario2.setEnabled(true);
    jButtonCenario2.setText("Cenário 2");
    jButtonCenario2.addActionListener(this);
    jPanelCenariosFixos.add(jButtonCenario2);
          
    jButtonCenario3 = new JButton();
    jButtonCenario3.setBounds(100, 140, 125, 25);
    jButtonCenario3.setEnabled(true);
    jButtonCenario3.setText("Cenário 3");
    jButtonCenario3.addActionListener(this);    
    jPanelCenariosFixos.add(jButtonCenario3);
          
    jButtonCenario4 = new JButton();
    jButtonCenario4.setBounds(100, 185, 125, 25);
    jButtonCenario4.setEnabled(true);
    jButtonCenario4.setText("Cenário 4");
    jButtonCenario4.addActionListener(this);     
    jPanelCenariosFixos.add(jButtonCenario4);   
    
    return jPanelCenariosFixos;
  }
  
  private JPanel getJPanelCenarioGenerico()
  {
    jPanelCenarioGenerico = new JPanel();
    jPanelCenarioGenerico.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
    jPanelCenarioGenerico.setLayout(null);
    jPanelCenarioGenerico.setBounds(425, 50, 325, 250);    
    
    jLabelCenarioGenerico = new JLabel();
    jLabelCenarioGenerico.setText("Simular cenário genérico:");
    jLabelCenarioGenerico.setFont(new java.awt.Font("Arial",1,14));
    jLabelCenarioGenerico.setBounds(70, 10, 250, 30);
    jPanelCenarioGenerico.add(jLabelCenarioGenerico);      

    jLabelParametroP = new JLabel();
    jLabelParametroP.setText("P");
    jLabelParametroP.setFont(new java.awt.Font("Arial",1,11));
    jLabelParametroP.setBounds(100, 40, 60, 20);
    jPanelCenarioGenerico.add(jLabelParametroP);        
    
    jLabelParametroA = new JLabel();
    jLabelParametroA.setText("A (em ms)");
    jLabelParametroA.setFont(new java.awt.Font("Arial",1,11));
    jLabelParametroA.setBounds(132, 40, 60, 20);
    jPanelCenarioGenerico.add(jLabelParametroA);  
    
    jLabelParametroDeterministico = new JLabel();
    jLabelParametroDeterministico.setText("A Determinístico?");
    jLabelParametroDeterministico.setFont(new java.awt.Font("Arial",1,11));
    jLabelParametroDeterministico.setBounds(210, 40, 100, 20);
    jPanelCenarioGenerico.add(jLabelParametroDeterministico);    
    
    jLabelPC1 = new JLabel();
    jLabelPC1.setText("Estação 1");
    jLabelPC1.setFont(new java.awt.Font("Arial",1,11));
    jLabelPC1.setBounds(10, 70, 80, 20);
    jPanelCenarioGenerico.add(jLabelPC1);  
    
    jText1P = new JTextField();
    jText1P.setBounds(80, 70, 50, 20);   
    jPanelCenarioGenerico.add(jText1P);   
    
    jText1A = new JTextField();
    jText1A.setBounds(140, 70, 50, 20);
    jPanelCenarioGenerico.add(jText1A); 
    
    jCheck1 = new JCheckBox();
    jCheck1.setBounds(250, 70, 50, 20);
    jCheck1.addItemListener(this);
    jPanelCenarioGenerico.add(jCheck1);
    
    jLabelPC2 = new JLabel();
    jLabelPC2.setText("Estação 2");
    jLabelPC2.setFont(new java.awt.Font("Arial",1,11));
    jLabelPC2.setBounds(10, 100, 80, 20);
    jPanelCenarioGenerico.add(jLabelPC2);  
    
    jText2P = new JTextField();
    jText2P.setBounds(80, 100, 50, 20);   
    jPanelCenarioGenerico.add(jText2P);   
    
    jText2A = new JTextField();
    jText2A.setBounds(140, 100, 50, 20);
    jPanelCenarioGenerico.add(jText2A); 
    
    jCheck2 = new JCheckBox();
    jCheck2.setBounds(250, 100, 50, 20);
    jCheck2.addItemListener(this);
    jPanelCenarioGenerico.add(jCheck2);    
    
    jLabelPC3 = new JLabel();
    jLabelPC3.setText("Estação 3");
    jLabelPC3.setFont(new java.awt.Font("Arial",1,11));
    jLabelPC3.setBounds(10, 130, 80, 20);
    jPanelCenarioGenerico.add(jLabelPC3);  
    
    jText3P = new JTextField();
    jText3P.setBounds(80, 130, 50, 20);   
    jPanelCenarioGenerico.add(jText3P);   
    
    jText3A = new JTextField();
    jText3A.setBounds(140, 130, 50, 20);
    jPanelCenarioGenerico.add(jText3A); 
    
    jCheck3 = new JCheckBox();
    jCheck3.setBounds(250, 130, 50, 20);
    jCheck3.addItemListener(this);
    jPanelCenarioGenerico.add(jCheck3);    
    
    jLabelPC4 = new JLabel();
    jLabelPC4.setText("Estação 4");
    jLabelPC4.setFont(new java.awt.Font("Arial",1,11));
    jLabelPC4.setBounds(10, 160, 80, 20);
    jPanelCenarioGenerico.add(jLabelPC4);   

    jText4P = new JTextField();
    jText4P.setBounds(80, 160, 50, 20);   
    jPanelCenarioGenerico.add(jText4P);   
    
    jText4A = new JTextField();
    jText4A.setBounds(140, 160, 50, 20);
    jPanelCenarioGenerico.add(jText4A); 
    
    jCheck4 = new JCheckBox();
    jCheck4.setBounds(250, 160, 50, 20);
    jCheck4.addItemListener(this);
    jPanelCenarioGenerico.add(jCheck4);
    
    jButtonCenarioGenerico = new JButton();
    jButtonCenarioGenerico.setBounds(65, 200, 200, 25);
    jButtonCenarioGenerico.setEnabled(true);
    jButtonCenarioGenerico.setText("Simular Cenário");
    jButtonCenarioGenerico.addActionListener(this);     
    jPanelCenarioGenerico.add(jButtonCenarioGenerico);   
    
    return jPanelCenarioGenerico;
  }
  
  private void configuraSimulacao(ConfiguracaoPc config1, ConfiguracaoPc config2, ConfiguracaoPc config3, ConfiguracaoPc config4)
  {
    Simulador simulador = new Simulador();
    double tempoSimulacao = TEMPO_RODADA_EM_SEGUNDOS * 1000; // Passando para milisegundos.
    
    jTextAreaEstatisticas.append("[LOG] NUMERO DE RODADAS:" + NUMERO_RODADAS + "\n");    
    jTextAreaEstatisticas.append("[LOG] TEMPO DE DE CADA RODADA:" + TEMPO_RODADA_EM_SEGUNDOS + " segundos\n");
  
    ArrayList<ConfiguracaoPc> parametros = new ArrayList<ConfiguracaoPc>();
    parametros.add(config1);
    parametros.add(config2);
    parametros.add(config3);
    parametros.add(config4);

    if (validaParametros(config1) && validaParametros(config2) && validaParametros(config3) && validaParametros(config4) && existeEstacaoTransmitindo(parametros))
    {
      simulador.iniciaSimulacao(tempoSimulacao, NUMERO_RODADAS, parametros, CODIGO_ESTACAO_ANALISE_UTILIZACAO); 
      for (String mensagem : AcumuladorEstatistico.getInstancia().getMensagens())
      {
        jTextAreaEstatisticas.append(mensagem);
      }
    }
    else
    {
      jTextAreaEstatisticas.append("FALTAM PARMETROS PARA SIMULACAO!\n");
    }
  }
  
  private boolean validaParametros(ConfiguracaoPc config)
  {
    if ((config.getP() > 0 && config.getA() == 0) || (config.getP() == 0 && config.getA() > 0))
      return false;
    return true;
  }
  
  private boolean existeEstacaoTransmitindo(ArrayList<ConfiguracaoPc> listaConfigs)
  {
    for (ConfiguracaoPc config : listaConfigs)
    {
      if (config.getP() > 0)
        return true;
    }
    return false;
  }
  
  public void itemStateChanged(ItemEvent e) {
      Object source = e.getItemSelectable();
      
      if (source == jCheck1) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
          check1 = false;
        } else {
          check1 = true;
        }
      }
      if (source == jCheck2) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
          check2 = false;
        } else {
          check2 = true;
        }
      }
      if (source == jCheck3) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
          check3 = false;
        } else {
          check3 = true;
        }
      }      
      if (source == jCheck4) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
          check4 = false;
        } else {
          check4 = true;
        }
      }
  }  
  
  public void actionPerformed(ActionEvent e) {
    ConfiguracaoPc config1 = null;
    ConfiguracaoPc config2 = null;
    ConfiguracaoPc config3 = null;
    ConfiguracaoPc config4 = null;
    
    jTextAreaEstatisticas.setText("");
    
    if (e.getSource().equals(jButtonCenario1)) {
      config1 = new ConfiguracaoPc(1, 100, 40, 80, true);
      config2 = new ConfiguracaoPc(2, 80, 40, 80, true);
      config3 = new ConfiguracaoPc(3, 60, 0, 0, false);
      config4 = new ConfiguracaoPc(4, 40, 0, 0, false);  
    } else if (e.getSource().equals(jButtonCenario2)) {
      config1 = new ConfiguracaoPc(1, 100, 40, 80, false);
      config2 = new ConfiguracaoPc(2, 80, 40, 80, false);
      config3 = new ConfiguracaoPc(3, 60, 0, 0, false);
      config4 = new ConfiguracaoPc(4, 40, 0, 0, false);   
    } else if (e.getSource().equals(jButtonCenario3)) {
      config1 = new ConfiguracaoPc(1, 100, 40, 80, true);
      config2 = new ConfiguracaoPc(2, 80, 1, 16, true);
      config3 = new ConfiguracaoPc(3, 60, 1, 16, true);
      config4 = new ConfiguracaoPc(4, 40, 1, 16, true);
    } else if (e.getSource().equals(jButtonCenario4)) {
      config1 = new ConfiguracaoPc(1, 100, 40, 80, true);
      config2 = new ConfiguracaoPc(2, 80, 1, 16, false);
      config3 = new ConfiguracaoPc(3, 60, 1, 16, false);
      config4 = new ConfiguracaoPc(4, 40, 1, 16, false);
    } else if (e.getSource().equals(jButtonCenarioGenerico)) {
      config1 = new ConfiguracaoPc(1, 100, trataDouble(jText1P.getText()), trataDouble(jText1A.getText()), check1);
      config2 = new ConfiguracaoPc(2, 80, trataDouble(jText2P.getText()), trataDouble(jText2A.getText()), check2);
      config3 = new ConfiguracaoPc(3, 60, trataDouble(jText3P.getText()), trataDouble(jText3A.getText()), check3);
      config4 = new ConfiguracaoPc(4, 40, trataDouble(jText4P.getText()), trataDouble(jText4A.getText()), check4); 
      
      // jTextAreaEstatisticas.append("[LOG] Parametros da Estacao 1: " + trataDouble(jText1P.getText()) + " " +  trataDouble(jText1A.getText()) + " " +  check1 + "\n");
      // jTextAreaEstatisticas.append("[LOG] Parametros da Estacao 2: " + trataDouble(jText2P.getText()) + " " +  trataDouble(jText2A.getText()) + " " +  check2+ "\n");
      // jTextAreaEstatisticas.append("[LOG] Parametros da Estacao 3: " + trataDouble(jText3P.getText()) + " " +  trataDouble(jText3A.getText()) + " " +  check3+ "\n");
      // jTextAreaEstatisticas.append("[LOG] Parametros da Estacao 4: " + trataDouble(jText4P.getText()) + " " +  trataDouble(jText4A.getText()) + " " +  check4+ "\n");
      
    } 
    
  	configuraSimulacao(config1, config2, config3, config4);
    jTextAreaEstatisticas.select(0, 0);
  }
  
  private double trataDouble(String texto)
  {
    if (texto == null || texto.isEmpty())
      return 0;
    else 
    {
      double retorno;
      try {
        retorno = Double.parseDouble(texto);
      } catch (NumberFormatException e) {
        jTextAreaEstatisticas.append("ERRO NA LEITURA DOS PARAMETROS PARA SIMULACAO\n");
        return 0;
      }
      return retorno;
    }
  }  
  
  public void windowClosing(WindowEvent e) {
    dispose();
    System.exit(0);
  }
  
  public void windowOpened(WindowEvent e) {}
  public void windowActivated(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowDeactivated(WindowEvent e) {}
  public void windowClosed(WindowEvent e) {}      

}
