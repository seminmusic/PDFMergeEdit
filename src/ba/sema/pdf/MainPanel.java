package ba.sema.pdf;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.awt.*;


@SuppressWarnings("serial")
public class MainPanel extends JPanel 
{	
	JButton btnIzaberiFolder;
	JButton btnMerge;
	JButton btnNumerisi;
	JFileChooser chooser;
	JLabel lblIzabraniFolder;
	JLabel lblMergeStatus;
	JLabel lblNumerisanjeStatus;
	JFormattedTextField txtPozicijaX;
	JFormattedTextField txtPozicijaY;
	JLabel lblX;
	JLabel lblY;
	
	String putanja = "";
	File izlazniFolder;
	String putanjaIzlaznogFajla = "";
	
	DecimalFormat format;
	
	public Dimension velicinaPanela() {
		return new Dimension(500, 350);
	}
	
	public void postaviKomponente() {
		
		// Biranje foldera:
		btnIzaberiFolder = new JButton(" Izaberi folder sa PDF fajlovima ...", UIManager.getIcon("FileView.directoryIcon"));
		btnIzaberiFolder.setBounds(100, 30, 300, 40);
		btnIzaberiFolder.setFocusPainted(false);
		btnIzaberiFolder.addActionListener(izaberiFolderListener());
		//
		lblIzabraniFolder = new JLabel("Izabrani folder: ", JLabel.CENTER);
		lblIzabraniFolder.setBounds(0, 90, 500, 15);
		lblIzabraniFolder.setVisible(false);
		
		// Merge:
		btnMerge = new JButton(" Spoji fajlove", UIManager.getIcon("FileView.fileIcon"));
		btnMerge.setBounds(40, 130, 150, 40);
		btnMerge.setFocusPainted(false);
		btnMerge.setVisible(false);
		btnMerge.addActionListener(mergeListener());
		//
		lblMergeStatus = new JLabel("", JLabel.LEFT);
		lblMergeStatus.setBounds(40, 180, 500, 15);
		lblMergeStatus.setVisible(false);
		
		// Numerisanje:
		btnNumerisi = new JButton(" Numeriši stranice", UIManager.getIcon("FileChooser.listViewIcon"));
		btnNumerisi.setBounds(40, 220, 190, 40);
		btnNumerisi.setFocusPainted(false);
		btnNumerisi.setVisible(false);
		btnNumerisi.addActionListener(numerisiListener());
		//
		lblNumerisanjeStatus = new JLabel("", JLabel.LEFT);
		lblNumerisanjeStatus.setBounds(40, 270, 500, 15);
		lblNumerisanjeStatus.setVisible(false);
		
		// Format:
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMAN); 
		format = new DecimalFormat();
		format.setDecimalFormatSymbols(symbols);
		format.setGroupingUsed(false);
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Float.class);
	    formatter.setMinimum(new Float(0.01));
	    formatter.setMaximum(Float.MAX_VALUE);
	    //formatter.setAllowsInvalid(false);
	    
	    // Pozicija:
	    lblX = new JLabel("X:", JLabel.LEFT);
	    lblX.setBounds(260, 233, 20, 15);
	    lblX.setVisible(false);
	    txtPozicijaX = new JFormattedTextField(formatter);
	    txtPozicijaX.setBounds(280, 226, 75, 30);
	    txtPozicijaX.setBorder(BorderFactory.createCompoundBorder(txtPozicijaX.getBorder(), BorderFactory.createEmptyBorder(0, 4, 0, 4)));
	    txtPozicijaX.setValue(new Float(21.00));
	    txtPozicijaX.setVisible(false);
	    lblY = new JLabel("Y:", JLabel.LEFT);
	    lblY.setBounds(370, 233, 20, 15);
	    lblY.setVisible(false);
	    txtPozicijaY = new JFormattedTextField(formatter);
	    txtPozicijaY.setBounds(390, 226, 75, 30);
	    txtPozicijaY.setBorder(BorderFactory.createCompoundBorder(txtPozicijaY.getBorder(), BorderFactory.createEmptyBorder(0, 4, 0, 4)));
	    txtPozicijaY.setValue(new Float(701.50));
	    txtPozicijaY.setVisible(false);
		
		// Dodavanje na panel:
		add(btnIzaberiFolder);
		add(lblIzabraniFolder);
		add(btnMerge);
		add(lblMergeStatus);
		add(btnNumerisi);
		add(lblNumerisanjeStatus);
		add(lblX);
		add(txtPozicijaX);
		add(lblY);
		add(txtPozicijaY);
	}
	
	private ActionListener izaberiFolderListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Izaberi folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);  // Disable the "All files" option
				//
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					//System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
					//System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
					putanja = chooser.getSelectedFile().toString();
					lblIzabraniFolder.setText("<html>Izabrani folder: <font color='red'>" + chooser.getSelectedFile().getName().toString() + "</font></html>");
					lblIzabraniFolder.setVisible(true);
					btnMerge.setVisible(true);
				} else {
					//System.out.println("No Selection");
				}
			}
		};
	}
	
	private ActionListener mergeListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lblMergeStatus.setVisible(true);
				merge();
			}
		};
	}
	
	private void merge() {
		btnMerge.setEnabled(false);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				
				List<String> naziviSortDesc = Helper.naziviFajlovaSortiraniPoVeliciniDesc(putanja);
				String izlaznaPutanja = putanja + "/izlaz";
				izlazniFolder = new File(izlaznaPutanja);
		        if (!izlazniFolder.exists()) {
		            try{
		                izlazniFolder.mkdir();
		            } 
		            catch(SecurityException se){
		            }
		        }
		        
		        putanjaIzlaznogFajla = izlazniFolder.getPath() + "/" + "Merged_PDF.pdf";
		        try {
		        	Document document = new Document();
		        	PdfCopy copy = new PdfCopy(document, new FileOutputStream(putanjaIzlaznogFajla));
		        	//
		        	document.open();
		        	PdfReader pdfReader;
		        	int ukupnoFajlova = naziviSortDesc.size();
		        	int number_of_pages;
		        	for (int i = 0; i < ukupnoFajlova; i++) {
		        		pdfReader = new PdfReader(putanja + "/" + naziviSortDesc.get(i));
		                number_of_pages = pdfReader.getNumberOfPages();
		                for (int page = 0; page < number_of_pages; ) {
		                    copy.addPage(copy.getImportedPage(pdfReader, ++page));
		                }
		                final int br = i;
		                // Runs inside of the Swing UI thread
		                SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								lblMergeStatus.setText("<html>Spajanje fajlova u toku ... (<font color='red'>" + (br + 1) + "</font>" + " od " + "<font color='red'>" + ukupnoFajlova + "</font>)</html>");
								if (br + 1 == ukupnoFajlova) {
									//btnMerge.setEnabled(true);
									lblMergeStatus.setText("<html>Završeno spajanje ukupno <font color='red'>" + ukupnoFajlova + "</font> fajlova</html>");
									btnNumerisi.setVisible(true);
									lblX.setVisible(true);
									txtPozicijaX.setVisible(true);
									lblY.setVisible(true);
									txtPozicijaY.setVisible(true);
								}
							}
						});
		                //Thread.sleep(2000);
		        	}
		        	document.close();
		        }
		        catch (Exception e) {
		        	btnMerge.setEnabled(true);
		        	e.printStackTrace();
		        }
		        
			}
		});
		thread.start();
	}
	
	private ActionListener numerisiListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lblNumerisanjeStatus.setVisible(true);
				
