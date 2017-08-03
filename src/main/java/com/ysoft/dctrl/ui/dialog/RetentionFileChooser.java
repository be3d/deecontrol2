package com.ysoft.dctrl.ui.dialog;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.settings.Settings;

import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Created by pilar on 17.7.2017.
 */

@Component
public class RetentionFileChooser {
    private final FileChooser fileChooser;
    private final Settings settings;

    @Autowired
    public RetentionFileChooser(DeeControlContext deeControlContext) {
        this.settings = deeControlContext.getSettings();
        this.fileChooser = new FileChooser();
    }

    public File showOpenDialog(Window parent, FileChooser.ExtensionFilter... filters) {
        setFilters(filters);
        setPwd();
        File f = fileChooser.showOpenDialog(parent);
        if(f != null) { updatePwd(f.getParent()); }
        return f;
    }

    public File showSaveDialog(Window parent, FileChooser.ExtensionFilter... filters) {
        setFilters(filters);
        File f = fileChooser.showSaveDialog(parent);
        if(f != null) { updatePwd(f.getParent()); }
        return f;
    }

    public void setFilters(FileChooser.ExtensionFilter... filters) {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(filters);
    }

    public void setPwd() {
        String pwd = settings.getLastOpenPwd();
        if(pwd == null) { return; }
        File f = new File(pwd);
        if(!f.exists()) { return; }
        fileChooser.setInitialDirectory(f);
    }

    public void updatePwd(String pwd) {
        if(pwd == null) { return; }
        settings.setLastOpenPwd(pwd).save();
    }
}
