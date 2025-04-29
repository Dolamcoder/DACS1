package Model;

public class type_room {
    private String loaiPhong;
    private String nameLoaiPhong;
    private String soGiuong;
    private String moTa;
    private int sucChua;
    private double kichThuoc;

    // Constructor không tham số
    public type_room() {
    }

    // Constructor đầy đủ tham số
    public type_room(String loaiPhong, String nameLoaiPhong, String soGiuong, String moTa, int sucChua, double kichThuoc) {
        this.loaiPhong = loaiPhong;
        this.nameLoaiPhong = nameLoaiPhong;
        this.soGiuong = soGiuong;
        this.moTa = moTa;
        this.sucChua = sucChua;
        this.kichThuoc = kichThuoc;
    }

    // Getter và Setter
    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public String getNameLoaiPhong() {
        return nameLoaiPhong;
    }

    public void setNameLoaiPhong(String nameLoaiPhong) {
        this.nameLoaiPhong = nameLoaiPhong;
    }

    public String getSoGiuong() {
        return soGiuong;
    }

    public void setSoGiuong(String soGiuong) {
        this.soGiuong = soGiuong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public double getKichThuoc() {
        return kichThuoc;
    }

    public void setKichThuoc(double kichThuoc) {
        this.kichThuoc = kichThuoc;
    }

    @Override
    public String toString() {
        return "type_room{" +
                "loaiPhong='" + loaiPhong + '\'' +
                ", nameLoaiPhong='" + nameLoaiPhong + '\'' +
                ", soGiuong='" + soGiuong + '\'' +
                ", moTa='" + moTa + '\'' +
                ", sucChua=" + sucChua +
                ", kichThuoc=" + kichThuoc +
                '}';
    }
}
