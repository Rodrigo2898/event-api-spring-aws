package com.rr_eventtec.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.rr_eventtec.api.domain.event.Event;
import com.rr_eventtec.api.domain.event.EventRequestDTO;
import com.rr_eventtec.api.service.IEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class EventServiceImpl implements IEventService {

    private final AmazonS3 s3Client;
    @Value("${aws.bucketName}")
    private String bucketName;
    private final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Event createEvent(EventRequestDTO data) {
        String imgUrl = null;

        if (data.image() != null) {
            imgUrl = uploadImg(data.image());
        }

        Event event = new Event();
        event.setTitle(data.title());
        event.setDescription(data.description());
        event.setEventUrl(data.eventUrl());
        event.setDate(new Date(data.date()));
        event.setImgUrl(imgUrl);

        return event;
    }

    private String uploadImg(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try {
            File file = convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            log.error("Error while uploading file: ", e);
            return null;
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convertedFile;
    }
}
