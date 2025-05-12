package Model;

public class Customer extends Person {
    private String card;
    private String status;
    public Customer(){
        super();
    }
    public void setCard(String card){
        this.card=card;
    }
    public String getCard(){
        return this.card;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getStatus(){
        return this.status;
    }
}
