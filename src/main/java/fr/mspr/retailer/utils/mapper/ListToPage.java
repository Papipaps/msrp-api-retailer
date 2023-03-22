package fr.mspr.retailer.utils.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ListToPage {
    public static Page<?> toPage(List<?> list, Pageable pageable, long totalElements){
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        return  new PageImpl<>(list.subList(start, end), pageable, totalElements);

    }
}
