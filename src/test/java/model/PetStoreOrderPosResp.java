package model;

public class PetStoreOrderPosResp {
    private double id;
    private int petId;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;

    public PetStoreOrderPosResp() {
    }

    public double getId() {
        return id;
    }

    public int getPetId() {
        return petId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getShipDate() {
        return shipDate;
    }

    public String getStatus() {
        return status;
    }

    public boolean isComplete() {
        return complete;
    }
}
