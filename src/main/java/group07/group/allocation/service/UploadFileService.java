package group07.group.allocation.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadFileService {

    public void uploadFile(MultipartFile filename) throws IOException {
        filename.transferTo(new File("//Users//zeynepnuraksay//Desktop//groupProject//group-07//src//main//resources//" + filename.getOriginalFilename()));
    }

}
