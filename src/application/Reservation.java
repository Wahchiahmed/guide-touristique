package application;

public class Reservation {
    private int reservationId;
    private String startDate;
    private String endDate;
    Reservation (int reservationId,String startDate,String endDate){
    	this.reservationId=reservationId;
    	this.startDate=startDate;
    	this.endDate=endDate;
    }
	public int getReservationId() {
		return reservationId;
	}
	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

    
}