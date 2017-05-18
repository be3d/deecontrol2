package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.slicer.AbstractSlicer;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;

import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import oracle.jrockit.jfr.StringConstantPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.IOUtils;


import java.io.*;
import java.util.*;



/**
 * Created by kuhn on 4/4/2017.
 */
@Component
public class Cura extends AbstractSlicer {

    @Autowired
    protected EventBus eventBus;
    @Autowired
    protected DeeControlContext deeControlContext;

    private static final String CURA_RESOURCES_PATH = "/print/slicer/cura";
    private static final String CURA_BIN = "C:\\Projects\\deecontrol\\bin\\cura";
    private static final String CURA_EXE_PATH = CURA_BIN + (System.getProperty("os.name").startsWith("Win") ? "/CuraEngine.exe" : "maccosi");
    private static final String PARAM_SAMPLE_FILE = CURA_BIN + (System.getProperty("os.name").startsWith("Win") ? "/def/fdmprinter.def.json" : "maccosi");;
    private static final String TEMP_PATH = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer";
    private static final String LOG_FILE = TEMP_PATH + File.separator + "slicer.log";
    private static final String GCODE_FILE = TEMP_PATH + File.separator + "sliced.gcode";

    private static final CuraParamMap curaParamMap =  new CuraParamMap(SlicerParamType.class);
    private Task task;
    private Process process = null;

    public Cura() throws IOException {
        super();
    }

    public boolean supportsParam(String paramName) {
        for (Object s : this.curaParamMap.keySet()){
            try {
                if (s == SlicerParamType.valueOf(paramName)) {
                    return true;
                }
            }catch(IllegalArgumentException e){
                // Not supported parameter
                continue;
            }
        }
        return false;
    }

    public Map<String, SlicerParam> filterSupportedParams(Map<String, SlicerParam> params){
        Iterator<Map.Entry<String,SlicerParam>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,SlicerParam> entry = iter.next();
            try {
                if (!this.curaParamMap.containsKey(SlicerParamType.valueOf(entry.getKey()))){
                    throw new NoSuchElementException();
                }
            }catch(IllegalArgumentException e){
                iter.remove();
                System.out.println("Slicer param type not defined. " + entry.getKey());

            }catch(NoSuchElementException e){
                iter.remove();
                System.out.println("Parameter not supported by Cura. " + entry.getKey());
            }
        }
        return params;
    }

    public List<SlicerParam> filterSupportedParams(List<SlicerParam> params){
        for (Iterator<SlicerParam> iter = params.iterator(); iter.hasNext();){
            SlicerParam param = iter.next();
            try {
                if (!this.curaParamMap.containsKey(SlicerParamType.valueOf(param.id))){
                    throw new NoSuchElementException();
                }
            }catch(IllegalArgumentException | NoSuchElementException e){
                iter.remove();
            }
        }
        return params;
    }

    //@Override
    public void run(Map<String, SlicerParam> slicerParams, String modelSTL, ProgressBar progress) throws Exception {
    // todo autowire parameters
        List<String> cmdParams = new ArrayList<>(
                Arrays.asList(CURA_EXE_PATH,"slice","-v","-p","-j", PARAM_SAMPLE_FILE,"-o", GCODE_FILE,"-e0")
        );

        for(Map.Entry<String, SlicerParam> entry : slicerParams.entrySet()){
            try{
                Object paramName = this.curaParamMap.get(SlicerParamType.valueOf((String)entry.getKey()));
                if (paramName != null){
                    SlicerParam param = entry.getValue();
                    if (param.getValue() != null){
                        cmdParams.add("-s");
                        cmdParams.add(paramName+"=" + param.getValue());
                    } else if(param.getDefaultValue() != null){
                        cmdParams.add("-s");
                        cmdParams.add(paramName+"=" + param.getDefaultValue());
                    } else {
                        System.err.println("Param has no value nor default value");
                    }
                }
            } catch(IllegalArgumentException e){
                // parameter not defined for cura
                continue;
            }
        }

        cmdParams.add("-l");
        cmdParams.add(modelSTL);
        String[] cmd = cmdParams.toArray(new String[cmdParams.size()]);

        this.startTask(cmd, progress);
    }

    private void startTask(String[] cmd, ProgressBar progressBar){
        this.task = new Task<Void>() {
            @Override public Void call() {

                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                try{
                    process = pb.start();
                }catch (IOException e){
                    updateMessage("Cura IO Exception");
                    cancel();
                    return null;
                }
                System.out.println("Cura process started. \n Parameters:" + Arrays.toString(cmd));

                try (
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(LOG_FILE )))
                ){
                    String s;
                    Double progress;
                    while ((s = stdInput.readLine()) != null) {

                        if (isCancelled()){
                            updateMessage("Cancelled");
                            break;
                        }

                        System.out.println(s);
                        bw.write(s); //todo -> does not write correctly
                        //bw.newLine();

                        String[] message = s.split(":");
                        if (message[0].equals("Progress") ) {
                            try{
                                String text = message[1];
                                progress = Double.parseDouble(s.substring(s.indexOf("\t")+1, s.indexOf("%")));
                                updateProgress(progress, 1);
                                System.out.println("Slicer Progress " + String.format("%.5f",progress*100) + "%");
                                if (progress == 1.0){
                                    System.out.println("Slicing Succesfully Done");
                                    //this.eventBus.publish(new Event(EventType.SLICER_PROGRESS.name(), new ProgressMsg(text, progress)));
                                }
                            } catch(Exception e){
                                // Not parsable line
                            }
                        }
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }

                return null;
            }
            @Override protected void succeeded(){
                System.out.println("suceeded");
            }
            @Override protected void cancelled(){
                super.cancelled();
                System.out.println("Slicing cancelled");
            }
            @Override protected void failed(){
                super.failed();
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        (new Thread(task)).start();

    }
    public void stopTask(){
        System.out.println("Cancelling slicer...");
        task.cancel();
    }
}
