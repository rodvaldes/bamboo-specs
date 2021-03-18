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

package cl.bice.coe.bamboospecs.models.deployment;

import cl.bice.coe.bamboospecs.models.deployment.environment.EnvironmentPermissionModel;
import cl.bice.coe.bamboospecs.models.PermissionModel;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DeploymentPermissionModel {
    @NotEmpty
    @NotNull
    public String name;

    @NotEmpty
    @NotNull
    public List<PermissionModel> permissions;

    @NotEmpty
    @NotNull
    public List<EnvironmentPermissionModel> environments;

    public void publishPermissions(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        Permissions permissions = new Permissions();

        for (PermissionModel perm: this.permissions) {
            perm.addToPermissions(permissions);
        }

        // Ensure our admin user always has admin permission
        permissions.userPermissions(adminUser.getUsername(), PermissionType.VIEW, PermissionType.EDIT);

        permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

        bambooServer.publish(new DeploymentPermissions(this.name).permissions(permissions));

        // now publish all the environment permissions
        this.environments.forEach(x -> x.publishPermissions(bambooServer, adminUser, this.name));
    }
}
