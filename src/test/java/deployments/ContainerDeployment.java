/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
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
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package deployments;

import com.arangodb.next.connection.HostDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.lifecycle.Startable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author Michele Rastelli
 */
public abstract class ContainerDeployment implements Startable {

    private static final Logger log = LoggerFactory.getLogger(ContainerDeployment.class);

    public static ContainerDeployment ofSingleServerNoAuth() {
        return new SingleServerNoAuthDeployment();
    }

    @Override
    public void start() {
        try {
            asyncStart().join();
        } catch (CompletionException e) {
            e.printStackTrace();
            if (e.getCause() instanceof ContainerLaunchException) {
                log.info("Containers failed to start, retrying...");
                stop();
                start();
            } else {
                throw e;
            }
        }
    }

    @Override
    public void stop() {
        asyncStop().join();
    }

    public abstract List<HostDescription> getHosts();

    protected String getImage() {
        return ContainerUtils.getImage();
    }

    protected abstract CompletableFuture<? extends ContainerDeployment> asyncStart();

    protected abstract CompletableFuture<ContainerDeployment> asyncStop();

}
