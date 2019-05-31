package application.rest.v1;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

@RestController
@RequestMapping(path = "v1/image")
public class Example {
	private final static String version = "2018-03-19";
	private final static String apiKey = "fncOJtJtogmpfx-e5ZWqks6VouBaPotqrn0FWSh01eLg";
	private final static String endPoint = "https://gateway.watsonplatform.net/visual-recognition/api";

	@PostMapping(path = "/recognize")
	public @ResponseBody String recognize(@RequestParam("file") MultipartFile file) {
		File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
		try {
			file.transferTo(localFile);
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// In the constructor, letting the SDK manage the IAM token
		IamOptions iam = new IamOptions.Builder().apiKey(apiKey).build();
		VisualRecognition service = new VisualRecognition(version, iam);
		service.setEndPoint(endPoint);

		ObjectMapper mapper = new ObjectMapper();
		Map<Double, String> map = new TreeMap<Double, String>(new Comparator<Double>() {
			public int compare(Double obj1, Double obj2) {
				// 降序排序
				return obj2.compareTo(obj1);
			}
		});

		// Add the URL of your image. The image size should not exceed 10MB.
		try {
			ClassifyOptions options = new ClassifyOptions.Builder().imagesFile(localFile).acceptLanguage("zh-cn")
					.build();
			ClassifiedImages result = service.classify(options).execute();
			JsonNode rootNode = mapper.readTree(result.toString());
			if (rootNode.has("error") || rootNode.has("status")) {
				return rootNode.toString();
			} else {
				Iterator<JsonNode> classes = rootNode.path("images").get(0).path("classifiers").get(0).path("classes")
						.elements();
				while (classes.hasNext()) {
					JsonNode cls = classes.next();
					map.put(cls.path("score").asDouble(), cls.path("class").asText());
				}
				return mapper.writeValueAsString(map);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Can not recognize this image.";
	}
}