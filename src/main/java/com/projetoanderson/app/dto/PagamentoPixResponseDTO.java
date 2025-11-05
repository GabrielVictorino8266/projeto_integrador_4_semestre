package com.projetoanderson.app.dto;

public class PagamentoPixResponseDTO {

    private Long paymentId;
    private String status;
    private String qrCodeBase64;
    private String qrCode;       

    public PagamentoPixResponseDTO(Long paymentId, String status, String qrCodeBase64, String qrCode) {
        this.paymentId = paymentId;
        this.status = status;
        this.qrCodeBase64 = qrCodeBase64;
        this.qrCode = qrCode;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}