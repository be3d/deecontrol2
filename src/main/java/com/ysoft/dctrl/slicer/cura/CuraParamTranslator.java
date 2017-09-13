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
    private void initDictionary() {
        dictionary.put(SlicerParamType.INFILL_DENSITY, x -> {
            double max = 1.143; // infill line distance when slider set to 100
            double a = 5.0; // slope adjustment constant
            double nzlD = (double) slicerParams.get(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER.name()).getValue();
            double hyperbolicComponent = (nzlD * 100 / x) - nzlD;
            double linearComponent = (a + max) - a * (0.01 * x);
            return new HashMap<String, Object>(){{
                put("infill_line_distance", hyperbolicComponent + linearComponent);
            }};
        });
        dictionary.put(SlicerParamType.SUPPORT_DENSITY, x -> {
            double max = 1.143; // infill line distance when slider set to 100
            double a = 5.0; // slope adjustment constant
            double nzlD = (double) slicerParams.get(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER.name()).getValue();
            double hyperbolicComponent = (nzlD * 100 / x) - nzlD;
            double linearComponent = (a + max) - a * (0.01 * x);
            return new HashMap<String, Object>(){{
                put("support_line_distance", hyperbolicComponent + linearComponent);
            }};
        });
        dictionary.put(SlicerParamType.SUPPORT_INTERFACE_DENSITY, x -> {
            double nzlD = (double) slicerParams.get(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER.name()).getValue();
            return new HashMap<String, Object>(){{
                put("support_roof_line_distance", x * nzlD / 100);
                put("support_bottom_line_distance", x * nzlD / 100);
            }};
        });
    }
}
