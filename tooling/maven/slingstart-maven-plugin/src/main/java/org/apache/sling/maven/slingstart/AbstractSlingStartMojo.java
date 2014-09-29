/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.maven.slingstart;

import java.io.File;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.sling.slingstart.model.SSMDeliverable;

public abstract class AbstractSlingStartMojo extends AbstractMojo {

    @Parameter(defaultValue="${basedir}/src/main/systems")
    protected File systemsDirectory;

    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;

    @Component
    protected MavenProjectHelper projectHelper;

    @Parameter(property = "session", readonly = true, required = true)
    protected MavenSession mavenSession;

    @Parameter(defaultValue="false")
    protected boolean createWebapp;

    private static final String CTX_RAW = SSMDeliverable.class.getName() + "/r";
    private static final String CTX_EFFECTIVE = SSMDeliverable.class.getName() + "/e";

    /**
     * Read the model prepared by the lifecycle plugin
     */
    protected SSMDeliverable readRawModel()
    throws MojoExecutionException {
        SSMDeliverable result = (SSMDeliverable)this.project.getContextValue(CTX_RAW);
        if ( result == null ) {
            try {
                result = ModelUtils.getRawModel(this.project);

                this.project.setContextValue(CTX_RAW, result);
            } catch ( final IOException ioe) {
                throw new MojoExecutionException("Unable to cache model", ioe);
            }
        }
        return result;
    }

    /**
     * Read the model prepared by the lifecycle plugin
     */
    protected SSMDeliverable readEffectiveModel()
    throws MojoExecutionException {
        SSMDeliverable result = (SSMDeliverable)this.project.getContextValue(CTX_EFFECTIVE);
        if ( result == null ) {
            try {
                result = ModelUtils.getEffectiveModel(this.project);

                this.project.setContextValue(CTX_EFFECTIVE, result);
            } catch ( final IOException ioe) {
                throw new MojoExecutionException("Unable to cache model", ioe);
            }
        }
        return result;
    }

    protected File getTmpDir() {
        return new File(this.project.getBuild().getDirectory(), "slingstart-tmp");
    }
}