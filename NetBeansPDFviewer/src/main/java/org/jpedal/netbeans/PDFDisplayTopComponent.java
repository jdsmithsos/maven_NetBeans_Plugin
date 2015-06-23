/*//GEN-LINE:variables
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jpedal.netbeans;

import java.awt.BorderLayout;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javax.swing.SwingUtilities;
import org.jpedal.examples.baseviewer.BaseViewerFX;
import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.JavaFXCommands;
import org.jpedal.examples.viewer.OpenViewerFX;
import org.jpedal.external.Options;
import org.jpedal.external.PluginHandler;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.jpedal.netbeans//PDFDisplay//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PDFDisplayTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.jpedal.netbeans.PDFDisplayTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PDFDisplayAction",
        preferredID = "PDFDisplayTopComponent"
)
@Messages({
    "CTL_PDFDisplayAction=PDFDisplay",
    "CTL_PDFDisplayTopComponent=PDFDisplay Window",
    "HINT_PDFDisplayTopComponent=This is a PDFDisplay window"
})
public final class PDFDisplayTopComponent extends TopComponent {

    BaseViewerFX viewer;

    //if you use the commerical jar, you will need to change this to ViewerFX.
    OpenViewerFX fullViewer;

    String PDFfile=null;

    JFXPanel fxPanel = new JFXPanel();

    private PDFViewerTypes viewerType =PDFViewerTypes.BASE_VIEWERFX;
    
    private String propertiesFile=null;

    public PDFDisplayTopComponent() {

        initComponents();
        setName(Bundle.CTL_PDFDisplayTopComponent());
        setToolTipText(Bundle.HINT_PDFDisplayTopComponent());
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);

    }
    public PDFDisplayTopComponent(PDFViewerTypes viewerType) {

        this();

        if(viewerType!=null){
            this.viewerType=viewerType;
        }

    }

    public PDFDisplayTopComponent(String file, PDFViewerTypes viewerType){

        this(viewerType);

        PDFfile = file;

        this.setDisplayName(file);

    }

    public PDFDisplayTopComponent(String file, String properties, PDFViewerTypes viewerType){

        this(viewerType);

        PDFfile = file;
        
        this.propertiesFile=properties;

        this.setDisplayName(file);

    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    // End of variables declaration                   
    @Override
    public void componentOpened() {

        this.setLayout(new BorderLayout());
        this.add(fxPanel, BorderLayout.CENTER);

        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    initFX();
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
                }
        });
    }

    private void initFX() throws FileNotFoundException {

        //get name of file which is displayed on title of panel
        String newPDFfile=this.getDisplayName();

        if(newPDFfile!=null){
            PDFfile=newPDFfile;
        }

        if (viewerType.equals(PDFViewerTypes.EXTERNAL_OPENVIEWERFX) || viewerType.equals(PDFViewerTypes.INTERNAL_OPENVIWERFX)) { //Use  OpenViewerFX 

            //Root pane which holds JavaFX PDF Viewer
            Pane viewerPane = new Pane();

            fullViewer = new OpenViewerFX(viewerPane, null);
            if(propertiesFile!=null){
                fullViewer.loadProperties(new FileInputStream(propertiesFile));
            }
            fullViewer.setupViewer();
            Scene scene = new Scene(viewerPane);

            fullViewer.addExternalHandler(new PluginCallBackHandler(this),Options.PluginHandler);

            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {

                fullViewer.getRoot().setMinWidth(newSceneWidth.doubleValue());

                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {

                fullViewer.getRoot().setMinHeight(newSceneHeight.doubleValue());                                

                }
            });

            fullViewer.getRoot().setMaxSize(scene.widthProperty().doubleValue(), scene.heightProperty().doubleValue());

            fxPanel.setScene(scene);
            
             //Open the PDF File in the Plugin Window.
            if (PDFfile != null && PDFfile.startsWith("http")) {

                String tmpDir = System.getProperty("java.io.tmpdir");
              
                String fileName=PDFfile.substring(1+PDFfile.lastIndexOf("/"));
               
                String fullPath=tmpDir+fileName;
                
                //see if already stored in TmpDir and cache if not
                File testFile=new File(fullPath);
                if(!testFile.exists()){
                    //copy from http to file                
                     try {
                        URL url = new URL(PDFfile);
                        Files.copy(url.openStream(), new File(fullPath).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                    }
                }

                fullViewer.executeCommand(Commands.OPENFILE, new String[]{fullPath});
            }else if (PDFfile != null) { //If the plugin state is still active load and write PDF
                fullViewer.executeCommand(Commands.OPENFILE, new String[]{PDFfile});
            }
            }else if (PDFfile != null) { //If the plugin state is still active load and write PDF
                fullViewer.executeCommand(Commands.OPENFILE, new String[]{PDFfile});
            
        } else { //Use BaseViewer
            viewer = new BaseViewerFX();

            Scene scene = viewer.setupViewer(this.getBounds().width, this.getBounds().height);

            viewer.addListeners();

            fxPanel.setScene(scene);

            if(PDFfile!=null){

                try {
                    viewer.loadPDF(PDFfile);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public void componentClosed() {
    }

    /**
     * Called when IDE is closed but plugin is left open.
     * Purpose is to save plugins last open state.
     * @param p is of type Properties
     */
    void writeProperties(Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings

        //Save the PDF path to property value "file"
        if (viewerType.equals(PDFViewerTypes.EXTERNAL_OPENVIEWERFX) || viewerType.equals(PDFViewerTypes.INTERNAL_OPENVIWERFX)) {
            String filename=fullViewer.executeCommand(JavaFXCommands.GETPDFNAME, null).toString();
            if(filename!=null){
                p.setProperty("file", filename);
            }
        } else {
            p.setProperty("file", viewer.getPDFfilename());
        }

        p.setProperty("viewerType", viewerType.toString());
    }

    /**
     * Called if plugin was left open when IDE was closed.
     * Purpose is to load plugin from its last state.
     * @param p is of type Properties
     */
    void readProperties(Properties p) {

        String version = p.getProperty("version");
        // TODO read your settings according to their version

        //new code for PDF plugin to restore
        String previousPDFfile = p.getProperty("file");
        
        if (previousPDFfile != null) {
            PDFfile = previousPDFfile;

            this.setDisplayName(PDFfile);
        }

        /**
         * restore viewer type to use
         */
        String viewerTypeStr = p.getProperty("viewerType");

        if(viewerTypeStr!=null){
            viewerType=PDFViewerTypes.valueOf(viewerTypeStr);
        }
    }

    public class PluginCallBackHandler implements PluginHandler  {

        PDFDisplayTopComponent handler;

        public PluginCallBackHandler(PDFDisplayTopComponent handler) {
            this.handler=handler;
        }

        @Override
        public void setFileName(final String string) {

            fullViewer.getRoot().setMinSize(handler.getBounds().width, handler.getBounds().height); //Set minimum size of JavaFX Viewer root container

            fullViewer.executeCommand(Commands.SCALING, new String[]{"Fit Page"});
             
            if (SwingUtilities.isEventDispatchThread()){ //remember this is Swing and not FX
                handler.setName(string);
            }else {
                final Runnable doPaintComponent = new Runnable() {
                    @Override
                    public void run() {
                        handler.setName(string);
                    }
                };
                SwingUtilities.invokeLater(doPaintComponent);
            }

        }
    }
}
