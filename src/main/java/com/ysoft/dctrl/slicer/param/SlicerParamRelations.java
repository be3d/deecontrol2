package com.ysoft.dctrl.slicer.param;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by kuhn on 4/21/2017.
 *
 * Change of some parameters effects values of other parameters.
 * This map stores relationships between those parameters as function handles.
 *
 */
@Component
public class SlicerParamRelations {

    public Map<SlicerParamType, Runnable> map = new HashMap<>();

    public SlicerParamRelations() {
    }

    public void init(Map<String,SlicerParam> params){
        map.put(SlicerParamType.RESOLUTION_LAYER_HEIGHT, new ParamChangeHandle(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name(), params){
                @Override
                public void run() {
                    super.run();
                    try{
                        // example
//                            this.params.get(SlicerParamType.SHELL_TOP_THICKNESS.name())
//                                    .setVal( (Double)this.param.getValue() * (Double)this.params.get(SlicerParamType.SHELL_TOP_LAYERS.name()).getValue());
//                           this.params.get(SlicerParamType.SHELL_TOP_THICKNESS.name())
//                                    .setVal( (Double)this.param.getValue() * (Double)this.params.get(SlicerParamType.SHELL_TOP_LAYERS.name()).getValue());
//                         // todo check if the parameter goes outside limits
                    }catch(NullPointerException e){}
                }
            });

    }

    public Runnable getHandle(SlicerParamType paramType){
       return map.get(paramType);
    }

}
