package com.spring.jwt.service;

import com.spring.jwt.Interfaces.IBidPhoto;
import com.spring.jwt.dto.BidCarDto;
import com.spring.jwt.entity.BidCarPhoto;
import com.spring.jwt.repository.IBidDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidPhotoImp implements IBidPhoto {
    @Autowired
    private IBidDoc iBidDoc;
    @Override
    public String addDocument(BidCarDto documentDto) {
        BidCarPhoto bidCarPhoto = new BidCarPhoto(documentDto);
        iBidDoc.save(bidCarPhoto);
        return "Bid photo added ";
    }

    @Override
    public String deleteById(Integer documentId) {
        iBidDoc.deleteById(documentId);
        return "bid photo deleted";
    }





    @Override
    public List<BidCarDto> getByDocumentType(Integer beadingCarId, String documentType) {
        List<BidCarPhoto> bidCarPhotos = iBidDoc.findBybeadingCarIdAndDocumentType(beadingCarId, documentType);
        if (bidCarPhotos.isEmpty()) {
            throw new RuntimeException("No documents found for the given car ID and document type");
        }
        return bidCarPhotos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private BidCarDto convertToDto(BidCarPhoto bidCarPhoto) {
        BidCarDto bidCarDto = new BidCarDto();
        bidCarDto.setBeadingCarId(bidCarPhoto.getBeadingCarId());
        bidCarDto.setDocumentLink(bidCarPhoto.getDocumentLink());
        bidCarDto.setDoctype(bidCarPhoto.getDoctype());
        bidCarDto.setSubtype(bidCarPhoto.getSubtype());
        bidCarDto.setComment(bidCarPhoto.getComment());
        return bidCarDto;
    }
    @Override
    public Object getById(Integer documentId) {
        return iBidDoc.findById(documentId);
    }

    @Override
    public String update( String doc, String doctype, String subtype, String comment,Integer bidDocumentId) {
        BidCarPhoto bidCarPhoto = iBidDoc.findById(bidDocumentId).orElseThrow(()->new RuntimeException("invalid bid document id"));
        if(!doctype.isEmpty())bidCarPhoto.setDoctype(doctype);
        if(!doc.isEmpty())bidCarPhoto.setDoc(doc);
        if(!subtype.isEmpty())bidCarPhoto.setSubtype(subtype);
        if(!comment.isEmpty())bidCarPhoto.setComment(comment);
        iBidDoc.save(bidCarPhoto);
        return "updated";

    }
}
