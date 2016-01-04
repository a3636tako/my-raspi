package controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Album;
import models.Audio;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class AudioQuery extends Controller {
	public Result get(String artist, String album, String title) throws IOException{
		System.out.println("Query:artist=" + artist + "&album=" + album + "&title=" + title);
		List<Album> res = Album.find(artist, album, title);
		
		ObjectMapper om = new ObjectMapper();
		JsonNode n = om.valueToTree(res);
		ObjectNode root = om.createObjectNode();
		root.set("list", n);
		//om.writeValue(System.out, root);
		
		return ok(root);
	}
}
