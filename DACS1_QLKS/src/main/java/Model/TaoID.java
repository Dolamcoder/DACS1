package Model;

public class TaoID {
    public  TaoID(){
    }
    public String taoIDKH(int number){
        if(number<0) return "";
        else if(number<10) return "KH000"+number;
        else if(number<100) return "KH00"+number;
        else if(number<1000) return "KH0"+number;
        else return "KH"+number;
    }
    public String taoIDRoom(int number){
        if(number<0) return "";
        else if(number<10) return "P00"+number;
        else if(number<100) return "P0"+number;
        else return "P"+number;
    }
    public String taoIDRoomBooking(int number){
        if(number<0) return "";
        else if(number<10) return "RB00"+number;
        else if(number<100) return "RB0"+number;
        else return "RB"+number;
    }
}
