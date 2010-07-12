package br.ufrj.ad.view;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class CriaGrafico {

	public static ChartPanel getGrafico(){
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart
		                     ("produção de leite", // titulo do grafico
		                      "meses",             // eixo X
		                      "litros",            // eixo Y
		                      createDataset(),     // dados para o gráfico
		                      true,true,false);	   // exibir: legendas, tooltips, url
		                      
		                      
		
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
	
		return chartPanel;
	}
	
	
	  private static XYDataset createDataset() {

	        TimeSeries s1 = new TimeSeries("Holandesas", Month.class);
	        s1.add(new Month(1, 2005), 501.8);
	        s1.add(new Month(2, 2005), 181.8);
	        s1.add(new Month(3, 2005), 167.3);
	        s1.add(new Month(4, 2005), 153.8);
	        s1.add(new Month(5, 2005), 167.6);
	        s1.add(new Month(6, 2005), 158.8);

//	        TimeSeries s2 = new TimeSeries("Frísias", Month.class);
//	        s2.add(new Month(1, 2005), 129.6);
//	        s2.add(new Month(2, 2005), 129.6);
//	        s2.add(new Month(3, 2005), 123.2);
//	        s2.add(new Month(4, 2005), 117.2);
//	        s2.add(new Month(5, 2005), 124.1);
//	        s2.add(new Month(6, 2005), 122.6);

	        TimeSeriesCollection dataset = new TimeSeriesCollection();
	        dataset.addSeries(s1);
//	        dataset.addSeries(s2);
	        dataset.setDomainIsPointsInTime(true);
	        return dataset;

	    }	
	  
	  private static CategoryDataset createDataset2() {
		  DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		  dataset.addValue(6, "Preto", "Corsa");
		  dataset.addValue(4, "Preto", "Fiesta");
		  dataset.addValue(3, "Preto", "Gol");
		  dataset.addValue(5, "Vermelho", "Corsa");
		  dataset.addValue(2, "Vermelho", "Fiesta");
		  dataset.addValue(3, "Vermelho", "Gol");
		  dataset.addValue(2, "Azul", "Corsa");
		  dataset.addValue(8, "Azul", "Fiesta");
		  dataset.addValue(1, "Azul", "Gol");
		  dataset.addValue(3, "Verde", "Teste");
		  return dataset;
		  }

	  private static JFreeChart createBarChart(CategoryDataset dataset) {
		  JFreeChart chart = ChartFactory.createBarChart(
		  "Escolha de cor por veículo", //Titulo
		  "Veículo", // Eixo X
		  "Quantidade", //Eixo Y
		  dataset, // Dados para o grafico
		  PlotOrientation.VERTICAL, //Orientacao do grafico
		  //PlotOrientation.HORIZONTAL,
		  true, false, false); // exibir: legendas, tooltips, url
		  return chart;
		  }
	  
	  public static ChartPanel getGrafico2(){
		  ChartPanel chartPanel = new ChartPanel(createBarChart(createDataset2()), false);
	        chartPanel.setPreferredSize(new java.awt.Dimension(50, 50));
	        chartPanel.setMouseZoomable(false, false);
		
			return chartPanel;
	  }
	  
	  public static ChartPanel getPanel(){
		  ChartPanel chartPanel = new ChartPanel(createBarChart(createDataset2()), false);
	        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	        chartPanel.setMouseZoomable(true, false);
		
			return chartPanel;
	  }
	  

	  
}
	
