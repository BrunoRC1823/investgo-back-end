package com.proyecto.investgo.app.utils.service;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IDecoderTokenService {
    Map<String, Object> decodeToken(String token);
    String getIdUser(HttpServletRequest request);
}
