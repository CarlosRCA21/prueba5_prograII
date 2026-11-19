package Vista;

import Controlador.Controlador;
import Logica.ResultadoEstadisticas;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;


public class VentanaPrincipal extends JFrame implements ActionListener, ChangeListener {

    private static final long serialVersionUID = 1L;

    private final Controlador controlador;

    private final JTextField txtRuta = new JTextField(36);
    private final JTextField txtProfundidad = new JTextField("0", 4);
    private final JTextField txtTexto = new JTextField(14);
    private final JTextField txtExtension = new JTextField(6);
    private final JTextField txtMinKb = new JTextField(7);
    private final JTextField txtMaxKb = new JTextField(7);
    private final JTextField txtDesde = new JTextField(9);
    private final JTextField txtHasta = new JTextField(9);

    private final JButton btnExaminar = new JButton("Examinar...");
    private final JButton btnArbol = new JButton("Cargar árbol");
    private final JButton btnEstadisticas = new JButton("Estadísticas");
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnDuplicados = new JButton("Duplicados");
    private final JButton btnCancelar = new JButton("Cancelar");
    private final JProgressBar barra = new JProgressBar(0, 100);
    private final JLabel etiquetaEstado = new JLabel("Listo.");
    private final JMenuItem itemCriterios = new JMenuItem("Criterios del programa");

    private final JTabbedPane pestanas = new JTabbedPane();
    private final PanelArbol panelArbol;
    private final PanelEstadisticas panelEstadisticas;
    private final PanelBusqueda panelBusqueda;
    private final PanelDuplicados panelDuplicados;
    private final PanelGestion panelGestion;
    private final PanelEditor panelEditor;
    private final PanelBitacora panelBitacora;

    
    public VentanaPrincipal(Controlador controlador) {
        super("Explorador, analizador y gestor de archivos");
        this.controlador = controlador;
        panelArbol = new PanelArbol(this, controlador);
        panelEstadisticas = new PanelEstadisticas(controlador);
        panelBusqueda = new PanelBusqueda(this);
        panelDuplicados = new PanelDuplicados();
        panelGestion = new PanelGestion(controlador);
        panelEditor = new PanelEditor(this, controlador);
        panelBitacora = new PanelBitacora(controlador);
        armarVentana();
        restaurarConfiguracion();
    }

