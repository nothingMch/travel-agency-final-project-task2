package com.epam.finaltask.repository.Specification;

import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import org.springframework.data.jpa.domain.Specification;

public class TourSpecifications {

    public static Specification<Tour> hasPriceGreaterThanOrEqualTo(Double price) {
        return (root, query, criteriaBuilder) ->
                price != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price) : null;
    }

    public static Specification<Tour> hasTourType(TourType tourType) {
        return (root, query, criteriaBuilder) ->
                tourType != null ? criteriaBuilder.equal(root.get("tourType"), tourType) : null;
    }

    public static Specification<Tour> hasTransferType(TransferType transferType) {
        return (root, query, criteriaBuilder) ->
                transferType != null ? criteriaBuilder.equal(root.get("transferType"), transferType) : null;
    }

    public static Specification<Tour> hasHotelType(HotelType hotelType) {
        return (root, query, criteriaBuilder) ->
                hotelType != null ? criteriaBuilder.equal(root.get("hotelType"), hotelType) : null;
    }

    public static Specification<Tour> titleContains(String titlePart) {
        return (root, query, criteriaBuilder) ->
                titlePart != null && !titlePart.isBlank()
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + titlePart.toLowerCase() + "%")
                        : null;
    }

}
