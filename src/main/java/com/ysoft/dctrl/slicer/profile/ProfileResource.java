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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kuhn on 4/19/2017.
 */
@Component
public class ProfileResource extends AbstractConfigResource {
    private static final String PROFILE_EXTENSION = ".def.json";

    private final Logger logger = LogManager.getLogger(ProfileResource.class);
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
            logger.warn("Unable to read profiles from resources", e);
        }

        try {
            List<Profile> userProfiles = super.loadFromFolder(FilePath.PROFILE_DIR, Profile.class);
            profiles.addAll(userProfiles);
        } catch (IOException e) {
            logger.warn("Unable to read profiles from user folder", e);
        }

        return profiles;
    }

    public List<Profile> getProfiles(){
        return this.profiles;
    }

    public void set(Profile profile){}

    public Profile[] get(){return null;}

    public void applyProfile(Profile profile){
        slicerParams.resetToDefault();
        slicerParams.updateParams(profile.params);
        slicerParams.updateProfileDefaults(profile.params);
        selectedProfile = profile;
    }

    public Profile saveNewProfile(String name){

        ArrayList<SlicerParam> slicerParamsCopy = new ArrayList<>();
        for (SlicerParam param : this.slicerParams.getAllParams().values()){
            slicerParamsCopy.add( new SlicerParam(param) );
        }

        Profile profile = new Profile(
                UUID.randomUUID().toString(),
                name, "",
                slicerController.selectedSlicerID,
                printerResource.getPrinter().printerFamily,
                printerResource.getPrinter().id,
                slicerParamsCopy
        );

        writeProfileFile(profile);
        profiles.add(profile);

        return profile;
    }

    private void writeProfileFile(Profile profile){
        try {
            deeControlContext.getObjectMapper().writeValue(
                    new File(profileFolder + File.separator + profile.getId() + PROFILE_EXTENSION), profile);
        } catch(IOException e){
            logger.error("Profile save failed.", e);
        }
    }

}
