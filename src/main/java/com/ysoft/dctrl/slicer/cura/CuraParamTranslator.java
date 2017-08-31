package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private final Logger logger = LogManager.getLogger(CuraParamTranslator.class);
    private final CuraParamMap curaParamMap;
    private final Map<String, SlicerParam> slicerParams;
    private Map<SlicerParamType, Function<Double, Map<String, Object>>> dictionary;

    public CuraParamTranslator(CuraParamMap curaParamMap, Map<String, SlicerParam> slicerParams) {
        this.curaParamMap = curaParamMap;
        this.slicerParams = slicerParams;
        this.dictionary = new HashMap<>();
        initDictionary();
    }

    public Map<String, Object> generateParams(SlicerParamType type, Object value){
        Map<String, Object> params = new HashMap<>();
        String s = curaParamMap.get(type);
        if (s != null){
            params.put(s, value);
        }

        Map<String, Object> additionalParams = new HashMap<>();
        Function<Double, Map<String, Object>> f = dictionary.get(type);
        if(f != null){
            if(value instanceof Integer){
                additionalParams = f.apply(((Integer) value).doubleValue());
            } else {
                try{
                    additionalParams = f.apply((Double) value);
                }catch(ClassCastException e){
                    logger.warn("Param translation failed: {}", type, e);
                }
            }
            additionalParams.forEach((k,v) -> params.put(k,v));
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
            out.put("infill_line_distance", infillLineWidth*100/value);
            return out;
        });
        dictionary.put(SlicerParamType.SUPPORT_DENSITY, value -> {
            Map<String, Object> out = new HashMap<>();
            Double supportLineWidth = (Double)slicerParams.get(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER.name()).getValue();
            out.put("support_line_distance", supportLineWidth*100/value);
            return out;
        });
    }
}
