package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Translates DCTRL general slicer parameters into set of CURA parameters.
 * Note that one DCTRL parameter can influence several CURA parameters with defined recalculation (stored in dictionary).
 *
 * Created by kuhn on 8/15/2017.
 */

public class CuraParamTranslator {

    private Map<SlicerParamType, Function<Double, Map<String, Object>>> dictionary = new HashMap<>();
    private final CuraParamMap curaParamMap;
    private final Map<String, SlicerParam> slicerParams;

    public CuraParamTranslator(CuraParamMap curaParamMap, Map<String, SlicerParam> slicerParams) {
        this.curaParamMap = curaParamMap;
        this.slicerParams = slicerParams;
        initDictionary();
    }

    public Map<String, Object> generateParams(SlicerParamType type, Object value){
        Map<String, Object> params = new HashMap<>();
        String s = curaParamMap.get(type);
        if (s != null){
            params.put(s, value);
        }

        Map<String, Object> additionalParams;
        try{
            additionalParams = dictionary.get(type).apply((Double)value);
            additionalParams.forEach((k,v) -> params.put(k,v));
        }catch(Exception e){
            System.out.println("fu");
        }

        return params;
    }

    /**
     * Expresses indirect relationships between slicer parameters DCTRL->CURA.
     */
    private void initDictionary(){
        dictionary.put(SlicerParamType.INFILL_DENSITY, value -> {
            Map<String, Object> out = new HashMap<>();
            Double infillLineWidth = (Double)slicerParams.get(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER.name()).getValue();
            Double k = 2.0; // pattern coefficient
            switch((String)slicerParams.get(SlicerParamType.INFILL_PATTERN.name()).getValue()) {
                case "grid":
                case "tetrahedral":
                    k = 2.0;
                    break;
                case "triangles":
                case "cubic":
                case "cubicsubdiv":
                    k = 3.0;
                    break;
            }
            out.put("infill_line_distance", infillLineWidth+(infillLineWidth*100)/(value*k));
            return out;
        });
        dictionary.put(SlicerParamType.SUPPORT_DENSITY, value -> {
            Map<String, Object> out = new HashMap<>();
            Double supportLineWidth = (Double)slicerParams.get(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER.name()).getValue();
            Double k = 1.0; // pattern coefficient
            switch((String)slicerParams.get(SlicerParamType.SUPPORT_PATTERN.name()).getValue()) {
                case "grid":
                    k = 2.0;
                    break;
                case "triangles":
                    k = 3.0;
                    break;
            }
            out.put("support_line_distance", (supportLineWidth*100)/(value*k));
            return out;
        });
    }
}
