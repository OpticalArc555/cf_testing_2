package com.spring.jwt.controller.DO;

import com.spring.jwt.Interfaces.IBidPhoto;
import com.spring.jwt.dto.BidCarDto;
import com.spring.jwt.dto.ResponceDto;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.repository.IBidDoc;
import com.spring.jwt.service.DOService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/uploadFileBidCar")
@RequiredArgsConstructor
public class DOBidCarUploadController {
    private final IBidPhoto iDocument;
    @Value("${do.CDN.No}")
    private String CDNNo;
    private final String uploadDir = "uploads";
    private DOService doService = new DOService();
    //    private final String NODEJS_SERVER_URL = "https://digitaloceannodeservice.up.railway.app" ; // Change this to your Node.js server's URL
    private final String NODEJS_SERVER_URL = "https://digitaloceannodeimageservice-production.up.railway.app" ; // Change this to your Node.js server's URL

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/addWithoutPhoto")
    public ResponseEntity<?> uploadWithoutImage(@RequestParam String documentType,@RequestParam Integer carId,@RequestParam String doc,@RequestParam String doctype,@RequestParam String subtype,@RequestParam String comment) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            String serviceResponse = null;



              BidCarDto documentDto = new BidCarDto();
                documentDto.setComment(comment);
                documentDto.setDoctype(doctype);
                documentDto.setSubtype(subtype);




                serviceResponse = iDocument.addDocument(documentDto);



            return ResponseEntity.status(HttpStatus.OK).body(new ResponceDto("success", serviceResponse));
        } catch (RuntimeException e) {
//            System.err.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        }  catch (Exception e) {

            System.err.println(e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponceDto("unsuccess", "Failed to upload image"));

        }

    }
    @PostMapping("/add")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam String documentType,@RequestParam Integer carId,@RequestParam String doc,@RequestParam String doctype,@RequestParam String subtype,@RequestParam String comment) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = Paths.get(uploadDir, fileName);

            System.out.println("File path: " + filePath.toString());

            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            // Save the uploaded file to the specified directory
            file.transferTo(filePath);

            byte[] imageBytes = file.getBytes();
            Files.delete(filePath);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> payloadObject = new HashMap<>();
            payloadObject.put("imageBytes", imageBytes);
            payloadObject.put("contentType", file.getContentType());
            payloadObject.put("contentLength", imageBytes.length);
            String uniqueName = this.doService.generateRandomString(15) + fileName;
            System.err.println(fileName.length());
            payloadObject.put("imageName", uniqueName);
            if (uniqueName.isEmpty()) {
                throw new RuntimeException("BidCarPhoto not found");
            }
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payloadObject, httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange(
                    NODEJS_SERVER_URL + "/forward-image",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

//            System.err.println();

//            String arr[] = documentDto.split(",");
//            System.out.println("1"+arr[0]);
//            System.out.println("2"+arr[1]);
//            System.out.println(response.getStatusCode());
            String serviceResponse = null;

            if (!response.getBody().isEmpty()) {
//                JSONArray jsonArray = new JSONArray(documentJSONArray);
//                JSONObject jsonArray = new JSONObject(documentJSONArray);

//                System.out.println(jsonArray.toString());
                BidCarDto documentDto = new BidCarDto();
                documentDto.setComment(comment);
                documentDto.setDoctype(doctype);
                documentDto.setSubtype(subtype);




                documentDto.setDocumentLink(CDNNo + "/" + response.getBody());
                serviceResponse = iDocument.addDocument(documentDto);
            }


            return ResponseEntity.status(HttpStatus.OK).body(new ResponceDto("success", serviceResponse));
        } catch (RuntimeException e) {
//            System.err.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        } catch (IOException e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponceDto("unsuccess", "Failed to upload image"));
        } catch (Exception e) {

            System.err.println(e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponceDto("unsuccess", "Failed to upload image"));

        }

    }
    @GetMapping("/delete")
    private ResponseEntity<?> delete(@RequestParam Integer DocumentId) {
        try {
            String documents =iDocument.deleteById(DocumentId);
            ResponseDto responceDto = new ResponseDto("success",documents);
            return ResponseEntity.status(HttpStatus.OK).body(responceDto);
        } catch (Exception e) {
            System.err.println(e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        }
    }

//    @GetMapping("/getDocuments")
//    private ResponseEntity<?> getDocumentByUserIdAndDocId(@RequestParam Integer carId, @RequestParam String DocumentType) {
//        try {
//            Object documents = iDocument.getAllDocument(carId, DocumentType);
//            ResponceDto responceDto = new ResponceDto("success",documents);
//            return ResponseEntity.status(HttpStatus.OK).body(responceDto);
//        } catch (Exception e) {
//            System.err.println(e);
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));
//
//        }
//    }

    @GetMapping("/getById")
    private ResponseEntity<?> getById(@RequestParam Integer documentId) {
        try {
            Object documents =iDocument.getById(documentId);
            ResponceDto responceDto = new ResponceDto("success",documents);
            return ResponseEntity.status(HttpStatus.OK).body(responceDto);
        } catch (Exception e) {
            System.err.println(e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        }
    }

    @GetMapping("/getByCarID")
    private ResponseEntity<?> getByCarID(@RequestParam Integer carId) {
        try {
            Object documents =iDocument.getByCarID(carId);
            ResponceDto responceDto = new ResponceDto("success",documents);
            return ResponseEntity.status(HttpStatus.OK).body(responceDto);
        } catch (Exception e) {
            System.err.println(e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        }
    }
    @GetMapping("/getCarIdType")
    private ResponseEntity<?> getCarIdType(@RequestParam Integer carId,@RequestParam String docType) {
        try {
            Object documents =iDocument.getCarIdType(carId,docType);
            ResponceDto responceDto = new ResponceDto("success",documents);
            return ResponseEntity.status(HttpStatus.OK).body(responceDto);
        } catch (Exception e) {
            System.err.println(e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        }
    }
    @PatchMapping("/update")
    private ResponseEntity<?> update(@RequestParam String doc,@RequestParam String doctype,@RequestParam String subtype,@RequestParam String comment,@RequestParam Integer bidDocumentId) {
        try {
            String documents =iDocument.update( doc,doctype,subtype, comment,bidDocumentId);
            ResponceDto responceDto = new ResponceDto("success",documents);
            return ResponseEntity.status(HttpStatus.OK).body(responceDto);
        } catch (Exception e) {
            System.err.println(e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponceDto("unsuccess", String.valueOf(e)));

        }
    }
}