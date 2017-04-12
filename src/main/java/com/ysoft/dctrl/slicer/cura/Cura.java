package com.ysoft.dctrl.slicer.cura;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.AbstractSlicer;

import com.ysoft.dctrl.slicer.ProgressMsg;
import com.ysoft.dctrl.slicer.SlicerParam;
import com.ysoft.dctrl.slicer.SlicerParams;
import com.ysoft.dctrl.utils.DeeControlContext;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.nio.file.Files.newBufferedWriter;

/**
 * Created by kuhn on 4/4/2017.
 */
public class Cura extends AbstractSlicer {

    private static final String CURA_RESOURCES_PATH = "src/main/resources/print/slicer/cura/";
    private static final String CURA_EXE_PATH = CURA_RESOURCES_PATH + "CuraEngine.exe";
    private static final String TEMP_PATH = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer";
    private static final String LOG_FILE = TEMP_PATH + File.separator + "slicer.log";
    //private static final String PARAM_SAMPLE_FILE =TEMP_PATH + File.separator + "fdmprinter.def.json";
    private static final String PARAM_SAMPLE_FILE =CURA_RESOURCES_PATH + File.separator + "config" + File.separator + "fdmprinter.def.json";

    private static final String GCODE_FILE = TEMP_PATH + File.separator + "sliced.gcode";
    private static final String STL_FILE = TEMP_PATH + File.separator + "parthenon_1.stl";

    protected ObjectMapper objectMapper;
    protected CuraParamMap curaParamMap;

    public Cura(EventBus eventBus, ObjectMapper objectMapper) throws IOException {
        super(eventBus, objectMapper);
        this.curaParamMap = new CuraParamMap();
    }


    //@Override
    public void run(Map<String,Object> slicerParameters) throws Exception {

        // Prepare the command for CuraEngine.exe
        List<String> cmdParams = new ArrayList<>(
                Arrays.asList(CURA_EXE_PATH,"slice","-v","-p","-j", PARAM_SAMPLE_FILE,"-o", GCODE_FILE,"-e0")
        );
        Iterator it = slicerParameters.entrySet().iterator();
        while (it.hasNext ()){
            Map.Entry pair = (Map.Entry)it.next();
            try{
                String paramName = this.curaParamMap.map.get(SlicerParams.valueOf((String)pair.getKey()));
                if (paramName != null){
                    if (pair.getValue() instanceof ObjectNode){
                        JsonNode node = (ObjectNode)pair.getValue();
                        if (node.get("value") != null ){
                            cmdParams.add("-s");
                            cmdParams.add(paramName+"=" + node.get("value"));
                        } else if (node.get("default") != null) {
                            cmdParams.add("-s");
                            cmdParams.add(paramName+"=" + node.get("default"));
                        } else if (node instanceof TextNode){
                            cmdParams.add("-s");
                            cmdParams.add(paramName+"=" + node);
                        }
                    }
                }
            } catch(IllegalArgumentException e){
                // parameter not defined for cura
                continue;
            }
        }
        cmdParams.add("-l");
        cmdParams.add(STL_FILE);
        String[] cmd = cmdParams.toArray(new String[cmdParams.size()]);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        System.out.println("Cura process started. \n Parameters:" + Arrays.toString(cmd));

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        this.progressHandler(stdInput);
    }

    public void progressHandler(BufferedReader stdInput) throws Exception{
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(LOG_FILE )));
        } catch(IOException e){
            e.printStackTrace();
        }

        String s;
        Double progress;
        while ((s = stdInput.readLine()) != null) {
            //System.out.println(s);
            bw.write(s); //todo -> does not write correctly
            bw.newLine();

            String[] message = s.split(":");
            if (message[0].equals("Progress") ) {

                try{
                    String text = message[1];
                    progress = Double.parseDouble(s.substring(s.indexOf("\t")+1, s.indexOf("%")));
                    //this.eventBus.publish(new Event(EventType.SLICER_PROGRESS.name(), new ProgressMsg(text, progress)));
                    System.out.println("Slicer Progress " + Double.toString(progress));
                    if (progress == 1.0){
                        System.out.println("Slicing Done");
                        //this.eventBus.publish(new Event(EventType.SLICER_PROGRESS.name(), new ProgressMsg(text, progress)));
                    }
                }
                catch(Exception e){ // Not parsable line
                }
            }

        }
    }
}
