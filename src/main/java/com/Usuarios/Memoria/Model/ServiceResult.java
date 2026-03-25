package com.Usuarios.Memoria.Model;

import java.util.List;


public class ServiceResult<T> {
    
    public boolean correct;
    public  int status;
    public String erroMessage;
    public T object;
    public Exception ex;
    public List<T> Objects;
    public  String Message;
    
    
    
}
