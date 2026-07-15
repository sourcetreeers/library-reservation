package com.library.vo;

import com.library.entity.Seat;

/**
 * 座位状态VO
 */
public class SeatStatusVO {
    private Long id;
    private Long libraryId;
    private String seatNumber;
    private String seatType;
    private String status; // 正常、维修、停用
    private Boolean isReserved; // 是否已被预约
    private String reservationStatus; // 当前预约状态：已预约/已使用(签到)/暂离/无
    private Integer rowNum;
    private Integer colNum;

    public SeatStatusVO() {
    }

    public SeatStatusVO(Seat seat, Boolean isReserved) {
        this.id = seat.getId();
        this.libraryId = seat.getLibraryId();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
        this.status = seat.getStatus();
        this.isReserved = isReserved;
        this.rowNum = seat.getRowNum();
        this.colNum = seat.getColNum();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsReserved() {
        return isReserved;
    }

    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getColNum() {
        return colNum;
    }

    public void setColNum(Integer colNum) {
        this.colNum = colNum;
    }
}
