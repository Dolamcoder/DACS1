package Model;

public class Room {
    private String id;
    private int number;
    private String loaiPhong;
    private int  price;
    public int status;

    //1: Sẵn sàng, 2: Đã đăt, 3: Cần vệ sinh, 4:Đang bảo trì
    public Room() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        if(status==1){
            return "Sẵn sàng";
        }
        if(status==2) return "Đã đặt";
        if(status==3) return "Cần vệ sinh";
        return "Đang sửa chửa";
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public int  getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}