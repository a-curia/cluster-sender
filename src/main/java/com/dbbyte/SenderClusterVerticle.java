package com.dbbyte;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.dbbyte.util.Constants.ADDRESS;

public class SenderClusterVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) throws Exception {

        final Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>Hello from non-clustered messenger example!</h1>");
        });
        router.post("/send/:message").handler(this::sendMessage);

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(config()
                    .getInteger("http.server.port", 8080), result ->
                        {
                        if (result.succeeded()) {
                            System.out.println("HTTP server running on port " + 8080 + vertx.hashCode());
                            future.complete();
                        } else {
                            System.out.println("Could not start a HTTP server -> "  + result.cause());
                            future.fail(result.cause());
                        }
                        });
    }

    private void sendMessage(RoutingContext routingContext){
        final EventBus eventBus = vertx.eventBus();
        System.out.println("**INSTANCE**"+vertx.hashCode()+"***"+eventBus.hashCode());
        final String message = routingContext.request().getParam("message");
        eventBus.publish("mytopic", message);
        System.out.printf("Current Thread Id %s Is Clustered %s With message: %s!\n", Thread.currentThread().getId(), vertx.isClustered(), message);
        routingContext.response().end(message);
    }
}
