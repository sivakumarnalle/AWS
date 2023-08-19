package com.example.upload;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

	@GetMapping("")
	public String viewHomePage() {
		return "upload";
	}

	@PostMapping("/upload")
	public String handleUploadForm(Model model, String description, @RequestParam("file") MultipartFile multipart) {
		String fileName = multipart.getOriginalFilename();

		System.out.println("Description: " + description);
		System.out.println("filename: " + fileName);

		String message = "";

		try {
			S3Util.uploadFile(fileName, multipart.getInputStream());
			message = "Your file has been uploaded successfully!";
		} catch (Exception ex) {
			message = "Error uploading file: " + ex.getMessage();
		}

		model.addAttribute("message", message);

		return "message";
	}

	@GetMapping(value = "/download", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] handleDownloadFile(@RequestParam String key) {

		System.out.println("Downloading: " + key);
		
		try {
			return S3Util.downloadFile(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
