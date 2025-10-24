package com.utp.libretago.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Reutilizables {
    public static Pageable ordernarPorDefectoDesc(Pageable pageable, String campo) {
        if (pageable.getSort().isSorted())
            return pageable;

        var defaultSort = Sort.by(Sort.Direction.DESC, campo);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }
}
