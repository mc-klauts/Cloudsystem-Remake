package io.dexiron.cloud.master.web;

import static spark.Spark.*;

public class WebServer {

    public WebServer() {
        port(8081);
        get("/template", new TemplateRoute());
        post("/deploy", new DeployRoute());
        System.out.println("Web-Server [port=8081] gestartet");
    }


}