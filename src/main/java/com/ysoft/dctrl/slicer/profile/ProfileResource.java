package com.ysoft.dctrl.slicer.profile;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.slicer.AbstractConfigResource;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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

    private List<Profile> profiles;
    private Profile selectedProfile;

    private final String profileFolder;

    @Autowired
    public ProfileResource(EventBus eventBus, DeeControlContext deeControlContext,
                           PrinterResource printerResource, SlicerParams slicerParams,
                           SlicerController slicerController, FilePathResource filePathResource) {

        super(eventBus, deeControlContext, filePathResource);

        this.printerResource = printerResource;
        this.slicerParams = slicerParams;
        this.slicerController = slicerController;
        this.profiles = this.loadProfiles();
        this.profileFolder = filePathResource.getPath(FilePath.PROFILE_DIR);
    }

    public List<Profile> loadProfiles(){
        List<Profile> profiles = new ArrayList<>();

        // Add default profile with no value overrides
        profiles.add(new Profile());

        try {
            List<Profile> factoryProfiles = super.loadFromResource(FilePath.RESOURCE_PROFILE_DIR, Profile.class);
            profiles.addAll(factoryProfiles);
        } catch (IOException e) {
            System.err.println("Unable to read profiles from resources");
            e.printStackTrace();
        }

        try {
            List<Profile> userProfiles = super.loadFromFolder(FilePath.PROFILE_DIR, Profile.class);
            profiles.addAll(userProfiles);
        } catch (IOException e) {
            System.err.println("Unable to read profiles from user folder");
            e.printStackTrace();
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
        logger.warn("Profile {} could not be set ", profileID);
    }



    public void resetToDefault(){}

    /**
     * Saves the currently selected profile
     */
    public void saveProfile(){
        if (this.selectedProfile == null){
            logger.debug("No profile selected.");
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
            deeControlContext.getObjectMapper().writeValue(new File(profileFolder + File.separator + profile.getId() + ".json"), profile);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
