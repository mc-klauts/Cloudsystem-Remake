package io.dexiron.cloud.master.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.MultipartConfigElement;

import io.dexiron.cloud.master.CloudBootsrap;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;
import spark.Route;


import javax.servlet.MultipartConfigElement;

public class DeployRoute implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String group = req.headers("group");
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
        try (InputStream in = req.raw().getInputStream()) {
            OutputStream out = new FileOutputStream(CloudBootsrap.getInstance().getCloudManager().getDeployDirectory() + "/" + group + ".zip");
            IOUtils.copy(in, out);
            out.close();
        }

        File zipServer = new File(CloudBootsrap.getInstance().getCloudManager().getDeployDirectory(), group + ".zip");
        File templateServer = new File(CloudBootsrap.getInstance().getCloudManager().getTemplatesDirectory() + "/Lobby/");
        FileUtils.deleteQuietly(templateServer);

        new ZipFile(CloudBootsrap.getInstance().getCloudManager().getDeployDirectory() + "/" + group + ".zip").extractAll(CloudBootsrap.getInstance().getCloudManager().getTemplatesDirectory() + "/Lobby/");


        System.out.println("Group [name=" + group + "] was updated!");
        return "File saved";
    }

}
