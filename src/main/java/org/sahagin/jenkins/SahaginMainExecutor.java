package org.sahagin.jenkins;


import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import org.sahagin.main.SahaginMain;
import org.sahagin.share.Config;
import org.sahagin.share.IllegalDataStructureException;
import org.sahagin.share.IllegalTestScriptException;
import org.sahagin.share.yaml.YamlConvertException;

public class SahaginMainExecutor implements FilePath.FileCallable<FilePath> {
    // TODO constant for temporal logic
    private static final String INVALID_CONFIG_YAML = "failed to load config file \"%s\": %s";
    private static final long serialVersionUID = 1L;
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
            Config config;
            try {
                config = Config.generateFromYamlConfig(file);
            } catch (YamlConvertException e) {
                throw new YamlConvertException(String.format(
                        INVALID_CONFIG_YAML, file.getAbsolutePath(), e.getLocalizedMessage()), e);
            }                    
            FilePath reportOutputDir = new FilePath(config.getRootBaseReportOutputDir());
            return reportOutputDir;
        } catch (YamlConvertException e) {
            throw new IOException(e);
        }
    }

}