//				System.out.println("X value: " + txtPozicijaX.getValue());
//				System.out.println("X tekst: " + txtPozicijaX.getText());
//				System.out.println("Y value: " + txtPozicijaY.getValue());
//				System.out.println("Y tekst: " + txtPozicijaY.getText());
				
				try {
					float x = format.parse(txtPozicijaX.getText()).floatValue();
					float y = format.parse(txtPozicijaY.getText()).floatValue();
					
//					System.out.println("Parsiran X: " + x);
//					System.out.println("Parsiran Y: " + y);
					
					numeracija(x, y);
					
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		};
	}
	
	private void numeracija(float pozicijaX, float pozicijaY) {
		btnNumerisi.setEnabled(false);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				
		        try {
				    PdfReader pdfReader = new PdfReader(putanjaIzlaznogFajla);
				    PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(izlazniFolder.getPath() + "/" + "Merged_PDF_Numbered.pdf"));
				    BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				    int pages = pdfReader.getNumberOfPages(); 
				    for(int j = 1; j <= pages; j++) {
				    	final Integer br = j;
						PdfContentByte pageContentByte = pdfStamper.getOverContent(j);
						pageContentByte.beginText();
						pageContentByte.setFontAndSize(baseFont, 7);
						pageContentByte.setColorFill(BaseColor.RED);
						pageContentByte.setTextMatrix(pozicijaX, pozicijaY);
						pageContentByte.showText(br.toString());
						pageContentByte.endText();
						// Runs inside of the Swing UI thread
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								lblNumerisanjeStatus.setText("<html>Numerisanje stranica u toku ... (<font color='red'>" + br + "</font>" + " od " + "<font color='red'>" + pages + "</font>)</html>");
								if (br == pages) {
									//btnNumerisi.setEnabled(true);
									lblNumerisanjeStatus.setText("<html>Završeno numerisanje ukupno <font color='red'>" + pages + "</font> stranica</html>");
								}
							}
						});
						//Thread.sleep(2000);
				    }
				    pdfStamper.close();
			    } catch (Exception e) {
			    	btnNumerisi.setEnabled(true);
			    	e.printStackTrace();
			    }
				
			}
		});
		thread.start();
	}

}
