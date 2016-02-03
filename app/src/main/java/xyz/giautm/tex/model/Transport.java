package xyz.giautm.tex.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * xyz.giautm.tex.models
 * 31/01/2016 - giau.tran.
 */
public class Transport {
    private String orderCode;                  // Mã đơn hàng.
    private long createdAt;                    // Thời điểm bàn giao vận chuyển
    private String createdBy;                  // Id của NVVC điều phối

    private HashMap<String, Long> callHistory; // Nhật ký cuộc gọi
    private HashMap<String, Long> sendHistory; // Nhật ký nhắn tin

    private int cause;                         // Lý do.
    private String causeNote;                  // Ghi chú.
    private long updatedAt;                    // Thời điểm cập nhật của NVVC.
    private List<Double> latLng;               // [Lat, Lng] Vị trí cập nhật

    private long confirmAt;                    // Thời điểm xác nhận.
    private String confirmBy;                  // Id của điều phối đã xác nhận.

    public enum CauseEnum {
        CANCEL(2, "Khách hàng yêu cầu huỷ"),
        TRY_AGAIN(3, "Giao lại vào lúc khác"),
        CONTACT_FAILED(4, "Không liên lạc được"),
        WRONG_ADDRESS(5, "Sai địa chỉ"),
        DELIVERY_FAILED(6, "Không giao kịp"),
        NO_PERSON(8, "Không có người nhận"),
        CHANGE_ORDER(9, "Yêu cầu thay đổi đơn hàng");

        private final int value;
        private final String message;

        CauseEnum(int value, String message) {
            this.value = value;
            this.message = message;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return message;
        }

        public static CauseEnum fromInt(int value) {
            for (CauseEnum e : CauseEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }

            return null;
        }
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public HashMap<String, Long> getCallHistory() {
        return callHistory;
    }

    public void setCallHistory(HashMap<String, Long> callHistory) {
        this.callHistory = callHistory;
    }

    public HashMap<String, Long> getSendHistory() {
        return sendHistory;
    }

    public void setSendHistory(HashMap<String, Long> sendHistory) {
        this.sendHistory = sendHistory;
    }

    public int getCause() {
        return cause;
    }

    public void setCause(int cause) {
        this.cause = cause;
    }

    public String getCauseNote() {
        return causeNote;
    }

    public void setCauseNote(String causeNote) {
        this.causeNote = causeNote;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Double> getLatLng() {
        return latLng;
    }

    public void setLatLng(double lat, double lng) {
        List<Double> latLng = new ArrayList<>(2);
        latLng.add(lat);
        latLng.add(lng);
        setLatLng(latLng);
    }

    public void setLatLng(List<Double> latLng) {
        this.latLng = latLng;
    }

    public long getConfirmAt() {
        return confirmAt;
    }

    public void setConfirmAt(long confirmAt) {
        this.confirmAt = confirmAt;
    }

    public String getConfirmBy() {
        return confirmBy;
    }

    public void setConfirmBy(String confirmBy) {
        this.confirmBy = confirmBy;
    }
}
