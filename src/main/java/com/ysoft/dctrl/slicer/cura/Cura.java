package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.AbstractSlicer;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;

import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.scene.control.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.*;



/**
 * Created by kuhn on 4/4/2017.
 */
@Component("Cura")
public class Cura extends AbstractSlicer {

    private String CURA_BIN_FOLDER;
    private String CURA_BIN_FILE;
    private String CURA_PARAM_SAMPLE_FILE;
    private String CURA_LOG_FILE;

    private static final CuraParamMap curaParamMap = new CuraParamMap(SlicerParamType.class);
    private static double progress = 0.0;

    @Autowired
    public Cura(EventBus eventBus, DeeControlContext deeControlContext) throws IOException {
        super(eventBus, deeControlContext);

        CURA_BIN_FOLDER = deeControlContext.getFileService().BIN_PATH + File.separator + "cura";
        CURA_BIN_FILE = CURA_BIN_FOLDER + "/CuraEngine.exe"; // for windows
        CURA_PARAM_SAMPLE_FILE = CURA_BIN_FOLDER + "/def/fdmprinter.def.json";
        CURA_LOG_FILE = deeControlContext.getFileService().TEMP_SLICER_PATH + File.separator + "cura.log";

    }

    public boolean supportsParam(String paramName) {
        for (Object s : this.curaParamMap.keySet()) {
            try {
                if (s == SlicerParamType.valueOf(paramName)) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                // Not supported parameter
                continue;
            }
        }
        return false;
    }

    public Map<String, SlicerParam> filterSupportedParams(Map<String, SlicerParam> params) {
        Iterator<Map.Entry<String, SlicerParam>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, SlicerParam> entry = iter.next();
            try {
                if (!this.curaParamMap.containsKey(SlicerParamType.valueOf(entry.getKey()))) {
                    throw new NoSuchElementException();
                }
            } catch (IllegalArgumentException e) {
                iter.remove();
                System.out.println("Slicer param type not defined. " + entry.getKey());

            } catch (NoSuchElementException e) {
                iter.remove();
                System.out.println("Parameter not supported by Cura. " + entry.getKey());
            }
        }
        return params;
    }

    public List<SlicerParam> filterSupportedParams(List<SlicerParam> params) {
        for (Iterator<SlicerParam> iter = params.iterator(); iter.hasNext(); ) {
            SlicerParam param = iter.next();
            try {
                if (!this.curaParamMap.containsKey(SlicerParamType.valueOf(param.getId()))) {
                    throw new NoSuchElementException();
                }
            } catch (IllegalArgumentException | NoSuchElementException e) {
                iter.remove();
            }
        }
        return params;
    }

    @Override
    public void run(Map<String, SlicerParam> slicerParams, String modelSTL, ProgressBar progressBar) throws IOException {

        // Construct the command with current parameters
        List<String> cmdParams = new ArrayList<>(
                Arrays.asList(CURA_BIN_FILE, "slice", "-v", "-p", "-j", CURA_PARAM_SAMPLE_FILE,
                        "-o", deeControlContext.getFileService().TEMP_SLICER_GCODE_FILE, "-e0")
        );
        for (Map.Entry<String, SlicerParam> entry : slicerParams.entrySet()) {
            try {
                Object paramName = this.curaParamMap.get(SlicerParamType.valueOf((String) entry.getKey()));
                if (paramName != null) {
                    SlicerParam param = entry.getValue();
                    if (param.getValue() != null) {
                        cmdParams.add("-s");
                        cmdParams.add(paramName + "=" + param.getValue());
                    } else if (param.getDefaultValue() != null) {
                        cmdParams.add("-s");
                        cmdParams.add(paramName + "=" + param.getDefaultValue());
                    } else {
                        System.err.println("Param has no value nor default value");
                    }
                }
            } catch (IllegalArgumentException e) {
                continue;   // parameter not defined for cura
            }
        }
        cmdParams.add("-l");
        cmdParams.add(modelSTL);
        String[] cmd = cmdParams.toArray(new String[cmdParams.size()]);

        // Start the CURA process
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true); // needs to be done for whatever reason
        Process process = pb.start();

        System.out.println("Cura process started. \n Parameters:" + Arrays.toString(cmd));

        try (
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(CURA_LOG_FILE)))
        ) {

            String s;
            while ((s = stdInput.readLine()) != null) {

                // Check for cancel command
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Exiting gracefully");
                    break;
                }

                // Log the console output of Cura to file
                bw.write(s);
                bw.newLine();

                // Parse the progress messages
                String[] message = s.split(":");
                if (message[0].equals("Progress")) {
                    try {
                        String text = message[1];
                        progress = Double.parseDouble(s.substring(s.indexOf("\t") + 1, s.indexOf("%")));
                        if (progress == 1.0) {
                            System.out.println("Slicing Succesfully Done");
                        }
                    } catch (Exception e) {
                        // Not parsable line
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Cura exception.");
            e.printStackTrace();
        }
    }

    public double getProgress(){
        return this.progress;
    }
}