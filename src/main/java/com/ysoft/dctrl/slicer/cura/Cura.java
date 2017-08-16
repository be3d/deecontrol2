package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.Slicer;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;

import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.OSVersion;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kuhn on 4/4/2017.
 */
@Component("Cura")
public class Cura implements Slicer {
    private final Logger logger = LogManager.getLogger(Cura.class);

    private static final String CURA_FOLDER = "cura" + File.separator;
    private static final String WIN_BINARY = "CuraEngine.exe";
    private static final String MAC_BINARY = "CuraEngine";
    private static final String CONFIG_FILE = "def" + File.separator + "printer.def.json";
    private static final String LOG_FILE = "cura.log";

    private static final Matcher durationMatcher = Pattern.compile("^;TIME:(?<time>\\d+)").matcher("");
    private static final Matcher materialMatcher = Pattern.compile("^;Filament used: (?<usage>\\d+(\\.\\d+)?)m").matcher("");
    private static final Matcher layerCountMatcher = Pattern.compile("^Layer count: (?<count>\\d+)").matcher("");
    private static final Matcher placeholderMatcher = Pattern.compile("\\{(?<param>[A-Z0-9_]+)}").matcher("");

    private final String binFile;
    private final String configFile;
    private final String logFile;
    private final String outputFile;

    private final DeeControlContext deeControlContext;

    private static final CuraParamMap curaParamMap = new CuraParamMap(SlicerParamType.class);
    private static double progress = 0.0;

    private volatile long duration;
    private volatile Long[] materialUsage;
    private volatile int layerCount;

    @Autowired
    public Cura(FilePathResource filePathResource, DeeControlContext deeControlContext) throws IOException {
        this.deeControlContext = deeControlContext;
        String binPath = filePathResource.getPath(FilePath.BIN_DIR) + File.separator + CURA_FOLDER;
        binFile = binPath + (OSVersion.is(OSVersion.WIN) ? WIN_BINARY : MAC_BINARY);
        configFile = binPath + CONFIG_FILE;
        logFile = filePathResource.getPath(FilePath.SLICER_DIR) + File.separator + LOG_FILE;
        outputFile = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);
    }

    public boolean supportsParam(String paramName) {
        try {
            return Cura.curaParamMap.containsKey(SlicerParamType.valueOf(paramName));
        } catch (IllegalArgumentException e) {
            logger.debug("Not supported param {}",paramName);
        }
        return false;
    }

    public Map<String, SlicerParam> filterSupportedParams(Map<String, SlicerParam> params) {
        Iterator<Map.Entry<String, SlicerParam>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, SlicerParam> entry = iter.next();
            try {
                if (!curaParamMap.containsKey(SlicerParamType.valueOf(entry.getKey()))) {
                    throw new NoSuchElementException();
                }
            } catch (IllegalArgumentException e) {
                iter.remove();
                logger.debug("Slicer param type not defined {}", entry.getKey());

            } catch (NoSuchElementException e) {
                iter.remove();
                logger.debug("Parameter not supported by Cura {}", entry.getKey());
            }
        }
        return params;
    }

    public List<SlicerParam> filterSupportedParams(List<SlicerParam> params) {
        for (Iterator<SlicerParam> iter = params.iterator(); iter.hasNext(); ) {
            SlicerParam param = iter.next();
            try {
                if (!curaParamMap.containsKey(SlicerParamType.valueOf(param.getId()))) {
                    throw new NoSuchElementException();
                }
            } catch (IllegalArgumentException | NoSuchElementException e) {
                iter.remove();
            }
        }
        return params;
    }

    @Override
    public void run(Map<String, SlicerParam> slicerParams, String modelSTL) throws IOException {
        duration = 0;
        materialUsage = new Long[16];
        layerCount = 0;
        int counter = 0;

        createConfigFile(slicerParams);

        List<String> cmdParams = new ArrayList<>(Arrays.asList(binFile, "slice", "-v", "-p", "-j", configFile,"-o", outputFile, "-e0"));

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
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(logFile)))
        ) {

            String s;
            while ((s = stdInput.readLine()) != null) {
                System.err.println(s);

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

                durationMatcher.reset(s);
                if(durationMatcher.find()) {
                    duration = Long.parseLong(durationMatcher.group("time"));
                }

                materialMatcher.reset(s);
                if(materialMatcher.find()) {
                    materialUsage[counter++] = Math.round(1000 * Double.parseDouble(materialMatcher.group("usage")));
                }

                layerCountMatcher.reset(s);
                if(layerCountMatcher.find()) {
                    layerCount = Integer.parseInt(layerCountMatcher.group("count"));
                }
            }
        } catch (IOException e) {
            System.out.println("Cura exception.");
            e.printStackTrace();
        }
    }

    private void createConfigFile(Map<String, SlicerParam> slicerParams) {
        final PrinterProfile printerProfile = new PrinterProfile();
        slicerParams.forEach((k, p) -> {
            String paramName = curaParamMap.get(SlicerParamType.valueOf(k));
            if(paramName == null) return;

            Object o;
            if((o = p.getValue()) != null) {
                printerProfile.addOverride(paramName, (o instanceof String) ? replacePlaceholders((String) o, slicerParams) : o);
            }
        });

        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(configFile))){
            deeControlContext.getObjectMapper().writeValue(os, printerProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replacePlaceholders(String value, Map<String, SlicerParam> slicerParams) {
        placeholderMatcher.reset(value);

        while(placeholderMatcher.find()) {
            String paramName = placeholderMatcher.group("param");
            try {
                SlicerParamType type = SlicerParamType.valueOf(paramName);
                if(!curaParamMap.containsKey(type)) { continue; }
                SlicerParam p = slicerParams.get(paramName);
                if(p.getValue() == null) { continue; }
                value = value.replace("{" + paramName + "}", p.getValue().toString());
            } catch (IllegalArgumentException e) {
                System.err.println("Unknown pramaeter");
            }
        }

        return value;
    }

    public double getProgress(){
        return progress;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public Long[] getMaterialUsage() {
        return materialUsage;
    }

    @Override
    public int getLayerCount() { return layerCount; }
}