package org.sahagin.jenkins;

import java.io.File;
import java.io.PrintStream;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

public class SahaginReportPublisher extends Recorder {
    private static final String buildReportDirName = "sahagin-report";
    private String sahaginYamlPath;

    @DataBoundConstructor
    public SahaginReportPublisher(String sahaginYamlPath) {
        if (sahaginYamlPath == null || sahaginYamlPath.equals("")) {
            this.sahaginYamlPath = "sahagin.yml"; // default
        } else {
            this.sahaginYamlPath = sahaginYamlPath;
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public SahaginReportPublisherDescriptor getDescriptor() {
        return (SahaginReportPublisherDescriptor) super.getDescriptor();
    }

    public String getSahaginYamlPath() {
        return sahaginYamlPath;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        PrintStream logger = listener.getLogger();
        FilePath configFilePath = new FilePath(build.getWorkspace(), sahaginYamlPath);
        
        try {
            logger.println("invoke SahaginMain with config: " + configFilePath.getRemote());
            SahaginMainExecutor exec = new SahaginMainExecutor(configFilePath.getRemote());
            // execute SahaginMain by SahaginMainExecutor 
            // on the machine on which the config file is located
            // TODO show more user-friendly error when report-input files have not been generated.
            FilePath reportOutputDir = configFilePath.act(exec);
            // move report to the directory for each build
            FilePath copyDest = new FilePath(new File(build.getRootDir(), buildReportDirName));
            // TODO should use rename instead of copy for efficiency
            logger.println("copy from remote dir " + reportOutputDir.getRemote() + " to " + copyDest.getRemote());
            reportOutputDir.copyRecursiveTo(copyDest);
            reportOutputDir.deleteRecursive();

            // add link from build result to sahagin report
            SahaginReportAction action = new SahaginReportAction(build);
            build.addAction(action);

            return true;
        } catch (Exception e) {
            e.printStackTrace(logger);
            return false;
        }
    }

    // post build action configuration
    @Extension
    public static final class SahaginReportPublisherDescriptor
    extends BuildStepDescriptor<Publisher> {

        @Override
        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Publish Sahagin HTML report";
        }
    }
}
