package com.ysoft.dctrl.slicer.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuhn on 4/19/2017.
 */
@Component
public class ProfileResource {

    @Autowired PrinterResource printerResource;
    @Autowired SlicerParams slicerParams;
    @Autowired SlicerController slicerController;

    private static final String PROFILE_FOLDER = System.getProperty("user.home") + File.separator + ".dctrl" +
            File.separator + ".slicer" + File.separator + ".userProfiles";

    private static ObjectMapper objectMapper;
    protected List<Profile> profiles;
    protected Profile selectedProfile;

    public ProfileResource() {
        // todo get the instance from context
        this.objectMapper = new ObjectMapper();
        this.profiles = this.loadProfiles();
    }

    public List<Profile> loadProfiles(){
        List<Profile> profiles = new ArrayList<>();

        File profilesFolder = Paths.get(PROFILE_FOLDER).toFile();
        profilesFolder.mkdir();

        File [] profileFiles = profilesFolder.listFiles();
        for(File f : profileFiles){
            if(f.isFile()){
                try{
                    Profile p = this.objectMapper.readValue(f, Profile.class);
                    if (p.id != null)
                        profiles.add(p);
                }catch ( IOException e){
                    System.out.println("Profile definition error." + f.toString() + " " + e.getMessage());
                }
            }
        }
        return profiles;
    }

    public List<Profile> getProfiles(){
        return this.profiles;
    }

    public void set(Profile profile){}

    public Profile[] get(){return null;}

    public void applyProfile(Profile profile){
        // update printer, slicer, params
        this.slicerParams.updateParams(profile.params);
        this.selectedProfile = profile;
    }

    public void applyProfile(String profileID){
        for (Profile p : profiles){
            if(p.id.equals(profileID)) {
                this.applyProfile(p);
                return;
            }
        }
        System.out.println("PrinterResource: Printer " + profileID + "could not be set.");
    }

    public void applyProfile(Object profile){
        if (profile instanceof Profile){
            this.applyProfile((Profile)profile);
        }
    }

    public void resetToDefault(){}

    public void saveProfile(){
        if (this.selectedProfile == null){
            System.out.println("No profile selected");
        }

        this.saveProfile();
    }

    public void saveNewProfile(String name){
        Profile profile = new Profile("usernameid", name, "",
                this.slicerController.selectedSlicerID,
                //(this.printerResource.getPrinter()).printerGroup,
                "",
                (this.printerResource.getPrinter()).id,
                this.slicerParams.getAllParams());

        this.saveProfile(profile);
    }

    private void saveProfile(Profile profile){
        try {
            this.objectMapper.writeValue(new File(PROFILE_FOLDER + File.separator + profile.id + ".json"), profile);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
