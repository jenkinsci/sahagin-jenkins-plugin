package org.sahagin.jenkins;

import hudson.model.AbstractBuild;
import hudson.tasks.junit.TestAction;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.sahagin.share.CommonUtils;


// This action adds link to Sahagin each test report
// to JUnit test report each result page.
public class SahaginTestAction extends TestAction {
    private AbstractBuild<?, ?> build;
    private String qualifiedClassName;
    private String testName;
    private static Pattern TEST_NG_TEST_NAME_PATTERN = Pattern.compile(".* on .*\\(.*\\)");

    public SahaginTestAction(AbstractBuild<?, ?> build, String qualifiedClassName, String testName) {
        this.build = build;
        this.qualifiedClassName = qualifiedClassName;
        this.testName = testName;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    public String getBuildUrl() {
        return build.getUrl();
    }

    public String getReportLinkIconFileName() {
        // TODO this is provisional, and is not transparent image
        return "/plugin/sahagin/provisional.png";
    }

    public String getQualifiedClassName() {
        return qualifiedClassName;
    }
    
    public String getEncodedQualifiedClassName() {
        return CommonUtils.encodeToSafeAsciiFileNameString(
                getQualifiedClassName(), Charset.forName("UTF-8"));
    }

    public String getTestHtmlFileName() {
        if (TEST_NG_TEST_NAME_PATTERN.matcher(testName).matches()) {
            return testName.substring(0, testName.indexOf(" "));
        } else {
            return testName;
        }
    }
    
    public String getEncodedTestHtmlFileName() {
        return CommonUtils.encodeToSafeAsciiFileNameString(
                getTestHtmlFileName(), Charset.forName("UTF-8"));
    }


}
