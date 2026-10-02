package com.example.order_service.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"meta", "data"})
@Getter
public class BaseResponse<T> {
    private T data;
    private Metadata meta = new Metadata();

    public static <T> BaseResponse<T> ofSuccess(T data)
    {
        BaseResponse<T> response = new BaseResponse<>();
        response.data = data;
        response.meta.code = HttpStatus.OK.value();
        return response;
    }
    public static <T> BaseResponse<List<T>> ofSuccess(Page<T> page)
    {
        BaseResponse<List<T>> response = new BaseResponse<>();
        response.data = page.getContent();
        response.meta.page = page.getNumber();
        response.meta.pageSize = page.getSize();
        response.meta.total = page.getTotalElements();
        response.meta.code = HttpStatus.OK.value();
        return response;
    }

    public static <T> BaseResponse<T> ofDeleteSuccess()
    {
        BaseResponse<T> response= new BaseResponse<>();
        response.meta.code = HttpStatus.NO_CONTENT.value();
        return response;
    }

    public static <T> BaseResponse<T> ofSuccess(String message)
    {
        BaseResponse<T> response = new BaseResponse<>();
        response.meta.message = message;
        response.meta.code = HttpStatus.OK.value();
        return response;
    }

    public static BaseResponse<String> ofSuccessDataMessage(String message)
    {
        BaseResponse<String> response = new BaseResponse<>();
        response.data = message;
        response.meta.code = HttpStatus.OK.value();
        return response;

    }
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Metadata
    {
        private Integer code;
        private String message;
        private String requestId;
        private Integer page;
        private Integer pageSize;
        private Long total;
        private List<FieldViolation> errors;

    }


}


