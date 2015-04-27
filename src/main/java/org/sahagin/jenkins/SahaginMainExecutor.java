package org.sahagin.jenkins;


import java.io.File;
import java.io.IOException;

import org.sahagin.main.SahaginMain;
import org.sahagin.share.Config;
import org.sahagin.share.IllegalDataStructureException;
import org.sahagin.share.IllegalTestScriptException;
import org.sahagin.share.yaml.YamlConvertException;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;

public class SahaginMainExecutor implements FilePath.FileCallable<FilePath> {
    private String configAbsPath;

    public SahaginMainExecutor(String configAbsPath) {
        this.configAbsPath = configAbsPath;
    }
    
    @Override
    public FilePath invoke(File file, VirtualChannel channel) 
            throws IOException, InterruptedException {
        try {
            SahaginMain.main(new String[]{"report", configAbsPath});
        } catch (YamlConvertException e) {
            throw new IOException(e);
        } catch (IllegalDataStructureException e) {
            throw new IOException(e);
        } catch (IllegalTestScriptException e) {
            throw new IOException(e);
        }
        try {
            Config config = Config.generateFromYamlConfig(file);
            FilePath reportOutputDir = new FilePath(config.getRootBaseReportOutputDir());
            return reportOutputDir;
        } catch (YamlConvertException e) {
            throw new IOException(e);
        }
    }

}