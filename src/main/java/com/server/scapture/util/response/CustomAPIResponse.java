package com.server.scapture.util.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor // 모든 멤버변수를 매개변수로 받아 할당하는 생성자를 자동으로 작성해준다.
@NoArgsConstructor // 기본생성자(어떠한 매개변수도 받지 않는 생성자)를 자동으로 작성해준다.
@Getter
@Setter // 모든 멤버변수에 대해 Getter, Setter함수를 자동으로 작성해준다.
public class CustomAPIResponse<T>{

    // 공통적으로 반환되길 원하는 값들
    private int status;
    private T data; // 아직 어떤 형태로 값이 반환될 지 정해지지 않았으므로 Generic으로 작성한다.
    private String message;

    // 요청에 따라 CustomAPIResponse 객체의 값을 적절히 수정해야한다.

    // 통신이 성공할 경우에 대한 응답 값을 작성하는 함수
    // <T> 를 선언부에 작성한 이유는, 해당 함수의 data 값이 아직 정해지지 않았기 때문이다.
    // Generic을 사용하여 함수 호출 시점에 T의 타입을 우리가 결정할 수 있다.
    public static <T> CustomAPIResponse<T> createSuccess(int status, T data, String message) {
        return new CustomAPIResponse<>(status, data, message);
    }
    public static <T> CustomAPIResponse<T> createSuccessWithoutData(int status, String message) {
        return new CustomAPIResponse<>(status, null, message);
    }

    // 통신이 실패할 경우에 대한 응답 값을 작성하는 함수
    public static <T> CustomAPIResponse<T> createFailWithoutData(int status, String message) {
        return new CustomAPIResponse<>(status, null, message);
    }
}


