/*
 * Copyright (C) 2012-2017 DataStax Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robinsystems;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Connects to a Cassandra cluster and extracts basic information from it.
 * <p/>
 * Preconditions:
 * - a Cassandra cluster is running and accessible through the contacts points identified by CONTACT_POINTS and PORT.
 * <p/>
 * Side effects: none.
 *
 * @see <a href="http://datastax.github.io/java-driver/manual/">Java driver online manual</a>
 */
public class CassandraTest {

    public static void main(String[] args) {
        String CONTACT_POINT = "127.0.0.1";
        int PORT = 9042;

        if (args.length > 0 ) {
            CONTACT_POINT = args[0];
        }
        if (args.length > 1) {
            PORT = Integer.parseInt(args[1]);
        }

        Cluster cluster = null;
        try {
            // The Cluster object is the main entry point of the driver.
            // It holds the known state of the actual Cassandra cluster (notably the Metadata).
            // This class is thread-safe, you should create a single instance (per target Cassandra cluster), and share
            // it throughout your application.
            cluster = Cluster.builder()
                    .addContactPoints(CONTACT_POINT).withPort(PORT)
                    .build();

            // The Session is what you use to execute queries. Likewise, it is thread-safe and should be reused.
            Session session = cluster.connect();
            System.out.printf("Cassandra cluster name is: %s%n", cluster.getClusterName());

            // We use execute to send a query to Cassandra. This returns a ResultSet, which is essentially a collection
            // of Row objects.
            ResultSet rs = session.execute("select release_version from system.local");
            //  Extract the first row (which is the only one in this case).
            Row row = rs.one();

            // Extract the value of the first (and only) column from the row.
            String releaseVersion = row.getString("release_version");
            System.out.printf("Cassandra version is: %s%n", releaseVersion);

        } finally {
            // Close the cluster after we’re done with it. This will also close any session that was created from this
            // cluster.
            // This step is important because it frees underlying resources (TCP connections, thread pools...). In a
            // real application, you would typically do this at shutdown (for example, when undeploying your webapp).
            if (cluster != null)
                cluster.close();
        }
    }
}
