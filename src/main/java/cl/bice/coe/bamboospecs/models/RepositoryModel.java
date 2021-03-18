/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.bice.coe.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.repository.VcsChangeDetection;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.google.common.base.Strings;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepositoryModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String name;

    public String projectKey;

    public String repositorySlug;

    public String gitURL;

    public String path;

    public String branch;

    public String triggerPattern;

    public boolean shallowClone = true;

    public boolean submodules = false;  
  
    public CheckoutItem asCheckoutItem() {
        CheckoutItem vcs = new CheckoutItem().repository(new VcsRepositoryIdentifier().name(this.name));
        if (this.path != null && !this.path.isEmpty()) vcs.path(this.path);
        return vcs;
    }

    Plan addToPlan(Plan plan) {
        if (this.projectKey != null && !this.projectKey.isEmpty()) {
            if (this.repositorySlug == null || this.repositorySlug.isEmpty()) {
                throw new RuntimeException("Invalid repository (projectKey AND repositorySlug)");
            }
            BitbucketServerRepository stash = new BitbucketServerRepository()
                .name(this.name)
                .server(new ApplicationLink().name("Stash"))
                .projectKey(this.projectKey)
                .repositorySlug(this.repositorySlug)
                // set some "default" options
                .repositoryViewer(new BitbucketServerRepositoryViewer())
                .shallowClonesEnabled(shallowClone)
                .submodulesEnabled(submodules)
                .remoteAgentCacheEnabled(false);
            if (this.branch != null && !this.branch.isEmpty()) {
                stash.branch(this.branch);
            }

            if (!Strings.isNullOrEmpty(triggerPattern)) {
                stash.changeDetection(new VcsChangeDetection()
                        .filterFilePatternOption(VcsChangeDetection.FileFilteringOption.INCLUDE_ONLY)
                        .filterFilePatternRegex(triggerPattern)
                );
            }

            return plan.planRepositories(stash);
        } else if (this.gitURL != null && !this.gitURL.isEmpty()) {
            GitRepository git = new GitRepository();
            if (this.name == null || this.name.isEmpty()) {
                throw new RuntimeException("Invalid repository (needs gitURL AND path)");
            }
            git.name(this.name).url(this.gitURL);
            if (this.branch != null) {
                git.branch(this.branch);
            }
            return plan.planRepositories(git);
        }
        throw new RuntimeException("Invalid repository (missing projectKey or gitURL)");
    }
}