    private void armarVentana() {
        setLayout(new BorderLayout(6, 6));
        add(crearPanelEntradas(), BorderLayout.NORTH);

        pestanas.addTab("Árbol", panelArbol);
        pestanas.addTab("Estadísticas", panelEstadisticas);
        pestanas.addTab("Búsqueda", panelBusqueda);
        pestanas.addTab("Duplicados", panelDuplicados);
        pestanas.addTab("Gestión", panelGestion);
        pestanas.addTab("Editor de texto", panelEditor);
        pestanas.addTab("Bitácora", panelBitacora);
        pestanas.addChangeListener(this);
        add(pestanas, BorderLayout.CENTER);

        barra.setStringPainted(true);
        barra.setString("Sin tareas en curso");
        btnCancelar.setEnabled(false);
        JPanel sur = new JPanel(new BorderLayout(6, 2));
        sur.add(etiquetaEstado, BorderLayout.NORTH);
        sur.add(barra, BorderLayout.CENTER);
        sur.add(btnCancelar, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        JMenuBar menu = new JMenuBar();
        JMenu ayuda = new JMenu("Ayuda");
        ayuda.add(itemCriterios);
        menu.add(ayuda);
        setJMenuBar(menu);

        btnExaminar.addActionListener(this);
        btnArbol.addActionListener(this);
        btnEstadisticas.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnDuplicados.addActionListener(this);
        btnCancelar.addActionListener(this);
        itemCriterios.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new CierreVentana());
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelEntradas() {
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        fila1.add(new JLabel("Directorio raíz:"));
        fila1.add(txtRuta);
        fila1.add(btnExaminar);
        fila1.add(new JLabel("   Profundidad máxima (0 = ilimitada):"));
        fila1.add(txtProfundidad);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        fila2.add(new JLabel("Texto de búsqueda:"));
        fila2.add(txtTexto);
        fila2.add(new JLabel("Extensión (ej: txt):"));
        fila2.add(txtExtension);
        fila2.add(new JLabel("Tamaño mín. (KB):"));
        fila2.add(txtMinKb);
        fila2.add(new JLabel("Tamaño máx. (KB):"));
        fila2.add(txtMaxKb);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        fila3.add(new JLabel("Modificado desde (aaaa-MM-dd):"));
        fila3.add(txtDesde);
        fila3.add(new JLabel("hasta (aaaa-MM-dd):"));
        fila3.add(txtHasta);

        JPanel fila4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        fila4.add(btnArbol);
        fila4.add(btnEstadisticas);
        fila4.add(btnBuscar);
        fila4.add(btnDuplicados);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(fila1);
        panel.add(fila2);
        panel.add(fila3);
        panel.add(fila4);
        return panel;
    }

    

    private void restaurarConfiguracion() {
        Map<String, String> valores = controlador.cargarConfiguracion();
        restaurar(txtRuta, valores, "ruta");
        restaurar(txtProfundidad, valores, "profundidad");
        restaurar(txtTexto, valores, "texto");
        restaurar(txtExtension, valores, "extension");
        restaurar(txtMinKb, valores, "minKb");
        restaurar(txtMaxKb, valores, "maxKb");
        restaurar(txtDesde, valores, "desde");
        restaurar(txtHasta, valores, "hasta");
    }

    private void restaurar(JTextField campo, Map<String, String> valores, String clave) {
        String valor = valores.get(clave);
        if (valor != null) {
            campo.setText(valor);
        }
    }

    private void guardarConfiguracion() {
        Map<String, String> valores = new LinkedHashMap<String, String>();
        valores.put("ruta", txtRuta.getText());
        valores.put("profundidad", txtProfundidad.getText());
        valores.put("texto", txtTexto.getText());
        valores.put("extension", txtExtension.getText());
        valores.put("minKb", txtMinKb.getText());
        valores.put("maxKb", txtMaxKb.getText());
        valores.put("desde", txtDesde.getText());
        valores.put("hasta", txtHasta.getText());
        controlador.guardarConfiguracion(valores);
    }

    

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.ERROR_MESSAGE);
    }

    
    public void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

 
    public boolean confirmar(String pregunta) {
        Object[] opciones = {"Sí", "No"};
        int respuesta = JOptionPane.showOptionDialog(this, pregunta, "Confirmación", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opciones, opciones[1]);
        return respuesta == 0;
    }

    
    public void iniciarProgreso(String nombreTarea) {
        barra.setValue(0);
        barra.setString("Contando elementos...");
        etiquetaEstado.setText("Ejecutando: " + nombreTarea + "...");
        activarBotonesDeTarea(false);
        btnCancelar.setEnabled(true);
    }

    
    public void actualizarProgreso(int porcentaje) {
        barra.setValue(porcentaje);
        barra.setString(porcentaje + " %");
    }

    
    public void terminarProgreso(String mensaje, boolean completa) {
        activarBotonesDeTarea(true);
        btnCancelar.setEnabled(false);
        etiquetaEstado.setText(mensaje);
        if (completa) {
            barra.setValue(100);
            barra.setString("100 %");
        } else {
            barra.setValue(0);
            barra.setString("Sin tareas en curso");
        }
    }

    private void activarBotonesDeTarea(boolean activos) {
        btnArbol.setEnabled(activos);
        btnEstadisticas.setEnabled(activos);
        btnBuscar.setEnabled(activos);
        btnDuplicados.setEnabled(activos);
    }

    
    public void mostrarArbol(DefaultMutableTreeNode raiz) {
        panelArbol.mostrar(raiz);
        pestanas.setSelectedComponent(panelArbol);
    }

  
    public void mostrarEstadisticas(ResultadoEstadisticas resultado) {
        panelEstadisticas.mostrar(resultado);
        pestanas.setSelectedComponent(panelEstadisticas);
    }

  
    public void mostrarResultadosBusqueda(List<File> archivos) {
        panelBusqueda.mostrar(archivos);
        pestanas.setSelectedComponent(panelBusqueda);
    }

   
    public void mostrarDuplicados(Map<String, List<File>> grupos) {
        panelDuplicados.mostrar(grupos);
        pestanas.setSelectedComponent(panelDuplicados);
    }

   
    public void mostrarEnEditor(File archivo, String contenido) {
        panelEditor.mostrar(archivo, contenido);
        pestanas.setSelectedComponent(panelEditor);
    }

   
    public void avisarCambios() {
        etiquetaEstado.setText("Hubo cambios en el disco: vuelve a pulsar \"Cargar árbol\" para actualizar la vista.");
    }

   
    public void actualizarElemento(File elemento) {
        panelGestion.setElemento(elemento);
    }

    
    public void irAGestion(File elemento) {
        panelGestion.setElemento(elemento);
        pestanas.setSelectedComponent(panelGestion);
    }

    
    public void abrirEnEditor(File archivo) {
        controlador.abrirEnEditor(archivo.getAbsolutePath());
    }

    

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == btnExaminar) {
            elegirDirectorio();
        } else if (origen == btnArbol) {
            guardarConfiguracion();
            controlador.cargarArbol(txtRuta.getText(), txtProfundidad.getText());
        } else if (origen == btnEstadisticas) {
            guardarConfiguracion();
            controlador.analizar(txtRuta.getText(), txtProfundidad.getText());
        } else if (origen == btnBuscar) {
            guardarConfiguracion();
            controlador.buscar(txtRuta.getText(), txtProfundidad.getText(), txtTexto.getText(),
                    txtExtension.getText(), txtMinKb.getText(), txtMaxKb.getText(), txtDesde.getText(),
                    txtHasta.getText());
        } else if (origen == btnDuplicados) {
            guardarConfiguracion();
            controlador.buscarDuplicados(txtRuta.getText(), txtProfundidad.getText());
        } else if (origen == btnCancelar) {
            controlador.cancelarTarea();
        } else if (origen == itemCriterios) {
            mostrarCriterios();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (pestanas.getSelectedComponent() == panelBitacora) {
            panelBitacora.actualizar();
        }
    }

    private void elegirDirectorio() {
        JFileChooser selector = new JFileChooser(txtRuta.getText());
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selector.setDialogTitle("Elige el directorio raíz");
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtRuta.setText(selector.getSelectedFile().getAbsolutePath());
        }
    }

    private void mostrarCriterios() {
        String texto = "CRITERIOS DEL PROGRAMA\n\n"
                + "Profundidad: los elementos dentro de la raíz tienen profundidad 1, los de sus subcarpetas 2, etc. "
                + "0 significa ilimitada.\n\n"
                + "Archivos ocultos: SÍ se incluyen en el árbol, las estadísticas, la búsqueda y los duplicados "
                + "(en el árbol llevan la marca \"(oculto)\").\n\n"
                + "Archivos sin extensión: cuentan en la categoría \"otros\" y no coinciden con un filtro de "
                + "extensión. Un nombre como \".gitignore\" o que termina en punto también se considera sin extensión.\n\n"
                + "Extensiones: se comparan sin distinguir mayúsculas (.TXT = .txt).\n\n"
                + "Enlaces simbólicos a carpetas: se omiten para evitar bucles infinitos y se anotan en la bitácora "
                + "(ENLACE_OMITIDO). Un enlace a un archivo se trata como un archivo normal.\n\n"
                + "Carpetas sin permiso de lectura: se omiten y se anotan en la bitácora (SIN_PERMISO).\n\n"
                + "Número de carpetas: no cuenta la carpeta raíz. Una carpeta vacía es la que no tiene nada dentro.\n\n"
                + "Búsqueda: el texto debe estar contenido en el nombre del archivo, sin distinguir mayúsculas. "
                + "Los filtros de extensión, tamaño y fecha se combinan (deben cumplirse todos). "
                + "La fecha \"hasta\" incluye todo ese día.\n\n"
                + "Duplicados: mismo nombre (sin distinguir mayúsculas) y mismo tamaño en bytes, en distintas carpetas.\n\n"
                + "Editor: trabaja en UTF-8. Avisa si el archivo pesa más de 1 MB o no parece de texto, "
                + "y no abre archivos de más de 50 MB.\n\n"
                + "Archivos del programa: bitacora.log y config.txt se guardan en la carpeta desde donde se ejecuta "
                + "el programa.";
        JTextArea area = new JTextArea(texto, 22, 60);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Criterios", JOptionPane.INFORMATION_MESSAGE);
    }

    
    private class CierreVentana extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            controlador.cancelarTarea();
            guardarConfiguracion();
        }
    }
}
