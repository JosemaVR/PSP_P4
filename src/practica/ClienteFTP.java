package practica;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTP extends JFrame 
{
	private static final long serialVersionUID = 1L;
	// Campos de la cabecera parte superior
	static JTextField txtServidor = new JTextField();
	static JTextField txtUsuario = new JTextField();
	static JTextField txtDirectorioRaiz = new JTextField();
	// Campos de mensajes parte inferior
	private static JTextField txtSeleccionado = new JTextField();
	// Textos indicativos
	private static JLabel lblServidor = new JLabel("Servidor FTP:");
	private static JLabel lblUsuario = new JLabel("Usuario:");
	private static JLabel lblDirectorioRaiz = new JLabel("Directorio Raiz:");
	private static JLabel lblSeleccionado = new JLabel("Selección:");
	// Botones
	JButton botonCargar = new JButton("Subir fichero");
	JButton botonDescargar = new JButton("Descargar fichero");
	JButton botonBorrar = new JButton("Eliminar fichero");
	JButton botonCreaDir = new JButton("Crear carpeta");
	JButton botonDelDir = new JButton("Eliminar carpeta");
	JButton botonSalir = new JButton("Salir");
	JButton botonRenoDir = new JButton("Renombrar carpeta");
	JButton botonReno = new JButton("Renombrar fichero");
	JButton botonVolver = new JButton("Volver atrás");
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "localhost";
	String user = "JosemaVR";
	String pasw = "Studium2020;";
	boolean login;
	static String direcInicial = "/";
	// para saber el directorio y fichero seleccionado
	static String direcSelec = direcInicial;
	static String ficheroSelec = "";

	public static void main(String[] args) throws IOException 
	{
		new ClienteFTP();
	} // final del main

	public ClienteFTP() throws IOException
	{
		super("CLIENTE FTP");
		setBounds(0, 0, 750, 550);
		//para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter (System.out)));
		cliente.connect(servidor); //conexi�n al servidor
		cliente.enterLocalPassiveMode();
		login = cliente.login(user, pasw);
		//Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(direcInicial);
		//Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		llenarLista(files, direcInicial);
		//Construyendo la lista de ficheros y directorios
		//del directorio de trabajo actual		
		//preparar campos de pantalla
		txtSeleccionado.setText("");
		txtServidor.setText(servidor);
		txtUsuario.setText(user);
		txtDirectorioRaiz.setText(direcInicial);
		//Preparaci�n de la lista
		//se configura el tipo de selecci�n para que solo se pueda
		//seleccionar un elemento de la lista

		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(325,500));
		barraDesplazamiento.setBounds(new Rectangle(5,5,325,500));
		add(barraDesplazamiento);

		txtServidor.setBounds(475, 5, 225, 30);
		add(txtServidor);
		txtServidor.setEditable(false);
		
		lblServidor.setBounds(350, 5, 150, 30);
		add(lblServidor);
		
		txtUsuario.setBounds(475, 55, 225, 30);
		add(txtUsuario);
		txtUsuario.setEditable(false);
		
		lblUsuario.setBounds(350, 55, 150, 30);
		add(lblUsuario);

		txtDirectorioRaiz.setBounds(475, 105, 225, 30);
		add(txtDirectorioRaiz);
		txtDirectorioRaiz.setEditable(false);
		
		lblDirectorioRaiz.setBounds(350, 105, 150, 30);
		add(lblDirectorioRaiz);

		txtSeleccionado.setBounds(475, 155, 225, 30);
		add(txtSeleccionado);
		txtSeleccionado.setEditable(false);

		lblSeleccionado.setBounds(350, 155, 150, 30);
		add(lblSeleccionado);

		botonCargar.setBounds(350, 255, 150, 30);
		add(botonCargar);

		botonCreaDir.setBounds(550, 255, 150, 30);
		add(botonCreaDir);

		botonDelDir.setBounds(350, 305, 150, 30);
		add(botonDelDir);

		botonDescargar.setBounds(550, 305, 150, 30);
		add(botonDescargar);

		botonBorrar.setBounds(350, 355, 150, 30);
		add(botonBorrar);

		botonSalir.setBounds(550, 355, 150, 30);
		add(botonSalir);
		
		botonRenoDir.setBounds(350, 405, 150, 30);
		add(botonRenoDir);

		botonReno.setBounds(550, 405, 150, 30);
		add(botonReno);
		
		botonVolver.setBounds(350,455,150,30);
		add(botonVolver);
		//se a�aden el resto de los campos de pantalla
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		//Acciones al pulsar en la lista o en los botones

		botonRenoDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ficheroSelec.contains("(DIR)")) {
					String directorio = txtSeleccionado.getText();
					String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el NUEVO nombre del directorio",
							txtSeleccionado.getText());
					if (!(nombreCarpeta == null)) {
						if (!direcSelec.equals("/"))
							directorio = direcSelec.trim() + txtSeleccionado.getText();
						try {
							if (cliente.isAvailable()) {
								cliente.rename(directorio, nombreCarpeta);
								String m = directorio.trim() + " => Se ha modificado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtSeleccionado.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2, direcSelec);
							} else {
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido renombrar ...");
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, ficheroSelec + "--> No es una CARPETA");
				}
			}
		});// Botón renombrar directorio
		botonReno.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!ficheroSelec.contains("(DIR)")) {
					String directorio = txtSeleccionado.getText();
					String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el NUEVO nombre del directorio",
							txtSeleccionado.getText());
					if (!(nombreCarpeta == null)) {
						if (!direcSelec.equals("/"))
							directorio = direcSelec.trim() + txtSeleccionado.getText();
						try {
							if (cliente.isAvailable()) {
								cliente.rename(directorio, nombreCarpeta);
								String m = directorio.trim() + " => Se ha modificado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtSeleccionado.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2, direcSelec);
							} else {
								JOptionPane.showMessageDialog(null,
										nombreCarpeta.trim() + " => No se ha podido renombrar ...");
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, ficheroSelec + "--> No es un ARCHIVO");
				}
			}
		});// Botón renombrar archivo
		
		botonVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listaDirec.removeAll();
				String quitar = "";
				for(int i = 0; i < txtDirectorioRaiz.getText().split("/").length; i++) {
					quitar = txtDirectorioRaiz.getText().split("/")[i];
				}
				String nuevoDir = txtDirectorioRaiz.getText().replace("/"+quitar, "");
				try {
					cliente.changeWorkingDirectory(nuevoDir);
					//Obteniendo ficheros y directorios del directorio actual
					FTPFile[] files = cliente.listFiles();
					llenarLista(files, nuevoDir);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(nuevoDir=="") {
					txtDirectorioRaiz.setText("/");
				} else {
					txtDirectorioRaiz.setText(nuevoDir);
				}
			}
		});

		listaDirec.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) {
				String fic = listaDirec.getSelectedValue().toString().replace("(DIR) ", "");
				txtSeleccionado.setText(fic);	
				ficheroSelec = listaDirec.getSelectedValue().toString();
				if (e.getClickCount() == 2 && listaDirec.getSelectedValue().toString().contains("(DIR)")) {
					listaDirec.removeAll();
					String nuevoDir = txtDirectorioRaiz.getText() + fic + "/";
					try {
						cliente.changeWorkingDirectory(nuevoDir);
						//Obteniendo ficheros y directorios del directorio actual
						FTPFile[] files = cliente.listFiles();
						llenarLista(files, nuevoDir);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					listaDirec.removeAll();
					txtDirectorioRaiz.setText(nuevoDir);
					direcSelec = nuevoDir;
				}
			}
		});
		
		botonSalir.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					cliente.disconnect();
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		botonCreaDir.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio","carpeta");
				if (!(nombreCarpeta==null)) 
				{
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					//nombre del directorio a crear
					directorio += nombreCarpeta.trim(); 
					//quita blancos a derecha y a izquierda
					try 
					{
						if (cliente.makeDirectory(directorio))
						{
							String m = nombreCarpeta.trim()+ " => Se ha creado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtSeleccionado.setText(m);
							//directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							//obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							//llenar la lista
							llenarLista(ff2, direcSelec);
						}
						else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				} // final del if
			}
		}); // final del bot�n CreaDir
		botonDelDir.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String nombreCarpeta = JOptionPane.showInputDialog(null,"Introduce el nombre del directorio a eliminar","carpeta");
				if (!(nombreCarpeta==null)) 
				{
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					//nombre del directorio a eliminar
					directorio += nombreCarpeta.trim(); //quita blancos a derecha y a izquierda
					try 
					{
						if(cliente.removeDirectory(directorio)) 
						{
							String m = nombreCarpeta.trim()+" => Se ha eliminado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtSeleccionado.setText(m);
							//directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							//obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							//llenar la lista
							llenarLista(ff2, direcSelec);
						}
						else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido eliminar ...");
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				} 
				// final del if
			}
		}); 
		//final del bot�n Eliminar Carpeta
		botonCargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser f;
				File file;
				f = new JFileChooser();
				//solo se pueden seleccionar ficheros
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//t�tulo de la ventana
				f.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
				//se muestra la ventana
				int returnVal = f.showDialog(f, "Cargar");
				if (returnVal == JFileChooser.APPROVE_OPTION) 
				{
					//fichero seleccionado
					file = f.getSelectedFile();
					//nombre completo del fichero
					String archivo = file.getAbsolutePath();
					//solo nombre del fichero
					String nombreArchivo = file.getName();
					try 
					{
						SubirFichero(archivo, nombreArchivo);
					}
					catch (IOException e1) 
					{
						e1.printStackTrace(); 
					}
				}
			}
		}); //Fin bot�n subir
		botonDescargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals("")) 
				{
					DescargarFichero(directorio + ficheroSelec, ficheroSelec);
				}
			}
		}); // Fin bot�n descargar
		botonBorrar.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals("")) 
				{
					BorrarFichero(directorio + ficheroSelec,ficheroSelec);
				}
			}
		});
	} // fin constructor

	private static void llenarLista(FTPFile[] files,String direc2) 
	{
		if (files == null)
			return;
		//se crea un objeto DefaultListModel
		DefaultListModel<String> modeloLista = new DefaultListModel<String>();
		modeloLista = new DefaultListModel<String>();
		//se definen propiedades para la lista, color y tipo de fuente

		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		//se eliminan los elementos de la lista
		listaDirec.removeAll();
		try 
		{
			//se establece el directorio de trabajo actual
			cliente.changeWorkingDirectory(direc2);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		direcSelec = direc2; //directorio actual
		//se a�ade el directorio de trabajo al listmodel, primerelementomodeloLista.addElement(direc2);
		//se recorre el array con los ficheros y directorios
		for (int i = 0; i < files.length; i++) 
		{
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")) 
			{
				//nos saltamos los directorios . y ..
				//Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				//Si es directorio se a�ade al nombre (DIR)
				if (files[i].isDirectory()) f = "(DIR) " + f;
				//se a�ade el nombre del fichero o directorio al listmodel
				modeloLista.addElement(f);
			}//fin if
		}//fin for
		try 
		{
			//se asigna el listmodel al JList,
			//se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(modeloLista);
		}
		catch (NullPointerException n) 
		{
			; //Se produce al cambiar de directorio
		}
	}//Fin llenarLista

	private boolean SubirFichero(String archivo, String soloNombre) throws IOException 
	{
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean ok = false;
		//directorio de trabajo actual
		cliente.changeWorkingDirectory(direcSelec);
		if (cliente.storeFile(soloNombre, in)) 
		{
			String s = " " + soloNombre + " => Subido correctamente...";
			txtSeleccionado.setText(s);
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			//obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			//llenar la lista con los ficheros del directorio actual
			llenarLista(ff2,direcSelec);
			ok = true;
		}
		else
			txtSeleccionado.setText("No se ha podido subir... " + soloNombre);
		return ok;
	}// final de SubirFichero

	private void DescargarFichero(String NombreCompleto, String nombreFichero) 
	{
		File file;
		String archivoyCarpetaDestino = "";
		String carpetaDestino = "";
		JFileChooser f = new JFileChooser();
		//solo se pueden seleccionar directorios
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//t�tulo de la ventana
		f.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = f.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		{
			file = f.getSelectedFile();
			//obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			//construimos el nombre completo que se crear� en nuestro disco
			archivoyCarpetaDestino = carpetaDestino + File.separator + nombreFichero;
			try 
			{
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null,	nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null,	nombreFichero + " => No se ha podido descargar ...");
				out.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	} // Final de DescargarFichero

	private void BorrarFichero(String NombreCompleto, String nombreFichero) 
	{
		//pide confirmaci�n
		int seleccion = JOptionPane.showConfirmDialog(null, "�Desea eliminar el fichero seleccionado?");
		if (seleccion == JOptionPane.OK_OPTION) 
		{
			try 
			{
				if (cliente.deleteFile(NombreCompleto)) 
				{
					String m = nombreFichero + " => Eliminado correctamente... ";
					JOptionPane.showMessageDialog(null, m);
					txtSeleccionado.setText(m);
					//directorio de trabajo actual
					cliente.changeWorkingDirectory(direcSelec);
					FTPFile[] ff2 = null;
					//obtener ficheros del directorio actual
					ff2 = cliente.listFiles();
					//llenar la lista con los ficheros del directorio actual
					llenarLista(ff2, direcSelec);
				}
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}// Final de BorrarFichero
}// Final de la clase ClienteFTPBasico