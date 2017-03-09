package com.adeptik.gateway.contracts.dto.users;

public class SignInInputDTO {

    public String UserName;

    public byte[] HashedPassword;

    public byte[] Salt;
}
