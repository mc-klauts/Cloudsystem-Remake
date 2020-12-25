package io.dexiron.cloud.master.web;

import io.dexiron.cloud.master.CloudBootsrap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;

public class TemplateRoute implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
            File templateZip = new File(CloudBootsrap.getInstance().getCloudManager().getDeployDirectory(), "templates.zip");
            res.raw().setContentType("application/octet-stream");
            res.raw().setHeader("Content-Disposition", "attachment; filename=templates.zip");

            try (final BufferedOutputStream outputStream = new BufferedOutputStream(res.raw().getOutputStream())) {
                outputStream.write(Files.readAllBytes(templateZip.toPath()));
            }
            return res.raw();
    }

}