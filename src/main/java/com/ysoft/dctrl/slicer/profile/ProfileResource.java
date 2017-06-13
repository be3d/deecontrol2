package com.ysoft.dctrl.slicer.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.slicer.AbstractConfigResource;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import com.ysoft.dctrl.utils.DeeControlContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuhn on 4/19/2017.
 */
@Component
public class ProfileResource extends AbstractConfigResource {

    private final PrinterResource printerResource;
    private final SlicerParams slicerParams;
    private final SlicerController slicerController;

    private static final String PROFILE_FOLDER = System.getProperty("user.home") + File.separator + ".dctrl" +
            File.separator + ".slicer" + File.separator + ".userProfiles";
    private static final String FACTORY_PROFILE_FOLDER = "print/slicer/definitions/factory_profiles";


    protected List<Profile> profiles;
    protected Profile selectedProfile;

    @Autowired
    public ProfileResource(EventBus eventBus, DeeControlContext deeControlContext,
                           PrinterResource printerResource, SlicerParams slicerParams,
                           SlicerController slicerController) {

        super(eventBus, deeControlContext);

        this.printerResource = printerResource;
        this.slicerParams = slicerParams;
        this.slicerController = slicerController;
        this.profiles = this.loadProfiles();
    }

    public List<Profile> loadProfiles(){
        List<Profile> profiles = new ArrayList<>();

        // Add default profile with no value overrides
        profiles.add(new Profile());

        List<Profile> factoryProfiles = super.loadObjects(FACTORY_PROFILE_FOLDER, Profile.class, true);
        for (Profile p : factoryProfiles){
            profiles.add(p);
        }

        List<Profile> userProfiles = super.loadObjects(PROFILE_FOLDER, Profile.class, false);
        for (Profile p : userProfiles){
            profiles.add(p);
        }

        return profiles;
    }

    public List<Profile> getProfiles(){
        return this.profiles;
    }

    public void set(Profile profile){}

    public Profile[] get(){return null;}

    /**
     * Cast profile object from Presenter to (Profile) and delegate
     * @param profile
     */
    public void applyProfile(Object profile){
        if (profile instanceof Profile){
            this.applyProfile((Profile)profile);
        }
    }

    public void applyProfile(Profile profile){
        // update printer, slicer, params
        this.slicerParams.resetToDefault();
        this.slicerParams.updateParams(profile.params);
        this.selectedProfile = profile;
    }

    public void applyProfile(String profileID){
        for (Profile p : profiles){
            if(p.getId().equals(profileID)) {
                this.applyProfile(p);
                return;
            }
        }
        System.out.println("PrinterResource: Printer " + profileID + "could not be set.");
    }



    public void resetToDefault(){}

    /**
     * Saves the currently selected profile
     */
    public void saveProfile(){
        if (this.selectedProfile == null){
            System.out.println("No profile selected");
        }

        this.saveProfile(this.selectedProfile);
    }

    /**
     * Saves a new profile
     * @param name of the profile
     * @return
     */
    public Profile saveNewProfile(String name){

        // Copy the parameters from context
        ArrayList<SlicerParam> slicerParamsCopy = new ArrayList<SlicerParam>();
        for (SlicerParam param : this.slicerParams.getAllParams().values()){
            slicerParamsCopy.add( new SlicerParam(param) );
        }

        Profile profile = new Profile("usernameid", name, "",
                this.slicerController.selectedSlicerID,
                //(this.printerResource.getPrinter()).printerGroup,
                "",
                (this.printerResource.getPrinter()).id,
                slicerParamsCopy);


        this.saveProfile(profile);
        return profile;
    }

    /**
     * Dumps the profile into file
     * @param profile
     */
    private void saveProfile(Profile profile){
        try {
            deeControlContext.getObjectMapper().writeValue(new File(PROFILE_FOLDER + File.separator + profile.getId() + ".json"), profile);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
