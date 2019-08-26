package com.dbbyte;

import com.hazelcast.config.Config;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args){

        Config hazelcastConfig = new Config();
        hazelcastConfig.getGroupConfig().setName("my-cluster").setPassword("mypassword");

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().deployVerticle(new SenderClusterVerticle(), res -> {
                    if(res.succeeded()){
                        System.out.println("Deployment id is: " + res.result());
                    } else {
                        System.out.println("Deployment failed!");
                    }
                });
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
